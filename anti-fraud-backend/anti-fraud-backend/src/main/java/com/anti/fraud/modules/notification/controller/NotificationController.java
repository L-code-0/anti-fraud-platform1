package com.anti.fraud.modules.notification.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.notification.entity.Notification;
import com.anti.fraud.modules.notification.service.NotificationService;
import com.anti.fraud.common.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通知控制器
 */
@Tag(name = "通知管理", description = "通知相关操作")
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @Operation(summary = "获取用户通知列表")
    @GetMapping("/list")
    public Result<IPage<Notification>> getNotificationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<Notification> result = notificationService.getUserNotifications(page, size, userId, type);
        return Result.success(result);
    }
    
    @Operation(summary = "获取通知统计")
    @GetMapping("/stats")
    public Result<Map<String, Integer>> getNotificationStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        Map<String, Integer> stats = notificationService.getNotificationStats(userId);
        return Result.success(stats);
    }
    
    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        int count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }
    
    @Operation(summary = "标记通知为已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(
            @Parameter(description = "通知ID") @PathVariable Long id
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean success = notificationService.markAsRead(userId, id);
        if (success) {
            return Result.success();
        }
        return Result.fail("标记失败");
    }
    
    @Operation(summary = "批量标记通知为已读")
    @PutMapping("/batch/read")
    public Result<Integer> markBatchAsRead(
            @RequestBody Long[] ids
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        int count = notificationService.markBatchAsRead(userId, ids);
        return Result.success(count);
    }
    
    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(
            @Parameter(description = "通知ID") @PathVariable Long id
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean success = notificationService.deleteNotification(userId, id);
        if (success) {
            return Result.success();
        }
        return Result.fail("删除失败");
    }
    
    @Operation(summary = "批量删除通知")
    @DeleteMapping("/batch")
    public Result<Integer> deleteBatchNotifications(
            @RequestBody Long[] ids
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        int count = notificationService.deleteBatchNotifications(userId, ids);
        return Result.success(count);
    }
    
    @Operation(summary = "发送测试通知")
    @PostMapping("/test")
    public Result<Notification> sendTestNotification() {
        Long userId = SecurityUtils.getCurrentUserId();
        Notification notification = notificationService.sendNotification(
                userId,
                "system",
                "测试通知",
                "这是一条测试通知，用于验证通知系统是否正常工作",
                "Bell",
                "/",
                "查看详情",
                "{\"test\": \"data\"}"
        );
        return Result.success(notification);
    }
}
