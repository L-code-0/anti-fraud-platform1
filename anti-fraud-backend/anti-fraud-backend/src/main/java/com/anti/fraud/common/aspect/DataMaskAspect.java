package com.anti.fraud.common.aspect;

import com.anti.fraud.common.annotation.MaskLog;
import com.anti.fraud.common.utils.DataMaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 日志脱敏切面
 * <p>
 * 在记录日志之前，自动对标记了@MaskLog注解的参数进行脱敏处理。
 * 配合@DataMaskUtils工具类使用，确保敏感信息不会在日志中泄露。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 * @see MaskLog
 * @see DataMaskUtils
 */
@Aspect
@Component
@Slf4j
public class DataMaskAspect {

    /**
     * 前置通知：在方法执行前处理脱敏
     * <p>
     * 遍历方法参数，检测带有@MaskLog注解的参数，
     * 将参数值替换为脱敏后的值后再进行日志记录。
     * </p>
     *
     * @param joinPoint 连接点
     */
    @Before("execution(* com.anti.fraud..*.*(..))")
    public void beforeMethod(JoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Parameter[] parameters = method.getParameters();
            Object[] args = joinPoint.getArgs();

            // 遍历参数，检查是否有@MaskLog注解
            for (int i = 0; i < parameters.length; i++) {
                MaskLog maskLog = parameters[i].getAnnotation(MaskLog.class);
                if (maskLog != null && args[i] != null) {
                    // 获取脱敏类型
                    String maskType = maskLog.value();
                    
                    // 对参数进行脱敏
                    String maskedValue = DataMaskUtils.mask(args[i].toString(), maskType);
                    
                    // 记录脱敏后的日志
                    log.debug("方法参数脱敏: method={}, param={}, type={}, original={}, masked={}",
                            method.getName(), parameters[i].getName(), maskType, 
                            maskType.equals("password") ? "***" : args[i], maskedValue);
                }
            }
        } catch (Exception e) {
            // 静默处理，避免影响业务
            log.trace("日志脱敏处理异常: {}", e.getMessage());
        }
    }
}
