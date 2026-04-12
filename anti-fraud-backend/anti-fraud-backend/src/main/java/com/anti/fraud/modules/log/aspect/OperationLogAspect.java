package com.anti.fraud.modules.log.aspect;

import com.anti.fraud.modules.log.service.OperationLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

/**
 * 操作日志切面
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OperationLogAspect {
    
    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;
    
    // 定义切点：拦截所有Controller方法
    @Pointcut("execution(* com.anti.fraud.modules..*Controller.*(..))")
    public void controllerPointcut() {}
    
    // 环绕通知，记录方法执行时间和结果
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求信息
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        
        // 获取方法信息
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String module = className.replace("Controller", "");
        
        // 构建操作描述
        String description = module + "模块 - " + methodName + "方法";
        
        // 记录请求参数
        String params = "";
        try {
            Object[] args = joinPoint.getArgs();
            // 过滤掉ServletRequest、ServletResponse等对象
            Object[] filteredArgs = Arrays.stream(args)
                    .filter(arg -> !(arg instanceof HttpServletRequest))
                    .toArray();
            params = objectMapper.writeValueAsString(filteredArgs);
        } catch (Exception e) {
            log.error("序列化请求参数失败", e);
        }
        
        // 执行方法
        Object result = null;
        long startTime = System.currentTimeMillis();
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long endTime = System.currentTimeMillis();
            String resultStr = "";
            try {
                if (result != null) {
                    resultStr = objectMapper.writeValueAsString(result);
                }
            } catch (Exception e) {
                log.error("序列化返回结果失败", e);
            }
            
            // 记录操作日志
            operationLogService.recordLog(
                    request,
                    "execute",
                    module,
                    description + " (执行时间: " + (endTime - startTime) + "ms)",
                    params,
                    resultStr,
                    true
            );
        }
    }
    
    // 异常通知，记录方法执行异常
    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Throwable e) {
        // 获取请求信息
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        
        // 获取方法信息
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String module = className.replace("Controller", "");
        
        // 构建操作描述
        String description = module + "模块 - " + methodName + "方法";
        
        // 记录请求参数
        String params = "";
        try {
            Object[] args = joinPoint.getArgs();
            // 过滤掉ServletRequest、ServletResponse等对象
            Object[] filteredArgs = Arrays.stream(args)
                    .filter(arg -> !(arg instanceof HttpServletRequest))
                    .toArray();
            params = objectMapper.writeValueAsString(filteredArgs);
        } catch (Exception ex) {
            log.error("序列化请求参数失败", ex);
        }
        
        // 记录异常信息
        String errorMessage = e.getMessage();
        if (errorMessage == null) {
            errorMessage = e.toString();
        }
        
        // 记录操作日志
        operationLogService.recordLog(
                request,
                "error",
                module,
                description + " (执行异常)",
                params,
                errorMessage,
                false
        );
    }
}
