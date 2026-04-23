package com.anti.fraud.modules.report.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.report.entity.Report;
import com.anti.fraud.modules.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 举报控制器
 */
@RestController
@RequestMapping("/api/report")
@Tag(name = "举报管理", description = "举报相关接口")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/submit")
    @Operation(summary = "提交举报")
    public Result<?> submitReport(@RequestBody Report report) {
        reportService.submitReport(report);
        return Result.success("举报提交成功");
    }

    @GetMapping("/list")
    @Operation(summary = "获取举报列表")
    public Result<?> getReportList() {
        return Result.success(reportService.getReportList());
    }
}
