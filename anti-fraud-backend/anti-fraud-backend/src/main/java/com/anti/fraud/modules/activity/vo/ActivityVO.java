package com.anti.fraud.modules.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "活动VO")
public class ActivityVO {

    @Schema(description = "活动ID")
    private Long id;

    @Schema(description = "活动名称")
    private String activityName;

    @Schema(description = "活动类型：1-征文比赛 2-短视频 3-知识竞赛 4-讲座")
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

    @Schema(description = "当前参与人数")
    private Integer currentParticipants;

    @Schema(description = "积分奖励")
    private Integer pointsReward;

    @Schema(description = "状态：0-草稿 1-报名中 2-进行中 3-已结束")
    private Integer status;

    @Schema(description = "是否已报名")
    private Boolean isRegistered;

    @Schema(description = "报名时间")
    private LocalDateTime registerTime;
}

