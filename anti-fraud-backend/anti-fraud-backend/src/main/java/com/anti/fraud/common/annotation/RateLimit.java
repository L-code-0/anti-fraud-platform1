package com.anti.fraud.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API限流注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    
    /**
     * 限流时间窗口（秒）
     */
    int window() default 60;
    
    /**
     * 时间窗口内允许的最大请求数
     */
    int limit() default 100;
    
    /**
     * 限流类型
     */
    Type type() default Type.IP;
    
    /**
     * 限流类型枚举
     */
    enum Type {
        IP,  // 基于IP限流
        USER,  // 基于用户限流
        GLOBAL  // 全局限流
    }
}
