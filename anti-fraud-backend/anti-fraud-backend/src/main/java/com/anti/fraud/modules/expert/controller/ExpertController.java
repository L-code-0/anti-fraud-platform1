package com.anti.fraud.modules.expert.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.expert.dto.AdviceCreateDTO;
import com.anti.fraud.modules.expert.dto.AnalysisCreateDTO;
import com.anti.fraud.modules.expert.service.ExpertService;
import com.anti.fraud.modules.expert.vo.AdviceVO;
import com.anti.fraud.modules.expert.vo.AnalysisVO;
import com.anti.fraud.modules.expert.vo.ExpertStatsVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 专家咨询控制器
 */
@Tag(name = "专家咨询", description = "专家案例分析、专家建议管理")
@RestController
@RequestMapping("/expert")
@RequiredArgsConstructor
public class ExpertController {

    private final ExpertService expertService;

    // ==================== 统计 ====================

    @Operation(summary = "获取专家统计", description = "获取专家发布的案例分析和建议的统计数据")
    @GetMapping("/stats")
    public Result<ExpertStatsVO> getStats(
            @Parameter(description = "专家ID，不传则获取当前用户统计") @RequestParam(required = false) Long expertId,
            @Parameter(hidden = true) @RequestAttribute(value = "userId", required = false) Long userId) {
        if (expertId == null) {
            expertId = userId;
        }
        return Result.success(expertService.getStats(expertId));
    }

    // ==================== 案例分析 ====================

    @Operation(summary = "获取案例分析列表", description = "分页查询案例分析列表")
    @GetMapping("/analysis/list")
    public Result<Page<AnalysisVO>> getAnalysisList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "9") Integer size,
            @Parameter(description = "专家ID") @RequestParam(required = false) Long expertId,
            @Parameter(description = "案例类型：1-典型案例 2-新型诈骗") @RequestParam(required = false) Integer type) {
        return Result.success(expertService.getAnalysisPage(page, size, expertId, type));
    }

    @Operation(summary = "获取案例分析详情", description = "根据ID获取案例分析详细信息")
    @GetMapping("/analysis/{id}")
    public Result<AnalysisVO> getAnalysisDetail(
            @Parameter(description = "案例分析ID") @PathVariable Long id) {
        return Result.success(expertService.getAnalysisDetail(id));
    }

    @Operation(summary = "发布案例分析", description = "专家发布案例分析")
    @PostMapping("/analysis/create")
    public Result<Long> createAnalysis(
            @RequestBody AnalysisCreateDTO createDTO,
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Parameter(hidden = true) @RequestAttribute(value = "userName", required = false) String userName) {
        Long id = expertService.createAnalysis(createDTO, userId, userName != null ? userName : "专家");
        return Result.success(id, "发布成功");
    }

    @Operation(summary = "删除案例分析", description = "删除自己发布的案例分析")
    @DeleteMapping("/analysis/{id}")
    public Result<String> deleteAnalysis(
            @Parameter(description = "案例分析ID") @PathVariable Long id,
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId) {
        expertService.deleteAnalysis(id, userId);
        return Result.success("删除成功");
    }

    // ==================== 专家建议 ====================

    @Operation(summary = "获取专家建议列表", description = "分页查询专家建议列表")
    @GetMapping("/advice/list")
    public Result<Page<AdviceVO>> getAdviceList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "专家ID") @RequestParam(required = false) Long expertId,
            @Parameter(description = "建议分类") @RequestParam(required = false) String category) {
        return Result.success(expertService.getAdvicePage(page, size, expertId, category));
    }

    @Operation(summary = "获取专家建议详情", description = "根据ID获取专家建议详细信息")
    @GetMapping("/advice/{id}")
    public Result<AdviceVO> getAdviceDetail(
            @Parameter(description = "专家建议ID") @PathVariable Long id) {
        return Result.success(expertService.getAdviceDetail(id));
    }

    @Operation(summary = "发布专家建议", description = "专家发布建议")
    @PostMapping("/advice/create")
    public Result<Long> createAdvice(
            @RequestBody AdviceCreateDTO createDTO,
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Parameter(hidden = true) @RequestAttribute(value = "userName", required = false) String userName) {
        Long id = expertService.createAdvice(createDTO, userId, userName != null ? userName : "专家");
        return Result.success(id, "发布成功");
    }

    @Operation(summary = "删除专家建议", description = "删除自己发布的专家建议")
    @DeleteMapping("/advice/{id}")
    public Result<String> deleteAdvice(
            @Parameter(description = "专家建议ID") @PathVariable Long id,
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId) {
        expertService.deleteAdvice(id, userId);
        return Result.success("删除成功");
    }

    // ==================== 点赞 ====================

    @Operation(summary = "点赞", description = "为案例分析或专家建议点赞")
    @PostMapping("/thumb")
    public Result<String> thumb(
            @Parameter(description = "类型：1-案例分析 2-专家建议") @RequestParam Long type,
            @Parameter(description = "ID") @RequestParam Long id) {
        expertService.thumb(type, id);
        return Result.success("点赞成功");
    }
}
