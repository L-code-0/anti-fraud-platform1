package com.anti.fraud.modules.classroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 班级创建 DTO
 */
@Data
@Schema(description = "创建班级请求")
public class ClassCreateDTO {

    @Schema(description = "班级名称", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
    private String className;

    @Schema(description = "班主任姓名")
    private String teacherName;

    @Schema(description = "班级描述")
    private String description;
}
