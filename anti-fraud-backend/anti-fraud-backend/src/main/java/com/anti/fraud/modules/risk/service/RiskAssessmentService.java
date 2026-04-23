package com.anti.fraud.modules.risk.service;

import com.anti.fraud.modules.risk.entity.RiskAssessment;
import com.anti.fraud.modules.risk.entity.BehaviorRisk;
import com.anti.fraud.modules.risk.entity.DeviceRisk;
import com.anti.fraud.modules.risk.entity.SocialRisk;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 风险评估服务接口
 */
public interface RiskAssessmentService extends IService<RiskAssessment> {

    /**
     * 创建风险评估
     * @param riskAssessment 风险评估信息
     * @return 评估ID
     */
    String createRiskAssessment(RiskAssessment riskAssessment);

    /**
     * 更新风险评估
     * @param riskAssessment 风险评估信息
     * @return 是否成功
     */
    boolean updateRiskAssessment(RiskAssessment riskAssessment);

    /**
     * 删除风险评估
     * @param assessmentId 评估ID
     * @return 是否成功
     */
    boolean deleteRiskAssessment(String assessmentId);

    /**
     * 获取风险评估详情
     * @param assessmentId 评估ID
     * @return 风险评估详情
     */
    RiskAssessment getRiskAssessmentByAssessmentId(String assessmentId);

    /**
     * 分页查询风险评估列表
     * @param userId 用户ID
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 风险评估列表和总数
     */
    Map<String, Object> getRiskAssessmentList(Long userId, Integer riskLevel, int page, int size);

    /**
     * 根据时间范围查询风险评估列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param riskLevel 风险等级
     * @return 风险评估列表
     */
    List<RiskAssessment> getRiskAssessmentListByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Integer riskLevel);

    /**
     * 多维度风险评估
     * @param userId 用户ID
     * @param username 用户名
     * @return 风险评估结果
     */
    Map<String, Object> multiDimensionalRiskAssessment(Long userId, String username);

    /**
     * 创建行为风险
     * @param behaviorRisk 行为风险信息
     * @return 风险ID
     */
    String createBehaviorRisk(BehaviorRisk behaviorRisk);

    /**
     * 更新行为风险
     * @param behaviorRisk 行为风险信息
     * @return 是否成功
     */
    boolean updateBehaviorRisk(BehaviorRisk behaviorRisk);

    /**
     * 删除行为风险
     * @param riskId 风险ID
     * @return 是否成功
     */
    boolean deleteBehaviorRisk(String riskId);

    /**
     * 获取行为风险详情
     * @param riskId 风险ID
     * @return 行为风险详情
     */
    BehaviorRisk getBehaviorRiskByRiskId(String riskId);

    /**
     * 分页查询行为风险列表
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 行为风险列表和总数
     */
    Map<String, Object> getBehaviorRiskList(Long userId, Integer behaviorType, Integer riskLevel, int page, int size);

    /**
     * 分析行为风险
     * @param userId 用户ID
     * @param username 用户名
     * @param behaviorType 行为类型
     * @param behaviorContent 行为内容
     * @return 行为风险分析结果
     */
    Map<String, Object> analyzeBehaviorRisk(Long userId, String username, Integer behaviorType, String behaviorContent);

    /**
     * 创建设备风险
     * @param deviceRisk 设备风险信息
     * @return 风险ID
     */
    String createDeviceRisk(DeviceRisk deviceRisk);

    /**
     * 更新设备风险
     * @param deviceRisk 设备风险信息
     * @return 是否成功
     */
    boolean updateDeviceRisk(DeviceRisk deviceRisk);

    /**
     * 删除设备风险
     * @param riskId 风险ID
     * @return 是否成功
     */
    boolean deleteDeviceRisk(String riskId);

    /**
     * 获取设备风险详情
     * @param riskId 风险ID
     * @return 设备风险详情
     */
    DeviceRisk getDeviceRiskByRiskId(String riskId);

    /**
     * 分页查询设备风险列表
     * @param userId 用户ID
     * @param deviceType 设备类型
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 设备风险列表和总数
     */
    Map<String, Object> getDeviceRiskList(Long userId, Integer deviceType, Integer riskLevel, int page, int size);

    /**
     * 分析设备风险
     * @param userId 用户ID
     * @param username 用户名
     * @param deviceId 设备ID
     * @param deviceInfo 设备信息
     * @param ipAddress IP地址
     * @param location 地理位置
     * @return 设备风险分析结果
     */
    Map<String, Object> analyzeDeviceRisk(Long userId, String username, String deviceId, String deviceInfo, String ipAddress, String location);

    /**
     * 创建社交风险
     * @param socialRisk 社交风险信息
     * @return 风险ID
     */
    String createSocialRisk(SocialRisk socialRisk);

    /**
     * 更新社交风险
     * @param socialRisk 社交风险信息
     * @return 是否成功
     */
    boolean updateSocialRisk(SocialRisk socialRisk);

    /**
     * 删除社交风险
     * @param riskId 风险ID
     * @return 是否成功
     */
    boolean deleteSocialRisk(String riskId);

    /**
     * 获取社交风险详情
     * @param riskId 风险ID
     * @return 社交风险详情
     */
    SocialRisk getSocialRiskByRiskId(String riskId);

    /**
     * 分页查询社交风险列表
     * @param userId 用户ID
     * @param socialType 社交类型
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 社交风险列表和总数
     */
    Map<String, Object> getSocialRiskList(Long userId, Integer socialType, Integer riskLevel, int page, int size);

    /**
     * 分析社交风险
     * @param userId 用户ID
     * @param username 用户名
     * @param targetUserId 社交对象ID
     * @param targetUsername 社交对象名称
     * @param socialType 社交类型
     * @param socialContent 社交内容
     * @return 社交风险分析结果
     */
    Map<String, Object> analyzeSocialRisk(Long userId, String username, String targetUserId, String targetUsername, Integer socialType, String socialContent);

    /**
     * 更新时间衰减因子
     * @param userId 用户ID
     * @param days 天数
     * @return 更新成功的数量
     */
    int updateTimeDecayFactor(Long userId, int days);

    /**
     * 获取风险评估统计信息
     * @return 统计信息
     */
    Map<String, Object> getRiskAssessmentStatistics();

    /**
     * 批量创建行为风险
     * @param behaviorRisks 行为风险列表
     * @return 成功创建的数量
     */
    int batchCreateBehaviorRisks(List<BehaviorRisk> behaviorRisks);

    /**
     * 批量创建设备风险
     * @param deviceRisks 设备风险列表
     * @return 成功创建的数量
     */
    int batchCreateDeviceRisks(List<DeviceRisk> deviceRisks);

    /**
     * 批量创建社交风险
     * @param socialRisks 社交风险列表
     * @return 成功创建的数量
     */
    int batchCreateSocialRisks(List<SocialRisk> socialRisks);
}
