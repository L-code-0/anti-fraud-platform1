package com.anti.fraud.modules.expert.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 案例分析 VO
 */
@Data
@Schema(description = "案例分析信息")
public class AnalysisVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "案例标题")
    private String title;

    @Schema(description = "案例类型：1-典型案例 2-新型诈骗")
    private Integer type;

    @Schema(description = "案例摘要")
    private String summary;

    @Schema(description = "详细内容")
    private String content;

    @Schema(description = "发布专家")
    private String expertName;

    @Schema(description = "阅读量")
    private Integer viewCount;

    @Schema(description = "点赞数")
    private Integer thumbCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
