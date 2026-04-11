package com.anti.fraud.modules.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 班级任务实体
 */
@Data
@TableName("class_task")
public class ClassTask {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskName;
    private String taskType; // VIDEO-视频学习, TEST-测试, KNOWLEDGE-知识学习
    private Long creatorId;
    private String creatorName;
    private Long classId;
    private String className;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalStudents;
    private Integer completedStudents;
    private Integer status; // 0-未开始, 1-进行中, 2-已结束
    private String description;
    private Long relatedId; // 关联的视频/测试/知识ID
    private String relatedName;
    private Integer points;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 提醒相关字段
    private Integer remindType; // 提醒类型：0-不提醒, 1-开始前提醒, 2-结束前提醒, 3-两者都提醒
    private Integer remindDays; // 提醒天数
    private LocalDateTime remindTime; // 提醒时间
    private Integer remindStatus; // 提醒状态：0-未提醒, 1-已提醒
    
    // 自动分配相关字段
    private Integer autoAssign; // 是否自动分配：0-手动, 1-自动
    private Integer assignStrategy; // 分配策略：1-随机分配, 2-按成绩分配, 3-按学习时长分配

    @TableLogic
    private Integer deleted;
}
