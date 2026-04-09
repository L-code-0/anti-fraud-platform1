package com.anti.fraud.common.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    STUDENT(1, "学生", "ROLE_STUDENT"),
    TEACHER(2, "教师", "ROLE_TEACHER"),
    ADMIN(3, "管理员", "ROLE_ADMIN"),
    EXPERT(4, "反诈专家", "ROLE_EXPERT"),
    SUPER_ADMIN(5, "系统管理员", "ROLE_SUPER_ADMIN");

    private final Integer code;
    private final String name;
    private final String authority;

    UserRole(Integer code, String name, String authority) {
        this.code = code;
        this.name = name;
        this.authority = authority;
    }

    public static UserRole getByCode(Integer code) {
        for (UserRole role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    public static String getAuthorityByCode(Integer code) {
        UserRole role = getByCode(code);
        return role != null ? role.getAuthority() : null;
    }

    // ==================== Spring Security 权限表达式使用的角色名称 ====================
    // 用于 @PreAuthorize("hasRole('ROLE_ADMIN')") 这样的表达式
    // 注意：hasRole() 会自动添加 ROLE_ 前缀，所以这里使用不带 ROLE_ 前缀的名称

    /**
     * 管理员角色名称 - 用于 @PreAuthorize 注解
     * 用法: hasRole(UserRole.ROLE_ADMIN)
     */
    public static final String ROLE_ADMIN = "ADMIN";

    /**
     * 反诈专家角色名称 - 用于 @PreAuthorize 注解
     * 用法: hasRole(UserRole.ROLE_EXPERT)
     */
    public static final String ROLE_EXPERT = "EXPERT";

    /**
     * 系统管理员角色名称 - 用于 @PreAuthorize 注解
     * 用法: hasRole(UserRole.ROLE_SUPER_ADMIN)
     */
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";

    /**
     * 教师角色名称 - 用于 @PreAuthorize 注解
     * 用法: hasRole(UserRole.ROLE_TEACHER)
     */
    public static final String ROLE_TEACHER = "TEACHER";

    /**
     * 学生角色名称 - 用于 @PreAuthorize 注解
     * 用法: hasRole(UserRole.ROLE_STUDENT)
     */
    public static final String ROLE_STUDENT = "STUDENT";
}

