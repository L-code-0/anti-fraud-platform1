package com.anti.fraud.modules.admin.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.admin.service.AdminService;
import com.anti.fraud.modules.admin.vo.DashboardStatsVO;
import com.anti.fraud.modules.admin.vo.DepartmentStatsVO;
import com.anti.fraud.modules.admin.vo.FraudTypeStatsVO;
import com.anti.fraud.modules.admin.vo.TrendDataVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台管理", description = "后台管理相关接口")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "获取仪表盘统计数据", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<DashboardStatsVO> getDashboardStats() {
        return Result.success(adminService.getDashboardStats());
    }

    @Operation(summary = "获取用户增长趋势", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/dashboard/user-trend")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<List<TrendDataVO>> getUserTrend(@RequestParam(defaultValue = "7") Integer days) {
        return Result.success(adminService.getUserTrend(days));
    }

    @Operation(summary = "获取测试参与趋势", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/dashboard/test-trend")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<List<TrendDataVO>> getTestTrend(@RequestParam(defaultValue = "7") Integer days) {
        return Result.success(adminService.getTestTrend(days));
    }

    @Operation(summary = "获取知识分类统计", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/dashboard/knowledge-stats")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<List<TrendDataVO>> getKnowledgeStats() {
        return Result.success(adminService.getKnowledgeStats());
    }

    @Operation(summary = "获取各院系学习参与率", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/dashboard/department-stats")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<List<DepartmentStatsVO>> getDepartmentStats() {
        return Result.success(adminService.getDepartmentStats());
    }

    @Operation(summary = "获取高发诈骗类型统计", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/dashboard/fraud-type-stats")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<List<FraudTypeStatsVO>> getFraudTypeStats() {
        return Result.success(adminService.getFraudTypeStats());
    }

    @Operation(summary = "获取举报处理时效", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/dashboard/report-efficiency")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<TrendDataVO> getReportEfficiency() {
        return Result.success(adminService.getReportEfficiency());
    }
}
