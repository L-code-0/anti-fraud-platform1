package com.anti.fraud.modules.exam.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.exam.entity.ExamMonitor;
import com.anti.fraud.modules.exam.service.ExamMonitorService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam/monitor")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "考试监控")
public class ExamMonitorController {

    private final ExamMonitorService examMonitorService;

    @Operation(summary = "记录考试监控数据")
    @PostMapping("/record")
    public Result<Void> recordMonitorData(@RequestBody ExamMonitor monitor) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        monitor.setUserId(userId);
        monitor.setUserName(SecurityUtils.getCurrentUserName());

        try {
            boolean success = examMonitorService.recordMonitorData(monitor);
            if (success) {
                return Result.successMsg("记录监控数据成功");
            } else {
                return Result.fail("记录监控数据失败");
            }
        } catch (Exception e) {
            log.error("记录考试监控数据失败: {}", e.getMessage(), e);
            return Result.fail("记录监控数据失败");
        }
    }

    @Operation(summary = "检测作弊行为")
    @PostMapping("/detect")
    public Result<Map<String, Object>> detectCheating(@RequestBody Map<String, Object> params) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        Long examId = Long.parseLong(params.get("examId").toString());
        String monitorData = params.get("monitorData").toString();

        try {
            Map<String, Object> result = examMonitorService.detectCheating(userId, examId, monitorData);
            return Result.success("检测作弊行为成功", result);
        } catch (Exception e) {
            log.error("检测作弊行为失败: {}", e.getMessage(), e);
            return Result.fail("检测作弊行为失败");
        }
    }

    @Operation(summary = "获取用户考试监控记录")
    @GetMapping("/records")
    public Result<List<ExamMonitor>> getMonitorRecords(
            @RequestParam Long examId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<ExamMonitor> records = examMonitorService.getMonitorRecords(userId, examId);
            return Result.success("获取监控记录成功", records);
        } catch (Exception e) {
            log.error("获取用户考试监控记录失败: {}", e.getMessage(), e);
            return Result.fail("获取监控记录失败");
        }
    }

    @Operation(summary = "获取考试作弊风险报告")
    @GetMapping("/risk-report/{examId}")
    public Result<Map<String, Object>> getCheatingRiskReport(@PathVariable Long examId) {
        try {
            Map<String, Object> report = examMonitorService.getCheatingRiskReport(examId);
            return Result.success("获取作弊风险报告成功", report);
        } catch (Exception e) {
            log.error("获取考试作弊风险报告失败: {}", e.getMessage(), e);
            return Result.fail("获取作弊风险报告失败");
        }
    }

    @Operation(summary = "处理作弊行为")
    @PostMapping("/handle/{monitorId}")
    public Result<Void> handleCheating(
            @PathVariable Long monitorId,
            @RequestBody Map<String, String> params) {
        String action = params.get("action");
        if (action == null) {
            return Result.fail("请指定处理动作");
        }

        try {
            boolean success = examMonitorService.handleCheating(monitorId, action);
            if (success) {
                return Result.successMsg("处理作弊行为成功");
            } else {
                return Result.fail("处理作弊行为失败");
            }
        } catch (Exception e) {
            log.error("处理作弊行为失败: {}", e.getMessage(), e);
            return Result.fail("处理作弊行为失败");
        }
    }

    @Operation(summary = "生成防作弊配置")
    @GetMapping("/anti-cheat-config/{examId}")
    public Result<Map<String, Object>> generateAntiCheatConfig(@PathVariable Long examId) {
        try {
            Map<String, Object> config = examMonitorService.generateAntiCheatConfig(examId);
            return Result.success("生成防作弊配置成功", config);
        } catch (Exception e) {
            log.error("生成防作弊配置失败: {}", e.getMessage(), e);
            return Result.fail("生成防作弊配置失败");
        }
    }
}
