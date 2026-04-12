package com.anti.fraud.modules.websocket.service;

import com.anti.fraud.modules.websocket.handler.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketService {
    
    private final WebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;
    
    /**
     * 发送通知消息
     * @param userId 用户ID
     * @param type 消息类型
     * @param title 消息标题
     * @param content 消息内容
     * @param data 附加数据
     * @return 是否发送成功
     */
    public boolean sendNotification(Long userId, String type, String title, String content, Object data) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", type);
            message.put("title", title);
            message.put("content", content);
            message.put("data", data);
            message.put("timestamp", System.currentTimeMillis());
            
            String jsonMessage = objectMapper.writeValueAsString(message);
            return webSocketHandler.sendMessageToUser(userId, jsonMessage);
        } catch (Exception e) {
            log.error("发送通知失败: userId={}", userId, e);
            return false;
        }
    }
    
    /**
     * 发送实时消息
     * @param userId 用户ID
     * @param messageType 消息类型
     * @param data 消息数据
     * @return 是否发送成功
     */
    public boolean sendRealTimeMessage(Long userId, String messageType, Object data) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", messageType);
            message.put("data", data);
            message.put("timestamp", System.currentTimeMillis());
            
            String jsonMessage = objectMapper.writeValueAsString(message);
            return webSocketHandler.sendMessageToUser(userId, jsonMessage);
        } catch (Exception e) {
            log.error("发送实时消息失败: userId={}", userId, e);
            return false;
        }
    }
    
    /**
     * 广播消息给所有在线用户
     * @param messageType 消息类型
     * @param data 消息数据
     */
    public void broadcastMessage(String messageType, Object data) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", messageType);
            message.put("data", data);
            message.put("timestamp", System.currentTimeMillis());
            
            String jsonMessage = objectMapper.writeValueAsString(message);
            webSocketHandler.sendMessageToAll(jsonMessage);
        } catch (Exception e) {
            log.error("广播消息失败", e);
        }
    }
    
    /**
     * 获取在线用户数量
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return webSocketHandler.getOnlineUserCount();
    }
    
    /**
     * 检查用户是否在线
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(Long userId) {
        return webSocketHandler.isUserOnline(userId);
    }
}
