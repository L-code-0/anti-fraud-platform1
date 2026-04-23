package com.anti.fraud.modules.audit.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.audit.entity.AuditLog;
import com.anti.fraud.modules.audit.service.AuditLogService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作审计日志控制器
 */
@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "操作审计日志管理")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Operation(summary = "记录审计日志")
    @PostMapping("/add")
    public Result<Void> addAuditLog(@ApiParam(value = "审计日志信息", required = true) @RequestBody AuditLog auditLog) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId != null) {
                auditLog.setUserId(userId);
                auditLog.setUsername(SecurityUtils.getCurrentUsername());
            }
            boolean success = auditLogService.addAuditLog(auditLog);
            if (success) {
                return Result.successMsg("记录审计日志成功");
            } else {
                return Result.fail("记录审计日志失败");
            }
        } catch (Exception e) {
            log.error("记录审计日志失败: {}", e.getMessage(), e);
            return Result.fail("记录审计日志失败");
        }
    }

    @Operation(summary = "删除审计日志")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteAuditLog(@ApiParam(value = "审计日志ID", required = true) @PathVariable Long id) {
        try {
            boolean success = auditLogService.deleteAuditLog(id);
            if (success) {
                return Result.successMsg("删除审计日志成功");
            } else {
                return Result.fail("删除审计日志失败");
            }
        } catch (Exception e) {
            log.error("删除审计日志失败: {}", e.getMessage(), e);
            return Result.fail("删除审计日志失败");
        }
    }

    @Operation(summary = "清理指定时间之前的审计日志")
    @PostMapping("/clean")
    public Result<Integer> cleanAuditLogs(
            @ApiParam(value = "清理时间", required = true) @RequestParam String beforeTime) {
        try {
            LocalDateTime time = LocalDateTime.parse(beforeTime);
            int count = auditLogService.cleanAuditLogs(time);
            return Result.success(count);
        } catch (Exception e) {
            log.error("清理审计日志失败: {}", e.getMessage(), e);
            return Result.fail("清理审计日志失败");
        }
    }

    @Operation(summary = "获取审计日志详情")
    @GetMapping("/detail/{id}")
    public Result<AuditLog> getAuditLogById(@ApiParam(value = "审计日志ID", required = true) @PathVariable Long id) {
        try {
            AuditLog auditLog = auditLogService.getAuditLogById(id);
            if (auditLog != null) {
                return Result.success(auditLog);
            } else {
                return Result.fail("审计日志不存在");
            }
        } catch (Exception e) {
            log.error("获取审计日志详情失败: {}", e.getMessage(), e);
            return Result.fail("获取审计日志详情失败");
        }
    }

    @Operation(summary = "分页查询审计日志")
    @PostMapping("/list")
    public Result<Map<String, Object>> getAuditLogList(
            @ApiParam(value = "查询参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            Map<String, Object> result = auditLogService.getAuditLogList(params, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询审计日志列表失败: {}", e.getMessage(), e);
            return Result.fail("查询审计日志列表失败");
        }
    }

    @Operation(summary = "根据用户ID查询审计日志")
    @GetMapping("/by-user")
    public Result<Map<String, Object>> getAuditLogsByUserId(
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.fail("请先登录");
            }
            Map<String, Object> result = auditLogService.getAuditLogsByUserId(userId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据用户ID查询审计日志失败: {}", e.getMessage(), e);
            return Result.fail("根据用户ID查询审计日志失败");
        }
    }

    @Operation(summary = "获取最近的审计日志")
    @GetMapping("/latest")
    public Result<List<AuditLog>> getLatestAuditLogs(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<AuditLog> auditLogs = auditLogService.getLatestAuditLogs(limit);
            return Result.success(auditLogs);
        } catch (Exception e) {
            log.error("获取最近的审计日志失败: {}", e.getMessage(), e);
            return Result.fail("获取最近的审计日志失败");
        }
    }

    @Operation(summary = "获取失败的审计日志")
    @GetMapping("/failed")
    public Result<List<AuditLog>> getFailedAuditLogs(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<AuditLog> auditLogs = auditLogService.getFailedAuditLogs(limit);
            return Result.success(auditLogs);
        } catch (Exception e) {
            log.error("获取失败的审计日志失败: {}", e.getMessage(), e);
            return Result.fail("获取失败的审计日志失败");
        }
    }

    @Operation(summary = "统计审计日志信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getAuditLogStats() {
        try {
            Map<String, Object> stats = auditLogService.getAuditLogStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计审计日志信息失败: {}", e.getMessage(), e);
            return Result.fail("统计审计日志信息失败");
        }
    }

    @Operation(summary = "按日期统计审计日志")
    @GetMapping("/daily-stats")
    public Result<List<Map<String, Object>>> getDailyAuditLogStats(
            @ApiParam(value = "天数", required = true) @RequestParam Integer days) {
        try {
            List<Map<String, Object>> stats = auditLogService.getDailyAuditLogStats(days);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("按日期统计审计日志失败: {}", e.getMessage(), e);
            return Result.fail("按日期统计审计日志失败");
        }
    }
}
