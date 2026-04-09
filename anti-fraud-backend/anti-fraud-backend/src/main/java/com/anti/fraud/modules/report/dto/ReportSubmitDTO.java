package com.anti.fraud.modules.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "举报提交DTO")
public class ReportSubmitDTO {

    @Schema(description = "举报类型：1-可疑电话 2-可疑短信 3-可疑链接 4-其他")
    private Integer reportType;

    @Schema(description = "诈骗类型")
    private String fraudType;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "可疑电话号码")
    private String phoneNumber;

    @Schema(description = "可疑链接")
    private String linkUrl;

    @Schema(description = "图片列表")
    private List<String> images;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "是否匿名")
    private Boolean isAnonymous;
}