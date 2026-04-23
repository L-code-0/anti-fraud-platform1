package com.anti.fraud.modules.knowledge.service.impl;

import com.anti.fraud.modules.knowledge.entity.KnowledgeNode;
import com.anti.fraud.modules.knowledge.entity.KnowledgeEdge;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeNodeMapper;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeEdgeMapper;
import com.anti.fraud.modules.knowledge.service.KnowledgeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 知识图谱服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeNodeMapper, KnowledgeNode> implements KnowledgeService {

    private final KnowledgeNodeMapper knowledgeNodeMapper;
    private final KnowledgeEdgeMapper knowledgeEdgeMapper;

    @Override
    @Transactional
    public String createNode(KnowledgeNode knowledgeNode) {
        try {
            // 生成节点ID
            String nodeId = UUID.randomUUID().toString();
            knowledgeNode.setNodeId(nodeId);
            knowledgeNode.setHotness(0);
            knowledgeNode.setStatus(1); // 启用
            knowledgeNode.setDeleted(0);
            knowledgeNode.setCreateTime(LocalDateTime.now());
            knowledgeNode.setUpdateTime(LocalDateTime.now());

            boolean success = save(knowledgeNode);
            if (success) {
                return nodeId;
            } else {
                throw new RuntimeException("创建节点失败");
            }
        } catch (Exception e) {
            log.error("创建节点失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建节点失败");
        }
    }

    @Override
    @Transactional
    public boolean updateNode(KnowledgeNode knowledgeNode) {
        try {
            knowledgeNode.setUpdateTime(LocalDateTime.now());
            return updateById(knowledgeNode);
        } catch (Exception e) {
            log.error("更新节点失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteNode(String nodeId) {
        try {
            KnowledgeNode node = knowledgeNodeMapper.selectByNodeId(nodeId);
            if (node != null) {
                node.setDeleted(1);
                node.setUpdateTime(LocalDateTime.now());
                boolean success = updateById(node);
                if (success) {
                    // 删除节点相关的所有边
                    knowledgeEdgeMapper.deleteByNodeId(nodeId);
                }
                return success;
            }
            return false;
        } catch (Exception e) {
            log.error("删除节点失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public KnowledgeNode getNodeByNodeId(String nodeId) {
        try {
            return knowledgeNodeMapper.selectByNodeId(nodeId);
        } catch (Exception e) {
            log.error("获取节点详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getNodeList(Integer type, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<KnowledgeNode> nodes = knowledgeNodeMapper.selectByType(type, status, offset, size);
            // 计算总数
            int total = knowledgeNodeMapper.countByType(type, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", nodes);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询节点列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> searchNodes(String keyword, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<KnowledgeNode> nodes = knowledgeNodeMapper.selectByKeyword(keyword, status, offset, size);
            // 这里简化处理，实际应该计算总数
            int total = nodes.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", nodes);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("模糊查询节点失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public String createEdge(KnowledgeEdge knowledgeEdge) {
        try {
            // 生成边ID
            String edgeId = UUID.randomUUID().toString();
            knowledgeEdge.setEdgeId(edgeId);
            knowledgeEdge.setWeight(1.0);
            knowledgeEdge.setStatus(1); // 启用
            knowledgeEdge.setDeleted(0);
            knowledgeEdge.setCreateTime(LocalDateTime.now());
            knowledgeEdge.setUpdateTime(LocalDateTime.now());

            boolean success = knowledgeEdgeMapper.insert(knowledgeEdge) > 0;
            if (success) {
                return edgeId;
            } else {
                throw new RuntimeException("创建边失败");
            }
        } catch (Exception e) {
            log.error("创建边失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建边失败");
        }
    }

    @Override
    @Transactional
    public boolean updateEdge(KnowledgeEdge knowledgeEdge) {
        try {
            knowledgeEdge.setUpdateTime(LocalDateTime.now());
            return knowledgeEdgeMapper.updateById(knowledgeEdge) > 0;
        } catch (Exception e) {
            log.error("更新边失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteEdge(String edgeId) {
        try {
            KnowledgeEdge edge = knowledgeEdgeMapper.selectByEdgeId(edgeId);
            if (edge != null) {
                edge.setDeleted(1);
                edge.setUpdateTime(LocalDateTime.now());
                return knowledgeEdgeMapper.updateById(edge) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("删除边失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public KnowledgeEdge getEdgeByEdgeId(String edgeId) {
        try {
            return knowledgeEdgeMapper.selectByEdgeId(edgeId);
        } catch (Exception e) {
            log.error("获取边详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getEdgeList(Integer type, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<KnowledgeEdge> edges = knowledgeEdgeMapper.selectByType(type, status, offset, size);
            // 计算总数
            int total = knowledgeEdgeMapper.countByType(type, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", edges);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询边列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public List<KnowledgeEdge> getInEdges(String nodeId, Integer status) {
        try {
            return knowledgeEdgeMapper.selectByTargetNodeId(nodeId, status);
        } catch (Exception e) {
            log.error("获取节点的入边失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<KnowledgeEdge> getOutEdges(String nodeId, Integer status) {
        try {
            return knowledgeEdgeMapper.selectBySourceNodeId(nodeId, status);
        } catch (Exception e) {
            log.error("获取节点的出边失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<KnowledgeNode> getNeighbors(String nodeId, Integer status) {
        try {
            List<KnowledgeEdge> outEdges = knowledgeEdgeMapper.selectBySourceNodeId(nodeId, status);
            List<KnowledgeEdge> inEdges = knowledgeEdgeMapper.selectByTargetNodeId(nodeId, status);

            List<KnowledgeNode> neighbors = new ArrayList<>();
            for (KnowledgeEdge edge : outEdges) {
                KnowledgeNode node = knowledgeNodeMapper.selectByNodeId(edge.getTargetNodeId());
                if (node != null) {
                    neighbors.add(node);
                }
            }
            for (KnowledgeEdge edge : inEdges) {
                KnowledgeNode node = knowledgeNodeMapper.selectByNodeId(edge.getSourceNodeId());
                if (node != null && !neighbors.contains(node)) {
                    neighbors.add(node);
                }
            }
            return neighbors;
        } catch (Exception e) {
            log.error("获取节点的邻居节点失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<KnowledgeNode> getRelatedKnowledgeByFraudType(Long fraudTypeId, int limit) {
        try {
            // 先查询诈骗类型节点
            KnowledgeNode fraudTypeNode = knowledgeNodeMapper.selectByRelatedId(fraudTypeId, 1);
            if (fraudTypeNode != null) {
                // 查询诈骗类型节点的出边
                List<KnowledgeEdge> edges = knowledgeEdgeMapper.selectBySourceNodeId(fraudTypeNode.getNodeId(), 1);
                // 根据边获取相关知识节点
                List<KnowledgeNode> relatedKnowledge = new ArrayList<>();
                for (KnowledgeEdge edge : edges) {
                    KnowledgeNode node = knowledgeNodeMapper.selectByNodeId(edge.getTargetNodeId());
                    if (node != null && (node.getType() == 2 || node.getType() == 3 || node.getType() == 4)) {
                        relatedKnowledge.add(node);
                        if (relatedKnowledge.size() >= limit) {
                            break;
                        }
                    }
                }
                return relatedKnowledge;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("根据诈骗类型获取相关知识失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean buildFraudTypeKnowledgeMapping(Long fraudTypeId, List<String> knowledgeIds) {
        try {
            // 先查询诈骗类型节点
            KnowledgeNode fraudTypeNode = knowledgeNodeMapper.selectByRelatedId(fraudTypeId, 1);
            if (fraudTypeNode != null) {
                // 删除现有的映射边
                List<KnowledgeEdge> existingEdges = knowledgeEdgeMapper.selectBySourceNodeId(fraudTypeNode.getNodeId(), 1);
                for (KnowledgeEdge edge : existingEdges) {
                    knowledgeEdgeMapper.deleteById(edge.getId());
                }
                // 创建新的映射边
                for (String knowledgeId : knowledgeIds) {
                    KnowledgeEdge edge = new KnowledgeEdge();
                    edge.setEdgeId(UUID.randomUUID().toString());
                    edge.setSourceNodeId(fraudTypeNode.getNodeId());
                    edge.setTargetNodeId(knowledgeId);
                    edge.setType(3); // 关联
                    edge.setName("关联");
                    edge.setDescription("诈骗类型与知识的关联");
                    edge.setWeight(1.0);
                    edge.setStatus(1); // 启用
                    edge.setDeleted(0);
                    edge.setCreateTime(LocalDateTime.now());
                    edge.setUpdateTime(LocalDateTime.now());
                    knowledgeEdgeMapper.insert(edge);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("建立诈骗类型与知识的映射失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<KnowledgeNode> getHotNodes(Integer type, Integer status, int limit) {
        try {
            return knowledgeNodeMapper.selectHotNodes(type, status, limit);
        } catch (Exception e) {
            log.error("获取热度最高的节点失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean updateNodeHotness(String nodeId, Integer hotness) {
        try {
            KnowledgeNode node = knowledgeNodeMapper.selectByNodeId(nodeId);
            if (node != null) {
                return knowledgeNodeMapper.updateHotness(node.getId(), hotness) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("更新节点热度失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public int batchCreateNodes(List<KnowledgeNode> nodes) {
        try {
            for (KnowledgeNode node : nodes) {
                node.setNodeId(UUID.randomUUID().toString());
                node.setHotness(0);
                node.setStatus(1); // 启用
                node.setDeleted(0);
                node.setCreateTime(LocalDateTime.now());
                node.setUpdateTime(LocalDateTime.now());
            }
            return knowledgeNodeMapper.batchInsert(nodes);
        } catch (Exception e) {
            log.error("批量创建节点失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    @Transactional
    public int batchCreateEdges(List<KnowledgeEdge> edges) {
        try {
            for (KnowledgeEdge edge : edges) {
                edge.setEdgeId(UUID.randomUUID().toString());
                edge.setWeight(1.0);
                edge.setStatus(1); // 启用
                edge.setDeleted(0);
                edge.setCreateTime(LocalDateTime.now());
                edge.setUpdateTime(LocalDateTime.now());
            }
            return knowledgeEdgeMapper.batchInsert(edges);
        } catch (Exception e) {
            log.error("批量创建边失败: {}", e.getMessage(), e);
            return 0;
        }
    }
}
