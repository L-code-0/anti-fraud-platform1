package com.anti.fraud.modules.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "举报处理DTO")
public class ReportHandleDTO {

    @Schema(description = "风险等级：1-低 2-中 3-高")
    private Integer riskLevel;

    @Schema(description = "处理结果")
    private String handleResult;

    @Schema(description = "奖励积分")
    private Integer rewardPoints;
}
