package com.anti.fraud.modules.user.controller;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.result.Result;
import com.anti.fraud.common.utils.RedisUtils;
import com.anti.fraud.common.utils.security.InputSecurityUtil;
import com.anti.fraud.common.utils.security.PasswordStrengthUtil;
import com.anti.fraud.modules.user.dto.LoginDTO;
import com.anti.fraud.modules.user.dto.RegisterDTO;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.service.UserService;
import com.anti.fraud.modules.user.vo.LoginVO;
import com.anti.fraud.modules.user.vo.UserVO;
import com.anti.fraud.security.service.*;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.core.util.IdUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证管理控制器
 * 处理用户登录、注册、登出、密码找回等认证相关操作
 */
@Tag(name = "认证管理", description = "登录、注册、登出、验证码等接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RedisUtils redisUtils;
    private final PasswordStrengthUtil passwordStrengthUtil;
    private final InputSecurityUtil inputSecurityUtil;
    private final LoginSecurityService loginSecurityService;
    private final TokenSecurityService tokenSecurityService;
    private final CaptchaService captchaService;
    private final RateLimitService rateLimitService;

    // Redis Key 前缀
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final String DEVICE_KEY_PREFIX = "device:";
    private static final String TOKEN_VERSION_PREFIX = "token:version:";

    // 配置
    private static final long CAPTCHA_EXPIRE_SECONDS = 300;
    private static final int TOKEN_EXPIRE_DAYS = 7;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        String deviceId = request.getHeader("X-Device-ID");
        String deviceInfo = request.getHeader("User-Agent");

        // 1. 登录安全检查
        LoginSecurityService.LoginSecurityResult securityCheck =
                loginSecurityService.checkLoginSecurity(loginDTO.getUsername());
        if (!securityCheck.isAllowed()) {
            return Result.fail(securityCheck.getMessage());
        }

        // 2. 频率限制检查
        RateLimitService.RateLimitResult rateLimitResult =
                rateLimitService.checkLoginLimit(request, loginDTO.getUsername());
        if (!rateLimitResult.isAllowed()) {
            return Result.fail(rateLimitResult.getMessage());
        }

        // 3. 图形验证码校验
        if (loginDTO.getCaptchaKey() == null || loginDTO.getCaptchaCode() == null ||
                loginDTO.getCaptchaKey().isEmpty() || loginDTO.getCaptchaCode().isEmpty()) {
            return Result.fail("请输入验证码");
        }

        String redisKey = CAPTCHA_KEY_PREFIX + loginDTO.getCaptchaKey();
        String correctCode = (String) redisUtils.get(redisKey);
        if (correctCode == null) {
            return Result.fail("验证码已过期，请重新获取");
        }

        if (!correctCode.equalsIgnoreCase(loginDTO.getCaptchaCode())) {
            return Result.fail("验证码错误");
        }

        // 验证成功后删除验证码
        redisUtils.delete(redisKey);

        try {
            // 4. 执行登录
            LoginVO loginVO = userService.login(loginDTO);

            // 5. 登录成功后的安全处理
            handleLoginSuccess(request, loginVO, clientIp, deviceId, deviceInfo);

            // 6. 记录登录日志
            loginSecurityService.recordLoginLog(
                    loginVO.getUserId(),
                    loginVO.getUsername(),
                    true,
                    clientIp,
                    deviceInfo,
                    null
            );

            // 返回增强的登录信息
            Map<String, Object> result = new HashMap<>();
            result.put("user", loginVO);
            result.put("deviceId", deviceId);
            result.put("remainingAttempts", MAX_LOGIN_FAIL_COUNT);

            return Result.success("登录成功", loginVO);

        } catch (BusinessException e) {
            // 登录失败处理
            handleLoginFailure(loginDTO.getUsername(), clientIp, e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO, HttpServletRequest request) {
        // 1. 注册频率限制
        RateLimitService.RateLimitResult rateLimitResult = rateLimitService.checkRegisterLimit(request);
        if (!rateLimitResult.isAllowed()) {
            return Result.fail(rateLimitResult.getMessage());
        }

        // 2. 输入安全校验
        validateRegistrationInput(registerDTO);

        // 3. 密码强度校验
        PasswordStrengthUtil.ValidationResult passwordValidation =
                passwordStrengthUtil.validateForRegistration(registerDTO.getPassword());
        if (!passwordValidation.isValid()) {
            return Result.fail(passwordValidation.getMessage());
        }

        // 4. 验证码校验（如果启用）
        if (registerDTO.getEmail() != null && !registerDTO.getEmail().isEmpty()) {
            if (registerDTO.getEmailCode() != null && !registerDTO.getEmailCode().isEmpty()) {
                if (!captchaService.verifyEmailCode(registerDTO.getEmail(), registerDTO.getEmailCode())) {
                    return Result.fail("邮箱验证码错误或已过期");
                }
            }
        }

        // 5. 注册协议确认
        if (registerDTO.getAgreement() == null || !registerDTO.getAgreement()) {
            return Result.fail("请阅读并同意用户协议和隐私政策");
        }

        // 6. 执行注册
        userService.register(registerDTO);

        return Result.successMsg("注册成功，请登录");
    }

    @Operation(summary = "用户登出", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                               HttpServletRequest request) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 检查Token是否在黑名单
            if (tokenSecurityService.isTokenBlacklisted(token)) {
                return Result.successMsg("已登出");
            }

            // 获取用户ID
            Long userId = null;
            try {
                userId = extractUserIdFromToken(token);
            } catch (Exception e) {
                // Token无效也继续处理
            }

            // 计算Token剩余有效期
            long remainingSeconds = 0;
            if (userId != null) {
                remainingSeconds = getTokenRemainingSeconds(token);
            }

            // 安全登出
            if (userId != null) {
                tokenSecurityService.secureLogout(token, userId, remainingSeconds);
            }

            // 删除旧的Token
            redisUtils.delete("token:" + token);
        }

        return Result.successMsg("登出成功");
    }

    @Operation(summary = "获取图形验证码")
    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha(HttpServletRequest request) {
        String clientIp = getClientIp(request);

        // 频率限制
        RateLimitService.RateLimitResult rateLimitResult = rateLimitService.checkApiLimit(request);
        if (!rateLimitResult.isAllowed()) {
            return Result.fail(rateLimitResult.getMessage());
        }

        // 生成验证码
        cn.hutool.captcha.ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(120, 40, 4, 4);
        String captchaKey = IdUtil.simpleUUID();
        String captchaCode = captcha.getCode();

        // 存储验证码
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        redisUtils.set(redisKey, captchaCode, CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);

        // 生成验证码图片
        String captchaImage = "data:image/png;base64," + captcha.getImageBase64Data();

        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        result.put("captchaImage", captchaImage);
        result.put("expireSeconds", String.valueOf(CAPTCHA_EXPIRE_SECONDS));

        return Result.success(result);
    }

    @Operation(summary = "发送短信验证码")
    @PostMapping("/sms-code")
    public Result<Map<String, String>> sendSmsCode(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String phone = params.get("phone");

        if (phone == null || phone.isEmpty()) {
            return Result.fail("手机号不能为空");
        }

        // 手机号格式验证
        if (!inputSecurityUtil.isValidPhone(phone)) {
            return Result.fail("手机号格式不正确");
        }

        // 频率限制
        RateLimitService.RateLimitResult rateLimitResult = rateLimitService.checkSmsCodeLimit(request, phone);
        if (!rateLimitResult.isAllowed()) {
            return Result.fail(rateLimitResult.getMessage());
        }

        // 发送验证码
        CaptchaService.CaptchaResult captchaResult = captchaService.sendSmsCode(phone);
        if (!captchaResult.isSuccess()) {
            return Result.fail(captchaResult.getMessage());
        }

        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captchaResult.getCaptchaKey());

        return Result.success(result);
    }

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/email-code")
    public Result<Map<String, String>> sendEmailCode(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String email = params.get("email");

        if (email == null || email.isEmpty()) {
            return Result.fail("邮箱不能为空");
        }

        // 邮箱格式验证
        if (!inputSecurityUtil.isValidEmail(email)) {
            return Result.fail("邮箱格式不正确");
        }

        // 发送验证码
        CaptchaService.CaptchaResult captchaResult = captchaService.sendEmailCode(email);
        if (!captchaResult.isSuccess()) {
            return Result.fail(captchaResult.getMessage());
        }

        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captchaResult.getCaptchaKey());

        return Result.success(result);
    }

    @Operation(summary = "验证邮箱验证码")
    @PostMapping("/verify-email-code")
    public Result<Void> verifyEmailCode(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String code = params.get("code");

        if (email == null || code == null) {
            return Result.fail("邮箱和验证码不能为空");
        }

        if (!captchaService.verifyEmailCode(email, code)) {
            return Result.fail("验证码错误或已过期");
        }

        return Result.successMsg("验证成功");
    }

    @Operation(summary = "获取当前用户信息", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        UserVO userInfo = userService.getCurrentUserInfo();
        return Result.success(userInfo);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refreshToken(@RequestBody Map<String, String> params) {
        String refreshToken = params.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return Result.fail("Refresh Token不能为空");
        }

        // 验证Refresh Token
        TokenSecurityService.RefreshTokenResult tokenResult =
                tokenSecurityService.validateRefreshToken(refreshToken);

        if (!tokenResult.isValid()) {
            return Result.fail(tokenResult.getMessage());
        }

        // 生成新的Access Token
        String newAccessToken = generateNewAccessToken(tokenResult.getUserId(), tokenResult.getFamily());

        // 生成新的Refresh Token
        String newRefreshToken = tokenSecurityService.generateRefreshToken(
                tokenResult.getUserId(),
                tokenResult.getFamily()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", newAccessToken);
        result.put("refreshToken", newRefreshToken);
        result.put("expiresIn", 86400000L); // 24小时

        return Result.success(result);
    }

    @Operation(summary = "检查密码强度")
    @PostMapping("/check-password")
    public Result<Map<String, Object>> checkPasswordStrength(@RequestBody Map<String, String> params) {
        String password = params.get("password");
        String username = params.get("username");

        PasswordStrengthUtil.ValidationResult result =
                passwordStrengthUtil.validate(password);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", result.isValid());
        response.put("strength", result.getStrength().getScore());
        response.put("strengthText", result.getStrength().getDescription());
        response.put("strengthColor", result.getStrength().getColor());
        response.put("message", result.getMessage());
        response.put("isWeak", result.isWeakPassword());

        // 详细检查结果
        java.util.List<PasswordStrengthUtil.CheckResult> checks =
                passwordStrengthUtil.getDetailedChecks(password, username);
        java.util.List<Map<String, Object>> checkResults = new java.util.ArrayList<>();
        for (PasswordStrengthUtil.CheckResult check : checks) {
            Map<String, Object> checkMap = new HashMap<>();
            checkMap.put("passed", check.isPassed());
            checkMap.put("message", check.getMessage());
            checkResults.add(checkMap);
        }
        response.put("checks", checkResults);

        return Result.success(response);
    }

    @Operation(summary = "验证用户名是否可用")
    @GetMapping("/check-username")
    public Result<Map<String, Object>> checkUsername(@RequestParam String username) {
        // 输入验证
        if (!inputSecurityUtil.isValidUsername(username)) {
            Map<String, Object> result = new HashMap<>();
            result.put("available", false);
            result.put("message", "用户名格式不正确");
            return Result.success(result);
        }

        // 检查是否存在
        User user = userService.findByUsername(username);
        boolean available = user == null;

        Map<String, Object> result = new HashMap<>();
        result.put("available", available);
        result.put("message", available ? "用户名可用" : "用户名已被注册");

        return Result.success(result);
    }

    @Operation(summary = "获取登录安全状态")
    @GetMapping("/login-security-status")
    public Result<Map<String, Object>> getLoginSecurityStatus(@RequestParam String username) {
        LoginSecurityService.LoginSecurityResult securityResult =
                loginSecurityService.checkLoginSecurity(username);

        Map<String, Object> result = new HashMap<>();
        result.put("allowed", securityResult.isAllowed());
        result.put("message", securityResult.getMessage());
        result.put("remainingAttempts", securityResult.getRemainingAttempts());
        result.put("lockRemainingSeconds", securityResult.getLockRemainingSeconds());

        return Result.success(result);
    }

    @Operation(summary = "强制所有设备登出")
    @PostMapping("/logout-all-devices")
    public Result<Void> logoutAllDevices(@RequestHeader(value = "Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.fail("未登录");
        }

        String token = authHeader.substring(7);
        Long userId = extractUserIdFromToken(token);

        if (userId == null) {
            return Result.fail("Token无效");
        }

        // 使所有Token失效
        tokenSecurityService.invalidateAllUserTokens(userId);

        return Result.successMsg("已强制所有设备登出");
    }

    // ========== 私有方法 ==========

    private static final int MAX_LOGIN_FAIL_COUNT = 5;

    private void handleLoginSuccess(HttpServletRequest request, LoginVO loginVO,
                                    String clientIp, String deviceId, String deviceInfo) {
        // 清理登录失败记录
        loginSecurityService.onLoginSuccess(loginVO.getUsername());

        // 记录设备登录
        if (deviceId == null) {
            deviceId = loginSecurityService.generateDeviceId();
        }

        String location = getLocationFromIp(clientIp);
        loginSecurityService.recordDeviceLogin(
                loginVO.getUserId(),
                deviceId,
                deviceInfo,
                clientIp,
                location
        );

        // 检查异地登录
        if (loginSecurityService.checkAbnormalLocation(loginVO.getUserId(), clientIp, location)) {
            // 可以在这里发送异地登录提醒通知
            log.warn("检测到异地登录: userId={}, ip={}, location={}",
                    loginVO.getUserId(), clientIp, location);
        }

        // 更新Token版本
        incrementTokenVersion(loginVO.getUserId());
    }

    private void handleLoginFailure(String username, String clientIp, String reason) {
        // 记录失败
        boolean locked = loginSecurityService.recordLoginFailure(username);

        // 获取剩余尝试次数
        int remainingAttempts = loginSecurityService.getRemainingLoginAttempts(username);

        // 记录登录日志
        loginSecurityService.recordLoginLog(null, username, false, clientIp, null, reason);

        if (locked) {
            long lockRemaining = loginSecurityService.getLockRemainingSeconds(username);
            log.warn("账号已被锁定: username={}, remainingSeconds={}", username, lockRemaining);
        }
    }

    private void validateRegistrationInput(RegisterDTO registerDTO) {
        // 用户名验证
        if (!inputSecurityUtil.isValidUsername(registerDTO.getUsername())) {
            throw new BusinessException("用户名格式不正确，只能包含字母、数字和下划线，长度3-20位");
        }

        // 检查XSS和SQL注入
        inputSecurityUtil.validateInput(registerDTO.getUsername(), "用户名", 20, false, false);
        inputSecurityUtil.validateInput(registerDTO.getRealName(), "真实姓名", 50, false, false);

        if (registerDTO.getEmail() != null && !registerDTO.getEmail().isEmpty()) {
            if (!inputSecurityUtil.isValidEmail(registerDTO.getEmail())) {
                throw new BusinessException("邮箱格式不正确");
            }
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP",
                "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
        };

        String ip = null;
        for (String header : headerNames) {
            ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                break;
            }
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    private String getLocationFromIp(String ip) {
        // 实际应该调用IP地理位置服务
        // 这里简化处理
        if (ip.startsWith("192.168.") || ip.startsWith("10.") || ip.equals("127.0.0.1")) {
            return "本地";
        }
        return "未知位置";
    }

    private Long extractUserIdFromToken(String token) {
        // 简化实现，实际应该解析JWT
        return null;
    }

    private long getTokenRemainingSeconds(String token) {
        // 简化实现，实际应该解析JWT获取过期时间
        return 3600;
    }

    private void incrementTokenVersion(Long userId) {
        String versionKey = TOKEN_VERSION_PREFIX + userId;
        redisUtils.increment(versionKey);
        redisUtils.expire(versionKey, 365, TimeUnit.DAYS);
    }

    private String generateNewAccessToken(Long userId, String family) {
        // 实际应该使用JWT工具类生成
        return "new_access_token_" + System.currentTimeMillis();
    }
}

