package com.anti.fraud.modules.qa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "提问请求")
public class QuestionDTO {
    @Schema(description = "问题标题")
    private String title;

    @Schema(description = "问题内容")
    private String content;
}
