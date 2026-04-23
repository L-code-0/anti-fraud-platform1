package com.anti.fraud.modules.collaboration.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.collaboration.entity.CollaborationSession;
import com.anti.fraud.modules.collaboration.service.CollaborationSessionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 协作演练会话控制器
 */
@RestController
@RequestMapping("/collaboration/session")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "协作演练会话管理")
public class CollaborationSessionController {

    private final CollaborationSessionService collaborationSessionService;

    @Operation(summary = "创建会话")
    @PostMapping("/create")
    public Result<Void> createSession(@ApiParam(value = "会话信息", required = true) @RequestBody CollaborationSession session) {
        try {
            boolean success = collaborationSessionService.createSession(session);
            if (success) {
                return Result.successMsg("创建会话成功");
            } else {
                return Result.fail("创建会话失败");
            }
        } catch (Exception e) {
            log.error("创建会话失败: {}", e.getMessage(), e);
            return Result.fail("创建会话失败");
        }
    }

    @Operation(summary = "更新会话")
    @PutMapping("/update")
    public Result<Void> updateSession(@ApiParam(value = "会话信息", required = true) @RequestBody CollaborationSession session) {
        try {
            boolean success = collaborationSessionService.updateSession(session);
            if (success) {
                return Result.successMsg("更新会话成功");
            } else {
                return Result.fail("更新会话失败");
            }
        } catch (Exception e) {
            log.error("更新会话失败: {}", e.getMessage(), e);
            return Result.fail("更新会话失败");
        }
    }

    @Operation(summary = "删除会话")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteSession(@ApiParam(value = "会话ID", required = true) @PathVariable Long id) {
        try {
            boolean success = collaborationSessionService.deleteSession(id);
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

    @Operation(summary = "获取会话详情")
    @GetMapping("/detail/{id}")
    public Result<CollaborationSession> getSessionById(@ApiParam(value = "会话ID", required = true) @PathVariable Long id) {
        try {
            CollaborationSession session = collaborationSessionService.getSessionById(id);
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

    @Operation(summary = "分页查询会话")
    @PostMapping("/list")
    public Result<Map<String, Object>> getSessionList(
            @ApiParam(value = "查询参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            Map<String, Object> result = collaborationSessionService.getSessionList(params, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询会话列表失败: {}", e.getMessage(), e);
            return Result.fail("查询会话列表失败");
        }
    }

    @Operation(summary = "根据创建者ID查询会话")
    @GetMapping("/by-creator")
    public Result<Map<String, Object>> getSessionsByCreatorId(
            @ApiParam(value = "创建者ID", required = true) @RequestParam Long creatorId,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = collaborationSessionService.getSessionsByCreatorId(creatorId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据创建者ID查询会话失败: {}", e.getMessage(), e);
            return Result.fail("根据创建者ID查询会话失败");
        }
    }

    @Operation(summary = "根据场景ID查询会话")
    @GetMapping("/by-scenario")
    public Result<Map<String, Object>> getSessionsByScenarioId(
            @ApiParam(value = "场景ID", required = true) @RequestParam Long scenarioId,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = collaborationSessionService.getSessionsByScenarioId(scenarioId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据场景ID查询会话失败: {}", e.getMessage(), e);
            return Result.fail("根据场景ID查询会话失败");
        }
    }

    @Operation(summary = "开始会话")
    @PostMapping("/start/{id}")
    public Result<Void> startSession(@ApiParam(value = "会话ID", required = true) @PathVariable Long id) {
        try {
            boolean success = collaborationSessionService.startSession(id);
            if (success) {
                return Result.successMsg("开始会话成功");
            } else {
                return Result.fail("开始会话失败");
            }
        } catch (Exception e) {
            log.error("开始会话失败: {}", e.getMessage(), e);
            return Result.fail("开始会话失败");
        }
    }

    @Operation(summary = "结束会话")
    @PostMapping("/end/{id}")
    public Result<Void> endSession(@ApiParam(value = "会话ID", required = true) @PathVariable Long id) {
        try {
            boolean success = collaborationSessionService.endSession(id);
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

    @Operation(summary = "取消会话")
    @PostMapping("/cancel/{id}")
    public Result<Void> cancelSession(@ApiParam(value = "会话ID", required = true) @PathVariable Long id) {
        try {
            boolean success = collaborationSessionService.cancelSession(id);
            if (success) {
                return Result.successMsg("取消会话成功");
            } else {
                return Result.fail("取消会话失败");
            }
        } catch (Exception e) {
            log.error("取消会话失败: {}", e.getMessage(), e);
            return Result.fail("取消会话失败");
        }
    }

    @Operation(summary = "获取进行中的会话")
    @GetMapping("/active")
    public Result<List<CollaborationSession>> getActiveSessions(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<CollaborationSession> sessions = collaborationSessionService.getActiveSessions(limit);
            return Result.success(sessions);
        } catch (Exception e) {
            log.error("获取进行中的会话失败: {}", e.getMessage(), e);
            return Result.fail("获取进行中的会话失败");
        }
    }

    @Operation(summary = "统计会话信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getSessionStats() {
        try {
            Map<String, Object> stats = collaborationSessionService.getSessionStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计会话信息失败: {}", e.getMessage(), e);
            return Result.fail("统计会话信息失败");
        }
    }
}
