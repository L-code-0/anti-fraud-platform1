package com.anti.fraud.modules.websocket.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.websocket.entity.WebSocketMessageStore;
import com.anti.fraud.modules.websocket.service.WebSocketMessageStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * WebSocket消息存储服务控制器
 */
@RestController
@RequestMapping("/websocket/message")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "WebSocket消息存储服务")
public class WebSocketMessageStoreController {

    private final WebSocketMessageStoreService webSocketMessageStoreService;

    @Operation(summary = "存储消息")
    @PostMapping("/store")
    public Result<Void> storeMessage(@ApiParam(value = "消息", required = true) @RequestBody WebSocketMessageStore message) {
        try {
            boolean success = webSocketMessageStoreService.storeMessage(message);
            if (success) {
                return Result.successMsg("存储消息成功");
            } else {
                return Result.fail("存储消息失败");
            }
        } catch (Exception e) {
            log.error("存储消息失败: {}", e.getMessage(), e);
            return Result.fail("存储消息失败");
        }
    }

    @Operation(summary = "存储并发送消息")
    @PostMapping("/store-and-send")
    public Result<Void> storeAndSendMessage(@ApiParam(value = "消息", required = true) @RequestBody WebSocketMessageStore message) {
        try {
            boolean success = webSocketMessageStoreService.storeAndSendMessage(message);
            if (success) {
                return Result.successMsg("存储并发送消息成功");
            } else {
                return Result.fail("存储并发送消息失败");
            }
        } catch (Exception e) {
            log.error("存储并发送消息失败: {}", e.getMessage(), e);
            return Result.fail("存储并发送消息失败");
        }
    }

    @Operation(summary = "批量存储消息")
    @PostMapping("/batch-store")
    public Result<Integer> batchStoreMessages(@ApiParam(value = "消息列表", required = true) @RequestBody List<WebSocketMessageStore> messages) {
        try {
            int count = webSocketMessageStoreService.batchStoreMessages(messages);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量存储消息失败: {}", e.getMessage(), e);
            return Result.fail("批量存储消息失败");
        }
    }

    @Operation(summary = "获取消息")
    @GetMapping("/detail/{messageId}")
    public Result<WebSocketMessageStore> getMessage(@ApiParam(value = "消息ID", required = true) @PathVariable String messageId) {
        try {
            WebSocketMessageStore message = webSocketMessageStoreService.getMessage(messageId);
            if (message != null) {
                return Result.success(message);
            } else {
                return Result.fail("消息不存在");
            }
        } catch (Exception e) {
            log.error("获取消息失败: {}", e.getMessage(), e);
            return Result.fail("获取消息失败");
        }
    }

