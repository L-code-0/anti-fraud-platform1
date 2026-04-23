package com.anti.fraud.modules.learning.mapper;

import com.anti.fraud.modules.learning.entity.LearningPathNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 学习路径节点Mapper
 */
@Mapper
public interface LearningPathNodeMapper extends BaseMapper<LearningPathNode> {

    /**
     * 根据学习路径ID查询节点列表
     * @param pathId 学习路径ID
     * @return 节点列表
     */
    List<LearningPathNode> selectByPathId(@Param("pathId") Long pathId);

    /**
     * 根据学习路径ID和节点类型查询节点列表
     * @param pathId 学习路径ID
     * @param nodeType 节点类型
     * @return 节点列表
     */
    List<LearningPathNode> selectByPathIdAndNodeType(@Param("pathId") Long pathId, @Param("nodeType") Integer nodeType);

    /**
     * 按节点类型统计节点数量
     * @param pathId 学习路径ID
     * @return 类型统计
     */
    List<Map<String, Object>> selectNodeTypeStats(@Param("pathId") Long pathId);

    /**
     * 计算学习路径的总时长
     * @param pathId 学习路径ID
     * @return 总时长（分钟）
     */
    Integer calculateTotalDuration(@Param("pathId") Long pathId);

    /**
     * 获取学习路径的下一个节点
     * @param pathId 学习路径ID
     * @param currentNodeOrder 当前节点顺序
     * @return 下一个节点
     */
    LearningPathNode selectNextNode(@Param("pathId") Long pathId, @Param("currentNodeOrder") Integer currentNodeOrder);

    /**
     * 获取学习路径的前一个节点
     * @param pathId 学习路径ID
     * @param currentNodeOrder 当前节点顺序
     * @return 前一个节点
     */
    LearningPathNode selectPreviousNode(@Param("pathId") Long pathId, @Param("currentNodeOrder") Integer currentNodeOrder);

    /**
     * 重新排序节点
     * @param pathId 学习路径ID
     */
    void reorderNodes(@Param("pathId") Long pathId);
}
