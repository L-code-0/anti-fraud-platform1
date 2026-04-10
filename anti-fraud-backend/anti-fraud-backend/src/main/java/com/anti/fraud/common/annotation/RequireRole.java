package com.anti.fraud.common.annotation;

import java.lang.annotation.*;

/**
 * 角色校验注解
 * 用于标注接口需要的角色
 * 
 * 使用示例：
 * <pre>
 * // 需要管理员角色
 * @RequireRole("ADMIN")
 * 
 * // 需要多个角色之一
 * @RequireRole(value = {"ADMIN", "EXPERT"}, mode = RequireRole.Mode.ANY)
 * </pre>
 * 
 * @author Anti-Fraud Platform Team
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /**
     * 角色编码
     */
    String[] value() default {};

    /**
     * 角色校验模式
     * - ALL: 需要同时满足所有角色（默认）
     * - ANY: 满足任一角色即可
     */
    Mode mode() default Mode.ANY;

    enum Mode {
        /** 需要同时满足所有角色 */
        ALL,
        /** 满足任一角色即可 */
        ANY
    }
}
