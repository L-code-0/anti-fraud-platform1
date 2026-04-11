package com.anti.fraud.modules.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 案例分析创建 DTO
 */
@Data
@Schema(description = "创建案例分析请求")
public class AnalysisCreateDTO {

    @Schema(description = "案例标题", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "案例类型：1-典型案例 2-新型诈骗", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private Integer type;

    @Schema(description = "案例摘要", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private String summary;

    @Schema(description = "详细内容")
    private String content;
}
