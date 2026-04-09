package com.anti.fraud.modules.test.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.test.dto.AnswerSubmitDTO;
import com.anti.fraud.modules.test.service.TestService;
import com.anti.fraud.modules.test.vo.PaperVO;
import com.anti.fraud.modules.test.vo.QuestionVO;
import com.anti.fraud.modules.test.vo.TestResultVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "测试考试", description = "测试考试相关接口")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @Operation(summary = "获取可用试卷列表", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/papers")
    public Result<List<PaperVO>> getAvailablePapers() {
        return Result.success(testService.getAvailablePapers());
    }

    @Operation(summary = "获取试卷题目", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/papers/{paperId}/questions")
    public Result<List<QuestionVO>> getPaperQuestions(@PathVariable Long paperId) {
        return Result.success(testService.getPaperQuestions(paperId));
    }

    @Operation(summary = "开始测试", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/papers/{paperId}/start")
    public Result<Long> startTest(@PathVariable Long paperId) {
        return Result.success(testService.startTest(paperId));
    }

    @Operation(summary = "提交答案", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/submit")
    public Result<TestResultVO> submitAnswers(@RequestBody AnswerSubmitDTO submitDTO) {
        return Result.success(testService.submitAnswers(submitDTO));
    }

    @Operation(summary = "获取测试结果", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/records/{recordId}")
    public Result<TestResultVO> getTestResult(@PathVariable Long recordId) {
        return Result.success(testService.getTestResult(recordId));
    }

    @Operation(summary = "获取我的测试记录", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/my-records")
    public Result<Page<TestResultVO>> getMyTestRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(testService.getMyTestRecords(page, size));
    }

    @Operation(summary = "获取排行榜", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/papers/{paperId}/ranking")
    public Result<List<TestResultVO>> getRankingList(@PathVariable Long paperId) {
        return Result.success(testService.getRankingList(paperId));
    }
}