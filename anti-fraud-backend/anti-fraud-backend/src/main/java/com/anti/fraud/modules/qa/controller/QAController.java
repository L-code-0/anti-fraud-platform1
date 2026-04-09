package com.anti.fraud.modules.qa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.qa.dto.AnswerDTO;
import com.anti.fraud.modules.qa.dto.QuestionDTO;
import com.anti.fraud.modules.qa.service.QAService;
import com.anti.fraud.modules.qa.vo.QuestionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "在线答疑", description = "专家在线答疑接口")
@RestController
@RequestMapping("/qa")
@RequiredArgsConstructor
public class QAController {

    private final QAService qaService;

    @Operation(summary = "提问", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/question")
    public Result<Long> askQuestion(@RequestBody QuestionDTO dto) {
        Long questionId = qaService.askQuestion(dto);
        return Result.success(questionId, "提问成功");
    }

    @Operation(summary = "问题列表")
    @GetMapping("/questions")
    public Result<Page<QuestionVO>> getQuestions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        return Result.success(qaService.getQuestions(page, size, status));
    }

    @Operation(summary = "问题详情")
    @GetMapping("/question/{id}")
    public Result<QuestionVO> getQuestionDetail(@PathVariable Long id) {
        return Result.success(qaService.getQuestionDetail(id));
    }

    @Operation(summary = "回答问题（专家）", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/answer")
    public Result<Long> answerQuestion(@RequestBody AnswerDTO dto) {
        Long answerId = qaService.answerQuestion(dto);
        return Result.success(answerId, "回答成功");
    }

    @Operation(summary = "采纳回答", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/answer/{id}/accept")
    public Result<Void> acceptAnswer(@PathVariable Long id) {
        qaService.acceptAnswer(id);
        return Result.successMsg("采纳成功");
    }

    @Operation(summary = "点赞回答", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/answer/{id}/like")
    public Result<Void> likeAnswer(@PathVariable Long id) {
        qaService.likeAnswer(id);
        return Result.successMsg("点赞成功");
    }

    @Operation(summary = "删除问题", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/question/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        qaService.deleteQuestion(id);
        return Result.successMsg("删除成功");
    }
}

