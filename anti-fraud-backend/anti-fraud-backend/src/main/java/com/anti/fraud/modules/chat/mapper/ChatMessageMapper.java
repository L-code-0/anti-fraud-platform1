package com.anti.fraud.modules.chat.mapper;

import com.anti.fraud.modules.chat.entity.ChatMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 对话消息Mapper
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    /**
     * 根据会话ID查询消息列表
     * @param sessionId 会话ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表
     */
    List<ChatMessage> selectBySessionId(@Param("sessionId") String sessionId, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据消息ID查询消息
     * @param messageId 消息ID
     * @return 消息
     */
    ChatMessage selectByMessageId(@Param("messageId") String messageId);

    /**
     * 更新消息状态
     * @param id 消息ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新消息阅读时间
     * @param id 消息ID
     * @param readTime 阅读时间
     * @return 影响行数
     */
    int updateReadTime(@Param("id") Long id, @Param("readTime") java.time.LocalDateTime readTime);

    /**
     * 统计会话消息数量
     * @param sessionId 会话ID
     * @return 消息数量
     */
    Integer countBySessionId(@Param("sessionId") String sessionId);

    /**
     * 统计会话未读消息数量
     * @param sessionId 会话ID
     * @return 未读消息数量
     */
    Integer countUnreadMessages(@Param("sessionId") String sessionId);

    /**
     * 获取会话最后一条消息
     * @param sessionId 会话ID
     * @return 最后一条消息
     */
    ChatMessage selectLastMessage(@Param("sessionId") String sessionId);

    /**
     * 批量插入消息
     * @param messages 消息列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("messages") List<ChatMessage> messages);
}
