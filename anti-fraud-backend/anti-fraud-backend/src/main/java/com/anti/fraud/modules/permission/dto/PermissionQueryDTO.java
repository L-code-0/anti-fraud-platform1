package com.anti.fraud.modules.permission.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 权限查询DTO
 * 
 * @author Anti-Fraud Platform Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionQueryDTO {

    /** 权限名称（模糊查询） */
    private String name;

    /** 权限编码 */
    private String code;

    /** 类型：1-菜单 2-按钮 3-接口 */
    private Integer type;

    /** 状态：0-禁用 1-启用 */
    private Integer status;

    /** 父权限ID */
    private Long parentId;

    /** 当前页码 */
    private Integer pageNum = 1;

    /** 每页大小 */
    private Integer pageSize = 10;
}
