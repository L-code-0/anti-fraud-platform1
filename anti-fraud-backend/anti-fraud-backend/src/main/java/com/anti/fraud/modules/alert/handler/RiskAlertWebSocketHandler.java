package com.anti.fraud.modules.alert.handler;

import com.anti.fraud.modules.alert.service.impl.RiskAlertServiceImpl;
import com.anti.fraud.modules.websocket.handler.ProtobufMessageHandler;
import com.anti.fraud.modules.websocket.manager.HeartbeatManager;
import com.anti.fraud.modules.websocket.service.WebSocketMessageStoreService;
import com.anti.fraud.proto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

/**
 * 风险预警WebSocket处理器
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RiskAlertWebSocketHandler extends BinaryWebSocketHandler {

    private final ProtobufMessageHandler protobufMessageHandler;
    private final HeartbeatManager heartbeatManager;
    private final WebSocketMessageStoreService webSocketMessageStoreService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            // 从会话属性中获取用户ID
            Long userId = (Long) session.getAttributes().get("userId");
            if (userId != null) {
                // 添加WebSocket会话
                RiskAlertServiceImpl.addWebSocketSession(userId, session);
                // 注册心跳
                heartbeatManager.registerHeartbeat(userId);
                log.info("用户{}的WebSocket连接已建立", userId);

                // 发送连接成功消息
                WebSocketMessage connectionMessage = protobufMessageHandler.buildConnectionMessage(
                        userId.toString(),
                        "success",
                        "WebSocket连接成功",
                        "user_" + userId
                );
                byte[] messageBytes = protobufMessageHandler.serialize(connectionMessage);
                session.sendMessage(new BinaryMessage(messageBytes));

                // 发送离线消息
                int offlineMessageCount = webSocketMessageStoreService.sendOfflineMessages(userId);
                if (offlineMessageCount > 0) {
                    log.info("向用户{}发送了{}条离线消息", userId, offlineMessageCount);
                }
            } else {
                log.warn("WebSocket连接建立失败：缺少用户ID");
                session.close();
            }
        } catch (Exception e) {
            log.error("WebSocket连接建立失败: {}", e.getMessage(), e);
            session.close();
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        try {
            // 解析Protobuf消息
            WebSocketMessage webSocketMessage = protobufMessageHandler.deserialize(message.getPayload().array());
            if (webSocketMessage == null) {
                log.warn("接收到无效的Protobuf消息");
                return;
            }

            // 处理不同类型的消息
            switch (webSocketMessage.getType()) {
                case MESSAGE_TYPE_PING:
                    // 处理心跳请求
                    handlePingMessage(session, webSocketMessage);
                    break;
                case MESSAGE_TYPE_SUBSCRIBE:
                    // 处理订阅请求
                    handleSubscribeMessage(session, webSocketMessage);
                    break;
                default:
                    log.warn("未知的消息类型: {}", webSocketMessage.getType());
                    break;
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try {
            // 从会话属性中获取用户ID
            Long userId = (Long) session.getAttributes().get("userId");
            if (userId != null) {
                // 移除WebSocket会话
                RiskAlertServiceImpl.removeWebSocketSession(userId);
                // 移除心跳
                heartbeatManager.removeHeartbeat(userId);
                log.info("用户{}的WebSocket连接已关闭", userId);
            }
        } catch (Exception e) {
            log.error("WebSocket连接关闭处理失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误: {}", exception.getMessage(), exception);
        try {
            // 从会话属性中获取用户ID
            Long userId = (Long) session.getAttributes().get("userId");
            if (userId != null) {
                // 移除WebSocket会话
                RiskAlertServiceImpl.removeWebSocketSession(userId);
                // 移除心跳
                heartbeatManager.removeHeartbeat(userId);
            }
        } catch (Exception e) {
            log.error("处理WebSocket传输错误失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理心跳请求消息
     * @param session WebSocket会话
     * @param message WebSocket消息
     */
    private void handlePingMessage(WebSocketSession session, WebSocketMessage message) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null && message.hasPing()) {
            WebSocketMessage.PingMessage ping = message.getPing();
            // 更新心跳
            heartbeatManager.updateHeartbeat(userId);
            // 构建心跳响应消息
            WebSocketMessage pongMessage = protobufMessageHandler.buildPongMessage(
                    userId.toString(),
                    ping.getPingId(),
                    ping.getSequence()
            );
            byte[] messageBytes = protobufMessageHandler.serialize(pongMessage);
            session.sendMessage(new BinaryMessage(messageBytes));
        }
    }

    /**
     * 处理订阅请求消息
     * @param session WebSocket会话
     * @param message WebSocket消息
     */
    private void handleSubscribeMessage(WebSocketSession session, WebSocketMessage message) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null && message.hasSubscribe()) {
            WebSocketMessage.SubscribeMessage subscribe = message.getSubscribe();
            log.info("用户{}订阅预警: {}", userId, subscribe.getTopic());
            // 构建订阅响应消息
            WebSocketMessage subscribeResponse = protobufMessageHandler.buildSubscribeMessage(
                    userId.toString(),
                    subscribe.getSubscribeId(),
                    subscribe.getTopic(),
                    subscribe.getSubscribe(),
                    "success",
                    "订阅成功"
            );
            byte[] messageBytes = protobufMessageHandler.serialize(subscribeResponse);
            session.sendMessage(new BinaryMessage(messageBytes));
        }
    }
}
