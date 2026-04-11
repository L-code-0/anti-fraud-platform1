package com.anti.fraud.modules.test.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TestService testService;

    /**
     * 获取用户能力水平
     */
    @GetMapping("/ability/{userId}")
    public Result<?> getUserAbility(@PathVariable Long userId) {
        return Result.success(testService.getUserAbility(userId));
    }

    /**
     * 自适应测试组卷
     */
    @PostMapping("/adaptive")
    public Result<?> adaptiveTest(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Integer questionCount = Integer.valueOf(params.get("questionCount").toString());
        return Result.success(testService.adaptiveTest(userId, questionCount));
    }

    /**
     * 提交测试答案
     */
    @PostMapping("/submit")
    public Result<?> submitTest(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long paperId = Long.valueOf(params.get("paperId").toString());
        Object answersObj = params.get("answers");
        Map<String, Object> answers = answersObj instanceof Map ? (Map<String, Object>) answersObj : new HashMap<>();
        return Result.success(testService.submitTest(userId, paperId, answers));
    }

    /**
     * 生成测试报告
     */
    @GetMapping("/report/{userId}/{testId}")
    public Result<?> generateTestReport(@PathVariable Long userId, @PathVariable Long testId) {
        return Result.success(testService.generateTestReport(userId, testId));
    }

    /**
     * 分析学习薄弱点
     */
    @GetMapping("/weaknesses/{userId}")
    public Result<?> analyzeWeaknesses(@PathVariable Long userId) {
        return Result.success(testService.analyzeWeaknesses(userId));
    }

    /**
     * 推荐学习内容
     */
    @GetMapping("/recommend/{userId}")
    public Result<?> recommendLearningContent(@PathVariable Long userId) {
        return Result.success(testService.recommendLearningContent(userId));
    }
}
