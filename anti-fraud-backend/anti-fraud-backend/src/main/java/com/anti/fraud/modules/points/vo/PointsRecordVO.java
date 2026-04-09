package com.anti.fraud.modules.points.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "积分记录VO")
public class PointsRecordVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "积分变动")
    private Integer points;

    @Schema(description = "类型：1-获取 2-消耗")
    private Integer type;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "变动后余额")
    private Integer balanceAfter;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
