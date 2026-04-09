package com.anti.fraud.modules.simulation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "用户演练统计VO")
public class UserSimulationStatsVO {

    @Schema(description = "总演练次数")
    private Integer totalCount;

    @Schema(description = "通过次数")
    private Integer passCount;

    @Schema(description = "平均得分")
    private BigDecimal avgScore;

    @Schema(description = "最高得分")
    private BigDecimal maxScore;

    @Schema(description = "最低得分")
    private BigDecimal minScore;

    @Schema(description = "总演练时长(分钟)")
    private Integer totalDuration;

    @Schema(description = "通过率")
    private BigDecimal passRate;
}
