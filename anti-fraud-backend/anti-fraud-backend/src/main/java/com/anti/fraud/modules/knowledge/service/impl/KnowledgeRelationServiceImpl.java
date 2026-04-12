package com.anti.fraud.modules.knowledge.service.impl;

import com.anti.fraud.modules.knowledge.entity.KnowledgeContent;
import com.anti.fraud.modules.knowledge.entity.KnowledgeRelation;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeContentMapper;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeRelationMapper;
import com.anti.fraud.modules.knowledge.service.KnowledgeRelationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeRelationServiceImpl implements KnowledgeRelationService {

    private final KnowledgeRelationMapper knowledgeRelationMapper;
    private final KnowledgeContentMapper knowledgeContentMapper;

    @Override
    public boolean createRelation(KnowledgeRelation relation) {
        try {
            knowledgeRelationMapper.insert(relation);
            log.info("创建知识关联成功: sourceId={}, targetId={}, relationType={}", 
                    relation.getSourceId(), relation.getTargetId(), relation.getRelationType());
            return true;
        } catch (Exception e) {
            log.error("创建知识关联失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteRelation(Long id) {
        try {
            knowledgeRelationMapper.deleteById(id);
            log.info("删除知识关联成功: id={}", id);
            return true;
        } catch (Exception e) {
            log.error("删除知识关联失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<KnowledgeRelation> getRelationsByKnowledgeId(Long knowledgeId) {
        try {
            LambdaQueryWrapper<KnowledgeRelation> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(KnowledgeRelation::getSourceId, knowledgeId)
                    .eq(KnowledgeRelation::getStatus, 1);
            return knowledgeRelationMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取知识关联列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> getKnowledgeGraph(Long knowledgeId, int depth) {
        try {
            Map<String, Object> graph = new HashMap<>();
            List<Map<String, Object>> nodes = new ArrayList<>();
            List<Map<String, Object>> links = new ArrayList<>();
            
            // 递归构建图谱
            buildGraph(knowledgeId, depth, 0, nodes, links, new HashSet<>());
            
            graph.put("nodes", nodes);
            graph.put("links", links);
            
            return graph;
        } catch (Exception e) {
            log.error("获取知识图谱数据失败: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    @Override
    public int generateRelations(Long knowledgeId) {
        try {
            KnowledgeContent sourceContent = knowledgeContentMapper.selectById(knowledgeId);
            if (sourceContent == null) {
                return 0;
            }
            
            // 查找相关知识
            List<KnowledgeContent> relatedContents = findRelatedContents(sourceContent);
            int count = 0;
            
            for (KnowledgeContent targetContent : relatedContents) {
                // 检查是否已存在关联
                LambdaQueryWrapper<KnowledgeRelation> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(KnowledgeRelation::getSourceId, knowledgeId)
                        .eq(KnowledgeRelation::getTargetId, targetContent.getId());
                if (knowledgeRelationMapper.selectCount(queryWrapper) == 0) {
                    KnowledgeRelation relation = new KnowledgeRelation();
                    relation.setSourceId(knowledgeId);
                    relation.setTargetId(targetContent.getId());
                    relation.setRelationType("related");
                    relation.setRelationName("相关");
                    relation.setWeight(calculateWeight(sourceContent, targetContent));
                    relation.setStatus(1);
                    knowledgeRelationMapper.insert(relation);
                    count++;
                }
            }
            
            log.info("为知识生成关联成功: knowledgeId={}, count={}", knowledgeId, count);
            return count;
        } catch (Exception e) {
            log.error("生成知识关联失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public List<Map<String, Object>> getRelatedKnowledge(Long knowledgeId, int limit) {
        try {
            List<KnowledgeRelation> relations = getRelationsByKnowledgeId(knowledgeId);
            List<Map<String, Object>> related = new ArrayList<>();
            
            for (KnowledgeRelation relation : relations) {
                if (related.size() >= limit) {
                    break;
                }
                
                KnowledgeContent targetContent = knowledgeContentMapper.selectById(relation.getTargetId());
                if (targetContent != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", targetContent.getId());
                    item.put("title", targetContent.getTitle());
                    item.put("category", targetContent.getCategory());
                    item.put("viewCount", targetContent.getViewCount());
                    item.put("relationType", relation.getRelationType());
                    item.put("relationName", relation.getRelationName());
                    item.put("weight", relation.getWeight());
                    related.add(item);
                }
            }
            
            return related;
        } catch (Exception e) {
            log.error("获取相关知识推荐失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 递归构建知识图谱
     */
    private void buildGraph(Long knowledgeId, int maxDepth, int currentDepth, 
                          List<Map<String, Object>> nodes, 
                          List<Map<String, Object>> links, 
                          Set<Long> visited) {
        if (currentDepth >= maxDepth || visited.contains(knowledgeId)) {
            return;
        }
        
        visited.add(knowledgeId);
        
        // 添加节点
        KnowledgeContent content = knowledgeContentMapper.selectById(knowledgeId);
        if (content != null) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", content.getId());
            node.put("title", content.getTitle());
            node.put("category", content.getCategory());
            node.put("depth", currentDepth);
            nodes.add(node);
            
            // 添加关联
            List<KnowledgeRelation> relations = getRelationsByKnowledgeId(knowledgeId);
            for (KnowledgeRelation relation : relations) {
                Map<String, Object> link = new HashMap<>();
                link.put("source", relation.getSourceId());
                link.put("target", relation.getTargetId());
                link.put("type", relation.getRelationType());
                link.put("name", relation.getRelationName());
                link.put("weight", relation.getWeight());
                links.add(link);
                
                // 递归处理目标节点
                buildGraph(relation.getTargetId(), maxDepth, currentDepth + 1, nodes, links, visited);
            }
        }
    }

    /**
     * 查找相关知识
     */
    private List<KnowledgeContent> findRelatedContents(KnowledgeContent sourceContent) {
        List<KnowledgeContent> relatedContents = new ArrayList<>();
        
        // 按分类查找
        LambdaQueryWrapper<KnowledgeContent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KnowledgeContent::getCategory, sourceContent.getCategory())
                .ne(KnowledgeContent::getId, sourceContent.getId())
                .orderByDesc(KnowledgeContent::getViewCount)
                .last("LIMIT 10");
        relatedContents.addAll(knowledgeContentMapper.selectList(queryWrapper));
        
        return relatedContents;
    }

    /**
     * 计算关联权重
     */
    private int calculateWeight(KnowledgeContent source, KnowledgeContent target) {
        int weight = 0;
        
        // 分类相同，权重+3
        if (source.getCategory().equals(target.getCategory())) {
            weight += 3;
        }
        
        // 标题包含相同关键词，权重+2
        if (hasCommonKeywords(source.getTitle(), target.getTitle())) {
            weight += 2;
        }
        
        // 内容包含相同关键词，权重+1
        if (hasCommonKeywords(source.getContent(), target.getContent())) {
            weight += 1;
        }
        
        return Math.max(1, weight);
    }

    /**
     * 检查是否有共同关键词
     */
    private boolean hasCommonKeywords(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return false;
        }
        
        // 简单的关键词提取（实际项目中可以使用更复杂的算法）
        String[] keywords = {"诈骗", "防范", "电话", "短信", "网络", "银行卡", "密码", "验证码"};
        for (String keyword : keywords) {
            if (text1.contains(keyword) && text2.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
}
