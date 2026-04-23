package com.anti.fraud.modules.task.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.notification.service.NotificationService;
import com.anti.fraud.modules.points.service.PointsService;
import com.anti.fraud.modules.task.entity.TaskInfo;
import com.anti.fraud.modules.task.entity.TaskCompletion;
import com.anti.fraud.modules.task.mapper.TaskInfoMapper;
import com.anti.fraud.modules.task.mapper.TaskCompletionMapper;
import com.anti.fraud.modules.task.service.TaskService;
import com.anti.fraud.modules.task.vo.TaskVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskInfoMapper taskInfoMapper;
    private final TaskCompletionMapper taskCompletionMapper;
    private final PointsService pointsService;
    private final NotificationService notificationService;

    @Override
    public List<TaskVO> getUserTasks(Integer taskType) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        LocalDateTime currentTime = LocalDateTime.now();
        List<TaskInfo> tasks;

        // 根据任务类型查询任务列表
        if (taskType == 1) { // 每日任务
            tasks = taskInfoMapper.selectDailyTasks(currentTime);
        } else if (taskType == 2) { // 成就任务
            tasks = taskInfoMapper.selectAchievementTasks(currentTime);
        } else if (taskType == 3) { // 赛季任务
            // 暂时使用默认赛季ID 1
            tasks = taskInfoMapper.selectSeasonTasks(1L, currentTime);
        } else {
            tasks = taskInfoMapper.selectValidTasks(currentTime);
        }

        List<TaskVO> taskVOs = new ArrayList<>();
        for (TaskInfo task : tasks) {
            TaskVO vo = convertToTaskVO(task);
            // 查询任务完成记录
            String taskCycle = getTaskCycle(task);
            TaskCompletion completion = taskCompletionMapper.selectByUserIdAndTaskId(userId, task.getId(), taskCycle);
            if (completion != null) {
                vo.setCurrentProgress(completion.getCurrentProgress());
                vo.setStatus(completion.getStatus());
                vo.setTaskCycle(completion.getTaskCycle());
                vo.setCompleteTime(completion.getCompleteTime());
                vo.setClaimTime(completion.getClaimTime());
                vo.setCanClaim(completion.getStatus() == 2); // 已完成但未领取
            } else {
                // 创建任务完成记录
                TaskCompletion newCompletion = new TaskCompletion();
                newCompletion.setUserId(userId);
                newCompletion.setTaskId(task.getId());
                newCompletion.setCurrentProgress(0);
                newCompletion.setStatus(1); // 进行中
                newCompletion.setTaskCycle(taskCycle);
                newCompletion.setCreateTime(LocalDateTime.now());
                newCompletion.setUpdateTime(LocalDateTime.now());
                taskCompletionMapper.insert(newCompletion);
                
                vo.setCurrentProgress(0);
                vo.setStatus(1);
                vo.setTaskCycle(taskCycle);
                vo.setCanClaim(false);
            }
            taskVOs.add(vo);
        }

        return taskVOs;
    }

    @Override
    public List<TaskInfo> getAllTasks(Integer taskType) {
        LocalDateTime currentTime = LocalDateTime.now();
        if (taskType != null) {
            return taskInfoMapper.selectByTaskType(taskType, 1);
        } else {
            return taskInfoMapper.selectValidTasks(currentTime);
        }
    }

    @Override
    @Transactional
    public boolean updateTaskProgress(Long taskId, int progress) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        TaskInfo task = taskInfoMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        String taskCycle = getTaskCycle(task);
        TaskCompletion completion = taskCompletionMapper.selectByUserIdAndTaskId(userId, taskId, taskCycle);
        if (completion == null) {
            // 创建任务完成记录
            completion = new TaskCompletion();
            completion.setUserId(userId);
            completion.setTaskId(taskId);
            completion.setCurrentProgress(progress);
            completion.setStatus(1); // 进行中
            completion.setTaskCycle(taskCycle);
            completion.setCreateTime(LocalDateTime.now());
            completion.setUpdateTime(LocalDateTime.now());
            taskCompletionMapper.insert(completion);
        } else {
            // 更新任务进度
            int newProgress = completion.getCurrentProgress() + progress;
            completion.setCurrentProgress(newProgress);
            completion.setUpdateTime(LocalDateTime.now());
            
            // 检查是否完成任务
            if (newProgress >= task.getTargetCount() && completion.getStatus() == 1) {
                completion.setStatus(2); // 已完成
                completion.setCompleteTime(LocalDateTime.now());
                
                // 发送任务完成通知
                try {
                    notificationService.sendSystemNotification(
                            "任务完成",
                            "恭喜您完成了「" + task.getTaskName() + "」任务！",
                            userId
                    );
                } catch (Exception e) {
                    log.error("发送任务完成通知失败: {}", e.getMessage(), e);
                }
            }
            
            taskCompletionMapper.updateById(completion);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean claimTaskReward(Long taskId, String taskCycle) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        TaskInfo task = taskInfoMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        TaskCompletion completion = taskCompletionMapper.selectByUserIdAndTaskId(userId, taskId, taskCycle);
        if (completion == null || completion.getStatus() != 2) {
            throw new BusinessException("任务未完成，无法领取奖励");
        }

        // 发放奖励
        if (task.getPointsReward() > 0) {
            pointsService.addPoints(userId, task.getPointsReward(), "task", taskId, "完成任务：" + task.getTaskName());
        }

        // 发放勋章
        if (task.getBadgeId() != null) {
            // 勋章授予逻辑在pointsService.addPoints中已处理
        }

        // 更新任务状态
        completion.setStatus(3); // 已领取奖励
        completion.setClaimTime(LocalDateTime.now());
        completion.setUpdateTime(LocalDateTime.now());
        taskCompletionMapper.updateById(completion);

        // 发送奖励领取通知
        try {
            notificationService.sendSystemNotification(
                    "奖励领取",
                    "您已成功领取「" + task.getTaskName() + "」任务的奖励！",
                    userId
            );
        } catch (Exception e) {
            log.error("发送奖励领取通知失败: {}", e.getMessage(), e);
        }

        return true;
    }

    @Override
    public void resetDailyTasks() {
        // 重置每日任务逻辑
        // 实际实现需要根据任务的resetHour字段来确定重置时间
        log.info("重置每日任务");
    }

    @Override
    public void checkTaskCompletion(Long userId, String action, int count) {
        // 根据用户操作检查任务完成情况
        LocalDateTime currentTime = LocalDateTime.now();
        List<TaskInfo> tasks = taskInfoMapper.selectValidTasks(currentTime);

        for (TaskInfo task : tasks) {
            // 根据任务目标和用户操作类型匹配任务
            if (matchTaskAction(task, action)) {
                String taskCycle = getTaskCycle(task);
                TaskCompletion completion = taskCompletionMapper.selectByUserIdAndTaskId(userId, task.getId(), taskCycle);
                if (completion != null && completion.getStatus() < 2) {
                    int newProgress = completion.getCurrentProgress() + count;
                    if (newProgress >= task.getTargetCount()) {
                        completion.setCurrentProgress(task.getTargetCount());
                        completion.setStatus(2); // 已完成
                        completion.setCompleteTime(LocalDateTime.now());
                        
                        // 发送任务完成通知
                        try {
                            notificationService.sendSystemNotification(
                                    "任务完成",
                                    "恭喜您完成了「" + task.getTaskName() + "」任务！",
                                    userId
                            );
                        } catch (Exception e) {
                            log.error("发送任务完成通知失败: {}", e.getMessage(), e);
                        }
                    } else {
                        completion.setCurrentProgress(newProgress);
                    }
                    completion.setUpdateTime(LocalDateTime.now());
                    taskCompletionMapper.updateById(completion);
                }
            }
        }
    }

    @Override
    public int getUnclaimedTaskCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        return taskCompletionMapper.countUnclaimedTasks(userId);
    }

    @Override
    public boolean createTask(TaskInfo taskInfo) {
        try {
            taskInfo.setStatus(1);
            taskInfo.setCreateTime(LocalDateTime.now());
            taskInfo.setUpdateTime(LocalDateTime.now());
            taskInfoMapper.insert(taskInfo);
            return true;
        } catch (Exception e) {
            log.error("创建任务失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateTask(TaskInfo taskInfo) {
        try {
            taskInfo.setUpdateTime(LocalDateTime.now());
            taskInfoMapper.updateById(taskInfo);
            return true;
        } catch (Exception e) {
            log.error("更新任务失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteTask(Long taskId) {
        try {
            taskInfoMapper.deleteById(taskId);
            return true;
        } catch (Exception e) {
            log.error("删除任务失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public TaskInfo getTaskById(Long taskId) {
        return taskInfoMapper.selectById(taskId);
    }

    @Override
    public int batchCreateTasks(List<TaskInfo> tasks) {
        try {
            int count = 0;
            for (TaskInfo task : tasks) {
                task.setStatus(1);
                task.setCreateTime(LocalDateTime.now());
                task.setUpdateTime(LocalDateTime.now());
                taskInfoMapper.insert(task);
                count++;
            }
            return count;
        } catch (Exception e) {
            log.error("批量创建任务失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 转换任务信息为VO
     * @param task 任务信息
     * @return 任务VO
     */
    private TaskVO convertToTaskVO(TaskInfo task) {
        TaskVO vo = new TaskVO();
        vo.setId(task.getId());
        vo.setTaskName(task.getTaskName());
        vo.setTaskType(task.getTaskType());
        vo.setDescription(task.getDescription());
        vo.setTarget(task.getTarget());
        vo.setTargetCount(task.getTargetCount());
        vo.setPointsReward(task.getPointsReward());
        vo.setBadgeId(task.getBadgeId());
        vo.setStartTime(task.getStartTime());
        vo.setEndTime(task.getEndTime());
        return vo;
    }

    /**
     * 获取任务周期
     * @param task 任务信息
     * @return 任务周期
     */
    private String getTaskCycle(TaskInfo task) {
        if (task.getTaskType() == 1) { // 每日任务
            return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else if (task.getTaskType() == 3) { // 赛季任务
            return String.valueOf(task.getSeasonId());
        } else { // 成就任务
            return "achievement";
        }
    }

    /**
     * 匹配任务操作
     * @param task 任务信息
     * @param action 操作类型
     * @return 是否匹配
     */
    private boolean matchTaskAction(TaskInfo task, String action) {
        // 根据任务目标和操作类型匹配
        String target = task.getTarget().toLowerCase();
        switch (action) {
            case "test_completed":
                return target.contains("测验") || target.contains("考试");
            case "simulation_completed":
                return target.contains("演练") || target.contains("模拟");
            case "checkin":
                return target.contains("签到");
            case "post_created":
                return target.contains("发帖") || target.contains("社区");
            case "learning_completed":
                return target.contains("学习") || target.contains("课程");
            default:
                return false;
        }
    }
}
