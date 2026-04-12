package com.anti.fraud.modules.ai.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.ai.entity.AIQuestion;
import com.anti.fraud.modules.ai.service.AIService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "AI智能问答")
public class AIController {

    private final AIService aiService;

    @Operation(summary = "智能问答")
    @PostMapping("/ask")
    public Result<Map<String, Object>> askQuestion(@RequestBody Map<String, String> params) {
        String question = params.get("question");
        String category = params.get("category");

        if (question == null || question.isEmpty()) {
            return Result.fail("问题不能为空");
        }

        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            userId = 0L; // 未登录用户
        }

        try {
            Map<String, Object> result = aiService.askQuestion(userId, question, category);
            return Result.success("问答成功", result);
        } catch (Exception e) {
            log.error("智能问答失败: {}", e.getMessage(), e);
            return Result.fail("问答失败");
        }
    }

    @Operation(summary = "获取问答历史")
    @GetMapping("/history")
    public Result<List<AIQuestion>> getQuestionHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<AIQuestion> history = aiService.getQuestionHistory(userId, page, size);
            return Result.success("获取历史成功", history);
        } catch (Exception e) {
            log.error("获取问答历史失败: {}", e.getMessage(), e);
            return Result.fail("获取历史失败");
        }
    }

    @Operation(summary = "获取热门问题")
    @GetMapping("/hot")
    public Result<List<AIQuestion>> getHotQuestions(
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<AIQuestion> hotQuestions = aiService.getHotQuestions(limit);
            return Result.success("获取热门问题成功", hotQuestions);
        } catch (Exception e) {
            log.error("获取热门问题失败: {}", e.getMessage(), e);
            return Result.fail("获取热门问题失败");
        }
    }

    @Operation(summary = "标记问题为公开")
    @PostMapping("/question/{id}/public")
    public Result<Void> markAsPublic(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = aiService.markAsPublic(id, userId);
            if (success) {
                return Result.successMsg("标记成功");
            } else {
                return Result.fail("标记失败");
            }
        } catch (Exception e) {
            log.error("标记问题为公开失败: {}", e.getMessage(), e);
            return Result.fail("标记失败");
        }
    }

    @Operation(summary = "标记问题为私有")
    @PostMapping("/question/{id}/private")
    public Result<Void> markAsPrivate(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = aiService.markAsPrivate(id, userId);
            if (success) {
                return Result.successMsg("标记成功");
            } else {
                return Result.fail("标记失败");
            }
        } catch (Exception e) {
            log.error("标记问题为私有失败: {}", e.getMessage(), e);
            return Result.fail("标记失败");
        }
    }

    @Operation(summary = "删除问题")
    @DeleteMapping("/question/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            boolean success = aiService.deleteQuestion(id, userId);
            if (success) {
                return Result.successMsg("删除成功");
            } else {
                return Result.fail("删除失败");
            }
        } catch (Exception e) {
            log.error("删除问题失败: {}", e.getMessage(), e);
            return Result.fail("删除失败");
        }
    }
}
