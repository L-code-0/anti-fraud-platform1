package com.anti.fraud.modules.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分类掌握情况")
public class CategoryMasteryVO {
    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "总题数")
    private Integer totalQuestions;

    @Schema(description = "正确数")
    private Integer correctQuestions;

    @Schema(description = "正确率")
    private Double correctRate;

    @Schema(description = "掌握等级")
    private String masteryLevel;
}
