package com.anti.fraud.modules.collaboration.service.impl;

import com.anti.fraud.modules.collaboration.entity.CollaborationMessage;
import com.anti.fraud.modules.collaboration.mapper.CollaborationMessageMapper;
import com.anti.fraud.modules.collaboration.service.CollaborationMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 协作演练消息服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CollaborationMessageServiceImpl extends ServiceImpl<CollaborationMessageMapper, CollaborationMessage> implements CollaborationMessageService {

    private final CollaborationMessageMapper collaborationMessageMapper;

    @Override
    @Transactional
    public boolean sendMessage(CollaborationMessage message) {
        try {
            message.setMessageTime(LocalDateTime.now());
            message.setStatus(1);
            message.setDeleted(0);
            message.setCreateTime(LocalDateTime.now());
            message.setUpdateTime(LocalDateTime.now());
            return save(message);
        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateMessageStatus(Long id, Integer status) {
        try {
            CollaborationMessage message = getById(id);
            if (message != null) {
                message.setStatus(status);
                message.setUpdateTime(LocalDateTime.now());
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
    public boolean deleteMessage(Long id) {
        try {
            CollaborationMessage message = getById(id);
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
    public CollaborationMessage getMessageById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取消息详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getMessagesBySessionId(Long sessionId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<CollaborationMessage> messages = collaborationMessageMapper.selectBySessionId(sessionId, offset, size);
            int total = collaborationMessageMapper.selectCountBySessionId(sessionId);

            Map<String, Object> result = new HashMap<>();
            result.put("list", messages);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据会话ID查询消息失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getMessagesBySessionIdAndType(Long sessionId, Integer messageType, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<CollaborationMessage> messages = collaborationMessageMapper.selectBySessionIdAndType(sessionId, messageType, offset, size);
            // 计算总数
            int total = messages.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", messages);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据会话ID和消息类型查询消息失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public boolean sendSystemMessage(Long sessionId, String content) {
        try {
            CollaborationMessage message = new CollaborationMessage();
            message.setSessionId(sessionId);
            message.setSenderId(0L);
            message.setSenderName("系统");
            message.setMessageType(4);
            message.setContent(content);
            message.setMessageTime(LocalDateTime.now());
            message.setStatus(1);
            message.setDeleted(0);
            message.setCreateTime(LocalDateTime.now());
            message.setUpdateTime(LocalDateTime.now());
            return save(message);
        } catch (Exception e) {
            log.error("发送系统通知失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getMessageTypeStatsBySessionId(Long sessionId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("messageTypeStats", collaborationMessageMapper.selectMessageTypeStatsBySessionId(sessionId));
            return stats;
        } catch (Exception e) {
            log.error("统计会话消息类型失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
}
