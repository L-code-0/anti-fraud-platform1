package com.anti.fraud.modules.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "知识创建DTO")
public class KnowledgeCreateDTO {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "内容类型：1-文章 2-视频 3-案例")
    private Integer contentType;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "视频URL")
    private String videoUrl;

    @Schema(description = "视频时长(秒)")
    private Integer videoDuration;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "是否热门")
    private Boolean isHot;

    @Schema(description = "是否推荐")
    private Boolean isRecommend;
}