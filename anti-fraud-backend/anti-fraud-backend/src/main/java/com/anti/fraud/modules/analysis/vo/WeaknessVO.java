package com.anti.fraud.modules.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "薄弱点")
public class WeaknessVO {
    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "正确率")
    private Double correctRate;

    @Schema(description = "薄弱等级")
    private Integer weaknessLevel;

    @Schema(description = "建议")
    private String suggestion;
}
