package com.anti.fraud.modules.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预警创建DTO")
public class WarningCreateDTO {

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
}