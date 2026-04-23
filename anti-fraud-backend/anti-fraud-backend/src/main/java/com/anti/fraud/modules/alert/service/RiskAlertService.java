package com.anti.fraud.modules.alert.service;

import com.anti.fraud.modules.alert.entity.RiskAlert;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 风险预警服务接口
 */
public interface RiskAlertService extends IService<RiskAlert> {

    /**
     * 创建预警
     * @param riskAlert 预警信息
     * @return 预警ID
     */
    String createRiskAlert(RiskAlert riskAlert);

    /**
     * 更新预警
     * @param riskAlert 预警信息
     * @return 是否成功
     */
    boolean updateRiskAlert(RiskAlert riskAlert);

    /**
     * 删除预警
     * @param alertId 预警ID
     * @return 是否成功
     */
    boolean deleteRiskAlert(String alertId);

    /**
     * 获取预警详情
     * @param alertId 预警ID
     * @return 预警详情
     */
    RiskAlert getRiskAlertByAlertId(String alertId);

    /**
     * 分页查询预警列表
     * @param userId 用户ID
     * @param alertType 预警类型
     * @param riskLevel 风险等级
     * @param page 页码
     * @param size 每页大小
     * @return 预警列表和总数
     */
    Map<String, Object> getRiskAlertList(Long userId, Integer alertType, Integer riskLevel, int page, int size);

    /**
     * 根据时间范围查询预警列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userId 用户ID
     * @param alertType 预警类型
     * @param riskLevel 风险等级
     * @return 预警列表
     */
    List<RiskAlert> getRiskAlertListByTimeRange(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime, Long userId, Integer alertType, Integer riskLevel);

    /**
     * 发送预警
     * @param alertId 预警ID
     * @return 是否成功
     */
    boolean sendAlert(String alertId);

    /**
     * WebSocket推送预警
     * @param alertId 预警ID
     * @return 是否成功
     */
    boolean sendWebSocketAlert(String alertId);

    /**
     * 短信发送预警
     * @param alertId 预警ID
     * @return 是否成功
     */
    boolean sendSmsAlert(String alertId);

    /**
     * 邮件发送预警
     * @param alertId 预警ID
     * @return 是否成功
     */
    boolean sendEmailAlert(String alertId);

    /**
     * 创建并发送预警
     * @param userId 用户ID
     * @param username 用户名
     * @param alertType 预警类型
     * @param riskLevel 风险等级
     * @param alertTitle 预警标题
     * @param alertContent 预警内容
     * @param alertDetails 预警详情
     * @param notificationMethod 通知方式
     * @param phoneNumber 手机号
     * @param email 邮箱
     * @return 预警ID
     */
    String createAndSendAlert(Long userId, String username, Integer alertType, Integer riskLevel, String alertTitle, String alertContent, String alertDetails, Integer notificationMethod, String phoneNumber, String email);

    /**
     * 批量创建预警
     * @param alerts 预警列表
     * @return 成功创建的数量
     */
    int batchCreateAlerts(List<RiskAlert> alerts);

    /**
     * 获取预警统计信息
     * @return 统计信息
     */
    Map<String, Object> getRiskAlertStatistics();

    /**
     * 更新预警状态
     * @param alertId 预警ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateAlertStatus(String alertId, Integer status);
}
