package com.anti.fraud.modules.qa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "回答请求")
public class AnswerDTO {
    @Schema(description = "问题ID")
    private Long questionId;

    @Schema(description = "回答内容")
    private String content;
}