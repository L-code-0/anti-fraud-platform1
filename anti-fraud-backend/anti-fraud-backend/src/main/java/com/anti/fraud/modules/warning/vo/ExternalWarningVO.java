package com.anti.fraud.modules.warning.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 外部反诈预警信息VO
 */
@Data
@Schema(description = "外部反诈预警信息VO")
public class ExternalWarningVO {

    @Schema(description = "预警ID")
    private Long id;

    @Schema(description = "预警标题")
    private String title;

    @Schema(description = "预警内容")
    private String content;

    @Schema(description = "预警来源")
    private String source;

    @Schema(description = "预警类型：1-电信诈骗，2-网络诈骗，3-金融诈骗，4-其他")
    private Integer warningType;

    @Schema(description = "预警类型名称")
    private String warningTypeName;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "同步时间")
    private LocalDateTime syncTime;

    @Schema(description = "预警URL")
    private String url;

    @Schema(description = "状态：1-有效，2-无效")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
