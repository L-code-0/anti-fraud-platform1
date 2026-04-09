package com.anti.fraud.modules.expert.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专家统计 VO
 */
@Data
@Schema(description = "专家统计数据")
public class ExpertStatsVO {

    @Schema(description = "案例分析数")
    private Integer analysisCount;

    @Schema(description = "专家建议数")
    private Integer adviceCount;

    @Schema(description = "总阅读量")
    private Long viewCount;

    @Schema(description = "总获赞数")
    private Long thumbCount;
}
