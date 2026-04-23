package com.anti.fraud.modules.emotion.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.emotion.entity.EmotionAnalysis;
import com.anti.fraud.modules.emotion.service.EmotionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 情感分析服务控制器
 */
@RestController
@RequestMapping("/emotion")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "情感分析服务")
public class EmotionController {

    private final EmotionService emotionService;

    @Operation(summary = "创建情感分析")
    @PostMapping("/create")
    public Result<String> createEmotionAnalysis(@ApiParam(value = "情感分析信息", required = true) @RequestBody EmotionAnalysis emotionAnalysis) {
        try {
            String analysisId = emotionService.createEmotionAnalysis(emotionAnalysis);
            return Result.success(analysisId);
        } catch (Exception e) {
            log.error("创建情感分析失败: {}", e.getMessage(), e);
            return Result.fail("创建情感分析失败");
        }
    }

    @Operation(summary = "更新情感分析")
    @PutMapping("/update")
    public Result<Void> updateEmotionAnalysis(@ApiParam(value = "情感分析信息", required = true) @RequestBody EmotionAnalysis emotionAnalysis) {
        try {
            boolean success = emotionService.updateEmotionAnalysis(emotionAnalysis);
            if (success) {
                return Result.successMsg("更新情感分析成功");
            } else {
                return Result.fail("更新情感分析失败");
            }
        } catch (Exception e) {
            log.error("更新情感分析失败: {}", e.getMessage(), e);
            return Result.fail("更新情感分析失败");
        }
    }

    @Operation(summary = "删除情感分析")
    @DeleteMapping("/delete/{analysisId}")
    public Result<Void> deleteEmotionAnalysis(@ApiParam(value = "分析ID", required = true) @PathVariable String analysisId) {
        try {
            boolean success = emotionService.deleteEmotionAnalysis(analysisId);
            if (success) {
                return Result.successMsg("删除情感分析成功");
            } else {
                return Result.fail("删除情感分析失败");
            }
        } catch (Exception e) {
            log.error("删除情感分析失败: {}", e.getMessage(), e);
            return Result.fail("删除情感分析失败");
        }
    }

    @Operation(summary = "获取情感分析详情")
    @GetMapping("/detail/{analysisId}")
    public Result<EmotionAnalysis> getEmotionAnalysisByAnalysisId(@ApiParam(value = "分析ID", required = true) @PathVariable String analysisId) {
        try {
            EmotionAnalysis analysis = emotionService.getEmotionAnalysisByAnalysisId(analysisId);
            if (analysis != null) {
                return Result.success(analysis);
            } else {
                return Result.fail("情感分析不存在");
            }
        } catch (Exception e) {
            log.error("获取情感分析详情失败: {}", e.getMessage(), e);
            return Result.fail("获取情感分析详情失败");
        }
    }

    @Operation(summary = "根据消息ID获取情感分析")
    @GetMapping("/by-message/{messageId}")
    public Result<EmotionAnalysis> getEmotionAnalysisByMessageId(@ApiParam(value = "消息ID", required = true) @PathVariable String messageId) {
        try {
            EmotionAnalysis analysis = emotionService.getEmotionAnalysisByMessageId(messageId);
            if (analysis != null) {
                return Result.success(analysis);
            } else {
                return Result.fail("情感分析不存在");
            }
        } catch (Exception e) {
            log.error("根据消息ID获取情感分析失败: {}", e.getMessage(), e);
            return Result.fail("根据消息ID获取情感分析失败");
        }
    }

    @Operation(summary = "分页查询情感分析列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> getEmotionAnalysisList(
            @ApiParam(value = "情感类型: 1-积极，2-消极，3-中性", required = false) @RequestParam(required = false) Integer emotionType,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = emotionService.getEmotionAnalysisList(emotionType, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询情感分析列表失败: {}", e.getMessage(), e);
            return Result.fail("查询情感分析列表失败");
        }
    }

    @Operation(summary = "根据用户ID查询情感分析列表")
    @GetMapping("/list-by-user")
    public Result<Map<String, Object>> getEmotionAnalysisListByUserId(
            @ApiParam(value = "用户ID", required = true) @RequestParam Long userId,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = emotionService.getEmotionAnalysisListByUserId(userId, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据用户ID查询情感分析列表失败: {}", e.getMessage(), e);
            return Result.fail("根据用户ID查询情感分析列表失败");
        }
    }

    @Operation(summary = "根据会话ID查询情感分析列表")
    @GetMapping("/list-by-session")
    public Result<Map<String, Object>> getEmotionAnalysisListBySessionId(
            @ApiParam(value = "会话ID", required = true) @RequestParam String sessionId,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            Map<String, Object> result = emotionService.getEmotionAnalysisListBySessionId(sessionId, status, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据会话ID查询情感分析列表失败: {}", e.getMessage(), e);
            return Result.fail("根据会话ID查询情感分析列表失败");
        }
    }

