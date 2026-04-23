package com.anti.fraud.modules.task.service;

import com.anti.fraud.modules.task.entity.TaskInfo;
import com.anti.fraud.modules.task.entity.TaskCompletion;
import com.anti.fraud.modules.task.vo.TaskVO;

import java.util.List;

/**
 * 任务服务接口
 */
public interface TaskService {

    /**
     * 获取用户的任务列表
     * @param taskType 任务类型：1-每日任务，2-成就任务，3-赛季任务
     * @return 任务列表
     */
    List<TaskVO> getUserTasks(Integer taskType);

    /**
     * 获取所有任务列表
     * @param taskType 任务类型
     * @return 任务列表
     */
    List<TaskInfo> getAllTasks(Integer taskType);

    /**
     * 更新任务进度
     * @param taskId 任务ID
     * @param progress 进度增量
     * @return 是否成功
     */
    boolean updateTaskProgress(Long taskId, int progress);

    /**
     * 领取任务奖励
     * @param taskId 任务ID
     * @param taskCycle 任务周期
     * @return 是否成功
     */
    boolean claimTaskReward(Long taskId, String taskCycle);

    /**
     * 重置每日任务
     */
    void resetDailyTasks();

    /**
     * 检查任务完成情况
     * @param userId 用户ID
     * @param action 操作类型（如：test_completed, simulation_completed等）
     * @param count 操作数量
     */
    void checkTaskCompletion(Long userId, String action, int count);

    /**
     * 获取用户未领取奖励的任务数量
     * @return 未领取奖励的任务数量
     */
    int getUnclaimedTaskCount();

    /**
     * 创建任务
     * @param taskInfo 任务信息
     * @return 是否成功
     */
    boolean createTask(TaskInfo taskInfo);

    /**
     * 更新任务
     * @param taskInfo 任务信息
     * @return 是否成功
     */
    boolean updateTask(TaskInfo taskInfo);

    /**
     * 删除任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean deleteTask(Long taskId);

    /**
     * 获取任务详情
     * @param taskId 任务ID
     * @return 任务详情
     */
    TaskInfo getTaskById(Long taskId);

    /**
     * 批量创建任务
     * @param tasks 任务列表
     * @return 成功创建的数量
     */
    int batchCreateTasks(List<TaskInfo> tasks);
}