    @Operation(summary = "分页查询消息列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getMessageList(
            @ApiParam(value = "接收者ID", required = false) @RequestParam(required = false) Long receiverId,
            @ApiParam(value = "消息状态: 1-已发送，2-未发送，3-发送失败", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = webSocketMessageStoreService.getMessageList(receiverId, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询消息列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return Result.fail("查询消息列表失败");
        }
    }

    @Operation(summary = "根据时间范围查询消息列表")
    @GetMapping("/list-by-time-range")
    public Result<List<WebSocketMessageStore>> getMessageListByTimeRange(
            @ApiParam(value = "开始时间", required = true) @RequestParam LocalDateTime startTime,
            @ApiParam(value = "结束时间", required = true) @RequestParam LocalDateTime endTime,
            @ApiParam(value = "接收者ID", required = false) @RequestParam(required = false) Long receiverId,
            @ApiParam(value = "发送者ID", required = false) @RequestParam(required = false) Long senderId,
            @ApiParam(value = "消息状态: 1-已发送，2-未发送，3-发送失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            List<WebSocketMessageStore> messages = webSocketMessageStoreService.getMessageListByTimeRange(startTime, endTime, receiverId, senderId, status);
            return Result.success(messages);
        } catch (Exception e) {
            log.error("根据时间范围查询消息列表失败: {}", e.getMessage(), e);
            return Result.fail("根据时间范围查询消息列表失败");
        }
    }

    @Operation(summary = "更新消息状态")
    @PostMapping("/update-status/{messageId}")
    public Result<Void> updateMessageStatus(
            @ApiParam(value = "消息ID", required = true) @PathVariable String messageId,
            @ApiParam(value = "消息状态: 1-已发送，2-未发送，3-发送失败", required = true) @RequestParam Integer status) {
        try {
            boolean success = webSocketMessageStoreService.updateMessageStatus(messageId, status);
            if (success) {
                return Result.successMsg("更新消息状态成功");
            } else {
                return Result.fail("更新消息状态失败");
            }
        } catch (Exception e) {
            log.error("更新消息状态失败: {}", e.getMessage(), e);
            return Result.fail("更新消息状态失败");
        }
    }

    @Operation(summary = "发送离线消息")
    @PostMapping("/send-offline/{receiverId}")
    public Result<Integer> sendOfflineMessages(@ApiParam(value = "接收者ID", required = true) @PathVariable Long receiverId) {
        try {
            int count = webSocketMessageStoreService.sendOfflineMessages(receiverId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("发送离线消息失败: {}", e.getMessage(), e);
            return Result.fail("发送离线消息失败");
        }
    }

    @Operation(summary = "批量发送离线消息")
    @PostMapping("/batch-send-offline")
    public Result<Integer> batchSendOfflineMessages(@ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            int count = webSocketMessageStoreService.batchSendOfflineMessages(limit);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量发送离线消息失败: {}", e.getMessage(), e);
            return Result.fail("批量发送离线消息失败");
        }
    }

    @Operation(summary = "删除消息")
    @DeleteMapping("/delete/{messageId}")
    public Result<Void> deleteMessage(@ApiParam(value = "消息ID", required = true) @PathVariable String messageId) {
        try {
            boolean success = webSocketMessageStoreService.deleteMessage(messageId);
            if (success) {
                return Result.successMsg("删除消息成功");
            } else {
                return Result.fail("删除消息失败");
            }
        } catch (Exception e) {
            log.error("删除消息失败: {}", e.getMessage(), e);
            return Result.fail("删除消息失败");
        }
    }

    @Operation(summary = "清理过期消息")
    @PostMapping("/clean-expired")
    public Result<Integer> cleanExpiredMessages(@ApiParam(value = "天数", required = true) @RequestParam Integer days) {
        try {
            int count = webSocketMessageStoreService.cleanExpiredMessages(days);
            return Result.success(count);
        } catch (Exception e) {
            log.error("清理过期消息失败: {}", e.getMessage(), e);
            return Result.fail("清理过期消息失败");
        }
    }

    @Operation(summary = "获取消息统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getMessageStatistics() {
        try {
            Map<String, Object> statistics = webSocketMessageStoreService.getMessageStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取消息统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取消息统计信息失败");
        }
    }

    @Operation(summary = "获取未发送的消息")
    @GetMapping("/unsent")
    public Result<List<WebSocketMessageStore>> getUnsentMessages(@ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<WebSocketMessageStore> messages = webSocketMessageStoreService.getUnsentMessages(limit);
            return Result.success(messages);
        } catch (Exception e) {
            log.error("获取未发送的消息失败: {}", e.getMessage(), e);
            return Result.fail("获取未发送的消息失败");
        }
    }

    @Operation(summary = "重试发送失败的消息")
    @PostMapping("/retry/{messageId}")
    public Result<Void> retryFailedMessage(@ApiParam(value = "消息ID", required = true) @PathVariable String messageId) {
        try {
            boolean success = webSocketMessageStoreService.retryFailedMessage(messageId);
            if (success) {
                return Result.successMsg("重试发送失败的消息成功");
            } else {
                return Result.fail("重试发送失败的消息失败");
            }
        } catch (Exception e) {
            log.error("重试发送失败的消息失败: {}", e.getMessage(), e);
            return Result.fail("重试发送失败的消息失败");
        }
    }

    @Operation(summary = "批量重试发送失败的消息")
    @PostMapping("/batch-retry")
    public Result<Integer> batchRetryFailedMessages(@ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            int count = webSocketMessageStoreService.batchRetryFailedMessages(limit);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量重试发送失败的消息失败: {}", e.getMessage(), e);
            return Result.fail("批量重试发送失败的消息失败");
        }
    }
}
