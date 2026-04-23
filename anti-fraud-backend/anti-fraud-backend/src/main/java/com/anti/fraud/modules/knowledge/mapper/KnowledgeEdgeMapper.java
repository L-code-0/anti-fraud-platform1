package com.anti.fraud.modules.knowledge.mapper;

import com.anti.fraud.modules.knowledge.entity.KnowledgeEdge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 知识图谱边Mapper
 */
@Mapper
public interface KnowledgeEdgeMapper extends BaseMapper<KnowledgeEdge> {

    /**
     * 根据边ID查询边
     * @param edgeId 边ID
     * @return 边
     */
    KnowledgeEdge selectByEdgeId(@Param("edgeId") String edgeId);

    /**
     * 根据源节点ID查询边列表
     * @param sourceNodeId 源节点ID
     * @param status 状态
     * @return 边列表
     */
    List<KnowledgeEdge> selectBySourceNodeId(@Param("sourceNodeId") String sourceNodeId, @Param("status") Integer status);

    /**
     * 根据目标节点ID查询边列表
     * @param targetNodeId 目标节点ID
     * @param status 状态
     * @return 边列表
     */
    List<KnowledgeEdge> selectByTargetNodeId(@Param("targetNodeId") String targetNodeId, @Param("status") Integer status);

    /**
     * 根据类型查询边列表
     * @param type 边类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 边列表
     */
    List<KnowledgeEdge> selectByType(@Param("type") Integer type, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据源节点和目标节点查询边
     * @param sourceNodeId 源节点ID
     * @param targetNodeId 目标节点ID
     * @param type 边类型
     * @return 边
     */
    KnowledgeEdge selectBySourceAndTarget(@Param("sourceNodeId") String sourceNodeId, @Param("targetNodeId") String targetNodeId, @Param("type") Integer type);

    /**
     * 更新边权重
     * @param id 边ID
     * @param weight 权重
     * @return 影响行数
     */
    int updateWeight(@Param("id") Long id, @Param("weight") Double weight);

    /**
     * 更新边状态
     * @param id 边ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 统计边数量
     * @param type 边类型
     * @param status 状态
     * @return 边数量
     */
    Integer countByType(@Param("type") Integer type, @Param("status") Integer status);

    /**
     * 获取权重最高的边
     * @param type 边类型
     * @param status 状态
     * @param limit 数量限制
     * @return 边列表
     */
    List<KnowledgeEdge> selectHighWeightEdges(@Param("type") Integer type, @Param("status") Integer status, @Param("limit") Integer limit);

    /**
     * 批量插入边
     * @param edges 边列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("edges") List<KnowledgeEdge> edges);

    /**
     * 删除节点相关的所有边
     * @param nodeId 节点ID
     * @return 影响行数
     */
    int deleteByNodeId(@Param("nodeId") String nodeId);
}
