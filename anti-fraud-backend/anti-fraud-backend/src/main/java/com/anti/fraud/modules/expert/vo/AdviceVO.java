package com.anti.fraud.modules.expert.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专家建议 VO
 */
@Data
@Schema(description = "专家建议信息")
public class AdviceVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "建议标题")
    private String title;

    @Schema(description = "建议分类")
    private String category;

    @Schema(description = "建议内容")
    private String content;

    @Schema(description = "发布专家")
    private String author;

    @Schema(description = "阅读量")
    private Integer viewCount;

    @Schema(description = "点赞数")
    private Integer thumbCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
