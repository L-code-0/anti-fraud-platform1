package com.anti.fraud.modules.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "知识分类")
public class CategoryVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "图标")
    private String icon;
}
