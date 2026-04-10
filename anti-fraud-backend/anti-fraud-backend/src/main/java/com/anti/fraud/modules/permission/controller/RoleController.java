package com.anti.fraud.modules.permission.controller;

import com.anti.fraud.common.annotation.RequirePermission;
import com.anti.fraud.common.enums.UserRole;
import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.permission.entity.Role;
import com.anti.fraud.modules.permission.mapper.RoleMapper;
import com.anti.fraud.modules.permission.service.PermissionService;
import com.anti.fraud.modules.permission.vo.PermissionVO;
import com.anti.fraud.modules.permission.vo.RoleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色管理Controller
 * 
 * @author Anti-Fraud Platform Team
 */
@Tag(name = "角色管理", description = "角色相关接口")
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleMapper roleMapper;
    private final PermissionService permissionService;

    @Operation(summary = "分页查询角色列表")
    @GetMapping("/list")
    @RequirePermission("api:role:list")
    public Result<Page<Role>> queryPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Role::getLevel);
        Page<Role> result = roleMapper.selectPage(page, wrapper);
        return Result.success(result);
    }

    @Operation(summary = "获取所有角色列表")
    @GetMapping("/all")
    public Result<List<Role>> getAllRoles() {
        List<Role> roles = roleMapper.selectAllEnabled();
        return Result.success(roles);
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    @RequirePermission("api:role:detail")
    public Result<RoleVO> getById(@PathVariable Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            return Result.fail("角色不存在");
        }
        
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setName(role.getName());
        vo.setCode(role.getCode());
        vo.setLevel(role.getLevel());
        vo.setDescription(role.getDescription());
        vo.setStatus(role.getStatus());
        vo.setDataScope(role.getDataScope());
        vo.setCreateTime(role.getCreateTime());
        
        // 获取角色的权限列表
        List<Long> permissionIds = permissionService.getPermissionIdsByRoleId(id);
        vo.setPermissionIds(permissionIds);
        
        return Result.success(vo);
    }

    @Operation(summary = "获取当前用户的角色列表")
    @GetMapping("/user/current")
    public Result<List<Role>> getCurrentUserRoles() {
        // 从数据库查询用户的角色
        // 这里简化处理，实际应从SecurityContext获取
        return Result.success(null);
    }

    @Operation(summary = "创建角色")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> create(@RequestBody Role role) {
        // 检查角色编码是否已存在
        Role existingRole = roleMapper.selectByCode(role.getCode());
        if (existingRole != null) {
            return Result.fail("角色编码已存在");
        }
        
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.insert(role);
        return Result.success();
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> update(@PathVariable Long id, @RequestBody Role role) {
        Role existingRole = roleMapper.selectById(id);
        if (existingRole == null) {
            return Result.fail("角色不存在");
        }
        
        // 禁止修改系统内置角色的编码
        if (existingRole.getCode().equals(UserRole.CODE_SUPER_ADMIN) && 
            !role.getCode().equals(UserRole.CODE_SUPER_ADMIN)) {
            return Result.fail("禁止修改超级管理员角色编码");
        }
        
        role.setId(id);
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(role);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(@PathVariable Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            return Result.fail("角色不存在");
        }
        
        // 禁止删除系统内置角色
        if (role.getCode().equals(UserRole.CODE_SUPER_ADMIN)) {
            return Result.fail("禁止删除超级管理员角色");
        }
        
        // 删除角色
        roleMapper.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "分配权限给角色")
    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            return Result.fail("角色不存在");
        }
        
        boolean success = permissionService.assignPermissions(id, permissionIds);
        return success ? Result.success() : Result.fail("分配失败");
    }

    @Operation(summary = "获取角色的菜单权限树")
    @GetMapping("/{id}/menus")
    @RequirePermission("api:role:menu:tree")
    public Result<List<PermissionVO>> getRoleMenus(@PathVariable Long id) {
        List<Long> permissionIds = permissionService.getPermissionIdsByRoleId(id);
        
        // 获取所有菜单并标记已选中的
        List<PermissionVO> allMenus = permissionService.getAllMenuTree();
        markSelectedMenus(allMenus, permissionIds);
        
        return Result.success(allMenus);
    }

    private void markSelectedMenus(List<PermissionVO> menus, List<Long> selectedIds) {
        for (PermissionVO menu : menus) {
            if (selectedIds.contains(menu.getId())) {
                // 标记为选中
                menu.setPermissionIds(selectedIds);
            }
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                markSelectedMenus(menu.getChildren(), selectedIds);
            }
        }
    }

    // 内部类用于VO
    static class PermissionVO extends com.anti.fraud.modules.permission.vo.PermissionVO {
        private List<Long> permissionIds;
        
        public List<Long> getPermissionIds() {
            return permissionIds;
        }
        
        public void setPermissionIds(List<Long> permissionIds) {
            this.permissionIds = permissionIds;
        }
    }
}
