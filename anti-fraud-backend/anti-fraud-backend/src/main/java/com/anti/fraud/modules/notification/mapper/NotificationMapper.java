package com.anti.fraud.modules.notification.mapper;

import com.anti.fraud.modules.notification.entity.Notification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 通知Mapper
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 根据用户ID查询通知
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 通知列表
     */
    List<Notification> selectByUserId(@Param("userId") Long userId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据用户ID查询未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    Integer selectUnreadCountByUserId(@Param("userId") Long userId);

    /**
     * 根据通知类型查询通知
     * @param type 通知类型
     * @param page 页码
     * @param size 每页大小
     * @return 通知列表
     */
    List<Notification> selectByType(@Param("type") Integer type, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据通知方式查询通知
     * @param method 通知方式
     * @param page 页码
     * @param size 每页大小
     * @return 通知列表
     */
    List<Notification> selectByMethod(@Param("method") Integer method, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据发送状态查询通知
     * @param status 发送状态
     * @param page 页码
     * @param size 每页大小
     * @return 通知列表
     */
    List<Notification> selectByStatus(@Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 按通知类型统计通知数量
     * @return 类型统计
     */
    List<Map<String, Object>> selectTypeStats();

    /**
     * 按通知方式统计通知数量
     * @return 方式统计
     */
    List<Map<String, Object>> selectMethodStats();

    /**
     * 按发送状态统计通知数量
     * @return 状态统计
     */
    List<Map<String, Object>> selectStatusStats();

    /**
     * 标记通知为已读
     * @param id 通知ID
     */
    void markAsRead(@Param("id") Long id);

    /**
     * 批量标记通知为已读
     * @param ids 通知ID列表
     */
    void batchMarkAsRead(@Param("ids") List<Long> ids);
}
