package com.anti.fraud.modules.log.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    
    /**
     * 操作类型
     */
    String operationType() default "OTHER";
    
    /**
     * 模块名称
     */
    String moduleName() default "";
    
    /**
     * 操作描述
     */
    String description() default "";
    
    /**
     * 业务类型
     */
    String bizType() default "";
}
