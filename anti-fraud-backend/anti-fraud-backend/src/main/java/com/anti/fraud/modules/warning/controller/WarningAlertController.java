package com.anti.fraud.modules.warning.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.warning.dto.WarningProcessDTO;
import com.anti.fraud.modules.warning.dto.WarningQueryDTO;
import com.anti.fraud.modules.warning.service.WarningAlertService;
import com.anti.fraud.modules.warning.vo.WarningAlertVO;
import com.anti.fraud.modules.warning.vo.WarningStatsVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能预警系统控制器
 */
@Tag(name = "智能预警系统", description = "风险预警监控与处理")
@RestController
@RequestMapping("/warning")
@RequiredArgsConstructor
public class WarningAlertController {

    private final WarningAlertService warningAlertService;

    @Operation(summary = "获取预警统计", description = "获取各风险等级预警数量统计")
    @GetMapping("/stats")
    public Result<WarningStatsVO> getStats() {
        return Result.success(warningAlertService.getStats());
    }

    @Operation(summary = "获取预警列表", description = "分页查询预警信息，支持搜索和筛选")
    @GetMapping("/list")
    public Result<Page<WarningAlertVO>> getWarningList(WarningQueryDTO queryDTO) {
        return Result.success(warningAlertService.getWarningPage(queryDTO));
    }

    @Operation(summary = "获取预警详情", description = "根据ID获取预警详细信息")
    @GetMapping("/{id}")
    public Result<WarningAlertVO> getWarningDetail(
            @Parameter(description = "预警ID") @PathVariable Long id) {
        return Result.success(warningAlertService.getWarningDetail(id));
    }

    @Operation(summary = "处理预警", description = "处理单条预警，标记为已处理或误报")
    @PostMapping("/process")
    public Result<String> processWarning(@RequestBody WarningProcessDTO processDTO) {
        warningAlertService.processWarning(processDTO);
        return Result.success("处理成功");
    }

    @Operation(summary = "批量处理预警", description = "批量处理多条预警")
    @PostMapping("/batch-process")
    public Result<String> batchProcess(
            @Parameter(description = "预警ID列表") @RequestParam List<Long> ids,
            @Parameter(description = "处理状态") @RequestParam Integer status,
            @Parameter(description = "处理结果") @RequestParam String result) {
        warningAlertService.batchProcess(ids, status, result);
        return Result.success("批量处理成功");
    }
}
