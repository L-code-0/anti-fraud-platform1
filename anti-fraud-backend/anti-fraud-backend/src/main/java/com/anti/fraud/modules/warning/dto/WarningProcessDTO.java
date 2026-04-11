package com.anti.fraud.modules.warning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 预警处理 DTO
 */
@Data
@Schema(description = "预警处理请求")
public class WarningProcessDTO {

    @Schema(description = "预警ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "处理状态：1-已处理 2-误报", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    @Schema(description = "处理结果", requiredMode = Schema.RequiredMode.REQUIRED)
    private String result;
}
