package com.anti.fraud.modules.behavior.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.behavior.entity.BehaviorAnalysis;
import com.anti.fraud.modules.behavior.service.BehaviorAnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 行为分析服务控制器
 */
@RestController
@RequestMapping("/behavior")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "行为分析服务")
public class BehaviorAnalysisController {

    private final BehaviorAnalysisService behaviorAnalysisService;

    @Operation(summary = "创建行为分析")
    @PostMapping("/create")
    public Result<String> createBehaviorAnalysis(@ApiParam(value = "行为分析信息", required = true) @RequestBody BehaviorAnalysis behaviorAnalysis) {
        try {
            String analysisId = behaviorAnalysisService.createBehaviorAnalysis(behaviorAnalysis);
            return Result.success(analysisId);
        } catch (Exception e) {
            log.error("创建行为分析失败: {}", e.getMessage(), e);
            return Result.fail("创建行为分析失败");
        }
    }

    @Operation(summary = "更新行为分析")
    @PutMapping("/update")
    public Result<Void> updateBehaviorAnalysis(@ApiParam(value = "行为分析信息", required = true) @RequestBody BehaviorAnalysis behaviorAnalysis) {
        try {
            boolean success = behaviorAnalysisService.updateBehaviorAnalysis(behaviorAnalysis);
            if (success) {
                return Result.successMsg("更新行为分析成功");
            } else {
                return Result.fail("更新行为分析失败");
            }
        } catch (Exception e) {
            log.error("更新行为分析失败: {}", e.getMessage(), e);
            return Result.fail("更新行为分析失败");
        }
    }

    @Operation(summary = "删除行为分析")
    @DeleteMapping("/delete/{analysisId}")
    public Result<Void> deleteBehaviorAnalysis(@ApiParam(value = "分析ID", required = true) @PathVariable String analysisId) {
        try {
            boolean success = behaviorAnalysisService.deleteBehaviorAnalysis(analysisId);
            if (success) {
                return Result.successMsg("删除行为分析成功");
            } else {
                return Result.fail("删除行为分析失败");
            }
        } catch (Exception e) {
            log.error("删除行为分析失败: {}", e.getMessage(), e);
            return Result.fail("删除行为分析失败");
        }
    }

    @Operation(summary = "获取行为分析详情")
    @GetMapping("/detail/{analysisId}")
    public Result<BehaviorAnalysis> getBehaviorAnalysisByAnalysisId(@ApiParam(value = "分析ID", required = true) @PathVariable String analysisId) {
        try {
            BehaviorAnalysis analysis = behaviorAnalysisService.getBehaviorAnalysisByAnalysisId(analysisId);
            if (analysis != null) {
                return Result.success(analysis);
            } else {
                return Result.fail("行为分析不存在");
            }
        } catch (Exception e) {
            log.error("获取行为分析详情失败: {}", e.getMessage(), e);
            return Result.fail("获取行为分析详情失败");
        }
    }

    @Operation(summary = "分页查询行为分析列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getBehaviorAnalysisList(
            @ApiParam(value = "行为类型: 1-浏览，2-点击，3-停留，4-搜索，5-答题，6-演练，7-其他", required = false) @RequestParam(required = false) Integer behaviorType,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = behaviorAnalysisService.getBehaviorAnalysisList(behaviorType, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询行为分析列表失败: {}", e.getMessage(), e);
            return Result.fail("查询行为分析列表失败");
        }
    }

