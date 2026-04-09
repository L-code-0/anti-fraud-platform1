package com.anti.fraud.modules.knowledge.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.knowledge.dto.KnowledgeCreateDTO;
import com.anti.fraud.modules.knowledge.service.KnowledgeManageService;
import com.anti.fraud.modules.knowledge.vo.KnowledgeListVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "知识管理-后台", description = "知识管理后台接口")
@RestController
@RequestMapping("/admin/knowledge")
@RequiredArgsConstructor
public class KnowledgeManageController {

    private final KnowledgeManageService knowledgeManageService;

    @Operation(summary = "分页获取知识列表", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/list")
    @PreAuthorize("hasRole('3') or hasRole('4') or hasRole('5')")
    public Result<Page<KnowledgeListVO>> getKnowledgeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer auditStatus) {
        return Result.success(knowledgeManageService.getKnowledgePage(page, size, categoryId, status, auditStatus));
    }

    @Operation(summary = "创建知识", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping
    @PreAuthorize("hasRole('3') or hasRole('4') or hasRole('5')")
    public Result<Void> createKnowledge(@RequestBody KnowledgeCreateDTO createDTO) {
        knowledgeManageService.createKnowledge(createDTO);
        return Result.successMsg("创建成功");
    }

    @Operation(summary = "更新知识", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('3') or hasRole('4') or hasRole('5')")
    public Result<Void> updateKnowledge(@PathVariable Long id, @RequestBody KnowledgeCreateDTO createDTO) {
        knowledgeManageService.updateKnowledge(id, createDTO);
        return Result.successMsg("更新成功");
    }

    @Operation(summary = "删除知识", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('3') or hasRole('4') or hasRole('5')")
    public Result<Void> deleteKnowledge(@PathVariable Long id) {
        knowledgeManageService.deleteKnowledge(id);
        return Result.successMsg("删除成功");
    }

    @Operation(summary = "审核知识", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}/audit")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Void> auditKnowledge(@PathVariable Long id, @RequestParam Integer auditStatus) {
        knowledgeManageService.auditKnowledge(id, auditStatus);
        return Result.successMsg("审核成功");
    }

    @Operation(summary = "上架/下架知识", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Void> updateKnowledgeStatus(@PathVariable Long id, @RequestParam Integer status) {
        knowledgeManageService.updateKnowledgeStatus(id, status);
        return Result.successMsg("操作成功");
    }

    @Operation(summary = "设置热门/推荐", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}/recommend")
    @PreAuthorize("hasRole('3') or hasRole('5')")
    public Result<Void> setRecommend(@PathVariable Long id,
                                     @RequestParam(required = false) Boolean isHot,
                                     @RequestParam(required = false) Boolean isRecommend) {
        knowledgeManageService.setRecommend(id, isHot, isRecommend);
        return Result.successMsg("设置成功");
    }
}