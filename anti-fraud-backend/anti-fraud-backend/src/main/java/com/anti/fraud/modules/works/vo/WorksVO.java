package com.anti.fraud.modules.works.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "作品详情")
public class WorksVO {
    @Schema(description = "作品ID")
    private Long id;

    @Schema(description = "作品标题")
    private String title;

    @Schema(description = "作品类型")
    private String worksType;

    @Schema(description = "作品类型名称")
    private String worksTypeName;

    @Schema(description = "活动ID")
    private Long activityId;

    @Schema(description = "活动名称")
    private String activityName;

    @Schema(description = "作者ID")
    private Long authorId;

    @Schema(description = "作者名称")
    private String authorName;

    @Schema(description = "院系")
    private String department;

    @Schema(description = "作品内容")
    private String content;

    @Schema(description = "文件URL")
    private String fileUrl;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "审核人")
    private String auditorName;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "浏览量")
    private Integer viewCount;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "是否优秀作品")
    private Boolean isExcellent;

    @Schema(description = "排名")
    private Integer rank;

    @Schema(description = "奖励积分")
    private Integer points;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}