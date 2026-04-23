package com.anti.fraud.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务信息实体类
 */
@Data
@TableName("task_info")
public class TaskInfo {

    /**
     * 任务ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型：1-每日任务，2-成就任务，3-赛季任务
     */
    private Integer taskType;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务目标（如：完成5次测验）
     */
    private String target;

    /**
     * 目标数量
     */
    private Integer targetCount;

    /**
     * 奖励积分
     */
    private Integer pointsReward;

    /**
     * 奖励勋章ID
     */
    private Long badgeId;

    /**
     * 任务状态：1-有效，2-无效
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 每日任务重置时间（小时）
     */
    private Integer resetHour;

    /**
     * 赛季ID
     */
    private Long seasonId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
