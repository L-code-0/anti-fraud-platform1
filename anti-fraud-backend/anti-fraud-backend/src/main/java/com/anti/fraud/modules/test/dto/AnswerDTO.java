package com.anti.fraud.modules.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "答案DTO")
public class AnswerDTO {

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "用户答案")
    private String answer;
}