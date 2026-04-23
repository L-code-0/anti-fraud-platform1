package com.anti.fraud.modules.notification.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.notification.entity.Notification;
import com.anti.fraud.modules.notification.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "通知管理")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "发送通知")
    @PostMapping("/send")
    public Result<Void> sendNotification(@ApiParam(value = "通知信息", required = true) @RequestBody Notification notification) {
        try {
            boolean success = notificationService.sendNotification(notification);
            if (success) {
                return Result.successMsg("发送通知成功");
            } else {
                return Result.fail("发送通知失败");
            }
        } catch (Exception e) {
            log.error("发送通知失败: {}", e.getMessage(), e);
            return Result.fail("发送通知失败");
        }
    }

    @Operation(summary = "批量发送通知")
    @PostMapping("/batch-send")
    public Result<List<Notification>> batchSendNotifications(@ApiParam(value = "通知列表", required = true) @RequestBody List<Notification> notifications) {
        try {
            List<Notification> sentNotifications = notificationService.batchSendNotifications(notifications);
            return Result.success(sentNotifications);
        } catch (Exception e) {
            log.error("批量发送通知失败: {}", e.getMessage(), e);
            return Result.fail("批量发送通知失败");
        }
    }

    @Operation(summary = "发送短信通知")
    @PostMapping("/send-sms")
    public Result<Void> sendSmsNotification(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "通知内容", required = true) @RequestParam String content) {
        try {
            boolean success = notificationService.sendSmsNotification(userId, username, content);
            if (success) {
                return Result.successMsg("发送短信通知成功");
            } else {
                return Result.fail("发送短信通知失败");
            }
        } catch (Exception e) {
            log.error("发送短信通知失败: {}", e.getMessage(), e);
            return Result.fail("发送短信通知失败");
        }
    }

    @Operation(summary = "发送邮件通知")
    @PostMapping("/send-email")
    public Result<Void> sendEmailNotification(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "邮箱", required = true) @RequestParam String email,
            @ApiParam(value = "邮件主题", required = true) @RequestParam String subject,
            @ApiParam(value = "邮件内容", required = true) @RequestParam String content) {
        try {
            boolean success = notificationService.sendEmailNotification(userId, username, email, subject, content);
            if (success) {
                return Result.successMsg("发送邮件通知成功");
            } else {
                return Result.fail("发送邮件通知失败");
            }
        } catch (Exception e) {
            log.error("发送邮件通知失败: {}", e.getMessage(), e);
            return Result.fail("发送邮件通知失败");
        }
    }

    @Operation(summary = "更新通知状态")
    @PutMapping("/update-status/{id}")
    public Result<Void> updateNotificationStatus(
            @ApiParam(value = "通知ID", required = true) @PathVariable Long id,
            @ApiParam(value = "状态: 1-待发送, 2-已发送, 3-发送失败", required = true) @RequestParam Integer status) {
        try {
            boolean success = notificationService.updateNotificationStatus(id, status);
            if (success) {
                return Result.successMsg("更新通知状态成功");
            } else {
                return Result.fail("更新通知状态失败");
            }
        } catch (Exception e) {
            log.error("更新通知状态失败: {}", e.getMessage(), e);
            return Result.fail("更新通知状态失败");
        }
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/mark-as-read/{id}")
    public Result<Void> markAsRead(@ApiParam(value = "通知ID", required = true) @PathVariable Long id) {
        try {
            boolean success = notificationService.markAsRead(id);
            if (success) {
                return Result.successMsg("标记通知为已读成功");
            } else {
                return Result.fail("标记通知为已读失败");
            }
        } catch (Exception e) {
            log.error("标记通知为已读失败: {}", e.getMessage(), e);
            return Result.fail("标记通知为已读失败");
        }
    }

    @Operation(summary = "批量标记通知为已读")
    @PutMapping("/batch-mark-as-read")
    public Result<Void> batchMarkAsRead(@ApiParam(value = "通知ID列表", required = true) @RequestBody List<Long> ids) {
        try {
            boolean success = notificationService.batchMarkAsRead(ids);
            if (success) {
                return Result.successMsg("批量标记通知为已读成功");
            } else {
                return Result.fail("批量标记通知为已读失败");
            }
        } catch (Exception e) {
            log.error("批量标记通知为已读失败: {}", e.getMessage(), e);
            return Result.fail("批量标记通知为已读失败");
        }
    }

    @Operation(summary = "获取通知详情")
    @GetMapping("/detail/{id}")
    public Result<Notification> getNotificationById(@ApiParam(value = "通知ID", required = true) @PathVariable Long id) {
        try {
            Notification notification = notificationService.getNotificationById(id);
            if (notification != null) {
                return Result.success(notification);
            } else {
                return Result.fail("通知不存在");
            }
        } catch (Exception e) {
            log.error("获取通知详情失败: {}", e.getMessage(), e);
            return Result.fail("获取通知详情失败");
        }
    }

    @Operation(summary = "根据用户ID查询通知")
    @GetMapping("/by-user/{userId}")
    public Result<Map<String, Object>> getNotificationsByUserId(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = notificationService.getNotificationsByUserId(userId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据用户ID查询通知失败: {}", e.getMessage(), e);
            return Result.fail("根据用户ID查询通知失败");
        }
    }

    @Operation(summary = "根据用户ID查询未读通知数量")
    @GetMapping("/unread-count/{userId}")
    public Result<Integer> getUnreadCountByUserId(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        try {
            Integer count = notificationService.getUnreadCountByUserId(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("根据用户ID查询未读通知数量失败: {}", e.getMessage(), e);
            return Result.fail("根据用户ID查询未读通知数量失败");
        }
    }

    @Operation(summary = "根据通知类型查询通知")
    @GetMapping("/by-type")
    public Result<Map<String, Object>> getNotificationsByType(
            @ApiParam(value = "通知类型: 1-系统通知, 2-学习通知, 3-演练通知, 4-举报通知, 5-风险通知, 6-其他", required = true) @RequestParam Integer type,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = notificationService.getNotificationsByType(type, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据通知类型查询通知失败: {}", e.getMessage(), e);
            return Result.fail("根据通知类型查询通知失败");
        }
    }

    @Operation(summary = "根据通知方式查询通知")
    @GetMapping("/by-method")
    public Result<Map<String, Object>> getNotificationsByMethod(
            @ApiParam(value = "通知方式: 1-站内信, 2-短信, 3-邮件, 4-微信", required = true) @RequestParam Integer method,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = notificationService.getNotificationsByMethod(method, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据通知方式查询通知失败: {}", e.getMessage(), e);
            return Result.fail("根据通知方式查询通知失败");
        }
    }

    @Operation(summary = "根据发送状态查询通知")
    @GetMapping("/by-status")
    public Result<Map<String, Object>> getNotificationsByStatus(
            @ApiParam(value = "发送状态: 1-待发送, 2-已发送, 3-发送失败", required = true) @RequestParam Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = notificationService.getNotificationsByStatus(status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据发送状态查询通知失败: {}", e.getMessage(), e);
            return Result.fail("根据发送状态查询通知失败");
        }
    }

    @Operation(summary = "统计通知信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getNotificationStats() {
        try {
            Map<String, Object> stats = notificationService.getNotificationStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计通知信息失败: {}", e.getMessage(), e);
            return Result.fail("统计通知信息失败");
        }
    }

    @Operation(summary = "清理过期通知")
    @DeleteMapping("/clean-expired")
    public Result<Integer> cleanExpiredNotifications(@ApiParam(value = "天数", required = true) @RequestParam Integer days) {
        try {
            Integer count = notificationService.cleanExpiredNotifications(days);
            return Result.success(count);
        } catch (Exception e) {
            log.error("清理过期通知失败: {}", e.getMessage(), e);
            return Result.fail("清理过期通知失败");
        }
    }
}
