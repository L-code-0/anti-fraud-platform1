package com.anti.fraud.modules.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务VO
 */
@Data
@Schema(description = "任务VO")
public class TaskVO {

    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "任务类型：1-每日任务，2-成就任务，3-赛季任务")
    private Integer taskType;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "任务目标")
    private String target;

    @Schema(description = "目标数量")
    private Integer targetCount;

    @Schema(description = "当前进度")
    private Integer currentProgress;

    @Schema(description = "奖励积分")
    private Integer pointsReward;

    @Schema(description = "奖励勋章ID")
    private Long badgeId;

    @Schema(description = "任务状态：1-进行中，2-已完成，3-已领取奖励")
    private Integer status;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "任务周期")
    private String taskCycle;

    @Schema(description = "是否可领取奖励")
    private Boolean canClaim;

    @Schema(description = "完成时间")
    private LocalDateTime completeTime;

    @Schema(description = "领取时间")
    private LocalDateTime claimTime;
}
