package com.anti.fraud.modules.feedback.service.impl;

import com.anti.fraud.modules.feedback.service.RealTimeFeedbackService;
import com.anti.fraud.modules.websocket.handler.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 实时处理反馈服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RealTimeFeedbackServiceImpl implements RealTimeFeedbackService {

    private final WebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    @Override
    public boolean sendFeedback(Long userId, String feedbackType, String content, Map<String, Object> data) {
        try {
            // 构建反馈消息
            Map<String, Object> message = new HashMap<>();
            message.put("type", "feedback");
            message.put("feedbackType", feedbackType);
            message.put("content", content);
            message.put("data", data != null ? data : new HashMap<>());
            message.put("timestamp", System.currentTimeMillis());

            // 转换为JSON字符串
            String jsonMessage = objectMapper.writeValueAsString(message);

            // 发送消息
            boolean success = webSocketHandler.sendMessageToUser(userId, jsonMessage);
            if (success) {
                log.info("发送实时反馈成功: userId={}, feedbackType={}", userId, feedbackType);
            } else {
                log.warn("发送实时反馈失败: userId={}, feedbackType={}", userId, feedbackType);
            }
            return success;
        } catch (Exception e) {
            log.error("发送实时反馈失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void sendSystemNotification(String title, String content, Map<String, Object> data) {
        try {
            // 构建通知消息
            Map<String, Object> message = new HashMap<>();
            message.put("type", "notification");
            message.put("title", title);
            message.put("content", content);
            message.put("data", data != null ? data : new HashMap<>());
            message.put("timestamp", System.currentTimeMillis());

            // 转换为JSON字符串
            String jsonMessage = objectMapper.writeValueAsString(message);

            // 发送给所有在线用户
            webSocketHandler.sendMessageToAll(jsonMessage);
            log.info("发送系统通知成功: title={}", title);
        } catch (Exception e) {
            log.error("发送系统通知失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean sendExamProgress(Long userId, Long examId, int progress, int currentQuestion, int totalQuestions) {
        try {
            // 构建考试进度消息
            Map<String, Object> data = new HashMap<>();
            data.put("examId", examId);
            data.put("progress", progress);
            data.put("currentQuestion", currentQuestion);
            data.put("totalQuestions", totalQuestions);

            return sendFeedback(userId, "exam_progress", "考试进度更新", data);
        } catch (Exception e) {
            log.error("发送考试进度反馈失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean sendTaskStatus(Long userId, String taskId, String status, int progress, String message) {
        try {
            // 构建任务状态消息
            Map<String, Object> data = new HashMap<>();
            data.put("taskId", taskId);
            data.put("status", status);
            data.put("progress", progress);
            data.put("message", message);

            return sendFeedback(userId, "task_status", "任务状态更新", data);
        } catch (Exception e) {
            log.error("发送任务状态反馈失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean sendRiskAlert(Long userId, String riskLevel, String riskType, String message, Map<String, Object> data) {
        try {
            // 构建风险预警消息
            if (data == null) {
                data = new HashMap<>();
            }
            data.put("riskLevel", riskLevel);
            data.put("riskType", riskType);

            return sendFeedback(userId, "risk_alert", message, data);
        } catch (Exception e) {
            log.error("发送风险预警失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
