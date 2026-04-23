package com.anti.fraud.modules.task.mapper;

import com.anti.fraud.modules.task.entity.TaskCompletion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务完成记录Mapper
 */
@Mapper
public interface TaskCompletionMapper extends BaseMapper<TaskCompletion> {

    /**
     * 根据用户ID和任务ID查询任务完成记录
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param taskCycle 任务周期
     * @return 任务完成记录
     */
    TaskCompletion selectByUserIdAndTaskId(@Param("userId") Long userId, @Param("taskId") Long taskId, @Param("taskCycle") String taskCycle);

    /**
     * 根据用户ID查询任务完成记录列表
     * @param userId 用户ID
     * @param status 任务状态
     * @return 任务完成记录列表
     */
    List<TaskCompletion> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 根据用户ID和任务类型查询任务完成记录列表
     * @param userId 用户ID
     * @param taskType 任务类型
     * @return 任务完成记录列表
     */
    List<TaskCompletion> selectByUserIdAndTaskType(@Param("userId") Long userId, @Param("taskType") Integer taskType);

    /**
     * 更新任务进度
     * @param id 记录ID
     * @param currentProgress 当前进度
     * @return 影响行数
     */
    int updateProgress(@Param("id") Long id, @Param("currentProgress") Integer currentProgress);

    /**
     * 更新任务状态
     * @param id 记录ID
     * @param status 任务状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 批量更新任务状态
     * @param ids 记录ID列表
     * @param status 任务状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 统计用户已完成的任务数量
     * @param userId 用户ID
     * @param taskType 任务类型
     * @return 已完成任务数量
     */
    Integer countCompletedTasks(@Param("userId") Long userId, @Param("taskType") Integer taskType);

    /**
     * 统计用户未领取奖励的任务数量
     * @param userId 用户ID
     * @return 未领取奖励任务数量
     */
    Integer countUnclaimedTasks(@Param("userId") Long userId);
}
