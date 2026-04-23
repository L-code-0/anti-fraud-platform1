package com.anti.fraud.modules.dashboard.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.dashboard.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 数据大屏控制器
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "数据大屏管理")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "获取实时数据大屏首页数据")
    @GetMapping("/home")
    public Result<Map<String, Object>> getDashboardHomeData() {
        try {
            Map<String, Object> data = dashboardService.getDashboardHomeData();
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取数据大屏首页数据失败: {}", e.getMessage(), e);
            return Result.fail("获取数据大屏首页数据失败");
        }
    }

    @Operation(summary = "获取用户统计数据")
    @GetMapping("/user-stats")
    public Result<Map<String, Object>> getUserStats() {
        try {
            Map<String, Object> stats = dashboardService.getUserStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户统计数据失败: {}", e.getMessage(), e);
            return Result.fail("获取用户统计数据失败");
        }
    }

    @Operation(summary = "获取打卡统计数据")
    @GetMapping("/checkin-stats")
    public Result<Map<String, Object>> getCheckinStats() {
        try {
            Map<String, Object> stats = dashboardService.getCheckinStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取打卡统计数据失败: {}", e.getMessage(), e);
            return Result.fail("获取打卡统计数据失败");
        }
    }

    @Operation(summary = "获取演练统计数据")
    @GetMapping("/exercise-stats")
    public Result<Map<String, Object>> getExerciseStats() {
        try {
            Map<String, Object> stats = dashboardService.getExerciseStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取演练统计数据失败: {}", e.getMessage(), e);
            return Result.fail("获取演练统计数据失败");
        }
    }

    @Operation(summary = "获取举报统计数据")
    @GetMapping("/report-stats")
    public Result<Map<String, Object>> getReportStats() {
        try {
            Map<String, Object> stats = dashboardService.getReportStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取举报统计数据失败: {}", e.getMessage(), e);
            return Result.fail("获取举报统计数据失败");
        }
    }

    @Operation(summary = "获取案例统计数据")
    @GetMapping("/case-stats")
    public Result<Map<String, Object>> getCaseStats() {
        try {
            Map<String, Object> stats = dashboardService.getCaseStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取案例统计数据失败: {}", e.getMessage(), e);
            return Result.fail("获取案例统计数据失败");
        }
    }

    @Operation(summary = "获取题目统计数据")
    @GetMapping("/question-stats")
    public Result<Map<String, Object>> getQuestionStats() {
        try {
            Map<String, Object> stats = dashboardService.getQuestionStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取题目统计数据失败: {}", e.getMessage(), e);
            return Result.fail("获取题目统计数据失败");
        }
    }

    @Operation(summary = "获取访问统计数据")
    @GetMapping("/access-stats")
    public Result<List<Map<String, Object>>> getAccessStats(
            @ApiParam(value = "天数", required = true) @RequestParam Integer days) {
        try {
            List<Map<String, Object>> stats = dashboardService.getAccessStats(days);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取访问统计数据失败: {}", e.getMessage(), e);
            return Result.fail("获取访问统计数据失败");
        }
    }

    @Operation(summary = "获取趋势统计数据")
    @GetMapping("/trend-stats")
    public Result<List<Map<String, Object>>> getTrendStats(
            @ApiParam(value = "统计类型: user, checkin, exercise, report", required = true) @RequestParam String type,
            @ApiParam(value = "天数", required = true) @RequestParam Integer days) {
        try {
            List<Map<String, Object>> stats = dashboardService.getTrendStats(type, days);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取趋势统计数据失败: {}", e.getMessage(), e);
            return Result.fail("获取趋势统计数据失败");
        }
    }

    @Operation(summary = "获取排行榜数据")
    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> getRankingData(
            @ApiParam(value = "排行榜类型: checkin, exercise, report, points", required = true) @RequestParam String type,
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<Map<String, Object>> ranking = dashboardService.getRankingData(type, limit);
            return Result.success(ranking);
        } catch (Exception e) {
            log.error("获取排行榜数据失败: {}", e.getMessage(), e);
            return Result.fail("获取排行榜数据失败");
        }
    }

    @Operation(summary = "获取系统运行状态")
    @GetMapping("/system-status")
    public Result<Map<String, Object>> getSystemStatus() {
        try {
            Map<String, Object> status = dashboardService.getSystemStatus();
            return Result.success(status);
        } catch (Exception e) {
            log.error("获取系统运行状态失败: {}", e.getMessage(), e);
            return Result.fail("获取系统运行状态失败");
        }
    }
}
