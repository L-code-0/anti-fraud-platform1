package com.anti.fraud.modules.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Schema(description = "活动创建DTO")
public class ActivityCreateDTO {

    @NotBlank(message = "活动名称不能为空")
    @Size(max = 100, message = "活动名称长度不能超过100")
    @Schema(description = "活动名称")
    private String activityName;

    @NotNull(message = "活动类型不能为空")
    @Min(value = 1, message = "活动类型不正确")
    @Max(value = 5, message = "活动类型不正确")
    @Schema(description = "活动类型")
    private Integer activityType;

    @Pattern(regexp = "^$|^https?://.*", message = "封面图URL格式不正确")
    @Schema(description = "封面图")
    private String coverImage;

    @Size(max = 2000, message = "活动描述长度不能超过2000")
    @Schema(description = "活动描述")
    private String description;

    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须是未来时间")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Min(value = 1, message = "最大参与人数至少为1")
    @Max(value = 10000, message = "最大参与人数不能超过10000")
    @Schema(description = "最大参与人数")
    private Integer maxParticipants;

    @Min(value = 0, message = "积分奖励不能为负数")
    @Max(value = 1000, message = "积分奖励不能超过1000")
    @Schema(description = "积分奖励")
    private Integer pointsReward;
}
