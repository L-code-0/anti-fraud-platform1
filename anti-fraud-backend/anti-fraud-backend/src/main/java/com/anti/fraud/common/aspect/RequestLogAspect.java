package com.anti.fraud.common.aspect;

import com.anti.fraud.common.utils.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局请求日志切面
 * 记录所有Controller方法的请求参数和响应结果
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequestLogAspect {

    private final ObjectMapper objectMapper;

    /**
     * 切入点：所有Controller层方法
     */
    @Pointcut("execution(* com.anti.fraud.modules..controller..*(..))")
    public void controllerPointcut() {}

    @Around("controllerPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = getClientIp(request);

        // 获取当前用户
        Long userId = null;
        String username = "anonymous";
        try {
            userId = SecurityUtils.getCurrentUserId();
            username = SecurityUtils.getCurrentUsername();
        } catch (Exception ignored) {
            // 未登录用户
        }

        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        String params = getParams(args);

        // 记录请求日志
        log.info(">>> 请求开始 [{} {}] IP: {} User: {}(id={}) Params: {}",
                method, uri, ip, username, userId, params);

        Object result;
        try {
            // 执行方法
            result = joinPoint.proceed();

            // 记录响应日志
            long costTime = System.currentTimeMillis() - startTime;
            log.info("<<< 请求结束 [{} {}] 耗时: {}ms 状态: 成功", method, uri, costTime);

            return result;
        } catch (Throwable e) {
            // 记录异常日志
            long costTime = System.currentTimeMillis() - startTime;
            log.error("<<< 请求异常 [{} {}] 耗时: {}ms 异常: {}", method, uri, costTime, e.getMessage());
            throw e;
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取请求参数字符串
     */
    private String getParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "{}";
        }

        try {
            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg == null) {
                    continue;
                }
                // 过滤掉HttpServletRequest、HttpServletResponse、MultipartFile等
                if (arg instanceof HttpServletRequest
                        || arg instanceof jakarta.servlet.http.HttpServletResponse
                        || arg instanceof MultipartFile) {
                    continue;
                }
                params.put("arg" + i, arg);
            }

            // 参数过长时截断
            String json = objectMapper.writeValueAsString(params);
            if (json.length() > 500) {
                json = json.substring(0, 500) + "...(truncated)";
            }
            return json;
        } catch (Exception e) {
            return "{\"error\": \"参数序列化失败\"}";
        }
    }
}
