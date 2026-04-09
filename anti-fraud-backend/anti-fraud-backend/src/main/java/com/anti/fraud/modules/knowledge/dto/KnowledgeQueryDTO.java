package com.anti.fraud.modules.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "知识查询参数")
public class KnowledgeQueryDTO {

    @Schema(description = "页码")
    private Integer page = 1;

    @Schema(description = "每页大小")
    private Integer size = 10;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "内容类型")
    private Integer contentType;
}