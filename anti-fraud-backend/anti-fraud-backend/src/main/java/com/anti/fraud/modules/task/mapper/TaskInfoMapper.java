package com.anti.fraud.modules.task.mapper;

import com.anti.fraud.modules.task.entity.TaskInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务信息Mapper
 */
@Mapper
public interface TaskInfoMapper extends BaseMapper<TaskInfo> {

    /**
     * 根据任务类型查询任务列表
     * @param taskType 任务类型
     * @param status 任务状态
     * @return 任务列表
     */
    List<TaskInfo> selectByTaskType(@Param("taskType") Integer taskType, @Param("status") Integer status);

    /**
     * 查询有效任务列表
     * @param currentTime 当前时间
     * @return 有效任务列表
     */
    List<TaskInfo> selectValidTasks(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询每日任务列表
     * @param currentTime 当前时间
     * @return 每日任务列表
     */
    List<TaskInfo> selectDailyTasks(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询成就任务列表
     * @param currentTime 当前时间
     * @return 成就任务列表
     */
    List<TaskInfo> selectAchievementTasks(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询赛季任务列表
     * @param seasonId 赛季ID
     * @param currentTime 当前时间
     * @return 赛季任务列表
     */
    List<TaskInfo> selectSeasonTasks(@Param("seasonId") Long seasonId, @Param("currentTime") LocalDateTime currentTime);

    /**
     * 根据ID查询任务详情
     * @param id 任务ID
     * @return 任务详情
     */
    TaskInfo selectById(@Param("id") Long id);

    /**
     * 批量更新任务状态
     * @param ids 任务ID列表
     * @param status 任务状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 批量删除任务
     * @param ids 任务ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);
}
