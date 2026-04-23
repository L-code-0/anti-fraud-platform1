package com.anti.fraud.modules.websocket.handler;

import com.anti.fraud.proto.WebSocketMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Protobuf消息处理器
 */
@Component
@Slf4j
public class ProtobufMessageHandler {

    /**
     * 序列化消息
     * @param message WebSocket消息
     * @return 字节数组
     */
    public byte[] serialize(WebSocketMessage message) {
        try {
            return message.toByteArray();
        } catch (Exception e) {
            log.error("序列化消息失败: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    /**
     * 反序列化消息
     * @param bytes 字节数组
     * @return WebSocket消息
     */
    public WebSocketMessage deserialize(byte[] bytes) {
        try {
            return WebSocketMessage.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            log.error("反序列化消息失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 构建心跳请求消息
     * @param userId 用户ID
     * @param pingId 心跳ID
     * @param sequence 序列号
     * @return WebSocket消息
     */
    public WebSocketMessage buildPingMessage(String userId, String pingId, long sequence) {
        WebSocketMessage.PingMessage ping = WebSocketMessage.PingMessage.newBuilder()
                .setPingId(pingId)
                .setSequence(sequence)
                .build();

        return WebSocketMessage.newBuilder()
                .setMessageId(java.util.UUID.randomUUID().toString())
                .setType(WebSocketMessage.MessageType.MESSAGE_TYPE_PING)
                .setTimestamp(System.currentTimeMillis())
                .setUserId(userId)
                .setPing(ping)
                .build();
    }

    /**
     * 构建心跳响应消息
     * @param userId 用户ID
     * @param pingId 对应心跳ID
     * @param sequence 序列号
     * @return WebSocket消息
     */
    public WebSocketMessage buildPongMessage(String userId, String pingId, long sequence) {
        WebSocketMessage.PongMessage pong = WebSocketMessage.PongMessage.newBuilder()
                .setPingId(pingId)
                .setSequence(sequence)
                .setServerTimestamp(System.currentTimeMillis())
                .build();

        return WebSocketMessage.newBuilder()
                .setMessageId(java.util.UUID.randomUUID().toString())
                .setType(WebSocketMessage.MessageType.MESSAGE_TYPE_PONG)
                .setTimestamp(System.currentTimeMillis())
                .setUserId(userId)
                .setPong(pong)
                .build();
    }

    /**
     * 构建预警消息
     * @param userId 用户ID
     * @param alertId 预警ID
     * @param alertType 预警类型
     * @param riskLevel 风险等级
     * @param alertTitle 预警标题
     * @param alertContent 预警内容
     * @param alertDetails 预警详情
     * @param phoneNumber 接收人手机号
     * @param email 接收人邮箱
     * @param alertTime 预警时间
     * @return WebSocket消息
     */
    public WebSocketMessage buildAlertMessage(String userId, String alertId, int alertType, int riskLevel, String alertTitle, String alertContent, String alertDetails, String phoneNumber, String email, long alertTime) {
        WebSocketMessage.AlertMessage alert = WebSocketMessage.AlertMessage.newBuilder()
                .setAlertId(alertId)
                .setAlertType(WebSocketMessage.AlertType.forNumber(alertType))
                .setRiskLevel(WebSocketMessage.RiskLevel.forNumber(riskLevel))
                .setAlertTitle(alertTitle)
                .setAlertContent(alertContent)
                .setAlertDetails(alertDetails)
                .setPhoneNumber(phoneNumber)
                .setEmail(email)
                .setAlertTime(alertTime)
                .build();

        return WebSocketMessage.newBuilder()
                .setMessageId(java.util.UUID.randomUUID().toString())
                .setType(WebSocketMessage.MessageType.MESSAGE_TYPE_ALERT)
                .setTimestamp(System.currentTimeMillis())
                .setUserId(userId)
                .setAlert(alert)
                .build();
    }

    /**
     * 构建连接消息
     * @param userId 用户ID
     * @param status 连接状态
     * @param message 连接消息
     * @param username 用户名
     * @return WebSocket消息
     */
    public WebSocketMessage buildConnectionMessage(String userId, String status, String message, String username) {
        WebSocketMessage.ConnectionMessage connection = WebSocketMessage.ConnectionMessage.newBuilder()
                .setStatus(status)
                .setMessage(message)
                .setUserId(userId)
                .setUsername(username)
                .build();

        return WebSocketMessage.newBuilder()
                .setMessageId(java.util.UUID.randomUUID().toString())
                .setType(WebSocketMessage.MessageType.MESSAGE_TYPE_CONNECTION)
                .setTimestamp(System.currentTimeMillis())
                .setUserId(userId)
                .setConnection(connection)
                .build();
    }

    /**
     * 构建订阅消息
     * @param userId 用户ID
     * @param subscribeId 订阅ID
     * @param topic 订阅主题
     * @param subscribe 订阅/取消订阅
     * @param status 订阅状态
     * @param message 订阅消息
     * @return WebSocket消息
     */
    public WebSocketMessage buildSubscribeMessage(String userId, String subscribeId, String topic, boolean subscribe, String status, String message) {
        WebSocketMessage.SubscribeMessage subscribeMsg = WebSocketMessage.SubscribeMessage.newBuilder()
                .setSubscribeId(subscribeId)
                .setTopic(topic)
                .setSubscribe(subscribe)
                .setStatus(status)
                .setMessage(message)
                .build();

        return WebSocketMessage.newBuilder()
                .setMessageId(java.util.UUID.randomUUID().toString())
                .setType(WebSocketMessage.MessageType.MESSAGE_TYPE_SUBSCRIBE)
                .setTimestamp(System.currentTimeMillis())
                .setUserId(userId)
                .setSubscribe(subscribeMsg)
                .build();
    }

    /**
     * 构建协作消息
     * @param userId 用户ID
     * @param collaborationId 协作ID
     * @param operationType 操作类型
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @param operationData 操作数据
     * @param operationTime 操作时间
     * @param username 操作用户名
     * @return WebSocket消息
     */
    public WebSocketMessage buildCollaborationMessage(String userId, String collaborationId, String operationType, String resourceType, String resourceId, String operationData, long operationTime, String username) {
        WebSocketMessage.CollaborationMessage collaboration = WebSocketMessage.CollaborationMessage.newBuilder()
                .setCollaborationId(collaborationId)
                .setOperationType(operationType)
                .setResourceType(resourceType)
                .setResourceId(resourceId)
                .setOperationData(operationData)
                .setOperationTime(operationTime)
                .setUserId(userId)
                .setUsername(username)
                .build();

        return WebSocketMessage.newBuilder()
                .setMessageId(java.util.UUID.randomUUID().toString())
                .setType(WebSocketMessage.MessageType.MESSAGE_TYPE_COLLABORATION)
                .setTimestamp(System.currentTimeMillis())
                .setUserId(userId)
                .setCollaboration(collaboration)
                .build();
    }

    /**
     * 构建错误消息
     * @param userId 用户ID
     * @param errorId 错误ID
     * @param errorCode 错误码
     * @param errorMessage 错误消息
     * @param errorDetails 错误详情
     * @return WebSocket消息
     */
    public WebSocketMessage buildErrorMessage(String userId, String errorId, int errorCode, String errorMessage, String errorDetails) {
        WebSocketMessage.ErrorMessage error = WebSocketMessage.ErrorMessage.newBuilder()
                .setErrorId(errorId)
                .setErrorCode(errorCode)
                .setErrorMessage(errorMessage)
                .setErrorDetails(errorDetails)
                .setErrorTime(System.currentTimeMillis())
                .build();

        return WebSocketMessage.newBuilder()
                .setMessageId(java.util.UUID.randomUUID().toString())
                .setType(WebSocketMessage.MessageType.MESSAGE_TYPE_ERROR)
                .setTimestamp(System.currentTimeMillis())
                .setUserId(userId)
                .setError(error)
                .build();
    }
}
