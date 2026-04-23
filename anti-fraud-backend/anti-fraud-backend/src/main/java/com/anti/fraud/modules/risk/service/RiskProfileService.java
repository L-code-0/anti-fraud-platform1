package com.anti.fraud.modules.risk.service;

import com.anti.fraud.modules.risk.entity.RiskProfile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 风险画像服务接口
 */
public interface RiskProfileService extends IService<RiskProfile> {

    /**
     * 创建风险画像
     * @param riskProfile 风险画像信息
     * @return 是否成功
     */
    boolean createRiskProfile(RiskProfile riskProfile);

    /**
     * 更新风险画像
     * @param riskProfile 风险画像信息
     * @return 是否成功
     */
    boolean updateRiskProfile(RiskProfile riskProfile);

    /**
     * 删除风险画像
     * @param id 风险画像ID
     * @return 是否成功
     */
    boolean deleteRiskProfile(Long id);

    /**
     * 获取风险画像详情
     * @param id 风险画像ID
     * @return 风险画像详情
     */
    RiskProfile getRiskProfileById(Long id);

    /**
     * 根据用户ID获取风险画像
     * @param userId 用户ID
     * @return 风险画像
     */
    RiskProfile getRiskProfileByUserId(Long userId);

    /**
     * 分页查询风险画像
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 风险画像列表和总数
     */
    Map<String, Object> getRiskProfileList(Map<String, Object> params, int page, int size);

    /**
     * 根据风险等级查询风险画像
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 风险画像列表和总数
     */
    Map<String, Object> getRiskProfilesByLevel(Integer riskLevel, int page, int size);

    /**
     * 根据风险评分范围查询风险画像
     * @param minScore 最低评分
     * @param maxScore 最高评分
     * @param page 页码
     * @param size 每页大小
     * @return 风险画像列表和总数
     */
    Map<String, Object> getRiskProfilesByScoreRange(Integer minScore, Integer maxScore, int page, int size);

    /**
     * 更新风险评分和等级
     * @param id 风险画像ID
     * @param riskScore 风险评分
     * @param riskLevel 风险等级
     * @return 是否成功
     */
    boolean updateRiskScoreAndLevel(Long id, Integer riskScore, Integer riskLevel);

    /**
     * 生成风险画像
     * @param userId 用户ID
     * @param username 用户名
     * @return 风险画像
     */
    RiskProfile generateRiskProfile(Long userId, String username);

    /**
     * 批量生成风险画像
     * @param userIds 用户ID列表
     * @return 生成的风险画像列表
     */
    List<RiskProfile> batchGenerateRiskProfiles(List<Long> userIds);

    /**
     * 统计风险画像信息
     * @return 统计信息
     */
    Map<String, Object> getRiskProfileStats();

    /**
     * 获取风险评分最高的用户
     * @param limit 数量限制
     * @return 风险评分最高的用户列表
     */
    List<RiskProfile> getTopRiskUsers(int limit);

    /**
     * 获取风险评分分布
     * @return 风险评分分布
     */
    Map<String, Object> getScoreDistribution();
}
