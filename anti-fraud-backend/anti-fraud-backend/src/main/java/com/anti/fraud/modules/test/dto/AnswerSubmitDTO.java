package com.anti.fraud.modules.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "答题提交DTO")
public class AnswerSubmitDTO {

    @Schema(description = "试卷ID")
    private Long paperId;

    @Schema(description = "记录ID")
    private Long recordId;

    @Schema(description = "答案列表")
    private List<AnswerDTO> answers;

    @Schema(description = "答题时长(秒)")
    private Integer duration;
}
