package com.anti.fraud.modules.knowledge.controller;

import com.anti.fraud.modules.knowledge.service.KnowledgeGraphService;
import com.anti.fraud.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge/graph")
public class KnowledgeGraphController {

    @Autowired
    private KnowledgeGraphService knowledgeGraphService;

    /**
     * 获取知识图谱数据
     */
    @GetMapping("/data")
    public Result<Map<String, Object>> getGraphData() {
        Map<String, Object> graphData = knowledgeGraphService.buildGraphData();
        return Result.success(graphData);
    }

    /**
     * 获取相关节点
     */
    @GetMapping("/related/{nodeId}")
    public Result<?> getRelatedNodes(@PathVariable Long nodeId) {
        return Result.success(knowledgeGraphService.getRelatedNodes(nodeId));
    }

    /**
     * 智能推荐知识
     */
    @GetMapping("/recommend")
    public Result<?> recommendKnowledge(@RequestParam Long userId) {
        return Result.success(knowledgeGraphService.recommendKnowledge(userId));
    }

    /**
     * 智能推荐内容
     */
    @GetMapping("/recommend/content")
    public Result<?> recommendContent(@RequestParam Long userId, @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(knowledgeGraphService.recommendContent(userId, limit));
    }
}
