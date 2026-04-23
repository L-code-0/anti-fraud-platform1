package com.anti.fraud.modules.knowledge.service;

import com.anti.fraud.modules.knowledge.entity.KnowledgeNode;
import com.anti.fraud.modules.knowledge.entity.KnowledgeEdge;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 知识图谱服务接口
 */
public interface KnowledgeService extends IService<KnowledgeNode> {

    /**
     * 创建节点
     * @param knowledgeNode 节点信息
     * @return 节点ID
     */
    String createNode(KnowledgeNode knowledgeNode);

    /**
     * 更新节点
     * @param knowledgeNode 节点信息
     * @return 是否成功
     */
    boolean updateNode(KnowledgeNode knowledgeNode);

    /**
     * 删除节点
     * @param nodeId 节点ID
     * @return 是否成功
     */
    boolean deleteNode(String nodeId);

    /**
     * 获取节点详情
     * @param nodeId 节点ID
     * @return 节点详情
     */
    KnowledgeNode getNodeByNodeId(String nodeId);

    /**
     * 分页查询节点列表
     * @param type 节点类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 节点列表和总数
     */
    Map<String, Object> getNodeList(Integer type, Integer status, int page, int size);

    /**
     * 模糊查询节点
     * @param keyword 关键词
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 节点列表和总数
     */
    Map<String, Object> searchNodes(String keyword, Integer status, int page, int size);

    /**
     * 创建边
     * @param knowledgeEdge 边信息
     * @return 边ID
     */
    String createEdge(KnowledgeEdge knowledgeEdge);

    /**
     * 更新边
     * @param knowledgeEdge 边信息
     * @return 是否成功
     */
    boolean updateEdge(KnowledgeEdge knowledgeEdge);

    /**
     * 删除边
     * @param edgeId 边ID
     * @return 是否成功
     */
    boolean deleteEdge(String edgeId);

    /**
     * 获取边详情
     * @param edgeId 边ID
     * @return 边详情
     */
    KnowledgeEdge getEdgeByEdgeId(String edgeId);

    /**
     * 分页查询边列表
     * @param type 边类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 边列表和总数
     */
    Map<String, Object> getEdgeList(Integer type, Integer status, int page, int size);

    /**
     * 获取节点的入边
     * @param nodeId 节点ID
     * @param status 状态
     * @return 入边列表
     */
    List<KnowledgeEdge> getInEdges(String nodeId, Integer status);

    /**
     * 获取节点的出边
     * @param nodeId 节点ID
     * @param status 状态
     * @return 出边列表
     */
    List<KnowledgeEdge> getOutEdges(String nodeId, Integer status);

    /**
     * 获取节点的邻居节点
     * @param nodeId 节点ID
     * @param status 状态
     * @return 邻居节点列表
     */
    List<KnowledgeNode> getNeighbors(String nodeId, Integer status);

    /**
     * 根据诈骗类型获取相关知识
     * @param fraudTypeId 诈骗类型ID
     * @param limit 数量限制
     * @return 相关知识列表
     */
    List<KnowledgeNode> getRelatedKnowledgeByFraudType(Long fraudTypeId, int limit);

    /**
     * 建立诈骗类型与知识的映射
     * @param fraudTypeId 诈骗类型ID
     * @param knowledgeIds 知识ID列表
     * @return 是否成功
     */
    boolean buildFraudTypeKnowledgeMapping(Long fraudTypeId, List<String> knowledgeIds);

    /**
     * 获取热度最高的节点
     * @param type 节点类型
     * @param status 状态
     * @param limit 数量限制
     * @return 节点列表
     */
    List<KnowledgeNode> getHotNodes(Integer type, Integer status, int limit);

    /**
     * 更新节点热度
     * @param nodeId 节点ID
     * @param hotness 热度
     * @return 是否成功
     */
    boolean updateNodeHotness(String nodeId, Integer hotness);

    /**
     * 批量创建节点
     * @param nodes 节点列表
     * @return 成功创建的数量
     */
    int batchCreateNodes(List<KnowledgeNode> nodes);

    /**
     * 批量创建边
     * @param edges 边列表
     * @return 成功创建的数量
     */
    int batchCreateEdges(List<KnowledgeEdge> edges);
}
