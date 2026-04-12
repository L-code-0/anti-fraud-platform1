package com.anti.fraud.modules.knowledge.service;

import com.anti.fraud.modules.knowledge.entity.KnowledgeRelation;
import java.util.List;
import java.util.Map;

public interface KnowledgeRelationService {

    /**
     * 创建知识关联
     * @param relation 关联信息
     * @return 是否成功
     */
    boolean createRelation(KnowledgeRelation relation);

    /**
     * 删除知识关联
     * @param id 关联ID
     * @return 是否成功
     */
    boolean deleteRelation(Long id);

    /**
     * 获取知识的关联列表
     * @param knowledgeId 知识ID
     * @return 关联列表
     */
    List<KnowledgeRelation> getRelationsByKnowledgeId(Long knowledgeId);

    /**
     * 获取知识图谱数据
     * @param knowledgeId 知识ID
     * @param depth 深度
     * @return 图谱数据
     */
    Map<String, Object> getKnowledgeGraph(Long knowledgeId, int depth);

    /**
     * 自动生成知识关联
     * @param knowledgeId 知识ID
     * @return 生成的关联数量
     */
    int generateRelations(Long knowledgeId);

    /**
     * 获取相关知识推荐
     * @param knowledgeId 知识ID
     * @param limit 数量限制
     * @return 推荐知识列表
     */
    List<Map<String, Object>> getRelatedKnowledge(Long knowledgeId, int limit);
}
