package com.anti.fraud.modules.exam.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.exam.entity.UserAbility;
import com.anti.fraud.modules.exam.service.AdaptiveTestService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam/adaptive")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "自适应测试")
public class AdaptiveTestController {

    private final AdaptiveTestService adaptiveTestService;

    @Operation(summary = "获取用户能力评估")
    @GetMapping("/ability")
    public Result<UserAbility> getUserAbility() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            UserAbility userAbility = adaptiveTestService.getUserAbility(userId);
            return Result.success("获取用户能力评估成功", userAbility);
        } catch (Exception e) {
            log.error("获取用户能力评估失败: {}", e.getMessage(), e);
            return Result.fail("获取用户能力评估失败");
        }
    }

    @Operation(summary = "评估用户能力")
    @PostMapping("/evaluate")
    public Result<UserAbility> evaluateUserAbility(@RequestBody Map<String, Object> testResult) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            UserAbility userAbility = adaptiveTestService.evaluateUserAbility(userId, testResult);
            return Result.success("评估用户能力成功", userAbility);
        } catch (Exception e) {
            log.error("评估用户能力失败: {}", e.getMessage(), e);
            return Result.fail("评估用户能力失败");
        }
    }

    @Operation(summary = "生成自适应测试题目")
    @PostMapping("/generate-questions")
    public Result<List<Map<String, Object>>> generateAdaptiveQuestions(@RequestBody Map<String, Object> params) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        String category = (String) params.get("category");
        int count = (int) params.getOrDefault("count", 10);

        try {
            List<Map<String, Object>> questions = adaptiveTestService.generateAdaptiveQuestions(userId, category, count);
            return Result.success("生成自适应测试题目成功", questions);
        } catch (Exception e) {
            log.error("生成自适应测试题目失败: {}", e.getMessage(), e);
            return Result.fail("生成自适应测试题目失败");
        }
    }

    @Operation(summary = "计算题目难度")
    @GetMapping("/question-difficulty/{questionId}")
    public Result<Double> calculateQuestionDifficulty(@PathVariable Long questionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            double difficulty = adaptiveTestService.calculateQuestionDifficulty(userId, questionId);
            return Result.success("计算题目难度成功", difficulty);
        } catch (Exception e) {
            log.error("计算题目难度失败: {}", e.getMessage(), e);
            return Result.fail("计算题目难度失败");
        }
    }

    @Operation(summary = "获取用户能力分析报告")
    @GetMapping("/ability-report")
    public Result<Map<String, Object>> getAbilityAnalysisReport() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            Map<String, Object> report = adaptiveTestService.getAbilityAnalysisReport(userId);
            return Result.success("获取用户能力分析报告成功", report);
        } catch (Exception e) {
            log.error("获取用户能力分析报告失败: {}", e.getMessage(), e);
            return Result.fail("获取用户能力分析报告失败");
        }
    }

    @Operation(summary = "推荐学习内容")
    @GetMapping("/recommend-content")
    public Result<List<Map<String, Object>>> recommendLearningContent(
            @RequestParam(defaultValue = "5") int count) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<Map<String, Object>> recommendations = adaptiveTestService.recommendLearningContent(userId, count);
            return Result.success("推荐学习内容成功", recommendations);
        } catch (Exception e) {
            log.error("推荐学习内容失败: {}", e.getMessage(), e);
            return Result.fail("推荐学习内容失败");
        }
    }

    @Operation(summary = "预测用户分数")
    @PostMapping("/predict-score")
    public Result<Double> predictUserScore(@RequestBody Map<String, Object> params) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        List<Long> questions = (List<Long>) params.get("questions");

        try {
            double score = adaptiveTestService.predictUserScore(userId, questions);
            return Result.success("预测用户分数成功", score);
        } catch (Exception e) {
            log.error("预测用户分数失败: {}", e.getMessage(), e);
            return Result.fail("预测用户分数失败");
        }
    }
}
