package com.anti.fraud.modules.warning.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.warning.service.ExternalWarningService;
import com.anti.fraud.modules.warning.vo.ExternalWarningVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 外部反诈预警控制器
 */
@RestController
@RequestMapping("/warning")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "反诈预警服务")
public class ExternalWarningController {

    private final ExternalWarningService externalWarningService;

    @Operation(summary = "获取最新的预警信息")
    @GetMapping("/latest")
    public Result<List<ExternalWarningVO>> getLatestWarnings(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<ExternalWarningVO> warnings = externalWarningService.getLatestWarnings(limit);
            return Result.success(warnings);
        } catch (Exception e) {
            log.error("获取最新预警信息失败: {}", e.getMessage(), e);
            return Result.fail("获取最新预警信息失败");
        }
    }

    @Operation(summary = "根据预警类型获取预警信息")
    @GetMapping("/by-type")
    public Result<List<ExternalWarningVO>> getWarningsByType(
            @ApiParam(value = "预警类型：1-电信诈骗，2-网络诈骗，3-金融诈骗，4-其他", required = true) @RequestParam Integer warningType,
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<ExternalWarningVO> warnings = externalWarningService.getWarningsByType(warningType, limit);
            return Result.success(warnings);
        } catch (Exception e) {
            log.error("根据类型获取预警信息失败: {}", e.getMessage(), e);
            return Result.fail("根据类型获取预警信息失败");
        }
    }

    @Operation(summary = "根据来源获取预警信息")
    @GetMapping("/by-source")
    public Result<List<ExternalWarningVO>> getWarningsBySource(
            @ApiParam(value = "预警来源", required = true) @RequestParam String source,
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit) {
        try {
            List<ExternalWarningVO> warnings = externalWarningService.getWarningsBySource(source, limit);
            return Result.success(warnings);
        } catch (Exception e) {
            log.error("根据来源获取预警信息失败: {}", e.getMessage(), e);
            return Result.fail("根据来源获取预警信息失败");
        }
    }

    @Operation(summary = "获取预警信息详情")
    @GetMapping("/detail/{id}")
    public Result<ExternalWarningVO> getWarningById(
            @ApiParam(value = "预警ID", required = true) @PathVariable Long id) {
        try {
            ExternalWarningVO warning = externalWarningService.getWarningById(id);
            return Result.success(warning);
        } catch (Exception e) {
            log.error("获取预警信息详情失败: {}", e.getMessage(), e);
            return Result.fail("获取预警信息详情失败");
        }
    }

    @Operation(summary = "手动同步预警信息")
    @PostMapping("/sync")
    public Result<Integer> manualSyncWarnings(
            @ApiParam(value = "预警来源", required = true) @RequestParam String source) {
        try {
            int syncCount = externalWarningService.manualSyncWarnings(source);
            return Result.success(syncCount);
        } catch (Exception e) {
            log.error("手动同步预警信息失败: {}", e.getMessage(), e);
            return Result.fail("手动同步预警信息失败");
        }
    }

    @Operation(summary = "同步所有预警信息")
    @PostMapping("/sync-all")
    public Result<Integer> syncAllWarnings() {
        try {
            int syncCount = externalWarningService.syncExternalWarnings();
            return Result.success(syncCount);
        } catch (Exception e) {
            log.error("同步所有预警信息失败: {}", e.getMessage(), e);
            return Result.fail("同步所有预警信息失败");
        }
    }

    @Operation(summary = "获取预警信息统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getWarningStatistics() {
        try {
            Map<String, Object> statistics = externalWarningService.getWarningStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取预警信息统计失败: {}", e.getMessage(), e);
            return Result.fail("获取预警信息统计失败");
        }
    }

    @Operation(summary = "测试预警信息接口连接")
    @GetMapping("/test-connection")
    public Result<Boolean> testConnection(
            @ApiParam(value = "预警来源", required = true) @RequestParam String source) {
        try {
            boolean result = externalWarningService.testConnection(source);
            return Result.success(result);
        } catch (Exception e) {
            log.error("测试连接失败: {}", e.getMessage(), e);
            return Result.fail("测试连接失败");
        }
    }

    @Operation(summary = "清理过期预警信息")
    @PostMapping("/clean-expired")
    public Result<Integer> cleanExpiredWarnings() {
        try {
            int cleanCount = externalWarningService.cleanExpiredWarnings();
            return Result.success(cleanCount);
        } catch (Exception e) {
            log.error("清理过期预警信息失败: {}", e.getMessage(), e);
            return Result.fail("清理过期预警信息失败");
        }
    }
}
