package com.anti.fraud.modules.analysis.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.analysis.service.AnalysisService;
import com.anti.fraud.modules.analysis.vo.LearningReportVO;
import com.anti.fraud.modules.analysis.vo.WeaknessVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "学习分析", description = "学习数据分析和推荐接口")
@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @Operation(summary = "获取学习报告", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/report")
    public Result<LearningReportVO> getLearningReport() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(analysisService.getLearningReport(userId));
    }

    @Operation(summary = "获取薄弱知识点", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/weaknesses")
    public Result<List<WeaknessVO>> getWeaknesses() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(analysisService.getWeaknesses(userId));
    }

    @Operation(summary = "获取学习推荐", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/recommendations")
    public Result<List<?>> getRecommendations() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(analysisService.getRecommendations(userId));
    }
}