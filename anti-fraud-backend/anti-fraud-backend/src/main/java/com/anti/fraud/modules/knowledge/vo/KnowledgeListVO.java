package com.anti.fraud.modules.knowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "知识列表项")
public class KnowledgeListVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "内容类型")
    private Integer contentType;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "点赞次数")
    private Integer likeCount;

    @Schema(description = "收藏次数")
    private Integer collectCount;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "作者")
    private String authorName;
}