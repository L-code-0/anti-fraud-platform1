package com.anti.fraud.modules.qa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "提问请求")
public class QuestionDTO {
    @Schema(description = "问题标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "问题标题不能为空")
    @Size(max = 100, message = "问题标题长度不能超过100个字符")
    private String title;

    @Schema(description = "问题内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "问题内容不能为空")
    @Size(max = 2000, message = "问题内容长度不能超过2000个字符")
    private String content;
}
