package com.anti.fraud.modules.knowledge.service;

import java.util.Map;

/**
 * 知识内容服务接口
 */
public interface KnowledgeContentService {
    
    /**
     * 获取知识内容详情
     * @param id 知识内容ID
     * @param userId 用户ID
     * @return 知识内容详情
     */
    Map<String, Object> getKnowledgeDetail(Long id, Long userId);
    
    /**
     * 增加浏览量
     * @param id 知识内容ID
     * @return 是否成功
     */
    boolean incrementViewCount(Long id);
    
    /**
     * 学习知识内容（增加积分）
     * @param id 知识内容ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean learnKnowledge(Long id, Long userId);
}
