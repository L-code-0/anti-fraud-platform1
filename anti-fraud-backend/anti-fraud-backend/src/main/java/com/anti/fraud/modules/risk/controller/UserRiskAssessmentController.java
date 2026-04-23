package com.anti.fraud.modules.risk.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.risk.entity.UserRiskAssessment;
import com.anti.fraud.modules.risk.service.UserRiskAssessmentService;
import com.anti.fraud.modules.risk.vo.RiskLevelStatsVO;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户风险评估控制器
 */
@RestController
@RequestMapping("/risk/assessment")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "用户风险评估")
public class UserRiskAssessmentController {

    private final UserRiskAssessmentService userRiskAssessmentService;

    @Operation(summary = "评估用户风险")
    @PostMapping("/assess")
    public Result<UserRiskAssessment> assessUserRisk(
            @ApiParam(value = "用户ID，不填则评估当前登录用户", required = false) @RequestParam(required = false) Long userId) {
        Long targetUserId = userId != null ? userId : SecurityUtils.getCurrentUserId();
        if (targetUserId == null) {
            return Result.fail("请先登录或指定用户ID");
        }

        try {
            UserRiskAssessment assessment = userRiskAssessmentService.assessUserRisk(targetUserId);
            return Result.success("评估用户风险成功", assessment);
        } catch (Exception e) {
            log.error("评估用户风险失败: {}", e.getMessage(), e);
            return Result.fail("评估用户风险失败");
        }
    }

    @Operation(summary = "获取用户风险评估记录")
    @GetMapping("/records")
    public Result<List<UserRiskAssessment>> getUserRiskAssessments(
            @ApiParam(value = "用户ID，不填则获取当前登录用户", required = false) @RequestParam(required = false) Long userId,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer size) {
        Long targetUserId = userId != null ? userId : SecurityUtils.getCurrentUserId();
        if (targetUserId == null) {
            return Result.fail("请先登录或指定用户ID");
        }

        try {
            List<UserRiskAssessment> assessments = userRiskAssessmentService.getUserRiskAssessments(targetUserId, page, size);
            return Result.success("获取用户风险评估记录成功", assessments);
        } catch (Exception e) {
            log.error("获取用户风险评估记录失败: {}", e.getMessage(), e);
            return Result.fail("获取用户风险评估记录失败");
        }
    }

    @Operation(summary = "获取用户最新风险评估记录")
    @GetMapping("/latest")
    public Result<UserRiskAssessment> getLatestUserRiskAssessment(
            @ApiParam(value = "用户ID，不填则获取当前登录用户", required = false) @RequestParam(required = false) Long userId) {
        Long targetUserId = userId != null ? userId : SecurityUtils.getCurrentUserId();
        if (targetUserId == null) {
            return Result.fail("请先登录或指定用户ID");
        }

        try {
            UserRiskAssessment assessment = userRiskAssessmentService.getLatestUserRiskAssessment(targetUserId);
            if (assessment != null) {
                return Result.success("获取用户最新风险评估记录成功", assessment);
            } else {
                return Result.fail("未找到用户风险评估记录");
            }
        } catch (Exception e) {
            log.error("获取用户最新风险评估记录失败: {}", e.getMessage(), e);
            return Result.fail("获取用户最新风险评估记录失败");
        }
    }

    @Operation(summary = "获取风险等级统计")
    @GetMapping("/stats")
    public Result<List<RiskLevelStatsVO>> getRiskLevelStats() {
        try {
            List<RiskLevelStatsVO> stats = userRiskAssessmentService.getRiskLevelStats();
            return Result.success("获取风险等级统计成功", stats);
        } catch (Exception e) {
            log.error("获取风险等级统计失败: {}", e.getMessage(), e);
            return Result.fail("获取风险等级统计失败");
        }
    }

    @Operation(summary = "获取高风险用户列表")
    @GetMapping("/high-risk")
    public Result<List<UserRiskAssessment>> getHighRiskUsers(
            @ApiParam(value = "限制数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<UserRiskAssessment> highRiskUsers = userRiskAssessmentService.getHighRiskUsers(limit);
            return Result.success("获取高风险用户列表成功", highRiskUsers);
        } catch (Exception e) {
            log.error("获取高风险用户列表失败: {}", e.getMessage(), e);
            return Result.fail("获取高风险用户列表失败");
        }
    }

    @Operation(summary = "批量评估用户风险")
    @PostMapping("/batch-assess")
    public Result<Map<Long, UserRiskAssessment>> batchAssessUserRisk(
            @ApiParam(value = "用户ID列表", required = true) @RequestBody List<Long> userIds) {
        try {
            Map<Long, UserRiskAssessment> results = userRiskAssessmentService.batchAssessUserRisk(userIds);
            return Result.success("批量评估用户风险成功", results);
        } catch (Exception e) {
            log.error("批量评估用户风险失败: {}", e.getMessage(), e);
            return Result.fail("批量评估用户风险失败");
        }
    }

    @Operation(summary = "更新用户风险评估")
    @PutMapping("/update")
    public Result<Void> updateUserRiskAssessment(
            @ApiParam(value = "风险评估记录", required = true) @RequestBody UserRiskAssessment assessment) {
        try {
            boolean success = userRiskAssessmentService.updateUserRiskAssessment(assessment);
            if (success) {
                return Result.successMsg("更新用户风险评估成功");
            } else {
                return Result.fail("更新用户风险评估失败");
            }
        } catch (Exception e) {
            log.error("更新用户风险评估失败: {}", e.getMessage(), e);
            return Result.fail("更新用户风险评估失败");
        }
    }

    @Operation(summary = "删除用户风险评估记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUserRiskAssessment(
            @ApiParam(value = "评估记录ID", required = true) @PathVariable Long id) {
        try {
            boolean success = userRiskAssessmentService.deleteUserRiskAssessment(id);
            if (success) {
                return Result.successMsg("删除用户风险评估记录成功");
            } else {
                return Result.fail("删除用户风险评估记录失败");
            }
        } catch (Exception e) {
            log.error("删除用户风险评估记录失败: {}", e.getMessage(), e);
            return Result.fail("删除用户风险评估记录失败");
        }
    }
}
