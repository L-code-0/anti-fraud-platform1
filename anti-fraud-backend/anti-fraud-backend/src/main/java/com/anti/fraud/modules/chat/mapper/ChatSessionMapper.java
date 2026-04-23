package com.anti.fraud.modules.chat.mapper;

import com.anti.fraud.modules.chat.entity.ChatSession;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 对话会话Mapper
 */
@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {

    /**
     * 根据用户ID查询会话列表
     * @param userId 用户ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 会话列表
     */
    List<ChatSession> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据会话ID查询会话
     * @param sessionId 会话ID
     * @return 会话
     */
    ChatSession selectBySessionId(@Param("sessionId") String sessionId);

    /**
     * 更新会话状态
     * @param id 会话ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新最后交互时间
     * @param id 会话ID
     * @param lastInteractionTime 最后交互时间
     * @return 影响行数
     */
    int updateLastInteractionTime(@Param("id") Long id, @Param("lastInteractionTime") java.time.LocalDateTime lastInteractionTime);

    /**
     * 更新对话历史
     * @param id 会话ID
     * @param chatHistory 对话历史
     * @return 影响行数
     */
    int updateChatHistory(@Param("id") Long id, @Param("chatHistory") String chatHistory);

    /**
     * 更新对话上下文
     * @param id 会话ID
     * @param context 对话上下文
     * @return 影响行数
     */
    int updateContext(@Param("id") Long id, @Param("context") String context);

    /**
     * 统计用户会话数量
     * @param userId 用户ID
     * @return 会话数量
     */
    Integer countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户活跃会话数量
     * @param userId 用户ID
     * @return 活跃会话数量
     */
    Integer countActiveSessions(@Param("userId") Long userId);

    /**
     * 获取最近的会话
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 会话列表
     */
    List<ChatSession> selectRecentSessions(@Param("userId") Long userId, @Param("limit") Integer limit);
}
