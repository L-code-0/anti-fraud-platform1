package com.anti.fraud.modules.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "学习推荐")
public class RecommendationVO {
    @Schema(description = "推荐类型: KNOWLEDGE-知识, TEST-测试, SIMULATION-演练")
    private String type;

    @Schema(description = "资源ID")
    private Long resourceId;

    @Schema(description = "资源名称")
    private String resourceName;

    @Schema(description = "推荐原因")
    private String reason;

    @Schema(description = "优先级")
    private Integer priority;
}
