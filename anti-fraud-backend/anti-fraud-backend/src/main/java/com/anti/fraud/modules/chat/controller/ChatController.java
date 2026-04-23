package com.anti.fraud.modules.chat.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.chat.entity.ChatSession;
import com.anti.fraud.modules.chat.entity.ChatMessage;
import com.anti.fraud.modules.chat.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 对话服务控制器
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "对话服务")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "创建会话")
    @PostMapping("/session/create")
    public Result<String> createSession(@ApiParam(value = "会话信息", required = true) @RequestBody ChatSession chatSession) {
        try {
            String sessionId = chatService.createSession(chatSession);
            return Result.success(sessionId);
        } catch (Exception e) {
            log.error("创建会话失败: {}", e.getMessage(), e);
            return Result.fail("创建会话失败");
        }
    }

    @Operation(summary = "结束会话")
    @PutMapping("/session/end/{sessionId}")
    public Result<Void> endSession(@ApiParam(value = "会话ID", required = true) @PathVariable String sessionId) {
        try {
            boolean success = chatService.endSession(sessionId);
            if (success) {
                return Result.successMsg("结束会话成功");
            } else {
                return Result.fail("结束会话失败");
            }
        } catch (Exception e) {
            log.error("结束会话失败: {}", e.getMessage(), e);
            return Result.fail("结束会话失败");
        }
    }

    @Operation(summary = "获取会话详情")
    @GetMapping("/session/detail/{sessionId}")
    public Result<ChatSession> getSessionBySessionId(@ApiParam(value = "会话ID", required = true) @PathVariable String sessionId) {
        try {
            ChatSession session = chatService.getSessionBySessionId(sessionId);
            if (session != null) {
                return Result.success(session);
            } else {
                return Result.fail("会话不存在");
            }
        } catch (Exception e) {
            log.error("获取会话详情失败: {}", e.getMessage(), e);
            return Result.fail("获取会话详情失败");
        }
    }

    @Operation(summary = "分页查询会话列表")
    @GetMapping("/session/list")
    public Result<Map<String, Object>> getSessionList(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "状态: 1-活跃, 2-已结束", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = chatService.getSessionList(userId, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询会话列表失败: {}", e.getMessage(), e);
            return Result.fail("查询会话列表失败");
        }
    }

    @Operation(summary = "发送消息")
    @PostMapping("/message/send")
    public Result<String> sendMessage(@ApiParam(value = "消息信息", required = true) @RequestBody ChatMessage chatMessage) {
        try {
            String messageId = chatService.sendMessage(chatMessage);
            return Result.success(messageId);
        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage(), e);
            return Result.fail("发送消息失败");
        }
    }

    @Operation(summary = "获取消息详情")
    @GetMapping("/message/detail/{messageId}")
    public Result<ChatMessage> getMessageByMessageId(@ApiParam(value = "消息ID", required = true) @PathVariable String messageId) {
        try {
            ChatMessage message = chatService.getMessageByMessageId(messageId);
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

    @Operation(summary = "分页查询消息列表")
    @GetMapping("/message/list")
    public Result<Map<String, Object>> getMessageList(
            @ApiParam(value = "会话ID", required = true) @RequestParam String sessionId,
            @ApiParam(value = "状态: 1-已发送, 2-已读, 3-已删除", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = chatService.getMessageList(sessionId, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询消息列表失败: {}", e.getMessage(), e);
            return Result.fail("查询消息列表失败");
        }
    }

    @Operation(summary = "标记消息为已读")
    @PutMapping("/message/mark-read/{messageId}")
    public Result<Void> markMessageAsRead(@ApiParam(value = "消息ID", required = true) @PathVariable String messageId) {
        try {
            boolean success = chatService.markMessageAsRead(messageId);
            if (success) {
                return Result.successMsg("标记消息为已读成功");
            } else {
                return Result.fail("标记消息为已读失败");
            }
        } catch (Exception e) {
            log.error("标记消息为已读失败: {}", e.getMessage(), e);
            return Result.fail("标记消息为已读失败");
        }
    }

    @Operation(summary = "标记会话所有消息为已读")
    @PutMapping("/session/mark-all-read/{sessionId}")
    public Result<Void> markAllMessagesAsRead(@ApiParam(value = "会话ID", required = true) @PathVariable String sessionId) {
        try {
            boolean success = chatService.markAllMessagesAsRead(sessionId);
            if (success) {
                return Result.successMsg("标记会话所有消息为已读成功");
            } else {
                return Result.fail("标记会话所有消息为已读失败");
            }
        } catch (Exception e) {
            log.error("标记会话所有消息为已读失败: {}", e.getMessage(), e);
            return Result.fail("标记会话所有消息为已读失败");
        }
    }

    @Operation(summary = "删除消息")
    @DeleteMapping("/message/delete/{messageId}")
    public Result<Void> deleteMessage(@ApiParam(value = "消息ID", required = true) @PathVariable String messageId) {
        try {
            boolean success = chatService.deleteMessage(messageId);
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

    @Operation(summary = "删除会话")
    @DeleteMapping("/session/delete/{sessionId}")
    public Result<Void> deleteSession(@ApiParam(value = "会话ID", required = true) @PathVariable String sessionId) {
        try {
            boolean success = chatService.deleteSession(sessionId);
            if (success) {
                return Result.successMsg("删除会话成功");
            } else {
                return Result.fail("删除会话失败");
            }
        } catch (Exception e) {
            log.error("删除会话失败: {}", e.getMessage(), e);
            return Result.fail("删除会话失败");
        }
    }

    @Operation(summary = "获取会话未读消息数量")
    @GetMapping("/session/unread-count/{sessionId}")
    public Result<Integer> getUnreadMessageCount(@ApiParam(value = "会话ID", required = true) @PathVariable String sessionId) {
        try {
            Integer count = chatService.getUnreadMessageCount(sessionId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取会话未读消息数量失败: {}", e.getMessage(), e);
            return Result.fail("获取会话未读消息数量失败");
        }
    }

    @Operation(summary = "获取用户未读消息总数")
    @GetMapping("/user/unread-count/{userId}")
    public Result<Integer> getTotalUnreadMessageCount(@ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        try {
            Integer count = chatService.getTotalUnreadMessageCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取用户未读消息总数失败: {}", e.getMessage(), e);
            return Result.fail("获取用户未读消息总数失败");
        }
    }

    @Operation(summary = "获取最近的会话")
    @GetMapping("/session/recent")
    public Result<List<ChatSession>> getRecentSessions(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<ChatSession> sessions = chatService.getRecentSessions(userId, limit);
            return Result.success(sessions);
        } catch (Exception e) {
            log.error("获取最近的会话失败: {}", e.getMessage(), e);
            return Result.fail("获取最近的会话失败");
        }
    }

    @Operation(summary = "更新会话标题")
    @PutMapping("/session/update-title/{sessionId}")
    public Result<Void> updateSessionTitle(
            @ApiParam(value = "会话ID", required = true) @PathVariable String sessionId,
            @ApiParam(value = "新标题", required = true) @RequestParam String title) {
        try {
            boolean success = chatService.updateSessionTitle(sessionId, title);
            if (success) {
                return Result.successMsg("更新会话标题成功");
            } else {
                return Result.fail("更新会话标题失败");
            }
        } catch (Exception e) {
            log.error("更新会话标题失败: {}", e.getMessage(), e);
            return Result.fail("更新会话标题失败");
        }
    }

    @Operation(summary = "保存对话历史到Redis")
    @PostMapping("/session/save-history/{sessionId}")
    public Result<Void> saveChatHistoryToRedis(
            @ApiParam(value = "会话ID", required = true) @PathVariable String sessionId,
            @ApiParam(value = "对话历史", required = true) @RequestBody List<Map<String, String>> chatHistory) {
        try {
            boolean success = chatService.saveChatHistoryToRedis(sessionId, chatHistory);
            if (success) {
                return Result.successMsg("保存对话历史到Redis成功");
            } else {
                return Result.fail("保存对话历史到Redis失败");
            }
        } catch (Exception e) {
            log.error("保存对话历史到Redis失败: {}", e.getMessage(), e);
            return Result.fail("保存对话历史到Redis失败");
        }
    }

    @Operation(summary = "从Redis获取对话历史")
    @GetMapping("/session/get-history/{sessionId}")
    public Result<List<Map<String, String>>> getChatHistoryFromRedis(@ApiParam(value = "会话ID", required = true) @PathVariable String sessionId) {
        try {
            List<Map<String, String>> chatHistory = chatService.getChatHistoryFromRedis(sessionId);
            return Result.success(chatHistory);
        } catch (Exception e) {
            log.error("从Redis获取对话历史失败: {}", e.getMessage(), e);
            return Result.fail("从Redis获取对话历史失败");
        }
    }

    @Operation(summary = "清理Redis中的对话历史")
    @DeleteMapping("/session/clear-history/{sessionId}")
    public Result<Void> clearChatHistoryFromRedis(@ApiParam(value = "会话ID", required = true) @PathVariable String sessionId) {
        try {
            boolean success = chatService.clearChatHistoryFromRedis(sessionId);
            if (success) {
                return Result.successMsg("清理Redis中的对话历史成功");
            } else {
                return Result.fail("清理Redis中的对话历史失败");
            }
        } catch (Exception e) {
            log.error("清理Redis中的对话历史失败: {}", e.getMessage(), e);
            return Result.fail("清理Redis中的对话历史失败");
        }
    }
}
