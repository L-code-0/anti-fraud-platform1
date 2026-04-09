package com.anti.fraud.modules.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "题目创建DTO")
public class QuestionCreateDTO {

    @Schema(description = "题目类型：1-单选 2-多选 3-判断")
    private Integer questionType;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "题目内容")
    private String content;

    @Schema(description = "选项列表")
    private List<String> options;

    @Schema(description = "正确答案")
    private String answer;

    @Schema(description = "答案解析")
    private String analysis;

    @Schema(description = "难度等级：1-5")
    private Integer difficulty;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "分值")
    private Integer score;
}