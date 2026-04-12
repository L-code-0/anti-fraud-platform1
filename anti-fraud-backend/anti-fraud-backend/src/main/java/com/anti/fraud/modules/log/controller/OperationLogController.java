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

/**
 * 操作日志控制器
 */
@Tag(name = "操作日志管理", description = "操作日志相关操作")
@RestController
@RequestMapping("/api/log/operation")
@RequiredArgsConstructor
public class OperationLogController {
    
    private final OperationLogService operationLogService;
    
    @Operation(summary = "获取操作日志列表")
    @GetMapping("/list")
    public Result<IPage<OperationLog>> getOperationLogList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        IPage<OperationLog> result = operationLogService.getOperationLogs(
                page, size, username, actionType, startTime, endTime
        );
        return Result.success(result);
    }
    
    @Operation(summary = "获取操作日志详情")
    @GetMapping("/{id}")
    public Result<OperationLog> getOperationLogDetail(
            @Parameter(description = "日志ID") @PathVariable Long id
    ) {
        OperationLog log = operationLogService.getOperationLogById(id);
        return Result.success(log);
    }
    
    @Operation(summary = "清理旧日志")
    @DeleteMapping("/clean")
    public Result<Integer> cleanOldLogs(
            @RequestParam(defaultValue = "30") Integer days
    ) {
        int count = operationLogService.cleanOldLogs(days);
        return Result.success(count);
    }
}
