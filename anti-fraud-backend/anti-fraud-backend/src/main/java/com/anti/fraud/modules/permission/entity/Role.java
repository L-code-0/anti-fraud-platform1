package com.anti.fraud.modules.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色实体类
 * 对应数据库表 sys_role
 * 
 * @author Anti-Fraud Platform Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 角色ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色名称 */
    private String name;

    /** 角色编码（唯一标识） */
    private String code;

    /** 角色级别（数字越大权限越高） */
    private Integer level;

    /** 角色描述 */
    private String description;

    /** 状态：0-禁用 1-启用 */
    private Integer status;

    /** 数据权限范围：1-全部 2-本部门及以下 3-本部门 4-仅本人 5-自定义 */
    private Integer dataScope;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    /** 备注 */
    private String remark;

    // ==================== 角色编码常量 ====================
    public static final String CODE_STUDENT = "STUDENT";
    public static final String CODE_TEACHER = "TEACHER";
    public static final String CODE_ADMIN = "ADMIN";
    public static final String CODE_EXPERT = "EXPERT";
    public static final String CODE_SUPER_ADMIN = "SUPER_ADMIN";

    // ==================== 角色级别常量 ====================
    /** 学生级别 */
    public static final int LEVEL_STUDENT = 1;
    /** 教师级别 */
    public static final int LEVEL_TEACHER = 2;
    /** 管理员级别 */
    public static final int LEVEL_ADMIN = 3;
    /** 专家级别 */
    public static final int LEVEL_EXPERT = 4;
    /** 超级管理员级别 */
    public static final int LEVEL_SUPER_ADMIN = 5;

    // ==================== 数据权限范围常量 ====================
    /** 全部数据权限 */
    public static final int DATA_SCOPE_ALL = 1;
    /** 本部门及以下数据权限 */
    public static final int DATA_SCOPE_DEPT_AND_CHILD = 2;
    /** 本部门数据权限 */
    public static final int DATA_SCOPE_DEPT = 3;
    /** 仅本人数据权限 */
    public static final int DATA_SCOPE_SELF = 4;
    /** 自定义数据权限 */
    public static final int DATA_SCOPE_CUSTOM = 5;
}
