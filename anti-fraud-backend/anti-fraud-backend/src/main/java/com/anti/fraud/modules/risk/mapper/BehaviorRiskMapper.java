package com.anti.fraud.modules.risk.mapper;

import com.anti.fraud.modules.risk.entity.BehaviorRisk;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 行为风险Mapper
 */
@Mapper
public interface BehaviorRiskMapper extends BaseMapper<BehaviorRisk> {

    /**
     * 根据风险ID查询行为风险
     * @param riskId 风险ID
     * @return 行为风险
     */
    BehaviorRisk selectByRiskId(@Param("riskId") String riskId);

    /**
     * 根据用户ID查询行为风险列表
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 行为风险列表
     */
    List<BehaviorRisk> selectByUserId(@Param("userId") Long userId, @Param("behaviorType") Integer behaviorType, @Param("riskLevel") Integer riskLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据行为类型查询行为风险列表
     * @param behaviorType 行为类型
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 行为风险列表
     */
    List<BehaviorRisk> selectByBehaviorType(@Param("behaviorType") Integer behaviorType, @Param("riskLevel") Integer riskLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据时间范围查询行为风险列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param riskLevel 风险等级
     * @return 行为风险列表
     */
    List<BehaviorRisk> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("userId") Long userId, @Param("behaviorType") Integer behaviorType, @Param("riskLevel") Integer riskLevel);

    /**
     * 更新行为风险状态
     * @param id 行为风险ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新行为风险分数
     * @param id 行为风险ID
     * @param riskScore 风险分数
     * @param riskLevel 风险等级
     * @param timeDecayFactor 时间衰减因子
     * @return 影响行数
     */
    int updateRiskScore(@Param("id") Long id, @Param("riskScore") Double riskScore, @Param("riskLevel") Integer riskLevel, @Param("timeDecayFactor") Double timeDecayFactor);

    /**
     * 统计行为风险数量
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param riskLevel 风险等级
     * @return 行为风险数量
     */
    Integer countByBehaviorType(@Param("userId") Long userId, @Param("behaviorType") Integer behaviorType, @Param("riskLevel") Integer riskLevel);

    /**
     * 获取最近的行为风险
     * @param limit 数量限制
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param riskLevel 风险等级
     * @return 行为风险列表
     */
    List<BehaviorRisk> selectRecentBehaviorRisks(@Param("limit") Integer limit, @Param("userId") Long userId, @Param("behaviorType") Integer behaviorType, @Param("riskLevel") Integer riskLevel);

    /**
     * 批量插入行为风险
     * @param behaviorRisks 行为风险列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("behaviorRisks") List<BehaviorRisk> behaviorRisks);

    /**
     * 批量更新时间衰减因子
     * @param ids 行为风险ID列表
     * @param timeDecayFactor 时间衰减因子
     * @return 影响行数
     */
    int batchUpdateTimeDecayFactor(@Param("ids") List<Long> ids, @Param("timeDecayFactor") Double timeDecayFactor);
}
