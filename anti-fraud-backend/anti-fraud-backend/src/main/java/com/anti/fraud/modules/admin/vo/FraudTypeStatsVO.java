package com.anti.fraud.modules.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "诈骗类型统计")
public class FraudTypeStatsVO {
    @Schema(description = "诈骗类型")
    private String fraudType;

    @Schema(description = "类型名称")
    private String typeName;

    @Schema(description = "举报数量")
    private Integer reportCount;

    @Schema(description = "占比")
    private Double percentage;

    @Schema(description = "涉及金额")
    private Integer involvedAmount;

    @Schema(description = "典型案例数")
    private Integer caseCount;
}
