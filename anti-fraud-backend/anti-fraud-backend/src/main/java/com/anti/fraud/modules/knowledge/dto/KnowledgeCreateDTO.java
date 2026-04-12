package com.anti.fraud.modules.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "知识创建DTO")
public class KnowledgeCreateDTO {

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "内容类型：1-文章 2-视频 3-案例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "内容类型不能为空")
    private Integer contentType;

    @Schema(description = "摘要")
    @Size(max = 200, message = "摘要长度不能超过200个字符")
    private String summary;

    @Schema(description = "内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "内容不能为空")
    @Size(max = 10000, message = "内容长度不能超过10000个字符")
    private String content;

    @Schema(description = "封面图")
    @Size(max = 255, message = "封面图URL长度不能超过255个字符")
    private String coverImage;

    @Schema(description = "视频URL")
    @Size(max = 255, message = "视频URL长度不能超过255个字符")
    private String videoUrl;

    @Schema(description = "视频时长(秒)")
    private Integer videoDuration;

    @Schema(description = "标签")
    @Size(max = 100, message = "标签长度不能超过100个字符")
    private String tags;

    @Schema(description = "来源")
    @Size(max = 50, message = "来源长度不能超过50个字符")
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