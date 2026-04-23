package com.anti.fraud.modules.risk.mapper;

import com.anti.fraud.modules.risk.entity.RiskProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 风险画像Mapper
 */
@Mapper
public interface RiskProfileMapper extends BaseMapper<RiskProfile> {

    /**
     * 根据用户ID查询风险画像
     * @param userId 用户ID
     * @return 风险画像
     */
    RiskProfile selectByUserId(@Param("userId") Long userId);

    /**
     * 根据风险等级查询风险画像
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 风险画像列表
     */
    List<RiskProfile> selectByRiskLevel(@Param("riskLevel") Integer riskLevel, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据风险评分范围查询风险画像
     * @param minScore 最低评分
     * @param maxScore 最高评分
     * @param page 页码
     * @param size 每页大小
     * @return 风险画像列表
     */
    List<RiskProfile> selectByScoreRange(@Param("minScore") Integer minScore, @Param("maxScore") Integer maxScore, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 按风险等级统计风险画像数量
     * @return 风险等级统计
     */
    List<Map<String, Object>> selectRiskLevelStats();

    /**
     * 计算风险评分分布
     * @return 风险评分分布
     */
    List<Map<String, Object>> selectScoreDistribution();

    /**
     * 获取风险评分最高的用户
     * @param limit 数量限制
     * @return 风险评分最高的用户列表
     */
    List<RiskProfile> selectTopRiskUsers(@Param("limit") Integer limit);

    /**
     * 更新风险评分和等级
     * @param id 风险画像ID
     * @param riskScore 风险评分
     * @param riskLevel 风险等级
     */
    void updateRiskScoreAndLevel(@Param("id") Long id, @Param("riskScore") Integer riskScore, @Param("riskLevel") Integer riskLevel);
}
