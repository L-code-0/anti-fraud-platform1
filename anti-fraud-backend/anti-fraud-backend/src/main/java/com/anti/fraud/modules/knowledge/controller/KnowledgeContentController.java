package com.anti.fraud.modules.knowledge.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.knowledge.service.KnowledgeContentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 知识内容控制器
 */
@RestController
@RequestMapping("/knowledge/content")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "知识内容服务")
public class KnowledgeContentController {
    
    private final KnowledgeContentService knowledgeContentService;
    
    @Operation(summary = "获取知识内容详情")
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getKnowledgeDetail(
            @ApiParam(value = "知识内容ID", required = true) @PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            Map<String, Object> detail = knowledgeContentService.getKnowledgeDetail(id, userId);
            if (detail != null) {
                return Result.success(detail);
            } else {
                return Result.fail("知识内容不存在");
            }
        } catch (Exception e) {
            log.error("获取知识内容详情失败: {}", e.getMessage(), e);
            return Result.fail("获取知识内容详情失败");
        }
    }
    
    @Operation(summary = "学习知识内容")
    @PostMapping("/learn/{id}")
    public Result<Void> learnKnowledge(
            @ApiParam(value = "知识内容ID", required = true) @PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            boolean success = knowledgeContentService.learnKnowledge(id, userId);
            if (success) {
                return Result.successMsg("学习成功");
            } else {
                return Result.fail("学习失败");
            }
        } catch (Exception e) {
            log.error("学习知识内容失败: {}", e.getMessage(), e);
            return Result.fail("学习失败");
        }
    }
}
