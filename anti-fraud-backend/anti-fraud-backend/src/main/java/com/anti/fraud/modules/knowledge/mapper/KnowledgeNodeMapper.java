package com.anti.fraud.modules.knowledge.mapper;

import com.anti.fraud.modules.knowledge.entity.KnowledgeNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 知识图谱节点Mapper
 */
@Mapper
public interface KnowledgeNodeMapper extends BaseMapper<KnowledgeNode> {

    /**
     * 根据节点ID查询节点
     * @param nodeId 节点ID
     * @return 节点
     */
    KnowledgeNode selectByNodeId(@Param("nodeId") String nodeId);

    /**
     * 根据类型查询节点列表
     * @param type 节点类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 节点列表
     */
    List<KnowledgeNode> selectByType(@Param("type") Integer type, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据关联ID查询节点
     * @param relatedId 关联ID
     * @param type 节点类型
     * @return 节点
     */
    KnowledgeNode selectByRelatedId(@Param("relatedId") Long relatedId, @Param("type") Integer type);

    /**
     * 模糊查询节点
     * @param keyword 关键词
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 节点列表
     */
    List<KnowledgeNode> selectByKeyword(@Param("keyword") String keyword, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 更新节点热度
     * @param id 节点ID
     * @param hotness 热度
     * @return 影响行数
     */
    int updateHotness(@Param("id") Long id, @Param("hotness") Integer hotness);

    /**
     * 更新节点状态
     * @param id 节点ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 统计节点数量
     * @param type 节点类型
     * @param status 状态
     * @return 节点数量
     */
    Integer countByType(@Param("type") Integer type, @Param("status") Integer status);

    /**
     * 获取热度最高的节点
     * @param type 节点类型
     * @param status 状态
     * @param limit 数量限制
     * @return 节点列表
     */
    List<KnowledgeNode> selectHotNodes(@Param("type") Integer type, @Param("status") Integer status, @Param("limit") Integer limit);

    /**
     * 批量插入节点
     * @param nodes 节点列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("nodes") List<KnowledgeNode> nodes);
}
