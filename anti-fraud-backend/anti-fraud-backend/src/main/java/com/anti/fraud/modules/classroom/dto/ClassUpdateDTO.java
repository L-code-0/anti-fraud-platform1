package com.anti.fraud.modules.classroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 班级更新 DTO
 */
@Data
@Schema(description = "更新班级请求")
public class ClassUpdateDTO {

    @Schema(description = "班级ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "班级名称", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private String className;

    @Schema(description = "班主任姓名")
    private String teacherName;

    @Schema(description = "班级描述")
    private String description;
}