    @Operation(summary = "更新情感分析状态")
    @PutMapping("/update-status/{analysisId}")
    public Result<Void> updateEmotionAnalysisStatus(
            @ApiParam(value = "分析ID", required = true) @PathVariable String analysisId,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = true) @RequestParam Integer status) {
        try {
            boolean success = emotionService.updateEmotionAnalysisStatus(analysisId, status);
            if (success) {
                return Result.successMsg("更新情感分析状态成功");
            } else {
                return Result.fail("更新情感分析状态失败");
            }
        } catch (Exception e) {
            log.error("更新情感分析状态失败: {}", e.getMessage(), e);
            return Result.fail("更新情感分析状态失败");
        }
    }

    @Operation(summary = "更新情感分析结果")
    @PutMapping("/update-result/{analysisId}")
    public Result<Void> updateEmotionAnalysisResult(
            @ApiParam(value = "分析ID", required = true) @PathVariable String analysisId,
            @ApiParam(value = "情感类型: 1-积极，2-消极，3-中性", required = true) @RequestParam Integer emotionType,
            @ApiParam(value = "情感得分（-1到1）", required = true) @RequestParam Double emotionScore,
            @ApiParam(value = "情绪标签（JSON格式）", required = true) @RequestParam String emotionTags,
            @ApiParam(value = "分析结果（JSON格式）", required = true) @RequestParam String analysisResult) {
        try {
            boolean success = emotionService.updateEmotionAnalysisResult(analysisId, emotionType, emotionScore, emotionTags, analysisResult);
            if (success) {
                return Result.successMsg("更新情感分析结果成功");
            } else {
                return Result.fail("更新情感分析结果失败");
            }
        } catch (Exception e) {
            log.error("更新情感分析结果失败: {}", e.getMessage(), e);
            return Result.fail("更新情感分析结果失败");
        }
    }

    @Operation(summary = "统计情感分析数量")
    @GetMapping("/count")
    public Result<Integer> countEmotionAnalysis(
            @ApiParam(value = "情感类型: 1-积极，2-消极，3-中性", required = false) @RequestParam(required = false) Integer emotionType,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            Integer count = emotionService.countEmotionAnalysis(emotionType, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计情感分析数量失败: {}", e.getMessage(), e);
            return Result.fail("统计情感分析数量失败");
        }
    }

    @Operation(summary = "统计用户情感分析数量")
    @GetMapping("/count-by-user/{userId}")
    public Result<Integer> countEmotionAnalysisByUserId(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            Integer count = emotionService.countEmotionAnalysisByUserId(userId, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户情感分析数量失败: {}", e.getMessage(), e);
            return Result.fail("统计用户情感分析数量失败");
        }
    }

    @Operation(summary = "统计会话情感分析数量")
    @GetMapping("/count-by-session/{sessionId}")
    public Result<Integer> countEmotionAnalysisBySessionId(
            @ApiParam(value = "会话ID", required = true) @PathVariable String sessionId,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            Integer count = emotionService.countEmotionAnalysisBySessionId(sessionId, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计会话情感分析数量失败: {}", e.getMessage(), e);
            return Result.fail("统计会话情感分析数量失败");
        }
    }

    @Operation(summary = "计算平均情感得分")
    @GetMapping("/average-score")
    public Result<Double> calculateAverageEmotionScore(
            @ApiParam(value = "情感类型: 1-积极，2-消极，3-中性", required = false) @RequestParam(required = false) Integer emotionType,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            Double averageScore = emotionService.calculateAverageEmotionScore(emotionType, status);
            return Result.success(averageScore);
        } catch (Exception e) {
            log.error("计算平均情感得分失败: {}", e.getMessage(), e);
            return Result.fail("计算平均情感得分失败");
        }
    }

    @Operation(summary = "获取最近的情感分析")
    @GetMapping("/recent")
    public Result<List<EmotionAnalysis>> getRecentEmotionAnalysis(
            @ApiParam(value = "数量限制", required = true) @RequestParam Integer limit,
            @ApiParam(value = "状态: 1-已分析，2-分析中，3-分析失败", required = false) @RequestParam(required = false) Integer status) {
        try {
            List<EmotionAnalysis> analyses = emotionService.getRecentEmotionAnalysis(limit, status);
            return Result.success(analyses);
        } catch (Exception e) {
            log.error("获取最近的情感分析失败: {}", e.getMessage(), e);
            return Result.fail("获取最近的情感分析失败");
        }
    }

    @Operation(summary = "批量创建情感分析")
    @PostMapping("/batch-create")
    public Result<Integer> batchCreateEmotionAnalysis(@ApiParam(value = "情感分析列表", required = true) @RequestBody List<EmotionAnalysis> analyses) {
        try {
            int count = emotionService.batchCreateEmotionAnalysis(analyses);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量创建情感分析失败: {}", e.getMessage(), e);
            return Result.fail("批量创建情感分析失败");
        }
    }

    @Operation(summary = "获取情感分析统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getEmotionAnalysisStatistics() {
        try {
            Map<String, Object> statistics = emotionService.getEmotionAnalysisStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取情感分析统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取情感分析统计信息失败");
        }
    }

    @Operation(summary = "分析文本情感")
    @PostMapping("/analyze")
    public Result<EmotionAnalysis> analyzeTextEmotion(@ApiParam(value = "文本", required = true) @RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            if (text == null || text.isEmpty()) {
                return Result.fail("文本不能为空");
            }
            EmotionAnalysis analysis = emotionService.analyzeTextEmotion(text);
            if (analysis != null) {
                return Result.success(analysis);
            } else {
                return Result.fail("分析文本情感失败");
            }
        } catch (Exception e) {
            log.error("分析文本情感失败: {}", e.getMessage(), e);
            return Result.fail("分析文本情感失败");
        }
    }

    @Operation(summary = "根据情感分析结果调整回答策略")
    @PostMapping("/adjust-strategy")
    public Result<Map<String, Object>> adjustResponseStrategy(@ApiParam(value = "情感分析结果", required = true) @RequestBody EmotionAnalysis emotionAnalysis) {
        try {
            Map<String, Object> strategy = emotionService.adjustResponseStrategy(emotionAnalysis);
            return Result.success(strategy);
        } catch (Exception e) {
            log.error("调整回答策略失败: {}", e.getMessage(), e);
            return Result.fail("调整回答策略失败");
        }
    }
}
