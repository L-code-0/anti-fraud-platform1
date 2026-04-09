package com.anti.fraud.modules.test.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.test.dto.PaperCreateDTO;
import com.anti.fraud.modules.test.dto.QuestionCreateDTO;
import com.anti.fraud.modules.test.service.TestManageService;
import com.anti.fraud.modules.test.vo.PaperVO;
import com.anti.fraud.modules.test.vo.QuestionVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "测试管理-后台", description = "测试管理后台接口")
@RestController
@RequestMapping("/admin/test")
@RequiredArgsConstructor
public class TestManageController {

    private final TestManageService testManageService;

    // ==================== 题目管理 ====================

    @Operation(summary = "分页获取题目列表", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/questions")
    @PreAuthorize("hasRole('3') or hasRole('4') or hasRole('5')")
    public Result<Page<QuestionVO>> getQuestionList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer questionType) {
        return Result.success(testManageService.getQuestionPage(page, size, categoryId, questionType));
    }

    @Operation(summary = "创建题目", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/questions")
    @PreAuthorize("hasRole('3') or hasRole('4') or hasRole('5')")
    public Result<Void> createQuestion(@RequestBody QuestionCreateDTO createDTO) {
        testManageService.createQuestion(createDTO);
        return Result.successMsg("创建成功");
    }

    @Operation(summary = "更新题目", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/questions/{id}")
    @PreAuthorize("hasRole('3') or hasRole('4') or hasRole('5')")
    public Result<Void> updateQuestion(@PathVariable Long id, @RequestBody QuestionCreateDTO createDTO) {
        testManageService.updateQuestion(id, createDTO);
        return Result.successMsg("更新成功");
    }

    @Operation(summary = "删除题目", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/questions/{id}")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        testManageService.deleteQuestion(id);
        return Result.successMsg("删除成功");
    }

    // ==================== 试卷管理 ====================

    @Operation(summary = "分页获取试卷列表", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/papers")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Page<PaperVO>> getPaperList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(testManageService.getPaperPage(page, size));
    }

    @Operation(summary = "创建试卷", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/papers")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Void> createPaper(@RequestBody PaperCreateDTO createDTO) {
        testManageService.createPaper(createDTO);
        return Result.successMsg("创建成功");
    }

    @Operation(summary = "更新试卷", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/papers/{id}")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Void> updatePaper(@PathVariable Long id, @RequestBody PaperCreateDTO createDTO) {
        testManageService.updatePaper(id, createDTO);
        return Result.successMsg("更新成功");
    }

    @Operation(summary = "删除试卷", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/papers/{id}")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Void> deletePaper(@PathVariable Long id) {
        testManageService.deletePaper(id);
        return Result.successMsg("删除成功");
    }
}