package com.anti.fraud.modules.collaboration.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.collaboration.manager.CollaborationSessionManager;
import com.anti.fraud.modules.collaboration.model.Operation;
import com.anti.fraud.modules.collaboration.service.CollaborationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 协作服务控制器
 */
@RestController
@RequestMapping("/collaboration")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "协作服务")
public class CollaborationController {

    private final CollaborationService collaborationService;

    @Operation(summary = "创建协作会话")
    @PostMapping("/session")
    public Result<CollaborationSessionManager.CollaborationSession> createSession(
            @ApiParam(value = "会话ID", required = true) @RequestParam String sessionId,
            @ApiParam(value = "初始内容", required = true) @RequestParam String initialContent,
            @ApiParam(value = "创建者ID", required = true) @RequestParam Long creatorId,
            @ApiParam(value = "创建者名称", required = true) @RequestParam String creatorName) {
        try {
            CollaborationSessionManager.CollaborationSession session = collaborationService.createSession(sessionId, initialContent, creatorId, creatorName);
            if (session != null) {
                return Result.success(session);
            } else {
                return Result.fail("创建协作会话失败");
            }
        } catch (Exception e) {
            log.error("创建协作会话失败: {}", e.getMessage(), e);
            return Result.fail("创建协作会话失败");
        }
    }

    @Operation(summary = "获取协作会话")
    @GetMapping("/session/{sessionId}")
    public Result<CollaborationSessionManager.CollaborationSession> getSession(@ApiParam(value = "会话ID", required = true) @PathVariable String sessionId) {
        try {
            CollaborationSessionManager.CollaborationSession session = collaborationService.getSession(sessionId);
            if (session != null) {
                return Result.success(session);
            } else {
                return Result.fail("协作会话不存在");
            }
        } catch (Exception e) {
            log.error("获取协作会话失败: {}", e.getMessage(), e);
            return Result.fail("获取协作会话失败");
        }
    }

    @Operation(summary = "加入协作会话")
    @PostMapping("/session/{sessionId}/join")
    public Result<Void> joinSession(
            @ApiParam(value = "会话ID", required = true) @PathVariable String sessionId,
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名称", required = true) @RequestParam String userName) {
        try {
            boolean success = collaborationService.joinSession(sessionId, userId, userName);
            if (success) {
                return Result.successMsg("加入协作会话成功");
            } else {
                return Result.fail("加入协作会话失败");
            }
        } catch (Exception e) {
            log.error("加入协作会话失败: {}", e.getMessage(), e);
            return Result.fail("加入协作会话失败");
        }
    }

    @Operation(summary = "离开协作会话")
    @PostMapping("/session/{sessionId}/leave")
    public Result<Void> leaveSession(
            @ApiParam(value = "会话ID", required = true) @PathVariable String sessionId,
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId) {
        try {
            boolean success = collaborationService.leaveSession(sessionId, userId);
            if (success) {
                return Result.successMsg("离开协作会话成功");
            } else {
                return Result.fail("离开协作会话失败");
            }
        } catch (Exception e) {
            log.error("离开协作会话失败: {}", e.getMessage(), e);
            return Result.fail("离开协作会话失败");
        }
    }

    @Operation(summary = "处理操作")
    @PostMapping("/session/{sessionId}/operation")
    public Result<Operation> processOperation(
            @ApiParam(value = "会话ID", required = true) @PathVariable String sessionId,
            @ApiParam(value = "操作", required = true) @RequestBody Operation operation) {
        try {
            Operation processedOperation = collaborationService.processOperation(sessionId, operation);
            if (processedOperation != null) {
                // 广播操作
                collaborationService.broadcastOperation(sessionId, processedOperation, operation.getUserId());
                return Result.success(processedOperation);
            } else {
                return Result.fail("处理操作失败");
            }
        } catch (Exception e) {
            log.error("处理操作失败: {}", e.getMessage(), e);
            return Result.fail("处理操作失败");
        }
    }

    @Operation(summary = "关闭协作会话")
    @DeleteMapping("/session/{sessionId}")
    public Result<Void> closeSession(@ApiParam(value = "会话ID", required = true) @PathVariable String sessionId) {
        try {
            boolean success = collaborationService.closeSession(sessionId);
            if (success) {
                return Result.successMsg("关闭协作会话成功");
            } else {
                return Result.fail("关闭协作会话失败");
            }
        } catch (Exception e) {
            log.error("关闭协作会话失败: {}", e.getMessage(), e);
            return Result.fail("关闭协作会话失败");
        }
    }

    @Operation(summary = "获取所有协作会话")
    @GetMapping("/sessions")
    public Result<List<CollaborationSessionManager.CollaborationSession>> getAllSessions() {
        try {
            List<CollaborationSessionManager.CollaborationSession> sessions = collaborationService.getAllSessions();
            return Result.success(sessions);
        } catch (Exception e) {
            log.error("获取所有协作会话失败: {}", e.getMessage(), e);
            return Result.fail("获取所有协作会话失败");
        }
    }

    @Operation(summary = "获取会话的在线用户")
    @GetMapping("/session/{sessionId}/online-users")
    public Result<Map<Long, String>> getOnlineUsers(@ApiParam(value = "会话ID", required = true) @PathVariable String sessionId) {
        try {
            Map<Long, String> onlineUsers = collaborationService.getOnlineUsers(sessionId);
            if (onlineUsers != null) {
                return Result.success(onlineUsers);
            } else {
                return Result.fail("协作会话不存在");
            }
        } catch (Exception e) {
            log.error("获取在线用户失败: {}", e.getMessage(), e);
            return Result.fail("获取在线用户失败");
        }
    }

    @Operation(summary = "获取会话的内容")
    @GetMapping("/session/{sessionId}/content")
    public Result<String> getSessionContent(@ApiParam(value = "会话ID", required = true) @PathVariable String sessionId) {
        try {
            String content = collaborationService.getSessionContent(sessionId);
            if (content != null) {
                return Result.success(content);
            } else {
                return Result.fail("协作会话不存在");
            }
        } catch (Exception e) {
            log.error("获取会话内容失败: {}", e.getMessage(), e);
            return Result.fail("获取会话内容失败");
        }
    }

    @Operation(summary = "获取会话的版本号")
    @GetMapping("/session/{sessionId}/version")
    public Result<Integer> getSessionVersion(@ApiParam(value = "会话ID", required = true) @PathVariable String sessionId) {
        try {
            int version = collaborationService.getSessionVersion(sessionId);
            if (version >= 0) {
                return Result.success(version);
            } else {
                return Result.fail("协作会话不存在");
            }
        } catch (Exception e) {
            log.error("获取会话版本号失败: {}", e.getMessage(), e);
            return Result.fail("获取会话版本号失败");
        }
    }

    @Operation(summary = "获取会话的操作历史")
    @GetMapping("/session/{sessionId}/history")
    public Result<List<Operation>> getOperationHistory(
            @ApiParam(value = "会话ID", required = true) @PathVariable String sessionId,
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<Operation> history = collaborationService.getOperationHistory(sessionId, limit);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取操作历史失败: {}", e.getMessage(), e);
            return Result.fail("获取操作历史失败");
        }
    }
}
