package com.anti.fraud.modules.exam.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.exam.entity.Question;
import com.anti.fraud.modules.exam.service.QuestionService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam/question")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "题目管理")
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "创建题目")
    @PostMapping("/create")
    public Result<Void> createQuestion(@RequestBody Question question) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        question.setCreatedBy(SecurityUtils.getCurrentUserName());
        question.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = questionService.createQuestion(question);
            if (success) {
                return Result.successMsg("创建题目成功");
            } else {
                return Result.fail("创建题目失败");
            }
        } catch (Exception e) {
            log.error("创建题目失败: {}", e.getMessage(), e);
            return Result.fail("创建题目失败");
        }
    }

    @Operation(summary = "更新题目")
    @PostMapping("/update")
    public Result<Void> updateQuestion(@RequestBody Question question) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        question.setUpdatedBy(SecurityUtils.getCurrentUserName());

        try {
            boolean success = questionService.updateQuestion(question);
            if (success) {
                return Result.successMsg("更新题目成功");
            } else {
                return Result.fail("更新题目失败");
            }
        } catch (Exception e) {
            log.error("更新题目失败: {}", e.getMessage(), e);
            return Result.fail("更新题目失败");
        }
    }

    @Operation(summary = "删除题目")
    @PostMapping("/delete/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        try {
            boolean success = questionService.deleteQuestion(id);
            if (success) {
                return Result.successMsg("删除题目成功");
            } else {
                return Result.fail("删除题目失败");
            }
        } catch (Exception e) {
            log.error("删除题目失败: {}", e.getMessage(), e);
            return Result.fail("删除题目失败");
        }
    }

    @Operation(summary = "获取题目详情")
    @GetMapping("/detail/{id}")
    public Result<Question> getQuestionById(@PathVariable Long id) {
        try {
            Question question = questionService.getQuestionById(id);
            if (question != null) {
                return Result.success("获取题目详情成功", question);
            } else {
                return Result.fail("题目不存在");
            }
        } catch (Exception e) {
            log.error("获取题目详情失败: {}", e.getMessage(), e);
            return Result.fail("获取题目详情失败");
        }
    }

    @Operation(summary = "获取题目列表")
    @GetMapping("/list")
    public Result<List<Question>> getQuestionList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Question> questions = questionService.getQuestionList(category, difficulty, page, size);
            return Result.success("获取题目列表成功", questions);
        } catch (Exception e) {
            log.error("获取题目列表失败: {}", e.getMessage(), e);
            return Result.fail("获取题目列表失败");
        }
    }

    @Operation(summary = "AI智能生成题目")
    @PostMapping("/generate")
    public Result<List<Question>> generateQuestions(@RequestBody Map<String, Object> params) {
        String category = (String) params.get("category");
        String difficulty = (String) params.get("difficulty");
        int count = (int) params.getOrDefault("count", 5);

        try {
            List<Question> questions = questionService.generateQuestions(category, difficulty, count);
            return Result.success("AI智能生成题目成功", questions);
        } catch (Exception e) {
            log.error("AI智能生成题目失败: {}", e.getMessage(), e);
            return Result.fail("AI智能生成题目失败");
        }
    }

    @Operation(summary = "批量生成题目")
    @PostMapping("/batch-generate")
    public Result<Map<String, Object>> batchGenerateQuestions(@RequestBody Map<String, Object> params) {
        try {
            Map<String, Object> result = questionService.batchGenerateQuestions(params);
            return Result.success("批量生成题目成功", result);
        } catch (Exception e) {
            log.error("批量生成题目失败: {}", e.getMessage(), e);
            return Result.fail("批量生成题目失败");
        }
    }

    @Operation(summary = "推荐题目")
    @GetMapping("/recommend")
    public Result<List<Question>> recommendQuestions(
            @RequestParam(defaultValue = "10") int count) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请先登录");
        }

        try {
            List<Question> questions = questionService.recommendQuestions(userId, count);
            return Result.success("推荐题目成功", questions);
        } catch (Exception e) {
            log.error("推荐题目失败: {}", e.getMessage(), e);
            return Result.fail("推荐题目失败");
        }
    }

    @Operation(summary = "更新题目使用情况")
    @PostMapping("/update-usage")
    public Result<Void> updateQuestionUsage(@RequestBody Map<String, Object> params) {
        Long id = Long.parseLong(params.get("id").toString());
        boolean isCorrect = (boolean) params.get("isCorrect");

        try {
            boolean success = questionService.updateQuestionUsage(id, isCorrect);
            if (success) {
                return Result.successMsg("更新题目使用情况成功");
            } else {
                return Result.fail("更新题目使用情况失败");
            }
        } catch (Exception e) {
            log.error("更新题目使用情况失败: {}", e.getMessage(), e);
            return Result.fail("更新题目使用情况失败");
        }
    }

    @Operation(summary = "获取题目统计信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getQuestionStats() {
        try {
            Map<String, Object> stats = questionService.getQuestionStats();
            return Result.success("获取题目统计信息成功", stats);
        } catch (Exception e) {
            log.error("获取题目统计信息失败: {}", e.getMessage(), e);
            return Result.fail("获取题目统计信息失败");
        }
    }
}
