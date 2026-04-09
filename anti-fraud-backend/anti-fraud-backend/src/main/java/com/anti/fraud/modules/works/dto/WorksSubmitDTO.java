package com.anti.fraud.modules.works.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "提交作品请求")
public class WorksSubmitDTO {
    @Schema(description = "作品标题")
    private String title;

    @Schema(description = "作品类型: ESSAY-征文, VIDEO-短视频")
    private String worksType;

    @Schema(description = "活动ID")
    private Long activityId;

    @Schema(description = "征文内容（征文类型必填）")
    private String content;

    @Schema(description = "文件URL（短视频类型必填）")
    private String fileUrl;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "作品描述")
    private String description;
}