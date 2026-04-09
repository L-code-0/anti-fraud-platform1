package com.anti.fraud.modules.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anti.fraud.modules.task.dto.TaskCreateDTO;
import com.anti.fraud.modules.task.vo.TaskVO;

public interface TaskService {
    /**
     * 创建任务（教师）
     */
    Long createTask(TaskCreateDTO dto);

    /**
     * 获取我发布的任务列表（教师）
     */
    Page<TaskVO> getMyTasks(Integer page, Integer size);

    /**
     * 获取学生的任务列表
     */
    Page<TaskVO> getStudentTasks(Integer page, Integer size);

    /**
     * 获取任务详情
     */
    TaskVO getTaskDetail(Long id);

    /**
     * 完成任务（学生）
     */
    void completeTask(Long taskId, Integer score);

    /**
     * 获取任务完成情况
     */
    Page<?> getTaskCompletions(Long taskId, Integer page, Integer size);

    /**
     * 删除任务
     */
    void deleteTask(Long id);
}
