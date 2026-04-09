package com.anti.fraud.modules.points.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "积分统计VO")
public class PointsStatsVO {

    @Schema(description = "当前积分")
    private Integer currentPoints;

    @Schema(description = "当前等级")
    private Integer currentLevel;

    @Schema(description = "下一等级所需积分")
    private Integer nextLevelPoints;

    @Schema(description = "排名")
    private Integer ranking;

    @Schema(description = "本周获得积分")
    private Integer weeklyPoints;

    @Schema(description = "本月获得积分")
    private Integer monthlyPoints;

    @Schema(description = "总获得积分")
    private Integer totalPoints;
}

