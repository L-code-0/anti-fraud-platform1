package com.anti.fraud.modules.behavior.mapper;

import com.anti.fraud.modules.behavior.entity.BehaviorAnalysis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 行为分析Mapper
 */
@Mapper
public interface BehaviorAnalysisMapper extends BaseMapper<BehaviorAnalysis> {

    /**
     * 根据分析ID查询分析
     * @param analysisId 分析ID
     * @return 分析
     */
    BehaviorAnalysis selectByAnalysisId(@Param("analysisId") String analysisId);

    /**
     * 根据用户ID查询分析列表
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 分析列表
     */
    List<BehaviorAnalysis> selectByUserId(@Param("userId") Long userId, @Param("behaviorType") Integer behaviorType, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据行为类型查询分析列表
     * @param behaviorType 行为类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 分析列表
     */
    List<BehaviorAnalysis> selectByBehaviorType(@Param("behaviorType") Integer behaviorType, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据行为时间范围查询分析列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param behaviorType 行为类型
     * @param status 状态
     * @return 分析列表
     */
    List<BehaviorAnalysis> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("behaviorType") Integer behaviorType, @Param("status") Integer status);

    /**
     * 更新分析状态
     * @param id 分析ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新分析结果
     * @param id 分析ID
     * @param analysisResult 分析结果
     * @param feedbackContent 反馈内容
     * @return 影响行数
     */
    int updateAnalysisResult(@Param("id") Long id, @Param("analysisResult") String analysisResult, @Param("feedbackContent") String feedbackContent);

    /**
     * 统计分析数量
     * @param behaviorType 行为类型
     * @param status 状态
     * @return 分析数量
     */
    Integer countByBehaviorType(@Param("behaviorType") Integer behaviorType, @Param("status") Integer status);

    /**
     * 统计用户分析数量
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param status 状态
     * @return 分析数量
     */
    Integer countByUserId(@Param("userId") Long userId, @Param("behaviorType") Integer behaviorType, @Param("status") Integer status);

    /**
     * 获取最近的分析
     * @param limit 数量限制
     * @param behaviorType 行为类型
     * @param status 状态
     * @return 分析列表
     */
    List<BehaviorAnalysis> selectRecentAnalysis(@Param("limit") Integer limit, @Param("behaviorType") Integer behaviorType, @Param("status") Integer status);

    /**
     * 批量插入分析
     * @param analyses 分析列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("analyses") List<BehaviorAnalysis> analyses);
}
