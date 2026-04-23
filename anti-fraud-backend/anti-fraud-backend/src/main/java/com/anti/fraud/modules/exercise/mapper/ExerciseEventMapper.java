package com.anti.fraud.modules.exercise.mapper;

import com.anti.fraud.modules.exercise.entity.ExerciseEvent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 演练事件Mapper
 */
@Mapper
public interface ExerciseEventMapper extends BaseMapper<ExerciseEvent> {

    /**
     * 根据事件ID查询事件
     * @param eventId 事件ID
     * @return 事件
     */
    ExerciseEvent selectByEventId(@Param("eventId") String eventId);

    /**
     * 根据演练ID查询事件列表
     * @param exerciseId 演练ID
     * @param eventType 事件类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 事件列表
     */
    List<ExerciseEvent> selectByExerciseId(@Param("exerciseId") String exerciseId, @Param("eventType") Integer eventType, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据用户ID查询事件列表
     * @param userId 用户ID
     * @param eventType 事件类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 事件列表
     */
    List<ExerciseEvent> selectByUserId(@Param("userId") Long userId, @Param("eventType") Integer eventType, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据事件类型查询事件列表
     * @param eventType 事件类型
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 事件列表
     */
    List<ExerciseEvent> selectByEventType(@Param("eventType") Integer eventType, @Param("status") Integer status, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据事件时间范围查询事件列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param eventType 事件类型
     * @param status 状态
     * @return 事件列表
     */
    List<ExerciseEvent> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("eventType") Integer eventType, @Param("status") Integer status);

    /**
     * 更新事件状态
     * @param id 事件ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 统计事件数量
     * @param eventType 事件类型
     * @param status 状态
     * @return 事件数量
     */
    Integer countByEventType(@Param("eventType") Integer eventType, @Param("status") Integer status);

    /**
     * 统计用户事件数量
     * @param userId 用户ID
     * @param eventType 事件类型
     * @param status 状态
     * @return 事件数量
     */
    Integer countByUserId(@Param("userId") Long userId, @Param("eventType") Integer eventType, @Param("status") Integer status);

    /**
     * 统计演练事件数量
     * @param exerciseId 演练ID
     * @param eventType 事件类型
     * @param status 状态
     * @return 事件数量
     */
    Integer countByExerciseId(@Param("exerciseId") String exerciseId, @Param("eventType") Integer eventType, @Param("status") Integer status);

    /**
     * 获取最近的事件
     * @param limit 数量限制
     * @param eventType 事件类型
     * @param status 状态
     * @return 事件列表
     */
    List<ExerciseEvent> selectRecentEvents(@Param("limit") Integer limit, @Param("eventType") Integer eventType, @Param("status") Integer status);

    /**
     * 批量插入事件
     * @param events 事件列表
     * @return 插入成功数量
     */
    int batchInsert(@Param("events") List<ExerciseEvent> events);
}
