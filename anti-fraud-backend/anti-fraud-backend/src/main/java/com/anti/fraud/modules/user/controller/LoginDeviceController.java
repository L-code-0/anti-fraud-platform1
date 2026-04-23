package com.anti.fraud.modules.user.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.user.entity.LoginDevice;
import com.anti.fraud.modules.user.service.LoginDeviceService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录设备管理控制器
 */
@RestController
@RequestMapping("/user/device")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "登录设备管理")
public class LoginDeviceController {

    private final LoginDeviceService loginDeviceService;

    @Operation(summary = "获取用户登录设备列表")
    @GetMapping("/list")
    public Result<List<LoginDevice>> getLoginDevices() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<LoginDevice> devices = loginDeviceService.getLoginDevices(userId);
            return Result.success("获取设备列表成功", devices);
        } catch (Exception e) {
            log.error("获取设备列表失败: {}", e.getMessage(), e);
            return Result.fail("获取设备列表失败");
        }
    }

    @Operation(summary = "标记设备为可信")
    @PostMapping("/{deviceId}/trust")
    public Result<Void> markDeviceAsTrusted(
            @ApiParam(value = "设备ID", required = true) @PathVariable String deviceId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = loginDeviceService.markDeviceAsTrusted(userId, deviceId);
            if (success) {
                return Result.successMsg("标记设备为可信成功");
            } else {
                return Result.fail("标记设备为可信失败");
            }
        } catch (Exception e) {
            log.error("标记设备为可信失败: {}", e.getMessage(), e);
            return Result.fail("标记设备为可信失败");
        }
    }

    @Operation(summary = "标记设备为不可信")
    @PostMapping("/{deviceId}/untrust")
    public Result<Void> markDeviceAsUntrusted(
            @ApiParam(value = "设备ID", required = true) @PathVariable String deviceId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = loginDeviceService.markDeviceAsUntrusted(userId, deviceId);
            if (success) {
                return Result.successMsg("标记设备为不可信成功");
            } else {
                return Result.fail("标记设备为不可信失败");
            }
        } catch (Exception e) {
            log.error("标记设备为不可信失败: {}", e.getMessage(), e);
            return Result.fail("标记设备为不可信失败");
        }
    }

    @Operation(summary = "禁用设备")
    @PostMapping("/{deviceId}/disable")
    public Result<Void> disableDevice(
            @ApiParam(value = "设备ID", required = true) @PathVariable String deviceId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = loginDeviceService.disableDevice(userId, deviceId);
            if (success) {
                return Result.successMsg("禁用设备成功");
            } else {
                return Result.fail("禁用设备失败");
            }
        } catch (Exception e) {
            log.error("禁用设备失败: {}", e.getMessage(), e);
            return Result.fail("禁用设备失败");
        }
    }

    @Operation(summary = "启用设备")
    @PostMapping("/{deviceId}/enable")
    public Result<Void> enableDevice(
            @ApiParam(value = "设备ID", required = true) @PathVariable String deviceId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = loginDeviceService.enableDevice(userId, deviceId);
            if (success) {
                return Result.successMsg("启用设备成功");
            } else {
                return Result.fail("启用设备失败");
            }
        } catch (Exception e) {
            log.error("启用设备失败: {}", e.getMessage(), e);
            return Result.fail("启用设备失败");
        }
    }

    @Operation(summary = "删除设备")
    @DeleteMapping("/{deviceId}")
    public Result<Void> deleteDevice(
            @ApiParam(value = "设备ID", required = true) @PathVariable String deviceId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = loginDeviceService.deleteDevice(userId, deviceId);
            if (success) {
                return Result.successMsg("删除设备成功");
            } else {
                return Result.fail("删除设备失败");
            }
        } catch (Exception e) {
            log.error("删除设备失败: {}", e.getMessage(), e);
            return Result.fail("删除设备失败");
        }
    }

    @Operation(summary = "获取设备详情")
    @GetMapping("/{deviceId}")
    public Result<LoginDevice> getDeviceById(
            @ApiParam(value = "设备ID", required = true) @PathVariable String deviceId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            LoginDevice device = loginDeviceService.getDeviceById(userId, deviceId);
            if (device != null) {
                return Result.success("获取设备详情成功", device);
            } else {
                return Result.fail("设备不存在");
            }
        } catch (Exception e) {
            log.error("获取设备详情失败: {}", e.getMessage(), e);
            return Result.fail("获取设备详情失败");
        }
    }
}
