package com.anti.fraud.modules.report.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.report.entity.ReportRecord;
import com.anti.fraud.modules.report.service.ReportRecordService;
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
 * 举报记录控制器
 */
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "举报记录管理")
public class ReportRecordController {

    private final ReportRecordService reportRecordService;

    @Operation(summary = "新增举报记录")
    @PostMapping("/add")
    public Result<Void> addReportRecord(@ApiParam(value = "举报记录信息", required = true) @RequestBody ReportRecord reportRecord) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }
            reportRecord.setUserId(userId);
            reportRecord.setUsername(SecurityUtils.getCurrentUsername());

            boolean success = reportRecordService.addReportRecord(reportRecord);
            if (success) {
                return Result.successMsg("新增举报记录成功");
            } else {
                return Result.fail("新增举报记录失败");
            }
        } catch (Exception e) {
            log.error("新增举报记录失败: {}", e.getMessage(), e);
            return Result.fail("新增举报记录失败");
        }
    }

    @Operation(summary = "更新举报记录")
    @PutMapping("/update")
    public Result<Void> updateReportRecord(@ApiParam(value = "举报记录信息", required = true) @RequestBody ReportRecord reportRecord) {
        try {
            boolean success = reportRecordService.updateReportRecord(reportRecord);
            if (success) {
                return Result.successMsg("更新举报记录成功");
            } else {
                return Result.fail("更新举报记录失败");
            }
        } catch (Exception e) {
            log.error("更新举报记录失败: {}", e.getMessage(), e);
            return Result.fail("更新举报记录失败");
        }
    }

    @Operation(summary = "删除举报记录")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteReportRecord(@ApiParam(value = "举报记录ID", required = true) @PathVariable Long id) {
        try {
            boolean success = reportRecordService.deleteReportRecord(id);
            if (success) {
                return Result.successMsg("删除举报记录成功");
            } else {
                return Result.fail("删除举报记录失败");
            }
        } catch (Exception e) {
            log.error("删除举报记录失败: {}", e.getMessage(), e);
            return Result.fail("删除举报记录失败");
        }
    }

    @Operation(summary = "获取举报记录详情")
    @GetMapping("/detail/{id}")
    public Result<ReportRecord> getReportRecordById(@ApiParam(value = "举报记录ID", required = true) @PathVariable Long id) {
        try {
            ReportRecord reportRecord = reportRecordService.getReportRecordById(id);
            if (reportRecord != null) {
                return Result.success(reportRecord);
            } else {
                return Result.fail("举报记录不存在");
            }
        } catch (Exception e) {
            log.error("获取举报记录详情失败: {}", e.getMessage(), e);
            return Result.fail("获取举报记录详情失败");
        }
    }

    @Operation(summary = "分页查询举报记录")
    @PostMapping("/list")
    public Result<Map<String, Object>> getReportRecordList(
            @ApiParam(value = "查询参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            Map<String, Object> result = reportRecordService.getReportRecordList(params, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询举报记录列表失败: {}", e.getMessage(), e);
            return Result.fail("查询举报记录列表失败");
        }
    }

    @Operation(summary = "根据用户ID查询举报记录")
    @GetMapping("/by-user")
    public Result<Map<String, Object>> getReportRecordsByUserId(
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }
            Map<String, Object> result = reportRecordService.getReportRecordsByUserId(userId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据用户ID查询举报记录失败: {}", e.getMessage(), e);
            return Result.fail("根据用户ID查询举报记录失败");
        }
    }

    @Operation(summary = "获取待处理的举报记录")
    @GetMapping("/pending")
    public Result<List<ReportRecord>> getPendingReports(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<ReportRecord> reportRecords = reportRecordService.getPendingReports(limit);
            return Result.success(reportRecords);
        } catch (Exception e) {
            log.error("获取待处理的举报记录失败: {}", e.getMessage(), e);
            return Result.fail("获取待处理的举报记录失败");
        }
    }

    @Operation(summary = "处理举报记录")
    @PostMapping("/handle")
    public Result<Void> handleReport(
            @ApiParam(value = "举报记录ID", required = true) @RequestParam Long id,
            @ApiParam(value = "处理状态: 2-已处理, 3-已驳回", required = true) @RequestParam Integer handleStatus,
            @ApiParam(value = "处理结果", required = true) @RequestParam String handleResult,
            @ApiParam(value = "积分奖励", required = false) @RequestParam(required = false) Integer rewardPoints) {
        try {
            String handler = SecurityUtils.getCurrentUsername();
            if (handler == null) {
                return Result.fail("请先登录");
            }

            boolean success = reportRecordService.handleReport(id, handleStatus, handleResult, handler, rewardPoints);
            if (success) {
                return Result.successMsg("处理举报记录成功");
            } else {
                return Result.fail("处理举报记录失败");
            }
        } catch (Exception e) {
            log.error("处理举报记录失败: {}", e.getMessage(), e);
            return Result.fail("处理举报记录失败");
        }
    }

    @Operation(summary = "统计举报记录信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getReportRecordStats() {
        try {
            Map<String, Object> stats = reportRecordService.getReportRecordStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计举报记录信息失败: {}", e.getMessage(), e);
            return Result.fail("统计举报记录信息失败");
        }
    }

    @Operation(summary = "获取用户举报统计信息")
    @GetMapping("/user-stats")
    public Result<Map<String, Object>> getUserReportStats() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }
            Map<String, Object> stats = reportRecordService.getUserReportStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户举报统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取用户举报统计信息失败");
        }
    }

    @Operation(summary = "获取用户获得的积分奖励")
    @GetMapping("/user-reward-points")
    public Result<Integer> getUserRewardPoints() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }
            Integer rewardPoints = reportRecordService.getUserRewardPoints(userId);
            return Result.success(rewardPoints);
        } catch (Exception e) {
            log.error("获取用户获得的积分奖励失败: {}", e.getMessage(), e);
            return Result.fail("获取用户获得的积分奖励失败");
        }
    }

    @Operation(summary = "按月份统计举报记录")
    @GetMapping("/monthly-stats")
    public Result<List<Map<String, Object>>> getMonthlyStats(
            @ApiParam(value = "月份数", required = true) @RequestParam Integer months) {
        try {
            List<Map<String, Object>> stats = reportRecordService.getMonthlyStats(months);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("按月份统计举报记录失败: {}", e.getMessage(), e);
            return Result.fail("按月份统计举报记录失败");
        }
    }

    @Operation(summary = "计算举报积分奖励")
    @PostMapping("/calculate-reward")
    public Result<Integer> calculateRewardPoints(
            @ApiParam(value = "举报类型", required = true) @RequestParam Integer reportType,
            @ApiParam(value = "举报内容", required = true) @RequestParam String reportContent,
            @ApiParam(value = "举报证据", required = false) @RequestParam(required = false) String evidence) {
        try {
            Integer rewardPoints = reportRecordService.calculateRewardPoints(reportType, reportContent, evidence);
            return Result.success(rewardPoints);
        } catch (Exception e) {
            log.error("计算举报积分奖励失败: {}", e.getMessage(), e);
            return Result.fail("计算举报积分奖励失败");
        }
    }
}
