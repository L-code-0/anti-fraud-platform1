package com.anti.fraud.modules.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "题目创建DTO")
public class QuestionCreateDTO {

    @Schema(description = "题目类型：1-单选 2-多选 3-判断", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "题目类型不能为空")
    private Integer questionType;

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "题目内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "题目内容不能为空")
    @Size(max = 1000, message = "题目内容长度不能超过1000个字符")
    private String content;

    @Schema(description = "选项列表")
    private List<String> options;

    @Schema(description = "正确答案", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "正确答案不能为空")
    @Size(max = 100, message = "正确答案长度不能超过100个字符")
    private String answer;

    @Schema(description = "答案解析")
    @Size(max = 2000, message = "答案解析长度不能超过2000个字符")
    private String analysis;

    @Schema(description = "难度等级：1-5")
    private Integer difficulty;

    @Schema(description = "标签")
    @Size(max = 100, message = "标签长度不能超过100个字符")
    private String tags;

    @Schema(description = "分值")
    private Integer score;
}