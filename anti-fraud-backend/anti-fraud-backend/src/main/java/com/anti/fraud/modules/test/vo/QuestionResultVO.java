package com.anti.fraud.modules.test.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "题目结果VO")
public class QuestionResultVO {

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "题目内容")
    private String content;

    @Schema(description = "用户答案")
    private String userAnswer;

    @Schema(description = "正确答案")
    private String correctAnswer;

    @Schema(description = "是否正确")
    private Boolean isCorrect;

    @Schema(description = "答案解析")
    private String analysis;

    @Schema(description = "题目分值")
    private Integer score;

    @Schema(description = "获得分数")
    private Integer earnedScore;
}
