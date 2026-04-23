package com.anti.fraud.modules.websocket.mapper;

import com.anti.fraud.modules.websocket.entity.WebSocketMessageStore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * WebSocket消息存储Mapper
 */
@Mapper
public interface WebSocketMessageStoreMapper extends BaseMapper<WebSocketMessageStore> {

    /**
     * 根据消息ID查询消息
     * @param messageId 消息ID
     * @return WebSocket消息存储
     */
    WebSocketMessageStore selectByMessageId(@Param("messageId") String messageId);

    /**
     * 根据接收者ID查询消息列表
     * @param receiverId 接收者ID
     * @param status 消息状态
     * @param page 页码
     * @param size 每页大小
     * @return WebSocket消息存储列表
     */
    List<WebSocketMessageStore> selectByReceiverId(@Param("receiverId") Long receiverId, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据发送者ID查询消息列表
     * @param senderId 发送者ID
     * @param status 消息状态
     * @param page 页码
     * @param size 每页大小
     * @return WebSocket消息存储列表
     */
    List<WebSocketMessageStore> selectBySenderId(@Param("senderId") Long senderId, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据时间范围查询消息列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @param status 消息状态
     * @return WebSocket消息存储列表
     */
    List<WebSocketMessageStore> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("receiverId") Long receiverId, @Param("senderId") Long senderId, @Param("status") Integer status);

    /**
     * 更新消息状态
     * @param id 消息ID
     * @param status 消息状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新消息接收时间
     * @param id 消息ID
     * @param receiveTime 接收时间
     * @return 影响行数
     */
    int updateReceiveTime(@Param("id") Long id, @Param("receiveTime") LocalDateTime receiveTime);

    /**
     * 更新消息重试次数
     * @param id 消息ID
     * @param retryCount 重试次数
     * @return 影响行数
     */
    int updateRetryCount(@Param("id") Long id, @Param("retryCount") Integer retryCount);

    /**
     * 统计消息数量
     * @param receiverId 接收者ID
     * @param status 消息状态
     * @return 消息数量
     */
    Integer countByReceiverId(@Param("receiverId") Long receiverId, @Param("status") Integer status);

    /**
     * 获取未发送的消息
     * @param limit 数量限制
     * @return 未发送的消息列表
     */
    List<WebSocketMessageStore> selectUnsentMessages(@Param("limit") Integer limit);

    /**
     * 批量插入消息
     * @param messages 消息列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("messages") List<WebSocketMessageStore> messages);

    /**
     * 批量更新消息状态
     * @param ids 消息ID列表
     * @param status 消息状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
}
