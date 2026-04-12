package com.anti.fraud.common.aspect;

import com.anti.fraud.common.annotation.RateLimit;
import com.anti.fraud.common.result.Result;
import com.anti.fraud.common.utils.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * API限流切面
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {
    
    private final RedisTemplate<String, Integer> redisTemplate;
    private final ObjectMapper objectMapper;
    
    // 定义切点
    @Pointcut("@annotation(com.anti.fraud.common.annotation.RateLimit)")
    public void rateLimitPointcut() {}
    
    @Around("rateLimitPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求信息
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        
        // 获取方法上的RateLimit注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        
        if (rateLimit == null) {
            // 没有注解，直接执行
            return joinPoint.proceed();
        }
        
        // 生成限流key
        String key = generateKey(request, rateLimit);
        int limit = rateLimit.limit();
        int window = rateLimit.window();
        
        // 检查是否超过限流
        if (isOverLimit(key, limit, window)) {
            // 返回限流响应
            Result<?> result = Result.fail("请求过于频繁，请稍后再试");
            return new ResponseEntity<>(result, HttpStatus.TOO_MANY_REQUESTS);
        }
        
        // 执行方法
        return joinPoint.proceed();
    }
    
    /**
     * 生成限流key
     */
    private String generateKey(HttpServletRequest request, RateLimit rateLimit) {
        StringBuilder key = new StringBuilder("rate_limit:");
        
        switch (rateLimit.type()) {
            case IP:
                key.append("ip:").append(SecurityUtils.getClientIp(request));
                break;
            case USER:
                try {
                    Long userId = SecurityUtils.getCurrentUserId();
                    key.append("user:").append(userId);
                } catch (Exception e) {
                    // 未登录用户使用IP
                    key.append("ip:").append(SecurityUtils.getClientIp(request));
                }
                break;
            case GLOBAL:
                key.append("global");
                break;
        }
        
        // 添加方法路径
        key.append(":").append(request.getRequestURI());
        
        return key.toString();
    }
    
    /**
     * 检查是否超过限流
     */
    private boolean isOverLimit(String key, int limit, int window) {
        try {
            // 使用Redis的incr命令实现限流
            Long count = redisTemplate.opsForValue().increment(key);
            
            if (count == 1) {
                // 第一次请求，设置过期时间
                redisTemplate.expire(key, Duration.ofSeconds(window));
            }
            
            return count > limit;
        } catch (Exception e) {
            log.error("Rate limit check failed: {}", e.getMessage(), e);
            // 异常时不进行限流，避免影响正常请求
            return false;
        }
    }
}
