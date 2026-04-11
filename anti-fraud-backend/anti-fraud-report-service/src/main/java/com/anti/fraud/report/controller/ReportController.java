package com.anti.fraud.report.controller;

import com.anti.fraud.report.entity.Report;
import com.anti.fraud.report.service.ReportService;
import com.anti.fraud.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 举报Controller
 */
@RestController
@RequestMapping("/api/report")
@Tag(name = "举报管理", description = "举报相关接口")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 提交举报
     */
    @PostMapping("/submit")
    @Operation(summary = "提交举报", description = "提交诈骗举报信息")
    public Result<Long> submitReport(@RequestBody Report report) {
        Long reportId = reportService.submitReport(report);
        return Result.success(reportId);
    }

    /**
     * 获取用户举报历史
     */
    @GetMapping("/history")
    @Operation(summary = "获取举报历史", description = "获取用户的举报历史记录")
    public Result<List<Report>> getReportHistory(@RequestParam Long userId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        List<Report> reports = reportService.getUserReportHistory(userId, page, size);
        return Result.success(reports);
    }

    /**
     * 智能分类举报
     */
    @PostMapping("/classify")
    @Operation(summary = "智能分类", description = "根据举报描述自动分类诈骗类型")
    public Result<String> autoClassify(@RequestParam String description) {
        String classification = reportService.autoClassify(description);
        return Result.success(classification);
    }

    /**
     * 处理举报
     */
    @PutMapping("/process/{id}")
    @Operation(summary = "处理举报", description = "处理用户举报")
    public Result<Boolean> processReport(@PathVariable Long id, @RequestParam Long handlerId, @RequestParam String result) {
        boolean success = reportService.processReport(id, handlerId, result);
        return Result.success(success);
    }

    /**
     * 获取举报统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "举报统计", description = "获取用户的举报统计信息")
    public Result<ReportService.ReportStatistics> getReportStatistics(@RequestParam Long userId) {
        ReportService.ReportStatistics statistics = reportService.getReportStatistics(userId);
        return Result.success(statistics);
    }
}
