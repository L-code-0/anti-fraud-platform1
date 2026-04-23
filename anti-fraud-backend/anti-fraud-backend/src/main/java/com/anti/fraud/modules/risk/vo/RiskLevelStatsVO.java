package com.anti.fraud.modules.risk.vo;

import lombok.Data;

/**
 * 风险等级统计VO
 */
@Data
public class RiskLevelStatsVO {
    private String riskLevel;
    private Integer count;
    private Double percentage;
}
