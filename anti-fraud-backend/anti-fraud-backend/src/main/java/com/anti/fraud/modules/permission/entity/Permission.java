package com.anti.fraud.modules.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限实体类
 * 对应数据库表 sys_permission
 * 
 * @author Anti-Fraud Platform Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 权限ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 权限名称 */
    private String name;

    /** 权限编码（唯一标识） */
    private String code;

    /** 类型：1-菜单 2-按钮 3-接口 */
    private Integer type;

    /** 父权限ID，0表示顶级 */
    private Long parentId;

    /** 路由路径（菜单用） */
    private String path;

    /** 组件路径（前端用） */
    private String component;

    /** 图标 */
    private String icon;

    /** 排序号 */
    private Integer sort;

    /** 状态：0-禁用 1-启用 */
    private Integer status;

    /** 是否显示：0-隐藏 1-显示 */
    private Integer visible;

    /** 是否缓存：0-不缓存 1-缓存 */
    private Integer keepAlive;

    /** 权限描述 */
    private String description;

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

    // ==================== 类型常量 ====================
    /** 菜单类型 */
    public static final int TYPE_MENU = 1;
    /** 按钮类型 */
    public static final int TYPE_BUTTON = 2;
    /** 接口类型 */
    public static final int TYPE_API = 3;

    // ==================== 状态常量 ====================
    /** 禁用 */
    public static final int STATUS_DISABLED = 0;
    /** 启用 */
    public static final int STATUS_ENABLED = 1;

    // ==================== 显示常量 ====================
    /** 隐藏 */
    public static final int VISIBLE_HIDDEN = 0;
    /** 显示 */
    public static final int VISIBLE_SHOW = 1;
}
