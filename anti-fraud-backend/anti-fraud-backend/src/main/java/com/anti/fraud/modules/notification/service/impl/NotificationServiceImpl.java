package com.anti.fraud.modules.notification.service.impl;

import com.anti.fraud.modules.notification.entity.Notification;
import com.anti.fraud.modules.notification.mapper.NotificationMapper;
import com.anti.fraud.modules.notification.service.NotificationService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationMapper notificationMapper;
    
    @Override
    public Notification sendNotification(Long userId, String type, String title, 
                                       String content, String icon, String action, 
                                       String actionText, String extra) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIcon(icon);
        notification.setAction(action);
        notification.setActionText(actionText);
        notification.setExtra(extra);
        
        int result = notificationMapper.insert(notification);
        if (result > 0) {
            log.info("发送通知成功: userId={}, title={}", userId, title);
            return notification;
        }
        
        log.error("发送通知失败: userId={}, title={}", userId, title);
        return null;
    }
    
    @Override
    public int sendBatchNotification(Long[] userIds, String type, String title, 
                                   String content, String icon, String action, 
                                   String actionText, String extra) {
        int successCount = 0;
        
        for (Long userId : userIds) {
            Notification notification = sendNotification(userId, type, title, content, 
                                                     icon, action, actionText, extra);
            if (notification != null) {
                successCount++;
            }
        }
        
        log.info("批量发送通知完成: 总数={}, 成功={}", userIds.length, successCount);
        return successCount;
    }
    
    @Override
    public IPage<Notification> getUserNotifications(int page, int size, Long userId, String type) {
        IPage<Notification> pageInfo = new Page<>(page, size);
        return notificationMapper.selectUserNotifications(pageInfo, userId, type);
    }
    
    @Override
    public int getUnreadCount(Long userId) {
        return notificationMapper.countUnreadNotifications(userId);
    }
    
    @Override
    public boolean markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            return false;
        }
        
        notification.setRead(true);
        int result = notificationMapper.updateById(notification);
        return result > 0;
    }
    
    @Override
    public int markBatchAsRead(Long userId, Long[] notificationIds) {
        return notificationMapper.markAsRead(userId, notificationIds);
    }
    
    @Override
    public boolean deleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            return false;
        }
        
        notification.setDeleted(true);
        int result = notificationMapper.updateById(notification);
        return result > 0;
    }
    
    @Override
    public int deleteBatchNotifications(Long userId, Long[] notificationIds) {
        int successCount = 0;
        
        for (Long id : notificationIds) {
            if (deleteNotification(userId, id)) {
                successCount++;
            }
        }
        
        return successCount;
    }
    
    @Override
    public Map<String, Integer> getNotificationStats(Long userId) {
        Map<String, Integer> stats = new HashMap<>();
        
        // 这里可以通过查询数据库获取各类型通知的数量
        // 暂时返回模拟数据
        stats.put("all", 24);
        stats.put("system", 5);
        stats.put("learning", 12);
        stats.put("interaction", 7);
        stats.put("activity", 3);
        stats.put("unread", getUnreadCount(userId));
        
        return stats;
    }
}
