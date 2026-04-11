package com.anti.fraud.modules.permission.controller;

import com.anti.fraud.common.annotation.RequirePermission;
import com.anti.fraud.common.result.Result;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.permission.dto.PermissionDTO;
import com.anti.fraud.modules.permission.dto.PermissionQueryDTO;
import com.anti.fraud.modules.permission.service.PermissionService;
import com.anti.fraud.modules.permission.vo.PermissionVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 权限管理Controller
 * 
 * @author Anti-Fraud Platform Team
 */
@Tag(name = "权限管理", description = "权限相关接口")
@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "分页查询权限列表")
    @GetMapping("/list")
    @RequirePermission("api:permission:list")
    public Result<Page<PermissionVO>> queryPage(PermissionQueryDTO queryDTO) {
        Page<PermissionVO> page = permissionService.queryPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取权限详情")
    @GetMapping("/{id}")
    @RequirePermission("api:permission:detail")
    public Result<PermissionVO> getById(@PathVariable Long id) {
        PermissionVO permission = permissionService.getById(id);
        return Result.success(permission);
    }

    @Operation(summary = "创建权限")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> create(@Valid @RequestBody PermissionDTO dto) {
        boolean success = permissionService.create(dto);
        return success ? Result.success() : Result.fail("创建失败");
    }

    @Operation(summary = "更新权限")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PermissionDTO dto) {
        dto.setId(id);
        boolean success = permissionService.update(dto);
        return success ? Result.success() : Result.fail("更新失败");
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = permissionService.delete(id);
        return success ? Result.success() : Result.fail("删除失败");
    }

    @Operation(summary = "获取当前用户的菜单权限树")
    @GetMapping("/user/menus")
    public Result<List<PermissionVO>> getUserMenus() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        List<PermissionVO> menus = permissionService.getMenuTreeByUserId(userId);
        return Result.success(menus);
    }

    @Operation(summary = "获取所有菜单权限树")
    @GetMapping("/menus/tree")
    @RequirePermission("api:permission:menu:tree")
    public Result<List<PermissionVO>> getAllMenuTree() {
        List<PermissionVO> menus = permissionService.getAllMenuTree();
        return Result.success(menus);
    }

    @Operation(summary = "获取角色的权限ID列表")
    @GetMapping("/role/{roleId}")
    @RequirePermission("api:permission:role:list")
    public Result<List<Long>> getPermissionsByRoleId(@PathVariable Long roleId) {
        List<Long> permissionIds = permissionService.getPermissionIdsByRoleId(roleId);
        return Result.success(permissionIds);
    }

    @Operation(summary = "分配权限给角色")
    @PostMapping("/role/assign")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> assignPermissions(@RequestParam Long roleId, @RequestBody List<Long> permissionIds) {
        boolean success = permissionService.assignPermissions(roleId, permissionIds);
        return success ? Result.success() : Result.fail("分配失败");
    }

    @Operation(summary = "检查当前用户是否拥有指定权限")
    @GetMapping("/check")
    public Result<Boolean> checkPermission(@Parameter(description = "权限编码") @RequestParam String code) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.success(false);
        }
        boolean hasPermission = permissionService.hasPermission(userId, code);
        return Result.success(hasPermission);
    }

    @Operation(summary = "检查当前用户是否拥有指定角色")
    @GetMapping("/check/role")
    public Result<Boolean> checkRole(@Parameter(description = "角色编码") @RequestParam String code) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.success(false);
        }
        boolean hasRole = permissionService.hasRole(userId, code);
        return Result.success(hasRole);
    }
}
