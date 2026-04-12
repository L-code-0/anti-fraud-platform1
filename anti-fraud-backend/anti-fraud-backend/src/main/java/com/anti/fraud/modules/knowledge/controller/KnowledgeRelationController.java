package com.anti.fraud.modules.knowledge.controller;

import com.anti.fraud.common.result.Result;
import com.anti.fraud.modules.knowledge.entity.KnowledgeRelation;
import com.anti.fraud.modules.knowledge.service.KnowledgeRelationService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/knowledge/relation")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "知识关联")
public class KnowledgeRelationController {

    private final KnowledgeRelationService knowledgeRelationService;

    @Operation(summary = "创建知识关联")
    @PostMapping("/create")
    public Result<Void> createRelation(@RequestBody KnowledgeRelation relation) {
        if (relation.getSourceId() == null || relation.getTargetId() == null) {
            return Result.fail("源知识ID和目标知识ID不能为空");
        }

        try {
            boolean success = knowledgeRelationService.createRelation(relation);
            if (success) {
                return Result.successMsg("创建关联成功");
            } else {
                return Result.fail("创建关联失败");
            }
        } catch (Exception e) {
            log.error("创建知识关联失败: {}", e.getMessage(), e);
            return Result.fail("创建关联失败");
        }
    }

    @Operation(summary = "删除知识关联")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteRelation(@PathVariable Long id) {
        try {
            boolean success = knowledgeRelationService.deleteRelation(id);
            if (success) {
                return Result.successMsg("删除关联成功");
            } else {
                return Result.fail("删除关联失败");
            }
        } catch (Exception e) {
            log.error("删除知识关联失败: {}", e.getMessage(), e);
            return Result.fail("删除关联失败");
        }
    }

    @Operation(summary = "获取知识的关联列表")
    @GetMapping("/list/{knowledgeId}")
    public Result<List<KnowledgeRelation>> getRelationsByKnowledgeId(@PathVariable Long knowledgeId) {
        try {
            List<KnowledgeRelation> relations = knowledgeRelationService.getRelationsByKnowledgeId(knowledgeId);
            return Result.success("获取关联列表成功", relations);
        } catch (Exception e) {
            log.error("获取知识关联列表失败: {}", e.getMessage(), e);
            return Result.fail("获取关联列表失败");
        }
    }

    @Operation(summary = "获取知识图谱数据")
    @GetMapping("/graph/{knowledgeId}")
    public Result<Map<String, Object>> getKnowledgeGraph(
            @PathVariable Long knowledgeId,
            @RequestParam(defaultValue = "2") Integer depth) {
        try {
            Map<String, Object> graph = knowledgeRelationService.getKnowledgeGraph(knowledgeId, depth);
            return Result.success("获取知识图谱数据成功", graph);
        } catch (Exception e) {
            log.error("获取知识图谱数据失败: {}", e.getMessage(), e);
            return Result.fail("获取知识图谱数据失败");
        }
    }

    @Operation(summary = "自动生成知识关联")
    @PostMapping("/generate/{knowledgeId}")
    public Result<Integer> generateRelations(@PathVariable Long knowledgeId) {
        try {
            int count = knowledgeRelationService.generateRelations(knowledgeId);
            return Result.success("生成关联成功", count);
        } catch (Exception e) {
            log.error("生成知识关联失败: {}", e.getMessage(), e);
            return Result.fail("生成关联失败");
        }
    }

    @Operation(summary = "获取相关知识推荐")
    @GetMapping("/related/{knowledgeId}")
    public Result<List<Map<String, Object>>> getRelatedKnowledge(
            @PathVariable Long knowledgeId,
            @RequestParam(defaultValue = "5") Integer limit) {
        try {
            List<Map<String, Object>> related = knowledgeRelationService.getRelatedKnowledge(knowledgeId, limit);
            return Result.success("获取相关知识推荐成功", related);
        } catch (Exception e) {
            log.error("获取相关知识推荐失败: {}", e.getMessage(), e);
            return Result.fail("获取相关知识推荐失败");
        }
    }
}
