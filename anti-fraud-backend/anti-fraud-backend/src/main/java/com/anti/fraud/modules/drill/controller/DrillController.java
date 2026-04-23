package com.anti.fraud.modules.drill.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.drill.entity.DrillRecord;
import com.anti.fraud.modules.drill.entity.DrillScenario;
import com.anti.fraud.modules.drill.service.DrillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 演练控制器
 */
@RestController
@RequestMapping("/api/drill")
@Tag(name = "演练管理", description = "演练相关接口")
@RequiredArgsConstructor
public class DrillController {

    private final DrillService drillService;

    @GetMapping("/scenario/list")
    @Operation(summary = "获取演练场景列表")
    public Result<?> getScenarioList() {
        return Result.success(drillService.getScenarioList());
    }

    @PostMapping("/record/save")
    @Operation(summary = "保存演练记录")
    public Result<?> saveDrillRecord(@RequestBody DrillRecord record) {
        drillService.saveDrillRecord(record);
        return Result.success("演练记录保存成功");
    }

    @GetMapping("/record/list")
    @Operation(summary = "获取演练记录列表")
    public Result<?> getDrillRecordList(@RequestParam String userId) {
        return Result.success(drillService.getDrillRecordList(userId));
    }
}