    @Operation(summary = "根据用户ID查询行为分析列表")
    @GetMapping("/list-by-user")
    public Result<Map<String, Object>> getBehaviorAnalysisListByUserId(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "行为类型: 1-浏览，2-点击，3-停留，4-搜索，5-答题，6-演练，7-其他", required = false) @RequestParam(required = false) Integer behaviorType,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = behaviorAnalysisService.getBehaviorAnalysisListByUserId(userId, behaviorType, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据用户ID查询行为分析列表失败: {}", e.getMessage(), e);
            return Result.fail("根据用户ID查询行为分析列表失败");
        }
    }

    @Operation(summary = "根据时间范围查询行为分析列表")
    @GetMapping("/list-by-time-range")
    public Result<List<BehaviorAnalysis>> getBehaviorAnalysisListByTimeRange(
            @ApiParam(value = "开始时间", required = true) @RequestParam LocalDateTime startTime,
            @ApiParam(value = "结束时间", required = true) @RequestParam LocalDateTime endTime,
            @ApiParam(value = "行为类型: 1-浏览，2-点击，3-停留，4-搜索，5-答题，6-演练，7-其他", required = false) @RequestParam(required = false) Integer behaviorType,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            List<BehaviorAnalysis> analyses = behaviorAnalysisService.getBehaviorAnalysisListByTimeRange(startTime, endTime, behaviorType, status);
            return Result.success(analyses);
        } catch (Exception e) {
            log.error("根据时间范围查询行为分析列表失败: {}", e.getMessage(), e);
            return Result.fail("根据时间范围查询行为分析列表失败");
        }
    }

    @Operation(summary = "更新分析状态")
    @PutMapping("/update-status/{analysisId}")
    public Result<Void> updateBehaviorAnalysisStatus(
            @ApiParam(value = "分析ID", required = true) @PathVariable String analysisId,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = true) @RequestParam Integer status) {
        try {
            boolean success = behaviorAnalysisService.updateBehaviorAnalysisStatus(analysisId, status);
            if (success) {
                return Result.successMsg("更新分析状态成功");
            } else {
                return Result.fail("更新分析状态失败");
            }
        } catch (Exception e) {
            log.error("更新分析状态失败: {}", e.getMessage(), e);
            return Result.fail("更新分析状态失败");
        }
    }

    @Operation(summary = "更新分析结果")
    @PutMapping("/update-result/{analysisId}")
    public Result<Void> updateBehaviorAnalysisResult(
            @ApiParam(value = "分析ID", required = true) @PathVariable String analysisId,
            @ApiParam(value = "分析结果（JSON格式）", required = true) @RequestParam String analysisResult,
            @ApiParam(value = "反馈内容（JSON格式）", required = true) @RequestParam String feedbackContent) {
        try {
            boolean success = behaviorAnalysisService.updateBehaviorAnalysisResult(analysisId, analysisResult, feedbackContent);
            if (success) {
                return Result.successMsg("更新分析结果成功");
            } else {
                return Result.fail("更新分析结果失败");
            }
        } catch (Exception e) {
            log.error("更新分析结果失败: {}", e.getMessage(), e);
            return Result.fail("更新分析结果失败");
        }
    }

    @Operation(summary = "统计行为分析数量")
    @GetMapping("/count")
    public Result<Integer> countBehaviorAnalysis(
            @ApiParam(value = "行为类型: 1-浏览，2-点击，3-停留，4-搜索，5-答题，6-演练，7-其他", required = false) @RequestParam(required = false) Integer behaviorType,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            Integer count = behaviorAnalysisService.countBehaviorAnalysis(behaviorType, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计行为分析数量失败: {}", e.getMessage(), e);
            return Result.fail("统计行为分析数量失败");
        }
    }

    @Operation(summary = "统计用户行为分析数量")
    @GetMapping("/count-by-user/{userId}")
    public Result<Integer> countBehaviorAnalysisByUserId(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "行为类型: 1-浏览，2-点击，3-停留，4-搜索，5-答题，6-演练，7-其他", required = false) @RequestParam(required = false) Integer behaviorType,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            Integer count = behaviorAnalysisService.countBehaviorAnalysisByUserId(userId, behaviorType, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户行为分析数量失败: {}", e.getMessage(), e);
            return Result.fail("统计用户行为分析数量失败");
        }
    }

    @Operation(summary = "获取最近的行为分析")
    @GetMapping("/recent")
    public Result<List<BehaviorAnalysis>> getRecentBehaviorAnalysis(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit,
            @ApiParam(value = "行为类型: 1-浏览，2-点击，3-停留，4-搜索，5-答题，6-演练，7-其他", required = false) @RequestParam(required = false) Integer behaviorType,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            List<BehaviorAnalysis> analyses = behaviorAnalysisService.getRecentBehaviorAnalysis(limit, behaviorType, status);
            return Result.success(analyses);
        } catch (Exception e) {
            log.error("获取最近的行为分析失败: {}", e.getMessage(), e);
            return Result.fail("获取最近的行为分析失败");
        }
    }

    @Operation(summary = "批量创建行为分析")
    @PostMapping("/batch-create")
    public Result<Integer> batchCreateBehaviorAnalysis(@ApiParam(value = "行为分析列表", required = true) @RequestBody List<BehaviorAnalysis> analyses) {
        try {
            int count = behaviorAnalysisService.batchCreateBehaviorAnalysis(analyses);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量创建行为分析失败: {}", e.getMessage(), e);
            return Result.fail("批量创建行为分析失败");
        }
    }

    @Operation(summary = "获取行为分析统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getBehaviorAnalysisStatistics() {
        try {
            Map<String, Object> statistics = behaviorAnalysisService.getBehaviorAnalysisStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取行为分析统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取行为分析统计信息失败");
        }
    }

    @Operation(summary = "分析用户行为")
    @PostMapping("/analyze/{analysisId}")
    public Result<Void> analyzeUserBehavior(@ApiParam(value = "分析ID", required = true) @PathVariable String analysisId) {
        try {
            boolean success = behaviorAnalysisService.analyzeUserBehavior(analysisId);
            if (success) {
                return Result.successMsg("分析用户行为成功");
            } else {
                return Result.fail("分析用户行为失败");
            }
        } catch (Exception e) {
            log.error("分析用户行为失败: {}", e.getMessage(), e);
            return Result.fail("分析用户行为失败");
        }
    }

    @Operation(summary = "记录用户浏览行为")
    @PostMapping("/record-browse")
    public Result<String> recordBrowseBehavior(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "浏览内容", required = true) @RequestParam String content,
            @ApiParam(value = "停留时间（秒）", required = true) @RequestParam Integer duration) {
        try {
            String analysisId = behaviorAnalysisService.recordBrowseBehavior(userId, username, content, duration);
            return Result.success(analysisId);
        } catch (Exception e) {
            log.error("记录用户浏览行为失败: {}", e.getMessage(), e);
            return Result.fail("记录用户浏览行为失败");
        }
    }

    @Operation(summary = "记录用户点击行为")
    @PostMapping("/record-click")
    public Result<String> recordClickBehavior(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "点击内容", required = true) @RequestParam String content,
            @ApiParam(value = "点击位置", required = true) @RequestParam String position) {
        try {
            String analysisId = behaviorAnalysisService.recordClickBehavior(userId, username, content, position);
            return Result.success(analysisId);
        } catch (Exception e) {
            log.error("记录用户点击行为失败: {}", e.getMessage(), e);
            return Result.fail("记录用户点击行为失败");
        }
    }

    @Operation(summary = "记录用户停留行为")
    @PostMapping("/record-stay")
    public Result<String> recordStayBehavior(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "停留内容", required = true) @RequestParam String content,
            @ApiParam(value = "停留时间（秒）", required = true) @RequestParam Integer duration) {
        try {
            String analysisId = behaviorAnalysisService.recordStayBehavior(userId, username, content, duration);
            return Result.success(analysisId);
        } catch (Exception e) {
            log.error("记录用户停留行为失败: {}", e.getMessage(), e);
            return Result.fail("记录用户停留行为失败");
        }
    }

    @Operation(summary = "记录用户搜索行为")
    @PostMapping("/record-search")
    public Result<String> recordSearchBehavior(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "搜索关键词", required = true) @RequestParam String keyword,
            @ApiParam(value = "搜索结果数量", required = true) @RequestParam Integer resultCount) {
        try {
            String analysisId = behaviorAnalysisService.recordSearchBehavior(userId, username, keyword, resultCount);
            return Result.success(analysisId);
        } catch (Exception e) {
            log.error("记录用户搜索行为失败: {}", e.getMessage(), e);
            return Result.fail("记录用户搜索行为失败");
        }
    }

    @Operation(summary = "记录用户答题行为")
    @PostMapping("/record-answer")
    public Result<String> recordAnswerBehavior(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "题目ID", required = true) @RequestParam String questionId,
            @ApiParam(value = "用户答案", required = true) @RequestParam String answer,
            @ApiParam(value = "正确答案", required = true) @RequestParam String correctAnswer,
            @ApiParam(value = "是否正确", required = true) @RequestParam Boolean isCorrect,
            @ApiParam(value = "用时（秒）", required = true) @RequestParam Integer timeUsed) {
        try {
            String analysisId = behaviorAnalysisService.recordAnswerBehavior(userId, username, questionId, answer, correctAnswer, isCorrect, timeUsed);
            return Result.success(analysisId);
        } catch (Exception e) {
            log.error("记录用户答题行为失败: {}", e.getMessage(), e);
            return Result.fail("记录用户答题行为失败");
        }
    }

    @Operation(summary = "记录用户演练行为")
    @PostMapping("/record-exercise")
    public Result<String> recordExerciseBehavior(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "用户名", required = true) @RequestParam String username,
            @ApiParam(value = "演练ID", required = true) @RequestParam String exerciseId,
            @ApiParam(value = "演练动作", required = true) @RequestParam String action,
            @ApiParam(value = "演练结果", required = true) @RequestParam String result) {
        try {
            String analysisId = behaviorAnalysisService.recordExerciseBehavior(userId, username, exerciseId, action, result);
            return Result.success(analysisId);
        } catch (Exception e) {
            log.error("记录用户演练行为失败: {}", e.getMessage(), e);
            return Result.fail("记录用户演练行为失败");
        }
    }
}
