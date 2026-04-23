package com.anti.fraud.modules.notification.service;

import com.anti.fraud.modules.notification.entity.Notification;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 通知服务接口
 */
public interface NotificationService extends IService<Notification> {

    /**
     * 发送通知
     * @param notification 通知信息
     * @return 是否成功
     */
    boolean sendNotification(Notification notification);

    /**
     * 批量发送通知
     * @param notifications 通知列表
     * @return 发送结果
     */
    List<Notification> batchSendNotifications(List<Notification> notifications);

    /**
     * 发送短信通知
     * @param userId 用户ID
     * @param username 用户名
     * @param content 通知内容
     * @return 是否成功
     */
    boolean sendSmsNotification(Long userId, String username, String content);

    /**
     * 发送邮件通知
     * @param userId 用户ID
     * @param username 用户名
     * @param email 邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否成功
     */
    boolean sendEmailNotification(Long userId, String username, String email, String subject, String content);

    /**
     * 更新通知状态
     * @param id 通知ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateNotificationStatus(Long id, Integer status);

    /**
     * 标记通知为已读
     * @param id 通知ID
     * @return 是否成功
     */
    boolean markAsRead(Long id);

    /**
     * 批量标记通知为已读
     * @param ids 通知ID列表
     * @return 是否成功
     */
    boolean batchMarkAsRead(List<Long> ids);

    /**
     * 获取通知详情
     * @param id 通知ID
     * @return 通知详情
     */
    Notification getNotificationById(Long id);

    /**
     * 根据用户ID查询通知
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 通知列表和总数
     */
    Map<String, Object> getNotificationsByUserId(Long userId, int page, int size);

    /**
     * 根据用户ID查询未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    Integer getUnreadCountByUserId(Long userId);

    /**
     * 根据通知类型查询通知
     * @param type 通知类型
     * @param page 页码
     * @param size 每页大小
     * @return 通知列表和总数
     */
    Map<String, Object> getNotificationsByType(Integer type, int page, int size);

    /**
     * 根据通知方式查询通知
     * @param method 通知方式
     * @param page 页码
     * @param size 每页大小
     * @return 通知列表和总数
     */
    Map<String, Object> getNotificationsByMethod(Integer method, int page, int size);

    /**
     * 根据发送状态查询通知
     * @param status 发送状态
     * @param page 页码
     * @param size 每页大小
     * @return 通知列表和总数
     */
    Map<String, Object> getNotificationsByStatus(Integer status, int page, int size);

    /**
     * 统计通知信息
     * @return 统计信息
     */
    Map<String, Object> getNotificationStats();

    /**
     * 清理过期通知
     * @param days 天数
     * @return 清理数量
     */
    Integer cleanExpiredNotifications(int days);
}
