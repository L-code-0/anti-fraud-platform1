package com.anti.fraud.modules.user.controller;

import com.anti.fraud.common.enums.UserRole;
import com.anti.fraud.common.result.Result;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.user.service.UserService;
import com.anti.fraud.modules.user.vo.UserVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取用户信息", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(userService.getUserInfo(userId));
    }

    @Operation(summary = "更新用户信息", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody UserVO userVO) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.updateUserInfo(userId, userVO);
        return Result.successMsg("更新成功");
    }

    @Operation(summary = "修改密码", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/password")
    public Result<Void> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.successMsg("密码修改成功");
    }

    @Operation(summary = "获取用户列表", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/list")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<Page<UserVO>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer roleId) {
        return Result.success(userService.getUserPage(page, size, keyword, roleId));
    }

    @Operation(summary = "禁用/启用用户", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status) {
        userService.updateUserStatus(userId, status);
        return Result.successMsg("操作成功");
    }
}