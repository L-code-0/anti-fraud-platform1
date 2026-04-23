package com.anti.fraud.modules.chat.service.impl;

import com.anti.fraud.modules.chat.entity.ChatSession;
import com.anti.fraud.modules.chat.entity.ChatMessage;
import com.anti.fraud.modules.chat.mapper.ChatSessionMapper;
import com.anti.fraud.modules.chat.mapper.ChatMessageMapper;
import com.anti.fraud.modules.chat.service.ChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 对话服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements ChatService {

    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // Redis键前缀
    private static final String CHAT_HISTORY_PREFIX = "chat:history:";
    private static final String CHAT_CONTEXT_PREFIX = "chat:context:";
    private static final long CHAT_HISTORY_EXPIRE_TIME = 24 * 60 * 60; // 24小时

    @Override
    @Transactional
    public String createSession(ChatSession chatSession) {
        try {
            // 生成会话ID
            String sessionId = UUID.randomUUID().toString();
            chatSession.setSessionId(sessionId);
            chatSession.setStatus(1); // 活跃
            chatSession.setLastInteractionTime(LocalDateTime.now());
            chatSession.setChatHistory("[]");
            chatSession.setContext("{}");
            chatSession.setDeleted(0);
            chatSession.setCreateTime(LocalDateTime.now());
            chatSession.setUpdateTime(LocalDateTime.now());

            boolean success = save(chatSession);
            if (success) {
                return sessionId;
            } else {
                throw new RuntimeException("创建会话失败");
            }
        } catch (Exception e) {
            log.error("创建会话失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建会话失败");
        }
    }

    @Override
    @Transactional
    public boolean endSession(String sessionId) {
        try {
            ChatSession session = chatSessionMapper.selectBySessionId(sessionId);
            if (session != null) {
                session.setStatus(2); // 已结束
                session.setUpdateTime(LocalDateTime.now());
                boolean success = updateById(session);
                if (success) {
                    // 清理Redis中的对话历史
                    clearChatHistoryFromRedis(sessionId);
                }
                return success;
            }
            return false;
        } catch (Exception e) {
            log.error("结束会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public ChatSession getSessionBySessionId(String sessionId) {
        try {
            return chatSessionMapper.selectBySessionId(sessionId);
        } catch (Exception e) {
            log.error("获取会话详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getSessionList(Long userId, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<ChatSession> sessions = chatSessionMapper.selectByUserId(userId, status, offset, size);
            // 计算总数
            int total = chatSessionMapper.countByUserId(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("list", sessions);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询会话列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public String sendMessage(ChatMessage chatMessage) {
        try {
            // 生成消息ID
            String messageId = UUID.randomUUID().toString();
            chatMessage.setMessageId(messageId);
            chatMessage.setStatus(1); // 已发送
            chatMessage.setSendTime(LocalDateTime.now());
            chatMessage.setDeleted(0);
            chatMessage.setCreateTime(LocalDateTime.now());
            chatMessage.setUpdateTime(LocalDateTime.now());

            boolean success = chatMessageMapper.insert(chatMessage) > 0;
            if (success) {
                // 更新会话的最后交互时间
                ChatSession session = chatSessionMapper.selectBySessionId(chatMessage.getSessionId());
                if (session != null) {
                    session.setLastInteractionTime(LocalDateTime.now());
                    updateById(session);
                }
                return messageId;
            } else {
                throw new RuntimeException("发送消息失败");
            }
        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage(), e);
            throw new RuntimeException("发送消息失败");
        }
    }

    @Override
    public ChatMessage getMessageByMessageId(String messageId) {
        try {
            return chatMessageMapper.selectByMessageId(messageId);
        } catch (Exception e) {
            log.error("获取消息详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getMessageList(String sessionId, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<ChatMessage> messages = chatMessageMapper.selectBySessionId(sessionId, status, offset, size);
            // 计算总数
            int total = chatMessageMapper.countBySessionId(sessionId);

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
    @Transactional
    public boolean markMessageAsRead(String messageId) {
        try {
            ChatMessage message = chatMessageMapper.selectByMessageId(messageId);
            if (message != null) {
                message.setStatus(2); // 已读
                message.setReadTime(LocalDateTime.now());
                message.setUpdateTime(LocalDateTime.now());
                return chatMessageMapper.updateById(message) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("标记消息为已读失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean markAllMessagesAsRead(String sessionId) {
        try {
            // 这里简化处理，实际应该批量更新
            List<ChatMessage> messages = chatMessageMapper.selectBySessionId(sessionId, 1, 0, Integer.MAX_VALUE);
            for (ChatMessage message : messages) {
                message.setStatus(2); // 已读
                message.setReadTime(LocalDateTime.now());
                message.setUpdateTime(LocalDateTime.now());
                chatMessageMapper.updateById(message);
            }
            return true;
        } catch (Exception e) {
            log.error("标记会话所有消息为已读失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteMessage(String messageId) {
        try {
            ChatMessage message = chatMessageMapper.selectByMessageId(messageId);
            if (message != null) {
                message.setStatus(3); // 已删除
                message.setUpdateTime(LocalDateTime.now());
                return chatMessageMapper.updateById(message) > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("删除消息失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteSession(String sessionId) {
        try {
            ChatSession session = chatSessionMapper.selectBySessionId(sessionId);
            if (session != null) {
                session.setDeleted(1);
                session.setUpdateTime(LocalDateTime.now());
                boolean success = updateById(session);
                if (success) {
                    // 标记会话所有消息为已删除
                    List<ChatMessage> messages = chatMessageMapper.selectBySessionId(sessionId, null, 0, Integer.MAX_VALUE);
                    for (ChatMessage message : messages) {
                        message.setStatus(3); // 已删除
                        message.setUpdateTime(LocalDateTime.now());
                        chatMessageMapper.updateById(message);
                    }
                    // 清理Redis中的对话历史
                    clearChatHistoryFromRedis(sessionId);
                }
                return success;
            }
            return false;
        } catch (Exception e) {
            log.error("删除会话失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Integer getUnreadMessageCount(String sessionId) {
        try {
            return chatMessageMapper.countUnreadMessages(sessionId);
        } catch (Exception e) {
            log.error("获取会话未读消息数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Integer getTotalUnreadMessageCount(Long userId) {
        try {
            // 这里简化处理，实际应该查询用户所有会话的未读消息总数
            List<ChatSession> sessions = chatSessionMapper.selectByUserId(userId, 1, 0, Integer.MAX_VALUE);
            int total = 0;
            for (ChatSession session : sessions) {
                total += chatMessageMapper.countUnreadMessages(session.getSessionId());
            }
            return total;
        } catch (Exception e) {
            log.error("获取用户未读消息总数失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public List<ChatSession> getRecentSessions(Long userId, int limit) {
        try {
            return chatSessionMapper.selectRecentSessions(userId, limit);
        } catch (Exception e) {
            log.error("获取最近的会话失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean updateSessionTitle(String sessionId, String title) {
        try {
            ChatSession session = chatSessionMapper.selectBySessionId(sessionId);
            if (session != null) {
                session.setTitle(title);
                session.setUpdateTime(LocalDateTime.now());
                return updateById(session);
            }
            return false;
        } catch (Exception e) {
            log.error("更新会话标题失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean saveChatHistoryToRedis(String sessionId, List<Map<String, String>> chatHistory) {
        try {
            String key = CHAT_HISTORY_PREFIX + sessionId;
            String json = objectMapper.writeValueAsString(chatHistory);
            redisTemplate.opsForValue().set(key, json, CHAT_HISTORY_EXPIRE_TIME, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("保存对话历史到Redis失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Map<String, String>> getChatHistoryFromRedis(String sessionId) {
        try {
            String key = CHAT_HISTORY_PREFIX + sessionId;
            String json = (String) redisTemplate.opsForValue().get(key);
            if (json != null) {
                return objectMapper.readValue(json, List.class);
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("从Redis获取对话历史失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean clearChatHistoryFromRedis(String sessionId) {
        try {
            String key = CHAT_HISTORY_PREFIX + sessionId;
            redisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            log.error("清理Redis中的对话历史失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
