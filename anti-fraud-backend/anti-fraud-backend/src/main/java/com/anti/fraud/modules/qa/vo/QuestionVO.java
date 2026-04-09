package com.anti.fraud.modules.qa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "问题详情")
public class QuestionVO {
    @Schema(description = "问题ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "提问者ID")
    private Long askerId;

    @Schema(description = "提问者名称")
    private String askerName;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "浏览量")
    private Integer viewCount;

    @Schema(description = "回答数")
    private Integer answerCount;

    @Schema(description = "回答列表")
    private List<AnswerVO> answers;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
