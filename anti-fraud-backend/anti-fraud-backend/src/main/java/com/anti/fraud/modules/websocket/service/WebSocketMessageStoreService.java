package com.anti.fraud.modules.websocket.service;

import com.anti.fraud.modules.websocket.entity.WebSocketMessageStore;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * WebSocket消息存储服务接口
 */
public interface WebSocketMessageStoreService extends IService<WebSocketMessageStore> {

    /**
     * 存储消息
     * @param message 消息
     * @return 是否成功
     */
    boolean storeMessage(WebSocketMessageStore message);

    /**
     * 存储并发送消息
     * @param message 消息
     * @return 是否成功
     */
    boolean storeAndSendMessage(WebSocketMessageStore message);

    /**
     * 批量存储消息
     * @param messages 消息列表
     * @return 成功存储的数量
     */
    int batchStoreMessages(List<WebSocketMessageStore> messages);

    /**
     * 获取消息
     * @param messageId 消息ID
     * @return 消息
     */
    WebSocketMessageStore getMessage(String messageId);

    /**
     * 分页查询消息列表
     * @param receiverId 接收者ID
     * @param status 消息状态
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表和总数
     */
    Map<String, Object> getMessageList(Long receiverId, Integer status, int page, int size);

    /**
     * 根据时间范围查询消息列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @param status 消息状态
     * @return 消息列表
     */
    List<WebSocketMessageStore> getMessageListByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Long receiverId, Long senderId, Integer status);

    /**
     * 更新消息状态
     * @param messageId 消息ID
     * @param status 消息状态
     * @return 是否成功
     */
    boolean updateMessageStatus(String messageId, Integer status);

    /**
     * 更新消息接收时间
     * @param messageId 消息ID
     * @param receiveTime 接收时间
     * @return 是否成功
     */
    boolean updateMessageReceiveTime(String messageId, LocalDateTime receiveTime);

    /**
     * 发送离线消息
     * @param receiverId 接收者ID
     * @return 发送成功的数量
     */
    int sendOfflineMessages(Long receiverId);

    /**
     * 批量发送离线消息
     * @param limit 数量限制
     * @return 发送成功的数量
     */
    int batchSendOfflineMessages(Integer limit);

    /**
     * 删除消息
     * @param messageId 消息ID
     * @return 是否成功
     */
    boolean deleteMessage(String messageId);

    /**
     * 清理过期消息
     * @param days 天数
     * @return 清理成功的数量
     */
    int cleanExpiredMessages(Integer days);

    /**
     * 获取消息统计信息
     * @return 统计信息
     */
    Map<String, Object> getMessageStatistics();

    /**
     * 获取未发送的消息
     * @param limit 数量限制
     * @return 未发送的消息列表
     */
    List<WebSocketMessageStore> getUnsentMessages(Integer limit);

    /**
     * 重试发送失败的消息
     * @param messageId 消息ID
     * @return 是否成功
     */
    boolean retryFailedMessage(String messageId);

    /**
     * 批量重试发送失败的消息
     * @param limit 数量限制
     * @return 重试成功的数量
     */
    int batchRetryFailedMessages(Integer limit);
}
