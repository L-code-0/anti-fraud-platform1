package com.anti.fraud.modules.permission.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色分配权限DTO
 * 
 * @author Anti-Fraud Platform Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignPermissionDTO {

    /** 角色ID */
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /** 权限ID列表 */
    private List<Long> permissionIds;
}
