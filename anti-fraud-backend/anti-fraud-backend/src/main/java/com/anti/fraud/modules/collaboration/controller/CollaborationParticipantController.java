package com.anti.fraud.modules.collaboration.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.collaboration.entity.CollaborationParticipant;
import com.anti.fraud.modules.collaboration.service.CollaborationParticipantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 协作演练参与者控制器
 */
@RestController
@RequestMapping("/collaboration/participant")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "协作演练参与者管理")
public class CollaborationParticipantController {

    private final CollaborationParticipantService collaborationParticipantService;

    @Operation(summary = "添加参与者")
    @PostMapping("/add")
    public Result<Void> addParticipant(@ApiParam(value = "参与者信息", required = true) @RequestBody CollaborationParticipant participant) {
        try {
            boolean success = collaborationParticipantService.addParticipant(participant);
            if (success) {
                return Result.successMsg("添加参与者成功");
            } else {
                return Result.fail("添加参与者失败");
            }
        } catch (Exception e) {
            log.error("添加参与者失败: {}", e.getMessage(), e);
            return Result.fail("添加参与者失败");
        }
    }

    @Operation(summary = "更新参与者信息")
    @PutMapping("/update")
    public Result<Void> updateParticipant(@ApiParam(value = "参与者信息", required = true) @RequestBody CollaborationParticipant participant) {
        try {
            boolean success = collaborationParticipantService.updateParticipant(participant);
            if (success) {
                return Result.successMsg("更新参与者信息成功");
            } else {
                return Result.fail("更新参与者信息失败");
            }
        } catch (Exception e) {
            log.error("更新参与者信息失败: {}", e.getMessage(), e);
            return Result.fail("更新参与者信息失败");
        }
    }

    @Operation(summary = "删除参与者")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteParticipant(@ApiParam(value = "参与者ID", required = true) @PathVariable Long id) {
        try {
            boolean success = collaborationParticipantService.deleteParticipant(id);
            if (success) {
                return Result.successMsg("删除参与者成功");
            } else {
                return Result.fail("删除参与者失败");
            }
        } catch (Exception e) {
            log.error("删除参与者失败: {}", e.getMessage(), e);
            return Result.fail("删除参与者失败");
        }
    }

    @Operation(summary = "获取参与者详情")
    @GetMapping("/detail/{id}")
    public Result<CollaborationParticipant> getParticipantById(@ApiParam(value = "参与者ID", required = true) @PathVariable Long id) {
        try {
            CollaborationParticipant participant = collaborationParticipantService.getParticipantById(id);
            if (participant != null) {
                return Result.success(participant);
            } else {
                return Result.fail("参与者不存在");
            }
        } catch (Exception e) {
            log.error("获取参与者详情失败: {}", e.getMessage(), e);
            return Result.fail("获取参与者详情失败");
        }
    }

    @Operation(summary = "根据会话ID查询参与者")
    @GetMapping("/by-session")
    public Result<Map<String, Object>> getParticipantsBySessionId(
            @ApiParam(value = "会话ID", required = true) @RequestParam Long sessionId,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = collaborationParticipantService.getParticipantsBySessionId(sessionId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据会话ID查询参与者失败: {}", e.getMessage(), e);
            return Result.fail("根据会话ID查询参与者失败");
        }
    }

    @Operation(summary = "根据用户ID查询参与的会话")
    @GetMapping("/by-user")
    public Result<Map<String, Object>> getParticipantsByUserId(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = collaborationParticipantService.getParticipantsByUserId(userId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据用户ID查询参与的会话失败: {}", e.getMessage(), e);
            return Result.fail("根据用户ID查询参与的会话失败");
        }
    }

