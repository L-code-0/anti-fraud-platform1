package com.anti.fraud.modules.test.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "试卷VO")
public class PaperVO {

    @Schema(description = "试卷ID")
    private Long id;

    @Schema(description = "试卷名称")
    private String paperName;

    @Schema(description = "试卷类型")
    private Integer paperType;

    @Schema(description = "总分")
    private Integer totalScore;

    @Schema(description = "及格分")
    private Integer passScore;

    @Schema(description = "考试时长(分钟)")
    private Integer duration;

    @Schema(description = "题目数量")
    private Integer questionCount;

    @Schema(description = "是否已参加")
    private Boolean hasJoined;

    @Schema(description = "最高分")
    private Integer highestScore;

    @Schema(description = "参加次数")
    private Integer joinCount;
}
