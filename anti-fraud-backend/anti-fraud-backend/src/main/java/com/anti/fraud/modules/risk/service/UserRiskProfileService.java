package com.anti.fraud.modules.risk.service;

import com.anti.fraud.modules.risk.entity.UserRiskProfile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户风险画像服务接口
 */
public interface UserRiskProfileService extends IService<UserRiskProfile> {

    /**
     * 创建用户风险画像
     * @param userRiskProfile 用户风险画像
     * @return 是否成功
     */
    boolean createUserRiskProfile(UserRiskProfile userRiskProfile);

    /**
     * 更新用户风险画像
     * @param userRiskProfile 用户风险画像
     * @return 是否成功
     */
    boolean updateUserRiskProfile(UserRiskProfile userRiskProfile);

    /**
     * 删除用户风险画像
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUserRiskProfile(Long userId);

    /**
     * 获取用户风险画像
     * @param userId 用户ID
     * @return 用户风险画像
     */
    UserRiskProfile getUserRiskProfile(Long userId);

    /**
     * 分页查询用户风险画像列表
     * @param userId 用户ID
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 用户风险画像列表和总数
     */
    Map<String, Object> getUserRiskProfileList(Long userId, Integer riskLevel, int page, int size);

    /**
     * 根据风险等级查询用户风险画像列表
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 用户风险画像列表和总数
     */
    Map<String, Object> getUserRiskProfileListByRiskLevel(Integer riskLevel, int page, int size);

    /**
     * 根据时间范围查询用户风险画像列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userId 用户ID
     * @param riskLevel 风险等级
     * @return 用户风险画像列表
     */
    List<UserRiskProfile> getUserRiskProfileListByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Long userId, Integer riskLevel);

    /**
     * 生成用户风险画像
     * @param userId 用户ID
     * @param username 用户名
     * @return 用户风险画像
     */
    UserRiskProfile generateUserRiskProfile(Long userId, String username);

    /**
     * 获取用户风险画像可视化数据
     * @param userId 用户ID
     * @return 可视化数据
     */
    Map<String, Object> getUserRiskProfileVisualization(Long userId);

    /**
     * 获取用户风险趋势
     * @param userId 用户ID
     * @param days 天数
     * @return 风险趋势
     */
    List<Map<String, Object>> getUserRiskTrend(Long userId, Integer days);

    /**
     * 获取高风险用户列表
     * @param limit 数量限制
     * @return 高风险用户列表
     */
    List<UserRiskProfile> getHighRiskUsers(Integer limit);

    /**
     * 获取用户风险画像统计信息
     * @return 统计信息
     */
    Map<String, Object> getUserRiskProfileStatistics();

    /**
     * 更新用户风险画像状态
     * @param userId 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateUserRiskProfileStatus(Long userId, Integer status);

    /**
     * 批量生成用户风险画像
     * @param userIds 用户ID列表
     * @return 成功生成的数量
     */
    int batchGenerateUserRiskProfiles(List<Long> userIds);

    /**
     * 获取用户风险因素分析
     * @param userId 用户ID
     * @return 风险因素分析
     */
    Map<String, Object> getUserRiskFactorAnalysis(Long userId);

    /**
     * 获取用户风险建议
     * @param userId 用户ID
     * @return 风险建议
     */
    List<String> getUserRiskRecommendations(Long userId);

    /**
     * 获取用户风险标签
     * @param userId 用户ID
     * @return 风险标签
     */
    List<String> getUserRiskLabels(Long userId);
}
