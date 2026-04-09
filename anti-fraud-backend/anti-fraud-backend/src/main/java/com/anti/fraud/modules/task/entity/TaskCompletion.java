package com.anti.fraud.modules.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学生任务完成记录
 */
@Data
@TableName("task_completion")
public class TaskCompletion {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;
    private Long studentId;
    private String studentName;
    private Integer status; // 0-未完成, 1-已完成
    private Integer score; // 测试得分（如果是测试任务）
    private LocalDateTime completeTime;
    private LocalDateTime createTime;
}
