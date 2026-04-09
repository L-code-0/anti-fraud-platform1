package com.anti.fraud.modules.warning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 预警统计 VO
 */
@Data
@Schema(description = "预警统计")
public class WarningStatsVO {

    @Schema(description = "高风险数量")
    private Integer highRiskCount;

    @Schema(description = "中风险数量")
    private Integer mediumRiskCount;

    @Schema(description = "低风险数量")
    private Integer lowRiskCount;

    @Schema(description = "待处理总数")
    private Integer pendingCount;

    @Schema(description = "今日新增")
    private Integer todayCount;
}