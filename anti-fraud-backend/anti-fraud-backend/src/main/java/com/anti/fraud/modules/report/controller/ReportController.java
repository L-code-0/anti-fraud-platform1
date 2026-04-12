package com.anti.fraud.modules.report.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.report.entity.Report;
import com.anti.fraud.modules.report.entity.ReportProgress;
import com.anti.fraud.modules.report.service.ReportService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "举报管理")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "创建举报")
    @PostMapping("/create")
    public Result<Void> createReport(@RequestBody Report report) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        report.setUserId(userId);
        report.setUserName(SecurityUtils.getCurrentUserName());
        report.setCreatedBy(SecurityUtils.getCurrentUserName());
        report.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = reportService.createReport(report);
            if (success) {
                return Result.successMsg("创建举报成功");
            } else {
                return Result.fail("创建举报失败");
            }
        } catch (Exception e) {
            log.error("创建举报失败: {}", e.getMessage(), e);
            return Result.fail("创建举报失败");
        }
    }

    @Operation(summary = "更新举报")
    @PostMapping("/update")
    public Result<Void> updateReport(@RequestBody Report report) {
        report.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = reportService.updateReport(report);
            if (success) {
                return Result.successMsg("更新举报成功");
            } else {
                return Result.fail("更新举报失败");
            }
        } catch (Exception e) {
            log.error("更新举报失败: {}", e.getMessage(), e);
            return Result.fail("更新举报失败");
        }
    }

    @Operation(summary = "删除举报")
    @PostMapping("/delete/{id}")
    public Result<Void> deleteReport(@PathVariable Long id) {
        try {
            boolean success = reportService.deleteReport(id);
            if (success) {
                return Result.successMsg("删除举报成功");
            } else {
                return Result.fail("删除举报失败");
            }
        } catch (Exception e) {
            log.error("删除举报失败: {}", e.getMessage(), e);
            return Result.fail("删除举报失败");
        }
    }

    @Operation(summary = "获取举报详情")
    @GetMapping("/detail/{id}")
    public Result<Report> getReportById(@PathVariable Long id) {
        try {
            Report report = reportService.getReportById(id);
            if (report != null) {
                return Result.success("获取举报详情成功", report);
            } else {
                return Result.fail("举报不存在");
            }
        } catch (Exception e) {
            log.error("获取举报详情失败: {}", e.getMessage(), e);
            return Result.fail("获取举报详情失败");
        }
    }

    @Operation(summary = "获取举报列表")
    @GetMapping("/list")
    public Result<List<Report>> getReportList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String reportType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Report> reports = reportService.getReportList(status, reportType, page, size);
            return Result.success("获取举报列表成功", reports);
        } catch (Exception e) {
            log.error("获取举报列表失败: {}", e.getMessage(), e);
            return Result.fail("获取举报列表失败");
        }
    }

    @Operation(summary = "获取用户举报历史")
    @GetMapping("/history")
    public Result<List<Report>> getUserReportHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<Report> reports = reportService.getUserReportHistory(userId, page, size);
            return Result.success("获取用户举报历史成功", reports);
        } catch (Exception e) {
            log.error("获取用户举报历史失败: {}", e.getMessage(), e);
            return Result.fail("获取用户举报历史失败");
        }
    }

    @Operation(summary = "处理举报")
    @PostMapping("/handle")
    public Result<Void> handleReport(@RequestBody Map<String, Object> data) {
        Long id = Long.valueOf(data.get("id").toString());
        String status = (String) data.get("status");
        String feedback = (String) data.get("feedback");
        Long handlerId = SecurityUtils.getCurrentUserId();
        String handlerName = SecurityUtils.getCurrentUserName();

        try {
            boolean success = reportService.handleReport(id, status, feedback, handlerId, handlerName);
            if (success) {
                return Result.successMsg("处理举报成功");
            } else {
                return Result.fail("处理举报失败");
            }
        } catch (Exception e) {
            log.error("处理举报失败: {}", e.getMessage(), e);
            return Result.fail("处理举报失败");
        }
    }

    @Operation(summary = "获取举报进度")
    @GetMapping("/progress/{reportId}")
    public Result<List<ReportProgress>> getReportProgress(@PathVariable Long reportId) {
        try {
            List<ReportProgress> progress = reportService.getReportProgress(reportId);
            return Result.success("获取举报进度成功", progress);
        } catch (Exception e) {
            log.error("获取举报进度失败: {}", e.getMessage(), e);
            return Result.fail("获取举报进度失败");
        }
    }

    @Operation(summary = "添加举报进度")
    @PostMapping("/progress/add")
    public Result<Void> addReportProgress(@RequestBody ReportProgress progress) {
        progress.setHandlerId(SecurityUtils.getCurrentUserId());
        progress.setHandlerName(SecurityUtils.getCurrentUserName());

        try {
            boolean success = reportService.addReportProgress(progress);
            if (success) {
                return Result.successMsg("添加举报进度成功");
            } else {
                return Result.fail("添加举报进度失败");
            }
        } catch (Exception e) {
            log.error("添加举报进度失败: {}", e.getMessage(), e);
            return Result.fail("添加举报进度失败");
        }
    }

    @Operation(summary = "获取用户举报积分")
    @GetMapping("/points")
    public Result<Map<String, Object>> getUserReportPoints() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Map<String, Object> points = reportService.getUserReportPoints(userId);
            return Result.success("获取用户举报积分成功", points);
        } catch (Exception e) {
            log.error("获取用户举报积分失败: {}", e.getMessage(), e);
            return Result.fail("获取用户举报积分失败");
        }
    }

    @Operation(summary = "获取举报数据分析")
    @GetMapping("/analysis")
    public Result<Map<String, Object>> getReportAnalysis(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String reportType) {
        try {
            Map<String, Object> analysis = reportService.getReportAnalysis(startTime, endTime, reportType);
            return Result.success("获取举报数据分析成功", analysis);
        } catch (Exception e) {
            log.error("获取举报数据分析失败: {}", e.getMessage(), e);
            return Result.fail("获取举报数据分析失败");
        }
    }

    @Operation(summary = "搜索举报")
    @GetMapping("/search")
    public Result<List<Report>> searchReports(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Report> reports = reportService.searchReports(keyword, page, size);
            return Result.success("搜索举报成功", reports);
        } catch (Exception e) {
            log.error("搜索举报失败: {}", e.getMessage(), e);
            return Result.fail("搜索举报失败");
        }
    }

    @Operation(summary = "批量处理举报")
    @PostMapping("/batch-handle")
    public Result<Map<String, Object>> batchHandleReports(@RequestBody Map<String, Object> data) {
        List<Long> ids = (List<Long>) data.get("ids");
        String status = (String) data.get("status");
        String feedback = (String) data.get("feedback");
        Long handlerId = SecurityUtils.getCurrentUserId();
        String handlerName = SecurityUtils.getCurrentUserName();

        try {
            Map<String, Object> result = reportService.batchHandleReports(ids, status, feedback, handlerId, handlerName);
            if ((boolean) result.get("success")) {
                return Result.success("批量处理举报成功", result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("批量处理举报失败: {}", e.getMessage(), e);
            return Result.fail("批量处理举报失败");
        }
    }
}
