package com.anti.fraud.modules.chat.service;

import com.anti.fraud.modules.chat.entity.ChatSession;
import com.anti.fraud.modules.chat.entity.ChatMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 对话服务接口
 */
public interface ChatService extends IService<ChatSession> {

    /**
     * 创建会话
     * @param chatSession 会话信息
     * @return 会话ID
     */
    String createSession(ChatSession chatSession);

    /**
     * 结束会话
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean endSession(String sessionId);

    /**
     * 获取会话详情
     * @param sessionId 会话ID
     * @return 会话详情
     */
    ChatSession getSessionBySessionId(String sessionId);

    /**
     * 分页查询会话列表
     * @param userId 用户ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 会话列表和总数
     */
    Map<String, Object> getSessionList(Long userId, Integer status, int page, int size);

    /**
     * 发送消息
     * @param chatMessage 消息信息
     * @return 消息ID
     */
    String sendMessage(ChatMessage chatMessage);

    /**
     * 获取消息详情
     * @param messageId 消息ID
     * @return 消息详情
     */
    ChatMessage getMessageByMessageId(String messageId);

    /**
     * 分页查询消息列表
     * @param sessionId 会话ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表和总数
     */
    Map<String, Object> getMessageList(String sessionId, Integer status, int page, int size);

    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @return 是否成功
     */
    boolean markMessageAsRead(String messageId);

    /**
     * 标记会话所有消息为已读
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean markAllMessagesAsRead(String sessionId);

    /**
     * 删除消息
     * @param messageId 消息ID
     * @return 是否成功
     */
    boolean deleteMessage(String messageId);

    /**
     * 删除会话
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean deleteSession(String sessionId);

    /**
     * 获取会话未读消息数量
     * @param sessionId 会话ID
     * @return 未读消息数量
     */
    Integer getUnreadMessageCount(String sessionId);

    /**
     * 获取用户未读消息总数
     * @param userId 用户ID
     * @return 未读消息总数
     */
    Integer getTotalUnreadMessageCount(Long userId);

    /**
     * 获取最近的会话
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 会话列表
     */
    List<ChatSession> getRecentSessions(Long userId, int limit);

    /**
     * 更新会话标题
     * @param sessionId 会话ID
     * @param title 新标题
     * @return 是否成功
     */
    boolean updateSessionTitle(String sessionId, String title);

    /**
     * 保存对话历史到Redis
     * @param sessionId 会话ID
     * @param chatHistory 对话历史
     * @return 是否成功
     */
    boolean saveChatHistoryToRedis(String sessionId, List<Map<String, String>> chatHistory);

    /**
     * 从Redis获取对话历史
     * @param sessionId 会话ID
     * @return 对话历史
     */
    List<Map<String, String>> getChatHistoryFromRedis(String sessionId);

    /**
     * 清理Redis中的对话历史
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean clearChatHistoryFromRedis(String sessionId);
}
