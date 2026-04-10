package com.anti.fraud.common.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 * 用于标注接口需要的权限，简化权限校验代码
 * 
 * 使用示例：
 * <pre>
 * // 方式1：单个权限
 * @RequirePermission("api:user:list")
 * 
 * // 方式2：多个权限（需要同时满足）
 * @RequirePermission({"api:user:list", "api:user:view"})
 * 
 * // 方式3：多个权限（满足其一即可）
 * @RequirePermission(value = {"api:user:list", "api:user:view"}, mode = RequirePermission.Mode.ANY)
 * 
 * // 方式4：基于角色
 * @RequirePermission(value = "ROLE_ADMIN", isRole = true)
 * </pre>
 * 
 * @author Anti-Fraud Platform Team
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限编码或角色编码
     */
    String[] value() default {};

    /**
     * 权限校验模式
     * - ALL: 需要满足所有权限（默认）
     * - ANY: 满足任一权限即可
     */
    Mode mode() default Mode.ALL;

    /**
     * 是否为角色校验
     * true-校验角色，false-校验权限
     */
    boolean isRole() default false;

    /**
     * 需要的最低角色级别（数字越大权限越高）
     * - 1: 学生
     * - 2: 教师
     * - 3: 管理员
     * - 4: 专家
     * - 5: 超级管理员
     * 设置后会自动检查用户角色级别是否达到要求
     */
    int level() default 0;

    /**
     * 需要的最低角色编码
     * 如 "TEACHER" 表示需要教师或更高角色
     * 会自动转换为角色级别进行校验
     */
    String levelRole() default "";

    enum Mode {
        /** 需要满足所有权限 */
        ALL,
        /** 满足任一权限即可 */
        ANY
    }
}
