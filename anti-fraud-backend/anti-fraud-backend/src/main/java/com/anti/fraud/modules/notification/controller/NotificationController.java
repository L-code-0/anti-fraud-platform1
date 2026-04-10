package com.anti.fraud.modules.notification.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.notification.dto.SendNotificationDTO;
import com.anti.fraud.modules.notification.entity.NotificationMessage;
import com.anti.fraud.modules.notification.service.NotificationService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通知控制器
 */
@Tag(name = "通知管理", description = "站内信通知相关接口")
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @Operation(summary = "发送通知")
    @PostMapping("/send")
    public Result<Map<String, Object>> sendNotification(
            @Validated @RequestBody SendNotificationDTO dto
    ) {
        int count = notificationService.sendNotification(dto);
        return Result.success(Map.of("count", count, "message", "发送成功"));
    }
    
    @Operation(summary = "获取我的通知列表")
    @GetMapping("/list")
    public Result<IPage<NotificationMessage>> getMyNotifications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer isRead
    ) {
        IPage<NotificationMessage> result = notificationService.getUserNotifications(page, size, type, isRead);
        return Result.success(result);
    }
    
    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread/count")
    public Result<Map<String, Object>> getUnreadCount() {
        Integer count = notificationService.getUnreadCount();
        return Result.success(Map.of("count", count));
    }
    
    @Operation(summary = "标记通知为已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(
            @Parameter(description = "通知ID") @PathVariable Long id
    ) {
        notificationService.markAsRead(id);
        return Result.success();
    }
    
    @Operation(summary = "标记所有通知为已读")
    @PutMapping("/read/all")
    public Result<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return Result.success();
    }
    
    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(
            @Parameter(description = "通知ID") @PathVariable Long id
    ) {
        notificationService.deleteNotification(id);
        return Result.success();
    }
    
    @Operation(summary = "清空已读通知")
    @DeleteMapping("/clear/read")
    public Result<Void> clearReadNotifications() {
        notificationService.clearReadNotifications();
        return Result.success();
    }
}
