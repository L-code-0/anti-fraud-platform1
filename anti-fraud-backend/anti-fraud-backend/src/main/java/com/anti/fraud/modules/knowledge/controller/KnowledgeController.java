package com.anti.fraud.modules.knowledge.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.knowledge.dto.KnowledgeQueryDTO;
import com.anti.fraud.modules.knowledge.service.KnowledgeService;
import com.anti.fraud.modules.knowledge.vo.CategoryVO;
import com.anti.fraud.modules.knowledge.vo.KnowledgeDetailVO;
import com.anti.fraud.modules.knowledge.vo.KnowledgeListVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "知识学习", description = "知识学习相关接口")
@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @Operation(summary = "获取知识分类列表")
    @GetMapping("/categories")
    public Result<List<CategoryVO>> getCategories() {
        return Result.success(knowledgeService.getCategories());
    }

    @Operation(summary = "分页查询知识列表")
    @GetMapping("/list")
    public Result<Page<KnowledgeListVO>> getKnowledgeList(KnowledgeQueryDTO queryDTO) {
        return Result.success(knowledgeService.getKnowledgePage(queryDTO));
    }

    @Operation(summary = "获取知识详情", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/{id}")
    public Result<KnowledgeDetailVO> getKnowledgeDetail(@PathVariable Long id) {
        return Result.success(knowledgeService.getKnowledgeDetail(id));
    }

    @Operation(summary = "收藏知识", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/{id}/collect")
    public Result<Void> collectKnowledge(@PathVariable Long id) {
        knowledgeService.collectKnowledge(id);
        return Result.successMsg("收藏成功");
    }

    @Operation(summary = "取消收藏", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/{id}/collect")
    public Result<Void> uncollectKnowledge(@PathVariable Long id) {
        knowledgeService.uncollectKnowledge(id);
        return Result.successMsg("取消收藏成功");
    }

    @Operation(summary = "记录学习进度", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/{id}/progress")
    public Result<Void> recordProgress(@PathVariable Long id, @RequestParam Integer duration) {
        knowledgeService.recordProgress(id, duration);
        return Result.success();
    }

    @Operation(summary = "点赞", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/{id}/like")
    public Result<Void> likeKnowledge(@PathVariable Long id) {
        knowledgeService.likeKnowledge(id);
        return Result.successMsg("点赞成功");
    }

    @Operation(summary = "获取推荐知识", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/recommend")
    public Result<List<KnowledgeListVO>> getRecommendKnowledge() {
        return Result.success(knowledgeService.getRecommendKnowledge());
    }

    @Operation(summary = "获取热门知识")
    @GetMapping("/hot")
    public Result<List<KnowledgeListVO>> getHotKnowledge() {
        return Result.success(knowledgeService.getHotKnowledge());
    }
}