    @Operation(summary = "参与者加入会话")
    @PostMapping("/join")
    public Result<Void> joinSession(
            @ApiParam(value = "会话ID", required = true) @RequestParam Long sessionId,
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "角色: 1-主持人, 2-参与者, 3-观察者", required = true) @RequestParam Integer role) {
        try {
            boolean success = collaborationParticipantService.joinSession(sessionId, userId, username, role);
            if (success) {
                return Result.successMsg("参与者加入会话成功");
            } else {
                return Result.fail("参与者加入会话失败");
            }
        } catch (Exception e) {
            log.error("参与者加入会话失败: {}", e.getMessage(), e);
            return Result.fail("参与者加入会话失败");
        }
    }

    @Operation(summary = "参与者离开会话")
    @PostMapping("/leave")
    public Result<Void> leaveSession(
            @ApiParam(value = "会话ID", required = true) @RequestParam Long sessionId,
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId) {
        try {
            boolean success = collaborationParticipantService.leaveSession(sessionId, userId);
            if (success) {
                return Result.successMsg("参与者离开会话成功");
            } else {
                return Result.fail("参与者离开会话失败");
            }
        } catch (Exception e) {
            log.error("参与者离开会话失败: {}", e.getMessage(), e);
            return Result.fail("参与者离开会话失败");
        }
    }

    @Operation(summary = "更新参与者状态")
    @PutMapping("/status/{id}")
    public Result<Void> updateParticipantStatus(
            @ApiParam(value = "参与者ID", required = true) @PathVariable Long id,
            @ApiParam(value = "状态: 1-在线, 2-离线", required = true) @RequestParam Integer status) {
        try {
            boolean success = collaborationParticipantService.updateParticipantStatus(id, status);
            if (success) {
                return Result.successMsg("更新参与者状态成功");
            } else {
                return Result.fail("更新参与者状态失败");
            }
        } catch (Exception e) {
            log.error("更新参与者状态失败: {}", e.getMessage(), e);
            return Result.fail("更新参与者状态失败");
        }
    }

    @Operation(summary = "更新参与者得分")
    @PutMapping("/score/{id}")
    public Result<Void> updateParticipantScore(
            @ApiParam(value = "参与者ID", required = true) @PathVariable Long id,
            @ApiParam(value = "得分", required = true) @RequestParam Integer score) {
        try {
            boolean success = collaborationParticipantService.updateParticipantScore(id, score);
            if (success) {
                return Result.successMsg("更新参与者得分成功");
            } else {
                return Result.fail("更新参与者得分失败");
            }
        } catch (Exception e) {
            log.error("更新参与者得分失败: {}", e.getMessage(), e);
            return Result.fail("更新参与者得分失败");
        }
    }

    @Operation(summary = "更新参与者完成情况")
    @PutMapping("/completion/{id}")
    public Result<Void> updateParticipantCompletionStatus(
            @ApiParam(value = "参与者ID", required = true) @PathVariable Long id,
            @ApiParam(value = "完成情况: 1-已完成, 2-未完成", required = true) @RequestParam Integer completionStatus) {
        try {
            boolean success = collaborationParticipantService.updateParticipantCompletionStatus(id, completionStatus);
            if (success) {
                return Result.successMsg("更新参与者完成情况成功");
            } else {
                return Result.fail("更新参与者完成情况失败");
            }
        } catch (Exception e) {
            log.error("更新参与者完成情况失败: {}", e.getMessage(), e);
            return Result.fail("更新参与者完成情况失败");
        }
    }

    @Operation(summary = "统计会话参与者角色")
    @GetMapping("/role-stats/{sessionId}")
    public Result<Map<String, Object>> getRoleStatsBySessionId(
            @ApiParam(value = "会话ID", required = true) @PathVariable Long sessionId) {
        try {
            Map<String, Object> stats = collaborationParticipantService.getRoleStatsBySessionId(sessionId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计会话参与者角色失败: {}", e.getMessage(), e);
            return Result.fail("统计会话参与者角色失败");
        }
    }

    @Operation(summary = "统计会话参与者状态")
    @GetMapping("/status-stats/{sessionId}")
    public Result<Map<String, Object>> getStatusStatsBySessionId(
            @ApiParam(value = "会话ID", required = true) @PathVariable Long sessionId) {
        try {
            Map<String, Object> stats = collaborationParticipantService.getStatusStatsBySessionId(sessionId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计会话参与者状态失败: {}", e.getMessage(), e);
            return Result.fail("统计会话参与者状态失败");
        }
    }
}
