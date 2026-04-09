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

    @TableLogic
    private Integer deleted;
}
