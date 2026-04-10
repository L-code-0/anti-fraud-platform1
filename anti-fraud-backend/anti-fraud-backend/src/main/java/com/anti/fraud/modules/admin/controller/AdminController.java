package com.anti.fraud.modules.admin.controller;

import com.anti.fraud.common.enums.UserRole;
import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.admin.service.AdminService;
import com.anti.fraud.modules.admin.vo.DashboardStatsVO;
import com.anti.fraud.modules.admin.vo.DepartmentStatsVO;
import com.anti.fraud.modules.admin.vo.FraudTypeStatsVO;
import com.anti.fraud.modules.admin.vo.TrendDataVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台管理控制器
 * <p>
 * 提供管理后台的各类统计和数据分析接口。
 * 所有接口需要管理员或超级管理员权限。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 */
@Tag(name = "后台管理", description = "后台管理相关接口")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    /**
     * 获取仪表盘统计数据
     * <p>
     * 返回管理后台首页所需的各类统计数据，包括用户总数、活动数量、测试参与情况等。
     * </p>
     *
     * @return 仪表盘统计数据
     */
    @Operation(summary = "获取仪表盘统计数据", 
               description = "返回管理后台首页所需的各类统计数据")
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<DashboardStatsVO> getDashboardStats() {
        log.debug("获取仪表盘统计数据");
        return Result.success(adminService.getDashboardStats());
    }

    /**
     * 获取用户增长趋势
     * <p>
     * 返回指定天数内的用户增长趋势数据。
     * </p>
     *
     * @param days 统计天数，默认7天
     * @return 用户增长趋势数据列表
     */
    @Operation(summary = "获取用户增长趋势", 
               description = "获取最近N天的用户增长趋势数据")
    @GetMapping("/dashboard/user-trend")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<List<TrendDataVO>> getUserTrend(
            @Parameter(description = "统计天数") @RequestParam(defaultValue = "7") Integer days) {
        log.debug("获取用户增长趋势: days={}", days);
        return Result.success(adminService.getUserTrend(days));
    }

    /**
     * 获取测试参与趋势
     * <p>
     * 返回指定天数内的测试参与趋势数据。
     * </p>
     *
     * @param days 统计天数，默认7天
     * @return 测试参与趋势数据列表
     */
    @Operation(summary = "获取测试参与趋势", 
               description = "获取最近N天的测试参与趋势数据")
    @GetMapping("/dashboard/test-trend")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<List<TrendDataVO>> getTestTrend(
            @Parameter(description = "统计天数") @RequestParam(defaultValue = "7") Integer days) {
        log.debug("获取测试参与趋势: days={}", days);
        return Result.success(adminService.getTestTrend(days));
    }

    /**
     * 获取知识分类统计
     * <p>
     * 返回各知识分类的学习统计数据。
     * </p>
     *
     * @return 知识分类统计数据列表
     */
    @Operation(summary = "获取知识分类统计", 
               description = "获取各知识分类的学习统计数据")
    @GetMapping("/dashboard/knowledge-stats")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<List<TrendDataVO>> getKnowledgeStats() {
        log.debug("获取知识分类统计");
        return Result.success(adminService.getKnowledgeStats());
    }

    /**
     * 获取各院系学习参与率
     * <p>
     * 返回各院系学生的学习参与统计数据。
     * </p>
     *
     * @return 各院系统计数据列表
     */
    @Operation(summary = "获取各院系学习参与率", 
               description = "获取各院系学生的学习参与统计数据")
    @GetMapping("/dashboard/department-stats")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<List<DepartmentStatsVO>> getDepartmentStats() {
        log.debug("获取各院系学习参与率");
        return Result.success(adminService.getDepartmentStats());
    }

    /**
     * 获取高发诈骗类型统计
     * <p>
     * 返回平台统计的高发诈骗类型排名数据。
     * </p>
     *
     * @return 诈骗类型统计数据列表
     */
    @Operation(summary = "获取高发诈骗类型统计", 
               description = "获取高发诈骗类型排名统计数据")
    @GetMapping("/dashboard/fraud-type-stats")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<List<FraudTypeStatsVO>> getFraudTypeStats() {
        log.debug("获取高发诈骗类型统计");
        return Result.success(adminService.getFraudTypeStats());
    }

    /**
     * 获取举报处理时效
     * <p>
     * 返回举报处理的平均时效统计数据。
     * </p>
     *
     * @return 举报处理时效数据
     */
    @Operation(summary = "获取举报处理时效", 
               description = "获取举报处理的平均时效统计")
    @GetMapping("/dashboard/report-efficiency")
    @PreAuthorize("hasRole('" + UserRole.ROLE_ADMIN + "') or hasRole('" + UserRole.ROLE_SUPER_ADMIN + "')")
    public Result<TrendDataVO> getReportEfficiency() {
        log.debug("获取举报处理时效");
        return Result.success(adminService.getReportEfficiency());
    }
}
