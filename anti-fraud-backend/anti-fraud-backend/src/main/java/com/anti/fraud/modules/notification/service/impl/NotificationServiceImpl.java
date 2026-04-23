package com.anti.fraud.modules.notification.service.impl;

import com.anti.fraud.modules.notification.entity.Notification;
import com.anti.fraud.modules.notification.mapper.NotificationMapper;
import com.anti.fraud.modules.notification.service.NotificationService;
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
 * 通知服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public boolean sendNotification(Notification notification) {
        try {
            notification.setStatus(1); // 待发送
            notification.setReadStatus(1); // 未读
            notification.setDeleted(0);
            notification.setCreateTime(LocalDateTime.now());
            notification.setUpdateTime(LocalDateTime.now());
            boolean saved = save(notification);
            if (saved) {
                // 根据通知方式发送通知
                switch (notification.getMethod()) {
                    case 2: // 短信
                        // 调用短信发送接口
                        break;
                    case 3: // 邮件
                        // 调用邮件发送接口
                        break;
                    case 4: // 微信
                        // 调用微信发送接口
                        break;
                }
                // 更新发送状态
                notification.setStatus(2); // 已发送
                notification.setSendTime(LocalDateTime.now());
                notification.setUpdateTime(LocalDateTime.now());
                return updateById(notification);
            }
            return false;
        } catch (Exception e) {
            log.error("发送通知失败: {}", e.getMessage(), e);
            // 更新发送状态为失败
            if (notification.getId() != null) {
                notification.setStatus(3); // 发送失败
                notification.setFailureReason(e.getMessage());
                notification.setUpdateTime(LocalDateTime.now());
                updateById(notification);
            }
            return false;
        }
    }

    @Override
    @Transactional
    public List<Notification> batchSendNotifications(List<Notification> notifications) {
        try {
            List<Notification> sentNotifications = new ArrayList<>();
            for (Notification notification : notifications) {
                if (sendNotification(notification)) {
                    sentNotifications.add(notification);
                }
            }
            return sentNotifications;
        } catch (Exception e) {
            log.error("批量发送通知失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean sendSmsNotification(Long userId, String username, String content) {
        try {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setUsername(username);
            notification.setType(1); // 系统通知
            notification.setTitle("系统通知");
            notification.setContent(content);
            notification.setMethod(2); // 短信
            notification.setStatus(1); // 待发送
            notification.setReadStatus(1); // 未读
            notification.setDeleted(0);
            notification.setCreateTime(LocalDateTime.now());
            notification.setUpdateTime(LocalDateTime.now());
            boolean saved = save(notification);
            if (saved) {
                // 调用短信发送接口
                // 这里简化处理，实际应该调用真实的短信发送API
                log.info("发送短信通知: {} - {}", username, content);
                // 更新发送状态
                notification.setStatus(2); // 已发送
                notification.setSendTime(LocalDateTime.now());
                notification.setUpdateTime(LocalDateTime.now());
                return updateById(notification);
            }
            return false;
        } catch (Exception e) {
            log.error("发送短信通知失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean sendEmailNotification(Long userId, String username, String email, String subject, String content) {
        try {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setUsername(username);
            notification.setType(1); // 系统通知
            notification.setTitle(subject);
            notification.setContent(content);
            notification.setMethod(3); // 邮件
            notification.setStatus(1); // 待发送
            notification.setReadStatus(1); // 未读
            notification.setDeleted(0);
            notification.setCreateTime(LocalDateTime.now());
            notification.setUpdateTime(LocalDateTime.now());
            boolean saved = save(notification);
            if (saved) {
                // 调用邮件发送接口
                // 这里简化处理，实际应该调用真实的邮件发送API
                log.info("发送邮件通知: {} - {}", email, subject);
                // 更新发送状态
                notification.setStatus(2); // 已发送
                notification.setSendTime(LocalDateTime.now());
                notification.setUpdateTime(LocalDateTime.now());
                return updateById(notification);
            }
            return false;
        } catch (Exception e) {
            log.error("发送邮件通知失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateNotificationStatus(Long id, Integer status) {
        try {
            Notification notification = getById(id);
            if (notification != null) {
                notification.setStatus(status);
                notification.setUpdateTime(LocalDateTime.now());
                return updateById(notification);
            }
            return false;
        } catch (Exception e) {
            log.error("更新通知状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean markAsRead(Long id) {
        try {
            notificationMapper.markAsRead(id);
            return true;
        } catch (Exception e) {
            log.error("标记通知为已读失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean batchMarkAsRead(List<Long> ids) {
        try {
            notificationMapper.batchMarkAsRead(ids);
            return true;
        } catch (Exception e) {
            log.error("批量标记通知为已读失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Notification getNotificationById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取通知详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getNotificationsByUserId(Long userId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<Notification> notifications = notificationMapper.selectByUserId(userId, offset, size);
            // 计算总数
            int total = notifications.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", notifications);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据用户ID查询通知失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Integer getUnreadCountByUserId(Long userId) {
        try {
            return notificationMapper.selectUnreadCountByUserId(userId);
        } catch (Exception e) {
            log.error("根据用户ID查询未读通知数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getNotificationsByType(Integer type, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<Notification> notifications = notificationMapper.selectByType(type, offset, size);
            // 计算总数
            int total = notifications.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", notifications);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据通知类型查询通知失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getNotificationsByMethod(Integer method, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<Notification> notifications = notificationMapper.selectByMethod(method, offset, size);
            // 计算总数
            int total = notifications.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", notifications);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据通知方式查询通知失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getNotificationsByStatus(Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<Notification> notifications = notificationMapper.selectByStatus(status, offset, size);
            // 计算总数
            int total = notifications.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", notifications);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据发送状态查询通知失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getNotificationStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("typeStats", notificationMapper.selectTypeStats());
            stats.put("methodStats", notificationMapper.selectMethodStats());
            stats.put("statusStats", notificationMapper.selectStatusStats());
            return stats;
        } catch (Exception e) {
            log.error("统计通知信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional
    public Integer cleanExpiredNotifications(int days) {
        try {
            // 这里简化处理，实际应该根据创建时间删除过期通知
            // 暂时返回0，表示清理了0条记录
            return 0;
        } catch (Exception e) {
            log.error("清理过期通知失败: {}", e.getMessage(), e);
            return 0;
        }
    }
}
