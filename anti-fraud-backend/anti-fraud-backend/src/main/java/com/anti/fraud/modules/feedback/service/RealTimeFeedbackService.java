package com.anti.fraud.modules.feedback.service;

import java.util.Map;

/**
 * 实时处理反馈服务接口
 */
public interface RealTimeFeedbackService {

    /**
     * 发送实时反馈消息
     * @param userId 用户ID
     * @param feedbackType 反馈类型
     * @param content 反馈内容
     * @param data 附加数据
     * @return 是否发送成功
     */
    boolean sendFeedback(Long userId, String feedbackType, String content, Map<String, Object> data);

    /**
     * 发送系统通知
     * @param title 通知标题
     * @param content 通知内容
     * @param data 附加数据
     */
    void sendSystemNotification(String title, String content, Map<String, Object> data);

    /**
     * 发送考试进度反馈
     * @param userId 用户ID
     * @param examId 考试ID
     * @param progress 进度（0-100）
     * @param currentQuestion 当前题目
     * @param totalQuestions 总题目数
     * @return 是否发送成功
     */
    boolean sendExamProgress(Long userId, Long examId, int progress, int currentQuestion, int totalQuestions);

    /**
     * 发送任务处理状态反馈
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param status 任务状态
     * @param progress 处理进度（0-100）
     * @param message 状态消息
     * @return 是否发送成功
     */
    boolean sendTaskStatus(Long userId, String taskId, String status, int progress, String message);

    /**
     * 发送风险预警
     * @param userId 用户ID
     * @param riskLevel 风险等级
     * @param riskType 风险类型
     * @param message 预警消息
     * @param data 附加数据
     * @return 是否发送成功
     */
    boolean sendRiskAlert(Long userId, String riskLevel, String riskType, String message, Map<String, Object> data);
}
