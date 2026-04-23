package com.anti.fraud.modules.risk.service;

import com.anti.fraud.modules.risk.entity.UserRiskAssessment;
import com.anti.fraud.modules.risk.vo.RiskLevelStatsVO;
import java.util.List;
import java.util.Map;

/**
 * 用户风险评估服务接口
 */
public interface UserRiskAssessmentService {

    /**
     * 评估用户风险
     * @param userId 用户ID
     * @return 风险评估结果
     */
    UserRiskAssessment assessUserRisk(Long userId);

    /**
     * 获取用户的风险评估记录
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 风险评估记录列表
     */
    List<UserRiskAssessment> getUserRiskAssessments(Long userId, int page, int size);

    /**
     * 获取用户最新的风险评估记录
     * @param userId 用户ID
     * @return 最新的风险评估记录
     */
    UserRiskAssessment getLatestUserRiskAssessment(Long userId);

    /**
     * 获取风险等级统计
     * @return 风险等级统计结果
     */
    List<RiskLevelStatsVO> getRiskLevelStats();

    /**
     * 获取高风险用户列表
     * @param limit 限制数量
     * @return 高风险用户列表
     */
    List<UserRiskAssessment> getHighRiskUsers(int limit);

    /**
     * 批量评估用户风险
     * @param userIds 用户ID列表
     * @return 评估结果
     */
    Map<Long, UserRiskAssessment> batchAssessUserRisk(List<Long> userIds);

    /**
     * 更新用户风险评估
     * @param assessment 风险评估记录
     * @return 是否成功
     */
    boolean updateUserRiskAssessment(UserRiskAssessment assessment);

    /**
     * 删除用户风险评估记录
     * @param id 评估记录ID
     * @return 是否成功
     */
    boolean deleteUserRiskAssessment(Long id);
}
