package com.anti.fraud.modules.exam.service;

import com.anti.fraud.modules.exam.entity.ExamMonitor;
import java.util.List;
import java.util.Map;

public interface ExamMonitorService {

    /**
     * 记录考试监控数据
     * @param monitor 监控数据
     * @return 是否成功
     */
    boolean recordMonitorData(ExamMonitor monitor);

    /**
     * 检测作弊行为
     * @param userId 用户ID
     * @param examId 考试ID
     * @param monitorData 监控数据
     * @return 检测结果
     */
    Map<String, Object> detectCheating(Long userId, Long examId, String monitorData);

    /**
     * 获取用户考试监控记录
     * @param userId 用户ID
     * @param examId 考试ID
     * @return 监控记录列表
     */
    List<ExamMonitor> getMonitorRecords(Long userId, Long examId);

    /**
     * 获取考试作弊风险报告
     * @param examId 考试ID
     * @return 风险报告
     */
    Map<String, Object> getCheatingRiskReport(Long examId);

    /**
     * 处理作弊行为
     * @param monitorId 监控记录ID
     * @param action 处理动作
     * @return 是否成功
     */
    boolean handleCheating(Long monitorId, String action);

    /**
     * 生成防作弊配置
     * @param examId 考试ID
     * @return 防作弊配置
     */
    Map<String, Object> generateAntiCheatConfig(Long examId);
}
