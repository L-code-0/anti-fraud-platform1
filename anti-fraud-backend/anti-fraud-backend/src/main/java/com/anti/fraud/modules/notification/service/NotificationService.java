package com.anti.fraud.modules.notification.service;

import com.anti.fraud.modules.notification.entity.Notification;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

/**
 * 通知服务
 */
public interface NotificationService {
    
    /**
     * 发送通知给指定用户
     * @param userId 用户ID
     * @param type 通知类型
     * @param title 通知标题
     * @param content 通知内容
     * @param icon 通知图标
     * @param action 操作链接
     * @param actionText 操作文本
     * @param extra 额外信息
     * @return 通知实体
     */
    Notification sendNotification(Long userId, String type, String title, 
                                 String content, String icon, String action, 
                                 String actionText, String extra);
    
    /**
     * 批量发送通知
     * @param userIds 用户ID列表
     * @param type 通知类型
     * @param title 通知标题
     * @param content 通知内容
     * @param icon 通知图标
     * @param action 操作链接
     * @param actionText 操作文本
     * @param extra 额外信息
     * @return 发送成功数量
     */
    int sendBatchNotification(Long[] userIds, String type, String title, 
                             String content, String icon, String action, 
                             String actionText, String extra);
    
    /**
     * 分页获取用户通知
     * @param page 页码
     * @param size 每页大小
     * @param userId 用户ID
     * @param type 通知类型
     * @return 通知列表
     */
    IPage<Notification> getUserNotifications(int page, int size, Long userId, String type);
    
    /**
     * 获取用户未读通知数量
     * @param userId 用户ID
     * @return 未读数量
     */
    int getUnreadCount(Long userId);
    
    /**
     * 标记通知为已读
     * @param userId 用户ID
     * @param notificationId 通知ID
     * @return 是否成功
     */
    boolean markAsRead(Long userId, Long notificationId);
    
    /**
     * 批量标记通知为已读
     * @param userId 用户ID
     * @param notificationIds 通知ID列表
     * @return 成功数量
     */
    int markBatchAsRead(Long userId, Long[] notificationIds);
    
    /**
     * 删除通知
     * @param userId 用户ID
     * @param notificationId 通知ID
     * @return 是否成功
     */
    boolean deleteNotification(Long userId, Long notificationId);
    
    /**
     * 批量删除通知
     * @param userId 用户ID
     * @param notificationIds 通知ID列表
     * @return 成功数量
     */
    int deleteBatchNotifications(Long userId, Long[] notificationIds);
    
    /**
     * 获取用户通知统计
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Integer> getNotificationStats(Long userId);
}
