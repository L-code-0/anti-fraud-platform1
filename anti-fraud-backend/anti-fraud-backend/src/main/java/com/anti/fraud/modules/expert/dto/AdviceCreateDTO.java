package com.anti.fraud.modules.expert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 专家建议创建 DTO
 */
@Data
@Schema(description = "创建专家建议请求")
public class AdviceCreateDTO {

    @Schema(description = "建议标题", required = true)
    private String title;

    @Schema(description = "建议分类：防骗技巧、安全提醒、案例警示、政策解读", required = true)
    private String category;

    @Schema(description = "建议内容", required = true)
    private String content;
}
