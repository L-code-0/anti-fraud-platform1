package com.anti.fraud.modules.report.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.file.service.FileService;
import com.anti.fraud.modules.report.entity.Report;
import com.anti.fraud.modules.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 举报控制器
 */
@RestController
@RequestMapping("/api/report")
@Tag(name = "举报管理", description = "举报相关接口")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final FileService fileService;

    @PostMapping("/submit")
    @Operation(summary = "提交举报")
    public Result<?> submitReport(@RequestBody Report report) {
        reportService.submitReport(report);
        return Result.success("举报提交成功");
    }

    @PostMapping("/upload-evidence")
    @Operation(summary = "上传举报证据文件")
    public Result<?> uploadEvidenceFile(
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件类型: image, document, audio") @RequestParam String fileType) {
        return Result.success(fileService.uploadFile(file, "report_evidence", "report", null));
    }

    @PostMapping("/upload-evidence/batch")
    @Operation(summary = "批量上传举报证据文件")
    public Result<?> batchUploadEvidenceFiles(
            @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "文件类型: image, document, audio") @RequestParam String fileType) {
        return Result.success(fileService.batchUploadFiles(files, "report_evidence", "report", null));
    }

    @GetMapping("/list")
    @Operation(summary = "获取举报列表")
    public Result<?> getReportList() {
        return Result.success(reportService.getReportList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取举报详情")
    public Result<?> getReportDetail(@Parameter(description = "举报ID") @PathVariable Long id) {
        return Result.success(reportService.getReportDetail(id));
    }

    @GetMapping("/{id}/files")
    @Operation(summary = "获取举报证据文件")
    public Result<?> getReportFiles(@Parameter(description = "举报ID") @PathVariable Long id) {
        return Result.success(reportService.getReportFiles(id));
    }

    @PostMapping("/{id}/add-files")
    @Operation(summary = "为举报添加证据文件")
    public Result<?> addReportFiles(
            @Parameter(description = "举报ID") @PathVariable Long id,
            @RequestBody List<Long> fileIds) {
        reportService.addReportFiles(id, fileIds);
        return Result.success("添加证据文件成功");
    }

    @DeleteMapping("/{id}/files/{fileId}")
    @Operation(summary = "移除举报证据文件")
    public Result<?> removeReportFile(
            @Parameter(description = "举报ID") @PathVariable Long id,
            @Parameter(description = "文件ID") @PathVariable Long fileId) {
        reportService.removeReportFile(id, fileId);
        return Result.success("移除证据文件成功");
    }
}
