package com.anti.fraud.modules.notification.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.notification.dto.SendNotificationDTO;
import com.anti.fraud.modules.notification.entity.NotificationMessage;
import com.anti.fraud.modules.notification.mapper.NotificationMessageMapper;
import com.anti.fraud.modules.notification.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationMessageMapper notificationMapper;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    @Transactional
    public int sendNotification(SendNotificationDTO dto) {
        List<Long> receiverIds;
        
        if (dto.getReceiverId() != null) {
            receiverIds = List.of(dto.getReceiverId());
        } else if (dto.getReceiverIds() != null && !dto.getReceiverIds().isEmpty()) {
            receiverIds = dto.getReceiverIds();
        } else {
            throw new BusinessException("请指定接收者");
        }
        
        Long senderId = SecurityUtils.getCurrentUserId();
        String senderName = SecurityUtils.getCurrentUserName();
        if (senderName == null) {
            senderName = "系统";
        }
        
        int count = 0;
        for (Long receiverId : receiverIds) {
            NotificationMessage message = new NotificationMessage();
            message.setType(dto.getType());
            message.setTitle(dto.getTitle());
            message.setContent(dto.getContent());
            message.setSenderId(senderId);
            message.setSenderName(senderName);
            message.setReceiverId(receiverId);
            message.setBizType(dto.getBizType());
            message.setBizId(dto.getBizId());
            message.setPriority(dto.getPriority() != null ? dto.getPriority() : 2);
            message.setIsRead(0);
            message.setStatus(1);
            message.setSendTime(LocalDateTime.now());
            message.setCreateTime(LocalDateTime.now());
            message.setUpdateTime(LocalDateTime.now());
            
            if (dto.getExpireTime() != null && !dto.getExpireTime().isEmpty()) {
                message.setExpireTime(LocalDateTime.parse(dto.getExpireTime(), FORMATTER));
            }
            
            notificationMapper.insert(message);
            count++;
        }
        
        log.info("发送通知成功，类型: {}, 数量: {}", dto.getType(), count);
        return count;
    }
    
    @Override
    @Async
    public void sendSystemNotification(String title, String content, Long receiverId) {
        SendNotificationDTO dto = new SendNotificationDTO();
        dto.setType(1);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setReceiverId(receiverId);
        dto.setPriority(2);
        sendNotification(dto);
    }
    
    @Override
    @Async
    public void sendActivityNotification(String title, String content, Long activityId, Long... receiverIds) {
        SendNotificationDTO dto = new SendNotificationDTO();
        dto.setType(2);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setBizType("activity");
        dto.setBizId(activityId);
        dto.setReceiverIds(Arrays.asList(receiverIds));
        dto.setPriority(2);
        sendNotification(dto);
    }
    
    @Override
    @Async
    public void sendTaskNotification(String title, String content, Long taskId, Long receiverId) {
        SendNotificationDTO dto = new SendNotificationDTO();
        dto.setType(3);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setReceiverId(receiverId);
        dto.setBizType("task");
        dto.setBizId(taskId);
        dto.setPriority(3);
        sendNotification(dto);
    }
    
    @Override
    public IPage<NotificationMessage> getUserNotifications(Integer page, Integer size, Integer type, Integer isRead) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        
        Page<NotificationMessage> pageParam = new Page<>(page, size);
        return notificationMapper.selectUserNotifications(pageParam, userId, type, isRead);
    }
    
    @Override
    public Integer getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return 0;
        }
        return notificationMapper.countUnread(userId);
    }
    
    @Override
    @Transactional
    public void markAsRead(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        NotificationMessage message = notificationMapper.selectById(id);
        
        if (message == null) {
            throw new BusinessException("通知不存在");
        }
        
        if (!message.getReceiverId().equals(userId)) {
            throw new BusinessException("无权操作此通知");
        }
        
        if (message.getIsRead() == 0) {
            message.setIsRead(1);
            message.setReadTime(LocalDateTime.now());
            notificationMapper.updateById(message);
        }
    }
    
    @Override
    @Transactional
    public void markAllAsRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        notificationMapper.markAllAsRead(userId);
        log.info("用户 {} 标记所有通知为已读", userId);
    }
    
    @Override
    @Transactional
    public void deleteNotification(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        NotificationMessage message = notificationMapper.selectById(id);
        
        if (message == null) {
            throw new BusinessException("通知不存在");
        }
        
        if (!message.getReceiverId().equals(userId)) {
            throw new BusinessException("无权删除他人通知");
        }
        
        notificationMapper.deleteById(id);
        log.info("用户 {} 删除通知 {}", userId, id);
    }
    
    @Override
    @Transactional
    public void clearReadNotifications() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        
        LambdaQueryWrapper<NotificationMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotificationMessage::getReceiverId, userId)
               .eq(NotificationMessage::getIsRead, 1);
        
        notificationMapper.delete(wrapper);
        log.info("用户 {} 清空已读通知", userId);
    }
}
