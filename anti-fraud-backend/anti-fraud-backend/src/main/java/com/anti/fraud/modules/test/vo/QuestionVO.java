package com.anti.fraud.modules.test.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "题目VO")
public class QuestionVO {

    @Schema(description = "题目ID")
    private Long id;

    @Schema(description = "题目类型：1-单选 2-多选 3-判断")
    private Integer questionType;

    @Schema(description = "题目内容")
    private String content;

    @Schema(description = "选项列表")
    private List<String> options;

    @Schema(description = "题目分值")
    private Integer score;

    @Schema(description = "难度等级")
    private Integer difficulty;
}
