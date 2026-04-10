package com.anti.fraud.modules.user.controller;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.result.Result;
import com.anti.fraud.common.utils.RedisUtils;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.user.dto.LoginDTO;
import com.anti.fraud.modules.user.dto.RegisterDTO;
import com.anti.fraud.modules.user.service.UserService;
import com.anti.fraud.modules.user.vo.LoginVO;
import com.anti.fraud.modules.user.vo.UserVO;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.core.util.IdUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证管理控制器
 * 处理用户登录、注册、登出等认证相关操作
 */
@Tag(name = "认证管理", description = "登录、注册、登出等接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RedisUtils redisUtils;

    /**
     * 验证码 Redis Key 前缀
     */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    /**
     * 验证码有效期（秒）
     */
    private static final long CAPTCHA_EXPIRE_SECONDS = 300;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        // 限流检查
        String clientIp = getClientIp(request);
        String limitKey = "login:limit:" + clientIp;
        Integer count = (Integer) redisUtils.get(limitKey);
        if (count != null && count >= 5) {
            return Result.fail("登录尝试次数过多，请稍后再试");
        }

        // 验证码校验
        String captchaKey = loginDTO.getCaptchaKey();
        String captchaCode = loginDTO.getCaptchaCode();
        if (captchaKey == null || captchaCode == null || captchaKey.isEmpty() || captchaCode.isEmpty()) {
            return Result.fail("请输入验证码");
        }

        // 从 Redis 获取验证码答案并验证
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        String correctCode = (String) redisUtils.get(redisKey);
        if (correctCode == null) {
            return Result.fail("验证码已过期，请重新获取");
        }

        // 验证码不区分大小写
        if (!correctCode.equalsIgnoreCase(captchaCode)) {
            return Result.fail("验证码错误");
        }

        // 验证成功后删除验证码
        redisUtils.delete(redisKey);

        try {
            LoginVO loginVO = userService.login(loginDTO);
            // 登录成功，重置计数器
            redisUtils.delete(limitKey);
            return Result.success("登录成功", loginVO);
        } catch (BusinessException e) {
            // 登录失败，增加计数器
            if (count == null) {
                redisUtils.set(limitKey, 1, 1, TimeUnit.HOURS);
            } else {
                redisUtils.set(limitKey, count + 1, 1, TimeUnit.HOURS);
            }
            throw e;
        }
    }

    /**
     * 获取客户端真实IP地址
     *
     * 支持多种代理头，按优先级从高到低依次为：
     * 1. X-Forwarded-For - 代理链中第一个IP为真实客户端IP
     * 2. X-Real-IP - Nginx等反向代理常用的真实IP头
     * 3. Proxy-Client-IP - Apache代理
     * 4. WL-Proxy-Client-IP - WebLogic代理
     * 5. HTTP_CLIENT_IP - 某些代理会设置此头
     * 6. HTTP_X_FORWARDED_FOR - 与X-Forwarded-For同义
     * 7. HTTP_X_FORWARDED - 旧版代理头
     * 8. HTTP_X_CLUSTER_CLIENT_IP - 集群环境
     * 9. HTTP_FORWARDED_FOR - 另一种形式
     * 10. HTTP_FORWARDED - 另一种形式
     * 11. request.getRemoteAddr() - 直接连接时的地址
     *
     * 注意：当从代理链获取IP时，可能存在多个IP（逗号分隔），
     * 此时取第一个IP（最接近客户端的）作为真实IP。
     *
     * @param request HTTP请求对象
     * @return 客户端真实IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        // 常见的代理头，按优先级排序
        String[] headerNames = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED"
        };

        String ip = null;

        // 遍历所有可能的代理头
        for (String headerName : headerNames) {
            ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                break;
            }
        }

        // 如果所有代理头都没有，返回直接连接的地址
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理代理链：X-Forwarded-For 可能包含多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // 处理 IPv6 本地地址
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.successMsg("注册成功");
    }

    @Operation(summary = "用户登出", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            redisUtils.delete("token:" + token);
        }
        return Result.successMsg("登出成功");
    }

    @Operation(summary = "获取图形验证码")
    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha() {
        // 使用 Hutool 生成图形验证码（剪切干扰验证码）
        cn.hutool.captcha.ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(120, 40, 4, 4);

        // 生成唯一 key 用于标识本次验证码
        String captchaKey = IdUtil.simpleUUID();

        // 获取验证码答案
        String captchaCode = captcha.getCode();

        // 将验证码答案存入 Redis，设置5分钟过期
        String redisKey = CAPTCHA_KEY_PREFIX + captchaKey;
        redisUtils.set(redisKey, captchaCode, CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);

        // 生成验证码图片 Base64
        String captchaImage = "data:image/png;base64," + captcha.getImageBase64Data();

        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        result.put("captchaImage", captchaImage);

        return Result.success(result);
    }

    @Operation(summary = "获取当前用户信息", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized("请先登录");
        }
        return Result.success(userService.getUserInfo(userId));
    }
}
