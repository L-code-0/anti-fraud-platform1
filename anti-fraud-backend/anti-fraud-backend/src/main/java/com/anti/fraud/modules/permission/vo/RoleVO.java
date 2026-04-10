package com.anti.fraud.modules.permission.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色VO
 * 
 * @author Anti-Fraud Platform Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 角色ID */
    private Long id;

    /** 角色名称 */
    private String name;

    /** 角色编码 */
    private String code;

    /** 角色级别 */
    private Integer level;

    /** 角色描述 */
    private String description;

    /** 状态 */
    private Integer status;

    /** 数据权限范围 */
    private Integer dataScope;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 权限ID列表 */
    private List<Long> permissionIds;

    /** 权限编码列表 */
    private List<String> permissionCodes;

    // ==================== 数据权限范围名称映射 ====================
    public String getDataScopeName() {
        if (dataScope == null) return "";
        switch (dataScope) {
            case 1: return "全部数据";
            case 2: return "本部门及以下";
            case 3: return "本部门";
            case 4: return "仅本人";
            case 5: return "自定义";
            default: return "未知";
        }
    }
}
