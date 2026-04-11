package com.anti.fraud.modules.knowledge.service.impl;

import com.anti.fraud.modules.knowledge.entity.KnowledgeNode;
import com.anti.fraud.modules.knowledge.entity.KnowledgeEdge;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeNodeMapper;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeEdgeMapper;
import com.anti.fraud.modules.knowledge.service.KnowledgeGraphService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KnowledgeGraphServiceImpl extends ServiceImpl<KnowledgeNodeMapper, KnowledgeNode> implements KnowledgeGraphService {

    @Autowired
    private KnowledgeNodeMapper knowledgeNodeMapper;

    @Autowired
    private KnowledgeEdgeMapper knowledgeEdgeMapper;

    @Override
    public List<KnowledgeNode> getNodes() {
        return knowledgeNodeMapper.selectList(null);
    }

    @Override
    public List<KnowledgeEdge> getEdges() {
        return knowledgeEdgeMapper.selectList(null);
    }

    @Override
    public List<Map<String, Object>> getRelatedNodes(Long nodeId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 查询以该节点为源的边
        List<KnowledgeEdge> outgoingEdges = knowledgeEdgeMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<KnowledgeEdge>()
                .eq("source_node_id", nodeId)
                .eq("deleted", 0)
        );
        
        // 查询以该节点为目标的边
        List<KnowledgeEdge> incomingEdges = knowledgeEdgeMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<KnowledgeEdge>()
                .eq("target_node_id", nodeId)
                .eq("deleted", 0)
        );
        
        // 处理出边
        for (KnowledgeEdge edge : outgoingEdges) {
            KnowledgeNode targetNode = knowledgeNodeMapper.selectById(edge.getTargetNodeId());
            if (targetNode != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("node", targetNode);
                map.put("edge", edge);
                map.put("direction", "outgoing");
                result.add(map);
            }
        }
        
        // 处理入边
        for (KnowledgeEdge edge : incomingEdges) {
            KnowledgeNode sourceNode = knowledgeNodeMapper.selectById(edge.getSourceNodeId());
            if (sourceNode != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("node", sourceNode);
                map.put("edge", edge);
                map.put("direction", "incoming");
                result.add(map);
            }
        }
        
        return result;
    }

    @Override
    public Map<String, Object> buildGraphData() {
        Map<String, Object> result = new HashMap<>();
        
        List<KnowledgeNode> nodes = getNodes();
        List<KnowledgeEdge> edges = getEdges();
        
        // 转换节点数据
        List<Map<String, Object>> nodeData = new ArrayList<>();
        for (KnowledgeNode node : nodes) {
            Map<String, Object> nodeMap = new HashMap<>();
            nodeMap.put("id", node.getId());
            nodeMap.put("label", node.getNodeName());
            nodeMap.put("type", node.getNodeType());
            nodeMap.put("importance", node.getImportance());
            nodeData.add(nodeMap);
        }
        
        // 转换边数据
        List<Map<String, Object>> edgeData = new ArrayList<>();
        for (KnowledgeEdge edge : edges) {
            Map<String, Object> edgeMap = new HashMap<>();
            edgeMap.put("source", edge.getSourceNodeId());
            edgeMap.put("target", edge.getTargetNodeId());
            edgeMap.put("type", edge.getRelationshipType());
            edgeMap.put("strength", edge.getStrength());
            edgeData.add(edgeMap);
        }
        
        result.put("nodes", nodeData);
        result.put("edges", edgeData);
        
        return result;
    }

    @Override
    public List<KnowledgeNode> recommendKnowledge(Long userId) {
        // 这里可以根据用户的学习历史、薄弱点等信息进行智能推荐
        // 暂时返回所有节点
        return getNodes();
    }

    @Override
    public List<Map<String, Object>> recommendContent(Long userId, Integer limit) {
        // 这里可以根据用户的学习历史、知识图谱关系等进行智能推荐
        // 暂时返回示例数据
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 模拟推荐数据
        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", 1);
        item1.put("title", "电信诈骗防范指南");
        item1.put("type", "文章");
        item1.put("score", 0.95);
        result.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", 2);
        item2.put("title", "网络诈骗案例分析");
        item2.put("type", "视频");
        item2.put("score", 0.90);
        result.add(item2);
        
        Map<String, Object> item3 = new HashMap<>();
        item3.put("id", 3);
        item3.put("title", "刷单诈骗识别技巧");
        item3.put("type", "文章");
        item3.put("score", 0.85);
        result.add(item3);
        
        return result;
    }
}
