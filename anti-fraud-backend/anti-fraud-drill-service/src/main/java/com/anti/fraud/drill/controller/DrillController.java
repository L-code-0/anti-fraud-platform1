package com.anti.fraud.drill.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.drill.service.DrillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/drill")
public class DrillController {

    @Autowired
    private DrillService drillService;

    /**
     * 获取演练场景列表
     */
    @GetMapping("/scenarios")
    public Result<?> getScenarios() {
        return Result.success(drillService.getScenarios());
    }

    /**
     * 获取场景详情
     */
    @GetMapping("/scenario/{id}")
    public Result<?> getScenarioById(@PathVariable Long id) {
        return Result.success(drillService.getScenarioById(id));
    }

    /**
     * 提交演练结果
     */
    @PostMapping("/submit")
    public Result<?> submitDrill(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long scenarioId = Long.valueOf(params.get("scenarioId").toString());
        Map<String, Object> answers = (Map<String, Object>) params.get("answers");
        return Result.success(drillService.submitDrill(userId, scenarioId, answers));
    }

    /**
     * 获取用户演练记录
     */
    @GetMapping("/records/{userId}")
    public Result<?> getDrillRecords(@PathVariable Long userId) {
        return Result.success(drillService.getDrillRecords(userId));
    }

    /**
     * 获取演练统计
     */
    @GetMapping("/stats/{userId}")
    public Result<?> getDrillStats(@PathVariable Long userId) {
        return Result.success(drillService.getDrillStats(userId));
    }

    /**
     * 生成演练报告
     */
    @GetMapping("/report/{recordId}")
    public Result<?> generateDrillReport(@PathVariable Long recordId) {
        return Result.success(drillService.generateDrillReport(recordId));
    }
}
