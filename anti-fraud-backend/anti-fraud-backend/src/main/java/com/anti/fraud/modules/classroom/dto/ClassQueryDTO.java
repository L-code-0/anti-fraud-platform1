package com.anti.fraud.modules.classroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 班级查询 DTO
 */
@Data
@Schema(description = "班级查询条件")
public class ClassQueryDTO {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "12")
    private Integer size = 12;

    @Schema(description = "搜索关键词")
    private String keyword;
}
