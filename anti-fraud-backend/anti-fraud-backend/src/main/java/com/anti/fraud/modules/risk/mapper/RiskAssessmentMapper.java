package com.anti.fraud.modules.risk.mapper;

import com.anti.fraud.modules.risk.entity.RiskAssessment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 风险评估Mapper
 */
@Mapper
public interface RiskAssessmentMapper extends BaseMapper<RiskAssessment> {

    /**
     * 根据评估ID查询风险评估
     * @param assessmentId 评估ID
     * @return 风险评估
     */
    RiskAssessment selectByAssessmentId(@Param("assessmentId") String assessmentId);

    /**
     * 根据用户ID查询风险评估列表
     * @param userId 用户ID
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 风险评估列表
     */
    List<RiskAssessment> selectByUserId(@Param("userId") Long userId, @Param("riskLevel") Integer riskLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据风险等级查询风险评估列表
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 风险评估列表
     */
    List<RiskAssessment> selectByRiskLevel(@Param("riskLevel") Integer riskLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据时间范围查询风险评估列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param riskLevel 风险等级
     * @return 风险评估列表
     */
    List<RiskAssessment> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("riskLevel") Integer riskLevel);

    /**
     * 更新风险评估状态
     * @param id 风险评估ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新风险评估分数
     * @param id 风险评估ID
     * @param behaviorRiskScore 行为风险分数
     * @param deviceRiskScore 设备风险分数
     * @param socialRiskScore 社交风险分数
     * @param overallRiskScore 综合风险分数
     * @param riskLevel 风险等级
     * @param assessmentTime 评估时间
     * @return 影响行数
     */
    int updateRiskScores(@Param("id") Long id, @Param("behaviorRiskScore") Double behaviorRiskScore, @Param("deviceRiskScore") Double deviceRiskScore, @Param("socialRiskScore") Double socialRiskScore, @Param("overallRiskScore") Double overallRiskScore, @Param("riskLevel") Integer riskLevel, @Param("assessmentTime") LocalDateTime assessmentTime);

    /**
     * 统计风险评估数量
     * @param riskLevel 风险等级
     * @return 风险评估数量
     */
    Integer countByRiskLevel(@Param("riskLevel") Integer riskLevel);

    /**
     * 统计用户风险评估数量
     * @param userId 用户ID
     * @param riskLevel 风险等级
     * @return 风险评估数量
     */
    Integer countByUserId(@Param("userId") Long userId, @Param("riskLevel") Integer riskLevel);

    /**
     * 获取最新的风险评估
     * @param limit 数量限制
     * @param riskLevel 风险等级
     * @return 风险评估列表
     */
    List<RiskAssessment> selectRecentAssessments(@Param("limit") Integer limit, @Param("riskLevel") Integer riskLevel);

    /**
     * 获取用户最新的风险评估
     * @param userId 用户ID
     * @return 风险评估
     */
    RiskAssessment selectLatestByUserId(@Param("userId") Long userId);

    /**
     * 批量插入风险评估
     * @param assessments 风险评估列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("assessments") List<RiskAssessment> assessments);
}
