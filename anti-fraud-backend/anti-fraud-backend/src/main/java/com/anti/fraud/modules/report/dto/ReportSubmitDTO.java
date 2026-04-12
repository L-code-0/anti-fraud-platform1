package com.anti.fraud.modules.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Schema(description = "举报提交DTO")
public class ReportSubmitDTO {

    @NotNull(message = "举报类型不能为空")
    @Schema(description = "举报类型：1-可疑电话 2-可疑短信 3-可疑链接 4-其他")
    private Integer reportType;

    @NotBlank(message = "诈骗类型不能为空")
    @Size(max = 50, message = "诈骗类型长度不能超过50")
    @Schema(description = "诈骗类型")
    private String fraudType;

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100")
    @Schema(description = "标题")
    private String title;

    @NotBlank(message = "描述不能为空")
    @Size(max = 2000, message = "描述长度不能超过2000")
    @Schema(description = "描述")
    private String description;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "可疑电话号码")
    private String phoneNumber;

    @Pattern(regexp = "^$|^https?://.*", message = "链接格式不正确")
    @Size(max = 255, message = "链接长度不能超过255个字符")
    @Schema(description = "可疑链接")
    private String linkUrl;

    @Schema(description = "图片列表")
    private List<String> images;

    @Size(max = 50, message = "联系人姓名长度不能超过50")
    @Schema(description = "联系人")
    private String contactName;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "是否匿名")
    private Boolean isAnonymous;
}