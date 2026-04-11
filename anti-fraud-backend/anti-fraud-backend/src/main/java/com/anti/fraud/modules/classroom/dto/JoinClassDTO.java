package com.anti.fraud.modules.classroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 加入班级 DTO
 */
@Data
@Schema(description = "加入班级请求")
public class JoinClassDTO {

    @Schema(description = "班级码", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private String classCode;
}

