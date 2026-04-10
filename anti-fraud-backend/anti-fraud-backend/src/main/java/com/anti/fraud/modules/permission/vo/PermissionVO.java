package com.anti.fraud.modules.permission.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限VO
 * 
 * @author Anti-Fraud Platform Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 权限ID */
    private Long id;

    /** 权限名称 */
    private String name;

    /** 权限编码 */
    private String code;

    /** 类型：1-菜单 2-按钮 3-接口 */
    private Integer type;

    /** 类型名称 */
    private String typeName;

    /** 父权限ID */
    private Long parentId;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 图标 */
    private String icon;

    /** 排序号 */
    private Integer sort;

    /** 状态 */
    private Integer status;

    /** 是否显示 */
    private Integer visible;

    /** 是否缓存 */
    private Integer keepAlive;

    /** 权限描述 */
    private String description;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 子权限列表 */
    private List<PermissionVO> children;

    // ==================== 类型名称映射 ====================
    public String getTypeName() {
        if (type == null) return "";
        switch (type) {
            case 1: return "菜单";
            case 2: return "按钮";
            case 3: return "接口";
            default: return "未知";
        }
    }
}
