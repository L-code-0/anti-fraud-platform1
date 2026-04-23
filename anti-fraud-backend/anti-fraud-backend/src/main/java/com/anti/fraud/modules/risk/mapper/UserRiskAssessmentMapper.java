package com.anti.fraud.modules.risk.mapper;

import com.anti.fraud.modules.risk.entity.UserRiskAssessment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户风险评估Mapper
 */
public interface UserRiskAssessmentMapper extends BaseMapper<UserRiskAssessment> {
    
    /**
     * 根据用户ID查询风险评估记录
     * @param userId 用户ID
     * @return 风险评估记录列表
     */
    List<UserRiskAssessment> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 获取用户最新的风险评估记录
     * @param userId 用户ID
     * @return 最新的风险评估记录
     */
    UserRiskAssessment selectLatestByUserId(@Param("userId") Long userId);
    
    /**
     * 根据风险等级查询用户
     * @param riskLevel 风险等级
     * @param limit 限制数量
     * @return 风险评估记录列表
     */
    List<UserRiskAssessment> selectByRiskLevel(@Param("riskLevel") String riskLevel, @Param("limit") Integer limit);
    
    /**
     * 统计不同风险等级的用户数量
     * @return 风险等级统计结果
     */
    List<com.anti.fraud.modules.risk.vo.RiskLevelStatsVO> countByRiskLevel();
}
