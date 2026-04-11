package com.anti.fraud.modules.qa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.qa.dto.AnswerDTO;
import com.anti.fraud.modules.qa.dto.QuestionDTO;
import com.anti.fraud.modules.qa.entity.FollowUpQuestion;
import com.anti.fraud.modules.qa.service.QAService;
import com.anti.fraud.modules.qa.vo.QuestionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        return Result.success("提问成功", questionId);
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
        return Result.success("回答成功", answerId);
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

    @Operation(summary = "追问", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/follow-up")
    public Result<Long> followUpQuestion(
            @RequestParam Long questionId,
            @RequestParam Long answerId,
            @RequestParam String content) {
        Long followUpId = qaService.followUpQuestion(questionId, answerId, content);
        return Result.success("追问成功", followUpId);
    }

    @Operation(summary = "回答追问", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/follow-up/{id}/answer")
    public Result<Void> answerFollowUp(
            @PathVariable Long id,
            @RequestParam String content) {
        qaService.answerFollowUp(id, content);
        return Result.successMsg("回答追问成功");
    }

    @Operation(summary = "获取追问列表")
    @GetMapping("/follow-ups/{questionId}")
    public Result<List<FollowUpQuestion>> getFollowUpQuestions(@PathVariable Long questionId) {
        return Result.success(qaService.getFollowUpQuestions(questionId));
    }

    @Operation(summary = "获取用户的追问列表", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/user/follow-ups")
    public Result<List<FollowUpQuestion>> getUserFollowUpQuestions(
            @RequestParam Long userId) {
        return Result.success(qaService.getUserFollowUpQuestions(userId));
    }

    @Operation(summary = "获取用户的采纳率")
    @GetMapping("/user/acceptance-rate")
    public Result<Map<String, Object>> getUserAcceptanceRate(
            @RequestParam Long userId) {
        return Result.success(qaService.getUserAcceptanceRate(userId));
    }

    @Operation(summary = "获取专家的采纳率排行榜")
    @GetMapping("/experts/acceptance-rate-rank")
    public Result<Page<Map<String, Object>>> getExpertAcceptanceRateRank(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(qaService.getExpertAcceptanceRateRank(page, size));
    }
}

