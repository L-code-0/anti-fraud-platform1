package com.anti.fraud.modules.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.anti.fraud.modules.activity.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
    
    /**
     * 分页查询活动列表
     * @param userId 用户ID
     * @param status 状态
     * @param activityType 活动类型
     * @param keyword 关键词
     * @return 活动列表
     */
    List<Map<String, Object>> selectActivityPage(
            @Param("userId") Long userId,
            @Param("status") Integer status,
            @Param("activityType") Integer activityType,
            @Param("keyword") String keyword);
    
    /**
     * 查询活动详情
     * @param id 活动ID
     * @param userId 用户ID
     * @return 活动详情
     */
    Map<String, Object> selectActivityDetail(
            @Param("id") Long id,
            @Param("userId") Long userId);
    
    /**
     * 查询活动参与者列表
     * @param activityId 活动ID
     * @return 参与者列表
     */
    List<Map<String, Object>> selectActivityParticipants(@Param("activityId") Long activityId);
    
    /**
     * 更新活动参与人数
     * @param activityId 活动ID
     */
    void updateParticipantCount(@Param("activityId") Long activityId);
    
    /**
     * 查询即将开始的活动
     * @param limit 限制数量
     * @return 即将开始的活动列表
     */
    List<Map<String, Object>> selectUpcomingActivities(@Param("limit") Integer limit);
    
}
