package com.anti.fraud.modules.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "举报信息VO")
public class ReportVO {

    @Schema(description = "举报ID")
    private Long id;

    @Schema(description = "举报编号")
    private String reportNo;

    @Schema(description = "举报类型")
    private Integer reportType;

    @Schema(description = "诈骗类型")
    private String fraudType;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态：0-待处理 1-处理中 2-已处理")
    private Integer status;

    @Schema(description = "风险等级")
    private Integer riskLevel;

    @Schema(description = "处理结果")
    private String handleResult;

    @Schema(description = "奖励积分")
    private Integer rewardPoints;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;
}
