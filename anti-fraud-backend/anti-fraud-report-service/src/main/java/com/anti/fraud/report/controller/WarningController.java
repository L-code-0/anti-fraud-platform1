package com.anti.fraud.report.controller;

import com.anti.fraud.report.entity.Warning;
import com.anti.fraud.report.service.WarningService;
import com.anti.fraud.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预警Controller
 */
@RestController
@RequestMapping("/api/warning")
@Tag(name = "预警管理", description = "预警相关接口")
public class WarningController {

    @Autowired
    private WarningService warningService;

    /**
     * 发布预警
     */
    @PostMapping("/publish")
    @Operation(summary = "发布预警", description = "发布诈骗预警信息")
    public Result<Long> publishWarning(@RequestBody Warning warning) {
        Long warningId = warningService.publishWarning(warning);
        return Result.success(warningId);
    }

    /**
     * 获取最新预警
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新预警", description = "获取最新的诈骗预警信息")
    public Result<List<Warning>> getLatestWarnings(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        List<Warning> warnings = warningService.getLatestWarnings(page, size);
        return Result.success(warnings);
    }

    /**
     * 根据类型获取预警
     */
    @GetMapping("/by-type")
    @Operation(summary = "按类型获取预警", description = "根据诈骗类型获取预警信息")
    public Result<List<Warning>> getWarningsByType(@RequestParam String type, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        List<Warning> warnings = warningService.getWarningsByType(type, page, size);
        return Result.success(warnings);
    }

    /**
     * 根据等级获取预警
     */
    @GetMapping("/by-level")
    @Operation(summary = "按等级获取预警", description = "根据预警等级获取预警信息")
    public Result<List<Warning>> getWarningsByLevel(@RequestParam String level, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        List<Warning> warnings = warningService.getWarningsByLevel(level, page, size);
        return Result.success(warnings);
    }

    /**
     * 获取预警详情
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "获取预警详情", description = "获取预警的详细信息")
    public Result<Warning> getWarningDetail(@PathVariable Long id) {
        Warning warning = warningService.getWarningDetail(id);
        return Result.success(warning);
    }

    /**
     * 更新预警状态
     */
    @PutMapping("/status/{id}")
    @Operation(summary = "更新预警状态", description = "更新预警的状态")
    public Result<Boolean> updateWarningStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = warningService.updateWarningStatus(id, status);
        return Result.success(success);
    }

    /**
     * 获取预警统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "预警统计", description = "获取预警的统计信息")
    public Result<WarningService.WarningStatistics> getWarningStatistics() {
        WarningService.WarningStatistics statistics = warningService.getWarningStatistics();
        return Result.success(statistics);
    }
}
