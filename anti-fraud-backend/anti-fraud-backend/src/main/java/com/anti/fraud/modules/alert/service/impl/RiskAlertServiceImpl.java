package com.anti.fraud.modules.alert.service.impl;

import com.anti.fraud.modules.alert.entity.RiskAlert;
import com.anti.fraud.modules.alert.mapper.RiskAlertMapper;
import com.anti.fraud.modules.alert.service.RiskAlertService;
import com.anti.fraud.modules.websocket.entity.WebSocketMessageStore;
import com.anti.fraud.modules.websocket.handler.ProtobufMessageHandler;
import com.anti.fraud.modules.websocket.service.WebSocketMessageStoreService;
import com.anti.fraud.proto.WebSocketMessage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 风险预警服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RiskAlertServiceImpl extends ServiceImpl<RiskAlertMapper, RiskAlert> implements RiskAlertService {

    private final RiskAlertMapper riskAlertMapper;
    private final ObjectMapper objectMapper;
    private final ProtobufMessageHandler protobufMessageHandler;
    private final WebSocketMessageStoreService webSocketMessageStoreService;

    // WebSocket会话管理
    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public String createRiskAlert(RiskAlert riskAlert) {
        try {
            // 生成预警ID
            String alertId = UUID.randomUUID().toString();
            riskAlert.setAlertId(alertId);
            riskAlert.setAlertTime(LocalDateTime.now());
            riskAlert.setStatus(2); // 处理中
            riskAlert.setDeleted(0);
            riskAlert.setCreateTime(LocalDateTime.now());
            riskAlert.setUpdateTime(LocalDateTime.now());

            boolean success = save(riskAlert);
            if (success) {
                // 异步发送预警
                sendAlertAsync(riskAlert);
                return alertId;
            } else {
                throw new RuntimeException("创建预警失败");
            }
        } catch (Exception e) {
            log.error("创建预警失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建预警失败");
        }
    }

    @Override
    @Transactional
    public boolean updateRiskAlert(RiskAlert riskAlert) {
        try {
            riskAlert.setUpdateTime(LocalDateTime.now());
            return updateById(riskAlert);
        } catch (Exception e) {
            log.error("更新预警失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteRiskAlert(String alertId) {
        try {
            RiskAlert alert = riskAlertMapper.selectByAlertId(alertId);
            if (alert != null) {
                alert.setDeleted(1);
                alert.setUpdateTime(LocalDateTime.now());
                return updateById(alert);
            }
            return false;
        } catch (Exception e) {
            log.error("删除预警失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public RiskAlert getRiskAlertByAlertId(String alertId) {
        try {
            return riskAlertMapper.selectByAlertId(alertId);
        } catch (Exception e) {
            log.error("获取预警详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getRiskAlertList(Long userId, Integer alertType, Integer riskLevel, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<RiskAlert> alerts = riskAlertMapper.selectByUserId(userId, alertType, riskLevel, offset, size);
            // 计算总数
            int total = riskAlertMapper.countByAlertType(userId, alertType, riskLevel);

            Map<String, Object> result = new HashMap<>();
            result.put("list", alerts);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询预警列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public List<RiskAlert> getRiskAlertListByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Long userId, Integer alertType, Integer riskLevel) {
        try {
            return riskAlertMapper.selectByTimeRange(startTime, endTime, userId, alertType, riskLevel);
        } catch (Exception e) {
            log.error("根据时间范围查询预警列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean sendAlert(String alertId) {
        try {
            RiskAlert alert = riskAlertMapper.selectByAlertId(alertId);
            if (alert == null) {
                throw new RuntimeException("预警不存在");
            }

            boolean success = true;

            // 根据通知方式发送预警
            if (alert.getNotificationMethod() == 1 || alert.getNotificationMethod() == 4) {
                // WebSocket推送
                success = sendWebSocketAlert(alertId);
            }

            if (alert.getNotificationMethod() == 2 || alert.getNotificationMethod() == 4) {
                // 短信发送
                success = success && sendSmsAlert(alertId);
            }

            if (alert.getNotificationMethod() == 3 || alert.getNotificationMethod() == 4) {
                // 邮件发送
                success = success && sendEmailAlert(alertId);
            }

            // 更新预警状态
            if (success) {
                riskAlertMapper.updateStatus(alert.getId(), 1); // 已处理
            } else {
                riskAlertMapper.updateStatus(alert.getId(), 3); // 处理失败
            }

            return success;
        } catch (Exception e) {
            log.error("发送预警失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean sendWebSocketAlert(String alertId) {
        try {
            RiskAlert alert = riskAlertMapper.selectByAlertId(alertId);
            if (alert == null) {
                throw new RuntimeException("预警不存在");
            }

            // 构建Protobuf预警消息
            WebSocketMessage alertMessage = protobufMessageHandler.buildAlertMessage(
                    alert.getUserId().toString(),
                    alert.getAlertId(),
                    alert.getAlertType(),
                    alert.getRiskLevel(),
                    alert.getAlertTitle(),
                    alert.getAlertContent(),
                    alert.getAlertDetails(),
                    alert.getPhoneNumber(),
                    alert.getEmail(),
                    alert.getAlertTime().toEpochSecond() * 1000
            );

            // 序列化消息
            byte[] messageBytes = protobufMessageHandler.serialize(alertMessage);

            // 构建消息存储对象
            WebSocketMessageStore messageStore = new WebSocketMessageStore();
            messageStore.setMessageId(alertMessage.getMessageId());
            messageStore.setMessageType(2); // 预警消息
            messageStore.setSenderId(1L); // 系统发送
            messageStore.setSenderName("系统");
            messageStore.setReceiverId(alert.getUserId());
            messageStore.setMessageContent(messageBytes);
            messageStore.setDescription("风险预警消息：" + alert.getAlertTitle());

            // 存储并发送消息
            boolean success = webSocketMessageStoreService.storeAndSendMessage(messageStore);

            // 更新WebSocket状态
            if (success) {
                riskAlertMapper.updateWebSocketStatus(alert.getId(), 1); // 已推送
            } else {
                riskAlertMapper.updateWebSocketStatus(alert.getId(), 2); // 推送失败
            }

            return success;
        } catch (Exception e) {
            log.error("WebSocket推送预警失败: {}", e.getMessage(), e);
            try {
                RiskAlert alert = riskAlertMapper.selectByAlertId(alertId);
                if (alert != null) {
                    riskAlertMapper.updateWebSocketStatus(alert.getId(), 2); // 推送失败
                }
            } catch (Exception ex) {
                log.error("更新WebSocket状态失败: {}", ex.getMessage(), ex);
            }
            return false;
        }
    }

    @Override
    @Transactional
    public boolean sendSmsAlert(String alertId) {
        try {
            RiskAlert alert = riskAlertMapper.selectByAlertId(alertId);
            if (alert == null) {
                throw new RuntimeException("预警不存在");
            }

            // 这里简化处理，实际应该调用短信服务API
            // 例如阿里云短信服务、腾讯云短信服务等
            log.info("发送短信预警: 手机号={}, 内容={}", alert.getPhoneNumber(), alert.getAlertContent());

            // 模拟短信发送
            boolean success = Math.random() > 0.1; // 90%成功率

            // 更新短信状态
            if (success) {
                riskAlertMapper.updateSmsStatus(alert.getId(), 1); // 已发送
            } else {
                riskAlertMapper.updateSmsStatus(alert.getId(), 2); // 发送失败
            }

            return success;
        } catch (Exception e) {
            log.error("短信发送预警失败: {}", e.getMessage(), e);
            try {
                RiskAlert alert = riskAlertMapper.selectByAlertId(alertId);
                if (alert != null) {
                    riskAlertMapper.updateSmsStatus(alert.getId(), 2); // 发送失败
                }
            } catch (Exception ex) {
                log.error("更新短信状态失败: {}", ex.getMessage(), ex);
            }
            return false;
        }
    }

    @Override
    @Transactional
    public boolean sendEmailAlert(String alertId) {
        try {
            RiskAlert alert = riskAlertMapper.selectByAlertId(alertId);
            if (alert == null) {
                throw new RuntimeException("预警不存在");
            }

            // 这里简化处理，实际应该调用邮件服务API
            // 例如JavaMail、Spring Mail等
            log.info("发送邮件预警: 邮箱={}, 标题={}, 内容={}", alert.getEmail(), alert.getAlertTitle(), alert.getAlertContent());

            // 模拟邮件发送
            boolean success = Math.random() > 0.05; // 95%成功率

            // 更新邮件状态
            if (success) {
                riskAlertMapper.updateEmailStatus(alert.getId(), 1); // 已发送
            } else {
                riskAlertMapper.updateEmailStatus(alert.getId(), 2); // 发送失败
            }

            return success;
        } catch (Exception e) {
            log.error("邮件发送预警失败: {}", e.getMessage(), e);
            try {
                RiskAlert alert = riskAlertMapper.selectByAlertId(alertId);
                if (alert != null) {
                    riskAlertMapper.updateEmailStatus(alert.getId(), 2); // 发送失败
                }
            } catch (Exception ex) {
                log.error("更新邮件状态失败: {}", ex.getMessage(), ex);
            }
            return false;
        }
    }

    @Override
    @Transactional
    public String createAndSendAlert(Long userId, String username, Integer alertType, Integer riskLevel, String alertTitle, String alertContent, String alertDetails, Integer notificationMethod, String phoneNumber, String email) {
        try {
            // 创建预警
            RiskAlert alert = new RiskAlert();
            alert.setUserId(userId);
            alert.setUsername(username);
            alert.setAlertType(alertType);
            alert.setRiskLevel(riskLevel);
            alert.setAlertTitle(alertTitle);
            alert.setAlertContent(alertContent);
            alert.setAlertDetails(alertDetails);
            alert.setNotificationMethod(notificationMethod);
            alert.setPhoneNumber(phoneNumber);
            alert.setEmail(email);
            alert.setWebSocketStatus(3); // 未推送
            alert.setSmsStatus(3); // 未发送
            alert.setEmailStatus(3); // 未发送

            String alertId = createRiskAlert(alert);

            // 发送预警
            sendAlert(alertId);

            return alertId;
        } catch (Exception e) {
            log.error("创建并发送预警失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建并发送预警失败");
        }
    }

    @Override
    @Transactional
    public int batchCreateAlerts(List<RiskAlert> alerts) {
        try {
            for (RiskAlert alert : alerts) {
                alert.setAlertId(UUID.randomUUID().toString());
                alert.setAlertTime(LocalDateTime.now());
                alert.setStatus(2); // 处理中
                alert.setDeleted(0);
                alert.setCreateTime(LocalDateTime.now());
                alert.setUpdateTime(LocalDateTime.now());
            }
            int count = riskAlertMapper.batchInsert(alerts);
            // 异步发送预警
            for (RiskAlert alert : alerts) {
                sendAlertAsync(alert);
            }
            return count;
        } catch (Exception e) {
            log.error("批量创建预警失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getRiskAlertStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 统计总预警数量
            int total = riskAlertMapper.selectList(null).size();
            statistics.put("total", total);

            // 统计各预警类型数量
            int behaviorAlert = riskAlertMapper.countByAlertType(null, 1, null);
            int deviceAlert = riskAlertMapper.countByAlertType(null, 2, null);
            int socialAlert = riskAlertMapper.countByAlertType(null, 3, null);
            int overallAlert = riskAlertMapper.countByAlertType(null, 4, null);
            statistics.put("behaviorAlert", behaviorAlert);
            statistics.put("deviceAlert", deviceAlert);
            statistics.put("socialAlert", socialAlert);
            statistics.put("overallAlert", overallAlert);

            // 统计各风险等级数量
            int lowRisk = riskAlertMapper.countByAlertType(null, null, 1);
            int mediumRisk = riskAlertMapper.countByAlertType(null, null, 2);
            int highRisk = riskAlertMapper.countByAlertType(null, null, 3);
            int veryHighRisk = riskAlertMapper.countByAlertType(null, null, 4);
            statistics.put("lowRisk", lowRisk);
            statistics.put("mediumRisk", mediumRisk);
            statistics.put("highRisk", highRisk);
            statistics.put("veryHighRisk", veryHighRisk);

            // 统计各通知方式数量
            int webSocketSent = riskAlertMapper.selectList(null).stream()
                    .filter(a -> a.getWebSocketStatus() == 1)
                    .collect(Collectors.toList())
                    .size();
            int smsSent = riskAlertMapper.selectList(null).stream()
                    .filter(a -> a.getSmsStatus() == 1)
                    .collect(Collectors.toList())
                    .size();
            int emailSent = riskAlertMapper.selectList(null).stream()
                    .filter(a -> a.getEmailStatus() == 1)
                    .collect(Collectors.toList())
                    .size();
            statistics.put("webSocketSent", webSocketSent);
            statistics.put("smsSent", smsSent);
            statistics.put("emailSent", emailSent);

            return statistics;
        } catch (Exception e) {
            log.error("获取预警统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional
    public boolean updateAlertStatus(String alertId, Integer status) {
        try {
            RiskAlert alert = riskAlertMapper.selectByAlertId(alertId);
            if (alert != null) {
                return riskAlertMapper.updateStatus(alert.getId(), status) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("更新预警状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 异步发送预警
     * @param alert 预警信息
     */
    private void sendAlertAsync(RiskAlert alert) {
        // 这里简化处理，实际应该使用线程池或消息队列进行异步处理
        new Thread(() -> {
            try {
                sendAlert(alert.getAlertId());
            } catch (Exception e) {
                log.error("异步发送预警失败: {}", e.getMessage(), e);
                try {
                    riskAlertMapper.updateStatus(alert.getId(), 3); // 处理失败
                } catch (Exception ex) {
                    log.error("更新预警状态失败: {}", ex.getMessage(), ex);
                }
            }
        }).start();
    }

    /**
     * 添加WebSocket会话
     * @param userId 用户ID
     * @param session WebSocket会话
     */
    public static void addWebSocketSession(Long userId, WebSocketSession session) {
        userSessions.put(userId, session);
        log.info("用户{}的WebSocket会话已添加", userId);
    }

    /**
     * 移除WebSocket会话
     * @param userId 用户ID
     */
    public static void removeWebSocketSession(Long userId) {
        userSessions.remove(userId);
        log.info("用户{}的WebSocket会话已移除", userId);
    }

    /**
     * 获取WebSocket会话
     * @param userId 用户ID
     * @return WebSocket会话
     */
    public static WebSocketSession getWebSocketSession(Long userId) {
        return userSessions.get(userId);
    }

    /**
     * 获取所有WebSocket会话
     * @return WebSocket会话映射
     */
    public static Map<Long, WebSocketSession> getAllWebSocketSessions() {
        return userSessions;
    }
}
