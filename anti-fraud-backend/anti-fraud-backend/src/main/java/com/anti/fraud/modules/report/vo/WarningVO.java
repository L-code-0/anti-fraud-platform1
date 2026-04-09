package com.anti.fraud.modules.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "预警信息VO")
public class WarningVO {

    @Schema(description = "预警ID")
    private Long id;

    @Schema(description = "预警标题")
    private String title;

    @Schema(description = "预警等级：1-蓝色 2-黄色 3-橙色 4-红色")
    private Integer warningLevel;

    @Schema(description = "诈骗类型")
    private String fraudType;

    @Schema(description = "预警内容")
    private String content;

    @Schema(description = "防范要点")
    private String preventionTips;

    @Schema(description = "阅读次数")
    private Integer viewCount;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;
}
