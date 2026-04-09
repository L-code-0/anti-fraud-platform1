package com.anti.fraud.modules.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "任务详情")
public class TaskVO {
    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "任务类型")
    private String taskType;

    @Schema(description = "任务类型名称")
    private String taskTypeName;

    @Schema(description = "创建者ID")
    private Long creatorId;

    @Schema(description = "创建者名称")
    private String creatorName;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "总学生数")
    private Integer totalStudents;

    @Schema(description = "已完成学生数")
    private Integer completedStudents;

    @Schema(description = "完成率")
    private Double completionRate;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "关联资源ID")
    private Long relatedId;

    @Schema(description = "关联资源名称")
    private String relatedName;

    @Schema(description = "奖励积分")
    private Integer points;

    @Schema(description = "当前学生是否完成")
    private Boolean isCompleted;

    @Schema(description = "当前学生得分")
    private Integer myScore;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}