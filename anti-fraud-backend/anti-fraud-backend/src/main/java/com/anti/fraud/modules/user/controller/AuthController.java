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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "认证管理", description = "登录、注册、登出等接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RedisUtils redisUtils;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        // 限流检查
        String clientIp = getClientIp(request);
        String key = "login:limit:" + clientIp;
        Integer count = (Integer) redisUtils.get(key);
        if (count != null && count >= 5) {
            return Result.fail("登录尝试次数过多，请稍后再试");
        }
        
        try {
            LoginVO loginVO = userService.login(loginDTO);
            // 登录成功，重置计数器
            redisUtils.delete(key);
            return Result.success("登录成功", loginVO);
        } catch (BusinessException e) {
            // 登录失败，增加计数器
            if (count == null) {
                redisUtils.set(key, 1, 1, TimeUnit.HOURS);
            } else {
                redisUtils.set(key, count + 1, 1, TimeUnit.HOURS);
            }
            throw e;
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
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

    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha() {
        // TODO: 接入真实验证码服务
        // 这里暂时返回一个简单的验证码key和示例图片
        String captchaKey = UUID.randomUUID().toString().replace("-", "");

        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        // 实际项目中应该返回真实的验证码图片base64
        result.put("captchaImage", "");
        result.put("message", "请在后端配置真实的验证码服务");
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

    @Operation(summary = "生成密码哈希（临时测试用）")
    @GetMapping("/gen-password")
    public Result<String> generatePassword(@RequestParam String password) {
        String hash = userService.generatePasswordHash(password);
        return Result.success(hash);
    }
}


