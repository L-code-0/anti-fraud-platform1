package com.anti.fraud.modules.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "试卷创建DTO")
public class PaperCreateDTO {

    @Schema(description = "试卷名称")
    private String paperName;

    @Schema(description = "试卷类型：1-随机组卷 2-固定试卷")
    private Integer paperType;

    @Schema(description = "总分")
    private Integer totalScore;

    @Schema(description = "及格分")
    private Integer passScore;

    @Schema(description = "考试时长(分钟)")
    private Integer duration;

    @Schema(description = "题目数量")
    private Integer questionCount;
}
