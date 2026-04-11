package com.anti.fraud.modules.knowledge.service;

import com.anti.fraud.modules.knowledge.entity.KnowledgeNode;
import com.anti.fraud.modules.knowledge.entity.KnowledgeEdge;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

public interface KnowledgeGraphService extends IService<KnowledgeNode> {

    /**
     * 获取知识图谱节点列表
     */
    List<KnowledgeNode> getNodes();

    /**
     * 获取知识图谱边列表
     */
    List<KnowledgeEdge> getEdges();

    /**
     * 根据节点ID获取相关节点
     */
    List<Map<String, Object>> getRelatedNodes(Long nodeId);

    /**
     * 构建知识图谱数据
     */
    Map<String, Object> buildGraphData();

    /**
     * 根据用户学习历史推荐相关知识
     */
    List<KnowledgeNode> recommendKnowledge(Long userId);

    /**
     * 智能推荐知识内容
     */
    List<Map<String, Object>> recommendContent(Long userId, Integer limit);
}
