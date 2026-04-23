package com.anti.fraud.modules.points.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "勋章VO")
public class BadgeVO {

    @Schema(description = "勋章ID")
    private Long id;

    @Schema(description = "勋章名称")
    private String badgeName;

    @Schema(description = "勋章类型")
    private Integer badgeType;

    @Schema(description = "勋章图标")
    private String badgeIcon;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "奖励积分")
    private Integer pointsReward;

    @Schema(description = "是否已获得")
    private Boolean isAcquired;

    @Schema(description = "获得时间")
    private LocalDateTime acquireTime;

    @Schema(description = "获得原因")
    private String acquireReason;
}
