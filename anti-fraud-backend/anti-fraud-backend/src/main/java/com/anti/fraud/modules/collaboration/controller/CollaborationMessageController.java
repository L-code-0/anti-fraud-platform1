package com.anti.fraud.modules.collaboration.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.collaboration.entity.CollaborationMessage;
import com.anti.fraud.modules.collaboration.service.CollaborationMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 协作演练消息控制器
 */
@RestController
@RequestMapping("/collaboration/message")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "协作演练消息管理")
public class CollaborationMessageController {

    private final CollaborationMessageService collaborationMessageService;

    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public Result<Void> sendMessage(@ApiParam(value = "消息信息", required = true) @RequestBody CollaborationMessage message) {
        try {
            boolean success = collaborationMessageService.sendMessage(message);
            if (success) {
                return Result.successMsg("发送消息成功");
            } else {
                return Result.fail("发送消息失败");
            }
        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage(), e);
            return Result.fail("发送消息失败");
        }
    }

    @Operation(summary = "更新消息状态")
    @PutMapping("/status/{id}")
    public Result<Void> updateMessageStatus(
            @ApiParam(value = "消息ID", required = true) @PathVariable Long id,
            @ApiParam(value = "状态: 1-已发送, 2-已读", required = true) @RequestParam Integer status) {
        try {
            boolean success = collaborationMessageService.updateMessageStatus(id, status);
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

    @Operation(summary = "删除消息")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteMessage(@ApiParam(value = "消息ID", required = true) @PathVariable Long id) {
        try {
            boolean success = collaborationMessageService.deleteMessage(id);
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

    @Operation(summary = "获取消息详情")
    @GetMapping("/detail/{id}")
    public Result<CollaborationMessage> getMessageById(@ApiParam(value = "消息ID", required = true) @PathVariable Long id) {
        try {
            CollaborationMessage message = collaborationMessageService.getMessageById(id);
            if (message != null) {
                return Result.success(message);
            } else {
                return Result.fail("消息不存在");
            }
        } catch (Exception e) {
            log.error("获取消息详情失败: {}", e.getMessage(), e);
            return Result.fail("获取消息详情失败");
        }
    }

    @Operation(summary = "根据会话ID查询消息")
    @GetMapping("/by-session")
    public Result<Map<String, Object>> getMessagesBySessionId(
            @ApiParam(value = "会话ID", required = true) @RequestParam Long sessionId,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = collaborationMessageService.getMessagesBySessionId(sessionId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据会话ID查询消息失败: {}", e.getMessage(), e);
            return Result.fail("根据会话ID查询消息失败");
        }
    }

    @Operation(summary = "根据会话ID和消息类型查询消息")
    @GetMapping("/by-type")
    public Result<Map<String, Object>> getMessagesBySessionIdAndType(
            @ApiParam(value = "会话ID", required = true) @RequestParam Long sessionId,
            @ApiParam(value = "消息类型: 1-文本, 2-图片, 3-文件, 4-系统通知", required = true) @RequestParam Integer messageType,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = collaborationMessageService.getMessagesBySessionIdAndType(sessionId, messageType, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据会话ID和消息类型查询消息失败: {}", e.getMessage(), e);
            return Result.fail("根据会话ID和消息类型查询消息失败");
        }
    }

    @Operation(summary = "发送系统通知")
    @PostMapping("/system")
    public Result<Void> sendSystemMessage(
            @ApiParam(value = "会话ID", required = true) @RequestParam Long sessionId,
            @ApiParam(value = "通知内容", required = true) @RequestParam String content) {
        try {
            boolean success = collaborationMessageService.sendSystemMessage(sessionId, content);
            if (success) {
                return Result.successMsg("发送系统通知成功");
            } else {
                return Result.fail("发送系统通知失败");
            }
        } catch (Exception e) {
            log.error("发送系统通知失败: {}", e.getMessage(), e);
            return Result.fail("发送系统通知失败");
        }
    }

    @Operation(summary = "统计会话消息类型")
    @GetMapping("/type-stats/{sessionId}")
    public Result<Map<String, Object>> getMessageTypeStatsBySessionId(
            @ApiParam(value = "会话ID", required = true) @PathVariable Long sessionId) {
        try {
            Map<String, Object> stats = collaborationMessageService.getMessageTypeStatsBySessionId(sessionId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计会话消息类型失败: {}", e.getMessage(), e);
            return Result.fail("统计会话消息类型失败");
        }
    }
}
