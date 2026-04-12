package com.anti.fraud.modules.roleplay.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.roleplay.entity.RolePlaySession;
import com.anti.fraud.modules.roleplay.entity.RolePlayScore;
import com.anti.fraud.modules.roleplay.service.RolePlayService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/roleplay")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "角色扮演")
public class RolePlayController {

    private final RolePlayService rolePlayService;

    @Operation(summary = "创建角色扮演会话")
    @PostMapping("/session/create")
    public Result<Void> createSession(@RequestBody RolePlaySession session) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        session.setUserId(userId);
        session.setUserName(SecurityUtils.getCurrentUserName());
        session.setCreatedBy(SecurityUtils.getCurrentUserName());
        session.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = rolePlayService.createSession(session);
            if (success) {
                return Result.successMsg("创建角色扮演会话成功");
            } else {
                return Result.fail("创建角色扮演会话失败");
            }
        } catch (Exception e) {
            log.error("创建角色扮演会话失败: {}", e.getMessage(), e);
            return Result.fail("创建角色扮演会话失败");
        }
    }

    @Operation(summary = "更新角色扮演会话")
    @PostMapping("/session/update")
    public Result<Void> updateSession(@RequestBody RolePlaySession session) {
        session.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = rolePlayService.updateSession(session);
            if (success) {
                return Result.successMsg("更新角色扮演会话成功");
            } else {
                return Result.fail("更新角色扮演会话失败");
            }
        } catch (Exception e) {
            log.error("更新角色扮演会话失败: {}", e.getMessage(), e);
            return Result.fail("更新角色扮演会话失败");
        }
    }

    @Operation(summary = "结束角色扮演会话")
    @PostMapping("/session/end/{sessionId}")
    public Result<Void> endSession(@PathVariable Long sessionId, @RequestBody Map<String, String> data) {
        String sessionData = data.get("sessionData");

        try {
            boolean success = rolePlayService.endSession(sessionId, sessionData);
            if (success) {
                return Result.successMsg("结束角色扮演会话成功");
            } else {
                return Result.fail("结束角色扮演会话失败");
            }
        } catch (Exception e) {
            log.error("结束角色扮演会话失败: {}", e.getMessage(), e);
            return Result.fail("结束角色扮演会话失败");
        }
    }

    @Operation(summary = "获取会话详情")
    @GetMapping("/session/detail/{sessionId}")
    public Result<RolePlaySession> getSessionById(@PathVariable Long sessionId) {
        try {
            RolePlaySession session = rolePlayService.getSessionById(sessionId);
            if (session != null) {
                return Result.success("获取会话详情成功", session);
            } else {
                return Result.fail("会话不存在");
            }
        } catch (Exception e) {
            log.error("获取会话详情失败: {}", e.getMessage(), e);
            return Result.fail("获取会话详情失败");
        }
    }

    @Operation(summary = "获取用户会话列表")
    @GetMapping("/session/user")
    public Result<List<RolePlaySession>> getUserSessions(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<RolePlaySession> sessions = rolePlayService.getUserSessions(userId, status, page, size);
            return Result.success("获取用户会话列表成功", sessions);
        } catch (Exception e) {
            log.error("获取用户会话列表失败: {}", e.getMessage(), e);
            return Result.fail("获取用户会话列表失败");
        }
    }

    @Operation(summary = "获取所有会话列表")
    @GetMapping("/session/all")
    public Result<List<RolePlaySession>> getAllSessions(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<RolePlaySession> sessions = rolePlayService.getAllSessions(status, page, size);
            return Result.success("获取所有会话列表成功", sessions);
        } catch (Exception e) {
            log.error("获取所有会话列表失败: {}", e.getMessage(), e);
            return Result.fail("获取所有会话列表失败");
        }
    }

    @Operation(summary = "评分角色扮演")
    @PostMapping("/score")
    public Result<Void> scoreRolePlay(@RequestBody RolePlayScore score) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        score.setUserId(userId);
        score.setEvaluator(SecurityUtils.getCurrentUserName());

        try {
            boolean success = rolePlayService.scoreRolePlay(score);
            if (success) {
                return Result.successMsg("评分成功");
            } else {
                return Result.fail("评分失败");
            }
        } catch (Exception e) {
            log.error("评分失败: {}", e.getMessage(), e);
            return Result.fail("评分失败");
        }
    }

    @Operation(summary = "获取会话评分")
    @GetMapping("/score/session/{sessionId}")
    public Result<List<RolePlayScore>> getSessionScores(@PathVariable Long sessionId) {
        try {
            List<RolePlayScore> scores = rolePlayService.getSessionScores(sessionId);
            return Result.success("获取会话评分成功", scores);
        } catch (Exception e) {
            log.error("获取会话评分失败: {}", e.getMessage(), e);
            return Result.fail("获取会话评分失败");
        }
    }

    @Operation(summary = "获取用户评分历史")
    @GetMapping("/score/history")
    public Result<List<RolePlayScore>> getUserScoreHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<RolePlayScore> scores = rolePlayService.getUserScoreHistory(userId, page, size);
            return Result.success("获取用户评分历史成功", scores);
        } catch (Exception e) {
            log.error("获取用户评分历史失败: {}", e.getMessage(), e);
            return Result.fail("获取用户评分历史失败");
        }
    }

    @Operation(summary = "计算会话总评分")
    @PostMapping("/score/calculate/{sessionId}")
    public Result<Integer> calculateTotalScore(@PathVariable Long sessionId) {
        try {
            Integer totalScore = rolePlayService.calculateTotalScore(sessionId);
            return Result.success("计算总评分成功", totalScore);
        } catch (Exception e) {
            log.error("计算总评分失败: {}", e.getMessage(), e);
            return Result.fail("计算总评分失败");
        }
    }

    @Operation(summary = "生成反馈")
    @PostMapping("/feedback/{sessionId}")
    public Result<String> generateFeedback(@PathVariable Long sessionId) {
        try {
            String feedback = rolePlayService.generateFeedback(sessionId);
            return Result.success("生成反馈成功", feedback);
        } catch (Exception e) {
            log.error("生成反馈失败: {}", e.getMessage(), e);
            return Result.fail("生成反馈失败");
        }
    }

    @Operation(summary = "获取角色扮演统计信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getRolePlayStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Map<String, Object> stats = rolePlayService.getRolePlayStats(userId);
            return Result.success("获取角色扮演统计信息成功", stats);
        } catch (Exception e) {
            log.error("获取角色扮演统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取角色扮演统计信息失败");
        }
    }

    @Operation(summary = "获取推荐场景")
    @GetMapping("/scenarios/recommended")
    public Result<List<Map<String, Object>>> getRecommendedScenarios(@RequestParam(defaultValue = "3") int count) {
        Long userId = SecurityUtils.getCurrentUserId();
        try {
            List<Map<String, Object>> scenarios = rolePlayService.getRecommendedScenarios(userId, count);
            return Result.success("获取推荐场景成功", scenarios);
        } catch (Exception e) {
            log.error("获取推荐场景失败: {}", e.getMessage(), e);
            return Result.fail("获取推荐场景失败");
        }
    }
}
