package com.anti.fraud.modules.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.modules.task.dto.TaskCreateDTO;
import com.anti.fraud.modules.task.entity.ClassTask;
import com.anti.fraud.modules.task.entity.TaskCompletion;
import com.anti.fraud.modules.task.mapper.ClassTaskMapper;
import com.anti.fraud.modules.task.mapper.TaskCompletionMapper;
import com.anti.fraud.modules.task.service.TaskService;
import com.anti.fraud.modules.task.vo.TaskVO;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.anti.fraud.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final ClassTaskMapper taskMapper;
    private final TaskCompletionMapper completionMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Long createTask(TaskCreateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);

        ClassTask task = new ClassTask();
        task.setTaskName(dto.getTaskName());
        task.setTaskType(dto.getTaskType());
        task.setCreatorId(userId);
        task.setCreatorName(user.getRealName());
        task.setClassId(dto.getClassId());
        task.setClassName(dto.getClassName());
        task.setStartTime(dto.getStartTime());
        task.setEndTime(dto.getEndTime());
        task.setDescription(dto.getDescription());
        task.setRelatedId(dto.getRelatedId());
        task.setRelatedName(dto.getRelatedName());
        task.setPoints(dto.getPoints() != null ? dto.getPoints() : 10);
        task.setStatus(0);
        task.setTotalStudents(0);
        task.setCompletedStudents(0);
        task.setCreateTime(LocalDateTime.now());

        // 判断任务状态
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(dto.getEndTime())) {
            task.setStatus(2);
        } else if (now.isAfter(dto.getStartTime())) {
            task.setStatus(1);
        }

        taskMapper.insert(task);
        return task.getId();
    }

    @Override
    public Page<TaskVO> getMyTasks(Integer page, Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();

        Page<ClassTask> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ClassTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassTask::getCreatorId, userId)
                .orderByDesc(ClassTask::getCreateTime);

        Page<ClassTask> result = taskMapper.selectPage(pageParam, wrapper);

        Page<TaskVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public Page<TaskVO> getStudentTasks(Integer page, Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();

        Page<ClassTask> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ClassTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassTask::getStatus, 1)
                .orderByDesc(ClassTask::getCreateTime);

        Page<ClassTask> result = taskMapper.selectPage(pageParam, wrapper);

        // 查询学生完成情况
        List<Long> taskIds = result.getRecords().stream()
                .map(ClassTask::getId)
                .collect(Collectors.toList());

        List<TaskCompletion> completions = List.of();
        if (!taskIds.isEmpty()) {
            completions = completionMapper.selectList(
                    new LambdaQueryWrapper<TaskCompletion>()
                            .eq(TaskCompletion::getStudentId, userId)
                            .in(TaskCompletion::getTaskId, taskIds)
            );
        }

        final List<TaskCompletion> finalCompletions = completions;

        Page<TaskVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(task -> {
                    TaskVO vo = convertToVO(task);
                    finalCompletions.stream()
                            .filter(c -> c.getTaskId().equals(task.getId()))
                            .findFirst()
                            .ifPresent(c -> {
                                vo.setIsCompleted(c.getStatus() == 1);
                                vo.setMyScore(c.getScore());
                            });
                    return vo;
                })
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public TaskVO getTaskDetail(Long id) {
        ClassTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        return convertToVO(task);
    }

    @Override
    @Transactional
    public void completeTask(Long taskId, Integer score) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);

        ClassTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        if (task.getStatus() != 1) {
            throw new BusinessException("任务未开始或已结束");
        }

        // 检查是否已完成
        TaskCompletion existing = completionMapper.selectOne(
                new LambdaQueryWrapper<TaskCompletion>()
                        .eq(TaskCompletion::getTaskId, taskId)
                        .eq(TaskCompletion::getStudentId, userId)
        );

        if (existing != null && existing.getStatus() == 1) {
            throw new BusinessException("任务已完成");
        }

        TaskCompletion completion = existing != null ? existing : new TaskCompletion();
        completion.setTaskId(taskId);
        completion.setStudentId(userId);
        completion.setStudentName(user.getRealName());
        completion.setStatus(1);
        completion.setScore(score);
        completion.setCompleteTime(LocalDateTime.now());
        completion.setCreateTime(LocalDateTime.now());

        if (existing != null) {
            completionMapper.updateById(completion);
        } else {
            completionMapper.insert(completion);

            // 更新任务完成人数
            task.setCompletedStudents(task.getCompletedStudents() + 1);
            taskMapper.updateById(task);
        }
    }

    @Override
    public Page<?> getTaskCompletions(Long taskId, Integer page, Integer size) {
        Page<TaskCompletion> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<TaskCompletion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskCompletion::getTaskId, taskId)
                .orderByDesc(TaskCompletion::getCompleteTime);

        return completionMapper.selectPage(pageParam, wrapper);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        ClassTask task = taskMapper.selectById(id);

        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        if (!task.getCreatorId().equals(userId)) {
            throw new BusinessException("无权删除此任务");
        }

        taskMapper.deleteById(id);
    }

    private TaskVO convertToVO(ClassTask task) {
        TaskVO vo = new TaskVO();
        vo.setId(task.getId());
        vo.setTaskName(task.getTaskName());
        vo.setTaskType(task.getTaskType());
        vo.setTaskTypeName(getTaskTypeName(task.getTaskType()));
        vo.setCreatorId(task.getCreatorId());
        vo.setCreatorName(task.getCreatorName());
        vo.setClassId(task.getClassId());
        vo.setClassName(task.getClassName());
        vo.setStartTime(task.getStartTime());
        vo.setEndTime(task.getEndTime());
        vo.setTotalStudents(task.getTotalStudents());
        vo.setCompletedStudents(task.getCompletedStudents());

        if (task.getTotalStudents() > 0) {
            vo.setCompletionRate((double) task.getCompletedStudents() / task.getTotalStudents() * 100);
        } else {
            vo.setCompletionRate(0.0);
        }

        vo.setStatus(task.getStatus());
        vo.setStatusName(getStatusName(task.getStatus()));
        vo.setDescription(task.getDescription());
        vo.setRelatedId(task.getRelatedId());
        vo.setRelatedName(task.getRelatedName());
        vo.setPoints(task.getPoints());
        vo.setCreateTime(task.getCreateTime());
        vo.setIsCompleted(false);

        return vo;
    }

    private String getTaskTypeName(String taskType) {
        switch (taskType) {
            case "VIDEO": return "视频学习";
            case "TEST": return "在线测试";
            case "KNOWLEDGE": return "知识学习";
            default: return taskType;
        }
    }

    private String getStatusName(Integer status) {
        switch (status) {
            case 0: return "未开始";
            case 1: return "进行中";
            case 2: return "已结束";
            default: return "未知";
        }
    }

    @Override
    @Transactional
    public void setTaskReminder(Long taskId, Integer remindType, Integer remindDays) {
        ClassTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        task.setRemindType(remindType);
        task.setRemindDays(remindDays);
        task.setRemindStatus(0);

        // 计算提醒时间
        if (remindType == 1 || remindType == 3) {
            // 开始前提醒
            task.setRemindTime(task.getStartTime().minusDays(remindDays));
        } else if (remindType == 2) {
            // 结束前提醒
            task.setRemindTime(task.getEndTime().minusDays(remindDays));
        }

        taskMapper.updateById(task);
    }

    @Override
    @Transactional
    public void triggerTaskReminders() {
        List<ClassTask> tasks = getTasksToRemind();
        LocalDateTime now = LocalDateTime.now();

        for (ClassTask task : tasks) {
            // 检查提醒时间是否已到
            if (task.getRemindTime() != null && now.isAfter(task.getRemindTime())) {
                // 这里可以添加发送提醒的逻辑，如发送邮件、短信或系统通知
                System.out.println("发送任务提醒: " + task.getTaskName());

                // 更新提醒状态
                task.setRemindStatus(1);
                taskMapper.updateById(task);
            }
        }
    }

    @Override
    @Transactional
    public void autoAssignTask(Long taskId, Integer strategy) {
        ClassTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        // 这里简化实现，实际应该根据策略自动分配任务给学生
        // 1-随机分配, 2-按成绩分配, 3-按学习时长分配
        task.setAutoAssign(1);
        task.setAssignStrategy(strategy);
        taskMapper.updateById(task);

        // 这里可以添加具体的分配逻辑
        System.out.println("自动分配任务: " + task.getTaskName() + "，策略: " + strategy);
    }

    @Override
    public List<ClassTask> getTasksToRemind() {
        LocalDateTime now = LocalDateTime.now();
        return taskMapper.selectList(
                new LambdaQueryWrapper<ClassTask>()
                        .ne(ClassTask::getRemindType, 0)
                        .eq(ClassTask::getRemindStatus, 0)
                        .le(ClassTask::getRemindTime, now)
        );
    }
}