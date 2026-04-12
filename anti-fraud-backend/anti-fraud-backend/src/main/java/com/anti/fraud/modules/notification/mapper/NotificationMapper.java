package com.anti.fraud.modules.notification.mapper;

import com.anti.fraud.modules.notification.entity.Notification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 通知Mapper
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    
    /**
     * 分页查询用户通知
     * @param page 分页对象
     * @param userId 用户ID
     * @param type 通知类型
     * @return 通知列表
     */
    IPage<Notification> selectUserNotifications(IPage<Notification> page, 
                                              @Param("userId") Long userId, 
                                              @Param("type") String type);
    
    /**
     * 查询用户未读通知数量
     * @param userId 用户ID
     * @return 未读数量
     */
    Integer countUnreadNotifications(@Param("userId") Long userId);
    
    /**
     * 批量标记通知为已读
     * @param userId 用户ID
     * @param ids 通知ID列表
     * @return 影响行数
     */
    Integer markAsRead(@Param("userId") Long userId, @Param("ids") Long[] ids);
}
