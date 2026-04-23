package com.anti.fraud.modules.websocket.service.impl;

import com.anti.fraud.modules.alert.service.impl.RiskAlertServiceImpl;
import com.anti.fraud.modules.websocket.entity.WebSocketMessageStore;
import com.anti.fraud.modules.websocket.mapper.WebSocketMessageStoreMapper;
import com.anti.fraud.modules.websocket.service.WebSocketMessageStoreService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

/**
 * WebSocket消息存储服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketMessageStoreServiceImpl extends ServiceImpl<WebSocketMessageStoreMapper, WebSocketMessageStore> implements WebSocketMessageStoreService {

    private final WebSocketMessageStoreMapper webSocketMessageStoreMapper;

    @Override
    @Transactional
    public boolean storeMessage(WebSocketMessageStore message) {
        try {
            if (message.getMessageId() == null || message.getMessageId().isEmpty()) {
                message.setMessageId(UUID.randomUUID().toString());
            }
            message.setStatus(2); // 未发送
            message.setSendTime(LocalDateTime.now());
            message.setRetryCount(0);
            message.setDeleted(0);
            message.setCreateTime(LocalDateTime.now());
            message.setUpdateTime(LocalDateTime.now());
            return save(message);
        } catch (Exception e) {
            log.error("存储消息失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean storeAndSendMessage(WebSocketMessageStore message) {
        try {
            // 存储消息
            if (!storeMessage(message)) {
                return false;
            }

            // 发送消息
            return sendMessage(message);
        } catch (Exception e) {
            log.error("存储并发送消息失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public int batchStoreMessages(List<WebSocketMessageStore> messages) {
        try {
            for (WebSocketMessageStore message : messages) {
                if (message.getMessageId() == null || message.getMessageId().isEmpty()) {
                    message.setMessageId(UUID.randomUUID().toString());
                }
                message.setStatus(2); // 未发送
                message.setSendTime(LocalDateTime.now());
                message.setRetryCount(0);
                message.setDeleted(0);
                message.setCreateTime(LocalDateTime.now());
                message.setUpdateTime(LocalDateTime.now());
            }
            int count = webSocketMessageStoreMapper.batchInsert(messages);
            // 异步发送消息
            for (WebSocketMessageStore message : messages) {
                sendMessageAsync(message);
            }
            return count;
        } catch (Exception e) {
            log.error("批量存储消息失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public WebSocketMessageStore getMessage(String messageId) {
        try {
            return webSocketMessageStoreMapper.selectByMessageId(messageId);
        } catch (Exception e) {
            log.error("获取消息失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getMessageList(Long receiverId, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<WebSocketMessageStore> messages = webSocketMessageStoreMapper.selectByReceiverId(receiverId, status, offset, size);
            // 计算总数
            int total = webSocketMessageStoreMapper.countByReceiverId(receiverId, status);

            Map<String, Object> result = new HashMap<>();
            result.put("list", messages);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询消息列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public List<WebSocketMessageStore> getMessageListByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Long receiverId, Long senderId, Integer status) {
        try {
            return webSocketMessageStoreMapper.selectByTimeRange(startTime, endTime, receiverId, senderId, status);
        } catch (Exception e) {
            log.error("根据时间范围查询消息列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean updateMessageStatus(String messageId, Integer status) {
        try {
            WebSocketMessageStore message = webSocketMessageStoreMapper.selectByMessageId(messageId);
            if (message != null) {
                message.setStatus(status);
                message.setUpdateTime(LocalDateTime.now());
                if (status == 1) { // 已发送
                    message.setReceiveTime(LocalDateTime.now());
                }
                return updateById(message);
            }
            return false;
        } catch (Exception e) {
            log.error("更新消息状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateMessageReceiveTime(String messageId, LocalDateTime receiveTime) {
        try {
            WebSocketMessageStore message = webSocketMessageStoreMapper.selectByMessageId(messageId);
            if (message != null) {
                message.setReceiveTime(receiveTime);
                message.setUpdateTime(LocalDateTime.now());
                return updateById(message);
            }
            return false;
        } catch (Exception e) {
            log.error("更新消息接收时间失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public int sendOfflineMessages(Long receiverId) {
        try {
            // 查询未发送的消息
            List<WebSocketMessageStore> messages = webSocketMessageStoreMapper.selectByReceiverId(receiverId, 2, 0, 100);
            int successCount = 0;

            for (WebSocketMessageStore message : messages) {
                if (sendMessage(message)) {
                    successCount++;
                }
            }

            return successCount;
        } catch (Exception e) {
            log.error("发送离线消息失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    @Transactional
    public int batchSendOfflineMessages(Integer limit) {
        try {
            // 查询未发送的消息
            List<WebSocketMessageStore> messages = webSocketMessageStoreMapper.selectUnsentMessages(limit);
            int successCount = 0;

            for (WebSocketMessageStore message : messages) {
                if (sendMessage(message)) {
                    successCount++;
                }
            }

            return successCount;
        } catch (Exception e) {
            log.error("批量发送离线消息失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    @Transactional
    public boolean deleteMessage(String messageId) {
        try {
            WebSocketMessageStore message = webSocketMessageStoreMapper.selectByMessageId(messageId);
            if (message != null) {
                message.setDeleted(1);
                message.setUpdateTime(LocalDateTime.now());
                return updateById(message);
            }
            return false;
        } catch (Exception e) {
            log.error("删除消息失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public int cleanExpiredMessages(Integer days) {
        try {
            LocalDateTime expiredTime = LocalDateTime.now().minusDays(days);
            // 这里简化处理，实际应该根据expiredTime删除过期消息
            // 暂时返回0
            return 0;
        } catch (Exception e) {
            log.error("清理过期消息失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getMessageStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 统计总消息数
            int totalMessages = count();
            statistics.put("totalMessages", totalMessages);

            // 统计各状态消息数
            int sentMessages = webSocketMessageStoreMapper.countByReceiverId(null, 1);
            int unsentMessages = webSocketMessageStoreMapper.countByReceiverId(null, 2);
            int failedMessages = webSocketMessageStoreMapper.countByReceiverId(null, 3);
            statistics.put("sentMessages", sentMessages);
            statistics.put("unsentMessages", unsentMessages);
            statistics.put("failedMessages", failedMessages);

            return statistics;
        } catch (Exception e) {
            log.error("获取消息统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<WebSocketMessageStore> getUnsentMessages(Integer limit) {
        try {
            return webSocketMessageStoreMapper.selectUnsentMessages(limit);
        } catch (Exception e) {
            log.error("获取未发送的消息失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean retryFailedMessage(String messageId) {
        try {
            WebSocketMessageStore message = webSocketMessageStoreMapper.selectByMessageId(messageId);
            if (message != null && message.getStatus() == 3) { // 发送失败
                message.setRetryCount(message.getRetryCount() + 1);
                message.setUpdateTime(LocalDateTime.now());
                updateById(message);
                return sendMessage(message);
            }
            return false;
        } catch (Exception e) {
            log.error("重试发送失败的消息失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public int batchRetryFailedMessages(Integer limit) {
        try {
            // 这里简化处理，实际应该查询发送失败的消息并重试
            // 暂时返回0
            return 0;
        } catch (Exception e) {
            log.error("批量重试发送失败的消息失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 发送消息
     * @param message 消息
     * @return 是否成功
     */
    private boolean sendMessage(WebSocketMessageStore message) {
        try {
            // 获取接收者的WebSocket会话
            WebSocketSession session = RiskAlertServiceImpl.getWebSocketSession(message.getReceiverId());
            if (session != null && session.isOpen()) {
                // 发送消息
                session.sendMessage(new BinaryMessage(message.getMessageContent()));

                // 更新消息状态
                updateMessageStatus(message.getMessageId(), 1); // 已发送
                return true;
            } else {
                // 发送失败，更新消息状态
                updateMessageStatus(message.getMessageId(), 3); // 发送失败
                return false;
            }
        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage(), e);
            // 更新消息状态
            updateMessageStatus(message.getMessageId(), 3); // 发送失败
            return false;
        }
    }

    /**
     * 异步发送消息
     * @param message 消息
     */
    private void sendMessageAsync(WebSocketMessageStore message) {
        new Thread(() -> {
            try {
                sendMessage(message);
            } catch (Exception e) {
                log.error("异步发送消息失败: {}", e.getMessage(), e);
            }
        }).start();
    }
}
