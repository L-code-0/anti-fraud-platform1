package com.anti.fraud.modules.notification.service;

import com.anti.fraud.modules.notification.dto.SendNotificationDTO;
import com.anti.fraud.modules.notification.entity.NotificationMessage;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 发送通知
     * @param dto 通知信息
     * @return 发送的消息数量
     */
    int sendNotification(SendNotificationDTO dto);
    
    /**
     * 发送系统通知
     * @param title 标题
     * @param content 内容
     * @param receiverId 接收者ID
     */
    void sendSystemNotification(String title, String content, Long receiverId);
    
    /**
     * 发送活动通知
     * @param title 标题
     * @param content 内容
     * @param activityId 活动ID
     * @param receiverIds 接收者ID列表
     */
    void sendActivityNotification(String title, String content, Long activityId, Long... receiverIds);
    
    /**
     * 发送任务通知
     * @param title 标题
     * @param content 内容
     * @param taskId 任务ID
     * @param receiverId 接收者ID
     */
    void sendTaskNotification(String title, String content, Long taskId, Long receiverId);
    
    /**
     * 获取用户通知分页列表
     * @param page 页码
     * @param size 每页数量
     * @param type 通知类型
     * @param isRead 是否已读
     * @return 通知分页列表
     */
    IPage<NotificationMessage> getUserNotifications(Integer page, Integer size, Integer type, Integer isRead);
    
    /**
     * 获取未读通知数量
     * @return 未读数量
     */
    Integer getUnreadCount();
    
    /**
     * 标记通知为已读
     * @param id 通知ID
     */
    void markAsRead(Long id);
    
    /**
     * 标记所有通知为已读
     */
    void markAllAsRead();
    
    /**
     * 删除通知
     * @param id 通知ID
     */
    void deleteNotification(Long id);
    
    /**
     * 清空已读通知
     */
    void clearReadNotifications();
}
