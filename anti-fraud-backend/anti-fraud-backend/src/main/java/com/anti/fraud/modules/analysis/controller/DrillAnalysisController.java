package com.anti.fraud.modules.analysis.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.analysis.entity.DrillAnalysis;
import com.anti.fraud.modules.analysis.service.DrillAnalysisService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "演练分析")
public class DrillAnalysisController {

    private final DrillAnalysisService drillAnalysisService;

    @Operation(summary = "创建演练分析")
    @PostMapping("/create")
    public Result<Void> createAnalysis(@RequestBody DrillAnalysis analysis) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        analysis.setUserId(userId);
        analysis.setUserName(SecurityUtils.getCurrentUserName());
        analysis.setCreatedBy(SecurityUtils.getCurrentUserName());
        analysis.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = drillAnalysisService.createAnalysis(analysis);
            if (success) {
                return Result.successMsg("创建演练分析成功");
            } else {
                return Result.fail("创建演练分析失败");
            }
        } catch (Exception e) {
            log.error("创建演练分析失败: {}", e.getMessage(), e);
            return Result.fail("创建演练分析失败");
        }
    }

    @Operation(summary = "更新演练分析")
    @PostMapping("/update")
    public Result<Void> updateAnalysis(@RequestBody DrillAnalysis analysis) {
        analysis.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = drillAnalysisService.updateAnalysis(analysis);
            if (success) {
                return Result.successMsg("更新演练分析成功");
            } else {
                return Result.fail("更新演练分析失败");
            }
        } catch (Exception e) {
            log.error("更新演练分析失败: {}", e.getMessage(), e);
            return Result.fail("更新演练分析失败");
        }
    }

    @Operation(summary = "获取分析详情")
    @GetMapping("/detail/{id}")
    public Result<DrillAnalysis> getAnalysisById(@PathVariable Long id) {
        try {
            DrillAnalysis analysis = drillAnalysisService.getAnalysisById(id);
            if (analysis != null) {
                return Result.success("获取分析详情成功", analysis);
            } else {
                return Result.fail("分析不存在");
            }
        } catch (Exception e) {
            log.error("获取分析详情失败: {}", e.getMessage(), e);
            return Result.fail("获取分析详情失败");
        }
    }

    @Operation(summary = "获取用户演练分析列表")
    @GetMapping("/user/list")
    public Result<List<DrillAnalysis>> getUserAnalysisList(
            @RequestParam(required = false) String drillType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<DrillAnalysis> analyses = drillAnalysisService.getUserAnalysisList(userId, drillType, page, size);
            return Result.success("获取用户演练分析列表成功", analyses);
        } catch (Exception e) {
            log.error("获取用户演练分析列表失败: {}", e.getMessage(), e);
            return Result.fail("获取用户演练分析列表失败");
        }
    }

    @Operation(summary = "分析演练数据")
    @PostMapping("/analyze")
    public Result<Map<String, Object>> analyzeDrillData(@RequestBody Map<String, Object> data) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        Long drillId = (Long) data.get("drillId");
        String drillType = (String) data.get("drillType");
        String drillName = (String) data.get("drillName");
        Integer score = (Integer) data.get("score");

        try {
            Map<String, Object> result = drillAnalysisService.analyzeDrillData(userId, drillId, drillType, drillName, score);
            if ((boolean) result.get("success")) {
                return Result.success("分析演练数据成功", result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("分析演练数据失败: {}", e.getMessage(), e);
            return Result.fail("分析演练数据失败");
        }
    }

    @Operation(summary = "与他人对比分析")
    @GetMapping("/compare")
    public Result<Map<String, Object>> compareWithOthers(@RequestParam String drillType) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Map<String, Object> result = drillAnalysisService.compareWithOthers(userId, drillType);
            if ((boolean) result.get("success")) {
                return Result.success("与他人对比分析成功", result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("与他人对比分析失败: {}", e.getMessage(), e);
            return Result.fail("与他人对比分析失败");
        }
    }

    @Operation(summary = "进步分析")
    @GetMapping("/progress")
    public Result<Map<String, Object>> analyzeProgress(
            @RequestParam String drillType,
            @RequestParam(defaultValue = "month") String period) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Map<String, Object> result = drillAnalysisService.analyzeProgress(userId, drillType, period);
            if ((boolean) result.get("success")) {
                return Result.success("进步分析成功", result);
            } else {
                return Result.fail((String) result.get("message"));
            }
        } catch (Exception e) {
            log.error("进步分析失败: {}", e.getMessage(), e);
            return Result.fail("进步分析失败");
        }
    }

    @Operation(summary = "获取演练统计信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDrillStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Map<String, Object> stats = drillAnalysisService.getDrillStats(userId);
            return Result.success("获取演练统计信息成功", stats);
        } catch (Exception e) {
            log.error("获取演练统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取演练统计信息失败");
        }
    }

    @Operation(summary = "生成分析报告")
    @GetMapping("/report")
    public Result<Map<String, Object>> generateAnalysisReport(@RequestParam String drillType) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Map<String, Object> report = drillAnalysisService.generateAnalysisReport(userId, drillType);
            if ((boolean) report.get("success")) {
                return Result.success("生成分析报告成功", report);
            } else {
                return Result.fail((String) report.get("message"));
            }
        } catch (Exception e) {
            log.error("生成分析报告失败: {}", e.getMessage(), e);
            return Result.fail("生成分析报告失败");
        }
    }

    @Operation(summary = "获取排名信息")
    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> getRanking(
            @RequestParam String drillType,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> ranking = drillAnalysisService.getRanking(drillType, limit);
            return Result.success("获取排名信息成功", ranking);
        } catch (Exception e) {
            log.error("获取排名信息失败: {}", e.getMessage(), e);
            return Result.fail("获取排名信息失败");
        }
    }

    @Operation(summary = "获取用户历史最高分")
    @GetMapping("/highest-score")
    public Result<Integer> getHighestScore(@RequestParam String drillType) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Integer highestScore = drillAnalysisService.getHighestScore(userId, drillType);
            return Result.success("获取用户历史最高分成功", highestScore);
        } catch (Exception e) {
            log.error("获取用户历史最高分失败: {}", e.getMessage(), e);
            return Result.fail("获取用户历史最高分失败");
        }
    }

    @Operation(summary = "获取用户历史平均分")
    @GetMapping("/average-score")
    public Result<Double> getAverageScore(@RequestParam String drillType) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Double averageScore = drillAnalysisService.getAverageScore(userId, drillType);
            return Result.success("获取用户历史平均分成功", averageScore);
        } catch (Exception e) {
            log.error("获取用户历史平均分失败: {}", e.getMessage(), e);
            return Result.fail("获取用户历史平均分失败");
        }
    }
}
