package com.anti.fraud.modules.notification.mapper;

import com.anti.fraud.modules.notification.entity.NotificationMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 通知消息Mapper
 */
@Mapper
public interface NotificationMessageMapper extends BaseMapper<NotificationMessage> {
    
    /**
     * 分页查询用户通知
     */
    IPage<NotificationMessage> selectUserNotifications(
            Page<NotificationMessage> page,
            @Param("userId") Long userId,
            @Param("type") Integer type,
            @Param("isRead") Integer isRead
    );
    
    /**
     * 统计用户未读通知数
     */
    Integer countUnread(@Param("userId") Long userId);
    
    /**
     * 标记所有通知为已读
     */
    @Update("UPDATE notification_message SET is_read = 1, read_time = NOW() WHERE receiver_id = #{userId} AND is_read = 0")
    int markAllAsRead(@Param("userId") Long userId);
}
