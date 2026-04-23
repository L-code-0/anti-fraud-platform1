package com.anti.fraud.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务完成记录实体类
 */
@Data
@TableName("task_completion")
public class TaskCompletion {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 当前进度
     */
    private Integer currentProgress;

    /**
     * 任务状态：1-进行中，2-已完成，3-已领取奖励
     */
    private Integer status;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 领取时间
     */
    private LocalDateTime claimTime;

    /**
     * 任务周期（如：2024-01-01表示每日任务的日期）
     */
    private String taskCycle;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
