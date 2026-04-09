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
}
