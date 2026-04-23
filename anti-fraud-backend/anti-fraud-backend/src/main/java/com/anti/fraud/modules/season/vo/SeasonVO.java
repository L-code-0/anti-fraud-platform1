package com.anti.fraud.modules.season.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 赛季VO
 */
@Data
@Schema(description = "赛季VO")
public class SeasonVO {

    @Schema(description = "赛季ID")
    private Long id;

    @Schema(description = "赛季名称")
    private String seasonName;

    @Schema(description = "赛季描述")
    private String description;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "赛季状态：1-未开始，2-进行中，3-已结束")
    private Integer status;

    @Schema(description = "赛季主题")
    private String theme;

    @Schema(description = "赛季皮肤ID")
    private Long skinId;

    @Schema(description = "赛季称号ID")
    private Long titleId;

    @Schema(description = "用户赛季积分")
    private Integer seasonPoints;

    @Schema(description = "用户赛季排名")
    private Integer seasonRank;

    @Schema(description = "用户完成任务数")
    private Integer completedTasks;

    @Schema(description = "用户获得勋章数")
    private Integer acquiredBadges;

    @Schema(description = "用户赛季皮肤ID")
    private Long userSkinId;

    @Schema(description = "用户赛季称号ID")
    private Long userTitleId;

    @Schema(description = "用户赛季状态：1-进行中，2-已结算")
    private Integer userStatus;

    @Schema(description = "剩余时间（天）")
    private Integer remainingDays;

    @Schema(description = "赛季排行榜")
    private java.util.List<java.util.Map<String, Object>> ranking;
}
