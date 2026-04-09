package com.anti.fraud.modules.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "创建任务请求")
public class TaskCreateDTO {
    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "任务类型: VIDEO-视频学习, TEST-测试, KNOWLEDGE-知识学习")
    private String taskType;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "关联资源ID")
    private Long relatedId;

    @Schema(description = "关联资源名称")
    private String relatedName;

    @Schema(description = "奖励积分")
    private Integer points;
}
