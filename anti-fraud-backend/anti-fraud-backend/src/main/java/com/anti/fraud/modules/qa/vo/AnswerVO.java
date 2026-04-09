package com.anti.fraud.modules.qa.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "回答详情")
public class AnswerVO {
    @Schema(description = "回答ID")
    private Long id;

    @Schema(description = "问题ID")
    private Long questionId;

    @Schema(description = "回答内容")
    private String content;

    @Schema(description = "回答者ID")
    private Long answererId;

    @Schema(description = "回答者名称")
    private String answererName;

    @Schema(description = "专家头衔")
    private String answererTitle;

    @Schema(description = "是否被采纳")
    private Boolean isAccepted;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
