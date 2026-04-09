package com.anti.fraud.modules.report.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.report.dto.ReportSubmitDTO;
import com.anti.fraud.modules.report.service.ReportService;
import com.anti.fraud.modules.report.vo.ReportVO;
import com.anti.fraud.modules.report.vo.WarningVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "举报预警", description = "举报预警相关接口")
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "提交举报", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/submit")
    public Result<Void> submitReport(@RequestBody ReportSubmitDTO submitDTO) {
        reportService.submitReport(submitDTO);
        return Result.successMsg("举报提交成功，感谢您的参与");
    }

    @Operation(summary = "获取我的举报记录", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/my-reports")
    public Result<Page<ReportVO>> getMyReports(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reportService.getMyReports(page, size));
    }

    @Operation(summary = "获取举报详情", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/{id}")
    public Result<ReportVO> getReportDetail(@PathVariable Long id) {
        return Result.success(reportService.getReportDetail(id));
    }

    @Operation(summary = "获取预警列表")
    @GetMapping("/warnings")
    public Result<List<WarningVO>> getWarningList() {
        return Result.success(reportService.getWarningList());
    }

    @Operation(summary = "获取预警详情")
    @GetMapping("/warnings/{id}")
    public Result<WarningVO> getWarningDetail(@PathVariable Long id) {
        return Result.success(reportService.getWarningDetail(id));
    }

    @Operation(summary = "获取最新预警")
    @GetMapping("/warnings/latest")
    public Result<List<WarningVO>> getLatestWarnings() {
        return Result.success(reportService.getLatestWarnings());
    }
}