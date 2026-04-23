package com.anti.fraud.modules.knowledge.service.impl;

import com.anti.fraud.modules.knowledge.mapper.KnowledgeContentMapper;
import com.anti.fraud.modules.knowledge.service.KnowledgeContentService;
import com.anti.fraud.modules.points.service.PointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 知识内容服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeContentServiceImpl implements KnowledgeContentService {
    
    private final KnowledgeContentMapper knowledgeContentMapper;
    private final PointsService pointsService;
    
    @Override
    public Map<String, Object> getKnowledgeDetail(Long id, Long userId) {
        try {
            // 增加浏览量
            incrementViewCount(id);
            // 获取知识内容详情
            return knowledgeContentMapper.selectKnowledgeDetail(id, userId);
        } catch (Exception e) {
            log.error("获取知识内容详情失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public boolean incrementViewCount(Long id) {
        try {
            knowledgeContentMapper.incrementViewCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加浏览量失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean learnKnowledge(Long id, Long userId) {
        try {
            // 增加积分（学习知识获得10积分）
            pointsService.addPoints(userId, 10, "knowledge", id, "学习知识内容");
            log.info("用户 {} 学习知识内容 {} 获得积分", userId, id);
            return true;
        } catch (Exception e) {
            log.error("学习知识内容失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
