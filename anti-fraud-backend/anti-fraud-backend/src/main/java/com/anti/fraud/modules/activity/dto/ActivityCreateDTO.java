package com.anti.fraud.modules.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "活动创建DTO")
public class ActivityCreateDTO {

    @Schema(description = "活动名称")
    private String activityName;

    @Schema(description = "活动类型")
    private Integer activityType;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "活动描述")
    private String description;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "最大参与人数")
    private Integer maxParticipants;

    @Schema(description = "积分奖励")
    private Integer pointsReward;
}
