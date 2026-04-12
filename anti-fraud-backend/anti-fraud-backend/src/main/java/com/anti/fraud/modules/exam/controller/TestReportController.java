package com.anti.fraud.modules.exam.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.exam.entity.TestReport;
import com.anti.fraud.modules.exam.service.TestReportService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam/report")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "测试报告")
public class TestReportController {

    private final TestReportService testReportService;

    @Operation(summary = "创建测试报告")
    @PostMapping("/create")
    public Result<Void> createTestReport(@RequestBody TestReport testReport) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        testReport.setUserId(userId);
        testReport.setUserName(SecurityUtils.getCurrentUserName());

        try {
            boolean success = testReportService.createTestReport(testReport);
            if (success) {
                return Result.successMsg("创建测试报告成功");
            } else {
                return Result.fail("创建测试报告失败");
            }
        } catch (Exception e) {
            log.error("创建测试报告失败: {}", e.getMessage(), e);
            return Result.fail("创建测试报告失败");
        }
    }

    @Operation(summary = "更新测试报告")
    @PostMapping("/update")
    public Result<Void> updateTestReport(@RequestBody TestReport testReport) {
        try {
            boolean success = testReportService.updateTestReport(testReport);
            if (success) {
                return Result.successMsg("更新测试报告成功");
            } else {
                return Result.fail("更新测试报告失败");
            }
        } catch (Exception e) {
            log.error("更新测试报告失败: {}", e.getMessage(), e);
            return Result.fail("更新测试报告失败");
        }
    }

    @Operation(summary = "获取测试报告详情")
    @GetMapping("/detail/{id}")
    public Result<TestReport> getTestReportById(@PathVariable Long id) {
        try {
            TestReport testReport = testReportService.getTestReportById(id);
            if (testReport != null) {
                return Result.success("获取测试报告详情成功", testReport);
            } else {
                return Result.fail("测试报告不存在");
            }
        } catch (Exception e) {
            log.error("获取测试报告详情失败: {}", e.getMessage(), e);
            return Result.fail("获取测试报告详情失败");
        }
    }

    @Operation(summary = "获取用户测试报告列表")
    @GetMapping("/list")
    public Result<List<TestReport>> getUserTestReports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<TestReport> reports = testReportService.getUserTestReports(userId, page, size);
            return Result.success("获取用户测试报告列表成功", reports);
        } catch (Exception e) {
            log.error("获取用户测试报告列表失败: {}", e.getMessage(), e);
            return Result.fail("获取用户测试报告列表失败");
        }
    }

    @Operation(summary = "生成详细测试报告")
    @PostMapping("/generate")
    public Result<TestReport> generateDetailedReport(@RequestBody Map<String, Object> params) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        Long examId = Long.parseLong(params.get("examId").toString());
        Map<String, Object> answers = (Map<String, Object>) params.get("answers");

        try {
            TestReport testReport = testReportService.generateDetailedReport(userId, examId, answers);
            if (testReport != null) {
                return Result.success("生成详细测试报告成功", testReport);
            } else {
                return Result.fail("生成详细测试报告失败");
            }
        } catch (Exception e) {
            log.error("生成详细测试报告失败: {}", e.getMessage(), e);
            return Result.fail("生成详细测试报告失败");
        }
    }

    @Operation(summary = "分析测试结果")
    @PostMapping("/analyze")
    public Result<Map<String, Object>> analyzeTestResult(@RequestBody TestReport testReport) {
        try {
            Map<String, Object> analysis = testReportService.analyzeTestResult(testReport);
            return Result.success("分析测试结果成功", analysis);
        } catch (Exception e) {
            log.error("分析测试结果失败: {}", e.getMessage(), e);
            return Result.fail("分析测试结果失败");
        }
    }

    @Operation(summary = "生成学习建议")
    @PostMapping("/recommendations")
    public Result<List<String>> generateLearningRecommendations(@RequestBody TestReport testReport) {
        try {
            List<String> recommendations = testReportService.generateLearningRecommendations(testReport);
            return Result.success("生成学习建议成功", recommendations);
        } catch (Exception e) {
            log.error("生成学习建议失败: {}", e.getMessage(), e);
            return Result.fail("生成学习建议失败");
        }
    }

    @Operation(summary = "获取测试统计信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getTestStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Map<String, Object> stats = testReportService.getTestStats(userId);
            return Result.success("获取测试统计信息成功", stats);
        } catch (Exception e) {
            log.error("获取测试统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取测试统计信息失败");
        }
    }

    @Operation(summary = "导出测试报告")
    @GetMapping("/export/{id}")
    public Result<Map<String, Object>> exportTestReport(@PathVariable Long id) {
        try {
            Map<String, Object> exportData = testReportService.exportTestReport(id);
            if (!exportData.isEmpty()) {
                return Result.success("导出测试报告成功", exportData);
            } else {
                return Result.fail("测试报告不存在");
            }
        } catch (Exception e) {
            log.error("导出测试报告失败: {}", e.getMessage(), e);
            return Result.fail("导出测试报告失败");
        }
    }

    @Operation(summary = "比较测试报告")
    @GetMapping("/compare")
    public Result<Map<String, Object>> compareTestReports(
            @RequestParam Long reportId1,
            @RequestParam Long reportId2) {
        try {
            Map<String, Object> comparison = testReportService.compareTestReports(reportId1, reportId2);
            if (!comparison.isEmpty()) {
                return Result.success("比较测试报告成功", comparison);
            } else {
                return Result.fail("测试报告不存在");
            }
        } catch (Exception e) {
            log.error("比较测试报告失败: {}", e.getMessage(), e);
            return Result.fail("比较测试报告失败");
        }
    }
}
