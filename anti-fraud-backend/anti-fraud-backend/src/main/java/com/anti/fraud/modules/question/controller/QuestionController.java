package com.anti.fraud.modules.question.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.question.entity.Question;
import com.anti.fraud.modules.question.service.QuestionService;
import com.anti.fraud.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 题目控制器
 */
@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "题目管理")
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "新增题目")
    @PostMapping("/add")
    public Result<Void> addQuestion(@ApiParam(value = "题目信息", required = true) @RequestBody Question question) {
        try {
            boolean success = questionService.addQuestion(question);
            if (success) {
                return Result.successMsg("新增题目成功");
            } else {
                return Result.fail("新增题目失败");
            }
        } catch (Exception e) {
            log.error("新增题目失败: {}", e.getMessage(), e);
            return Result.fail("新增题目失败");
        }
    }

    @Operation(summary = "更新题目")
    @PutMapping("/update")
    public Result<Void> updateQuestion(@ApiParam(value = "题目信息", required = true) @RequestBody Question question) {
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
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteQuestion(@ApiParam(value = "题目ID", required = true) @PathVariable Long id) {
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
    public Result<Question> getQuestionById(@ApiParam(value = "题目ID", required = true) @PathVariable Long id) {
        try {
            Question question = questionService.getQuestionById(id);
            if (question != null) {
                return Result.success(question);
            } else {
                return Result.fail("题目不存在");
            }
        } catch (Exception e) {
            log.error("获取题目详情失败: {}", e.getMessage(), e);
            return Result.fail("获取题目详情失败");
        }
    }

    @Operation(summary = "分页查询题目")
    @PostMapping("/list")
    public Result<Map<String, Object>> getQuestionList(
            @ApiParam(value = "查询参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "页码", required = true) @RequestParam Integer page,
            @ApiParam(value = "每页大小", required = true) @RequestParam Integer size) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            Map<String, Object> result = questionService.getQuestionList(params, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询题目列表失败: {}", e.getMessage(), e);
            return Result.fail("查询题目列表失败");
        }
    }

    @Operation(summary = "随机生成题目")
    @PostMapping("/generate")
    public Result<List<Question>> generateQuestions(
            @ApiParam(value = "筛选参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "题目数量", required = true) @RequestParam Integer count) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            List<Question> questions = questionService.generateQuestions(params, count);
            return Result.success(questions);
        } catch (Exception e) {
            log.error("随机生成题目失败: {}", e.getMessage(), e);
            return Result.fail("随机生成题目失败");
        }
    }

    @Operation(summary = "AI智能出题")
    @PostMapping("/ai/generate")
    public Result<Question> generateQuestionByAI(
            @ApiParam(value = "出题参数", required = false) @RequestBody(required = false) Map<String, Object> params) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            Question question = questionService.generateQuestionByAI(params);
            if (question != null) {
                return Result.success(question);
            } else {
                return Result.fail("AI智能出题失败");
            }
        } catch (Exception e) {
            log.error("AI智能出题失败: {}", e.getMessage(), e);
            return Result.fail("AI智能出题失败");
        }
    }

    @Operation(summary = "批量AI智能出题")
    @PostMapping("/ai/batch-generate")
    public Result<List<Question>> batchGenerateQuestionsByAI(
            @ApiParam(value = "出题参数", required = false) @RequestBody(required = false) Map<String, Object> params,
            @ApiParam(value = "题目数量", required = true) @RequestParam Integer count) {
        try {
            if (params == null) {
                params = new java.util.HashMap<>();
            }
            List<Question> questions = questionService.batchGenerateQuestionsByAI(params, count);
            return Result.success(questions);
        } catch (Exception e) {
            log.error("批量AI智能出题失败: {}", e.getMessage(), e);
            return Result.fail("批量AI智能出题失败");
        }
    }

    @Operation(summary = "统计题目信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getQuestionStats() {
        try {
            Map<String, Object> stats = questionService.getQuestionStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计题目信息失败: {}", e.getMessage(), e);
            return Result.fail("统计题目信息失败");
        }
    }

    @Operation(summary = "验证答案")
    @PostMapping("/verify-answer")
    public Result<Map<String, Object>> verifyAnswer(
            @ApiParam(value = "题目ID", required = true) @RequestParam Long questionId,
            @ApiParam(value = "用户答案", required = true) @RequestParam String answer) {
        try {
            Map<String, Object> result = questionService.verifyAnswer(questionId, answer);
            return Result.success(result);
        } catch (Exception e) {
            log.error("验证答案失败: {}", e.getMessage(), e);
            return Result.fail("验证答案失败");
        }
    }
}
