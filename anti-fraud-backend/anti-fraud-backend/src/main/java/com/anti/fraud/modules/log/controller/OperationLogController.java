package com.anti.fraud.modules.log.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.log.entity.OperationLog;
import com.anti.fraud.modules.log.service.OperationLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 操作日志控制器
 */
@Tag(name = "操作日志", description = "系统操作日志相关接口")
@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class OperationLogController {
    
    private final OperationLogService operationLogService;
    
    @Operation(summary = "分页查询操作日志")
    @GetMapping("/list")
    public Result<IPage<OperationLog>> getLogPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String moduleName,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        IPage<OperationLog> result = operationLogService.getLogPage(page, size, userId, operationType,
                moduleName, keyword, startTime, endTime);
        return Result.success(result);
    }
    
    @Operation(summary = "获取日志详情")
    @GetMapping("/{id}")
    public Result<OperationLog> getLogById(
            @Parameter(description = "日志ID") @PathVariable Long id
    ) {
        OperationLog log = operationLogService.getLogById(id);
        return Result.success(log);
    }
    
    @Operation(summary = "删除日志")
    @DeleteMapping("/{id}")
    public Result<Void> deleteLog(
            @Parameter(description = "日志ID") @PathVariable Long id
    ) {
        operationLogService.deleteLog(id);
        return Result.success();
    }
    
    @Operation(summary = "批量删除日志")
    @DeleteMapping("/batch")
    public Result<Void> deleteLogs(
            @RequestBody Long[] ids
    ) {
        operationLogService.deleteLogs(ids);
        return Result.success();
    }
    
    @Operation(summary = "清理旧日志")
    @DeleteMapping("/clean")
    public Result<Map<String, Object>> cleanOldLogs(
            @Parameter(description = "保留天数") @RequestParam(defaultValue = "30") int days
    ) {
        int count = operationLogService.cleanOldLogs(days);
        return Result.success(Map.of("count", count, "message", "清理成功"));
    }
    
    @Operation(summary = "获取操作统计")
    @GetMapping("/stats")
    public Result<Object> getOperationStats(
            @Parameter(description = "统计天数") @RequestParam(defaultValue = "7") int days
    ) {
        Object stats = operationLogService.getOperationStats(days);
        return Result.success(stats);
    }
}
