package com.anti.fraud.modules.permission.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 权限创建/更新DTO
 * 
 * @author Anti-Fraud Platform Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {

    /** 权限ID（更新时必填） */
    private Long id;

    /** 权限名称 */
    @NotBlank(message = "权限名称不能为空")
    private String name;

    /** 权限编码 */
    @NotBlank(message = "权限编码不能为空")
    private String code;

    /** 类型：1-菜单 2-按钮 3-接口 */
    @NotNull(message = "权限类型不能为空")
    private Integer type;

    /** 父权限ID */
    private Long parentId = 0L;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 图标 */
    private String icon;

    /** 排序号 */
    private Integer sort = 0;

    /** 状态：0-禁用 1-启用 */
    private Integer status = 1;

    /** 是否显示：0-隐藏 1-显示 */
    private Integer visible = 1;

    /** 是否缓存：0-不缓存 1-缓存 */
    private Integer keepAlive = 1;

    /** 权限描述 */
    private String description;

    /** 备注 */
    private String remark;
}
