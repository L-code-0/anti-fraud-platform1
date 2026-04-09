package com.anti.fraud.modules.test.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "测试结果VO")
public class TestResultVO {

    @Schema(description = "记录ID")
    private Long recordId;

    @Schema(description = "试卷ID")
    private Long paperId;

    @Schema(description = "试卷名称")
    private String paperName;

    @Schema(description = "总分")
    private Integer totalScore;

    @Schema(description = "得分")
    private BigDecimal userScore;

    @Schema(description = "正确题数")
    private Integer correctCount;

    @Schema(description = "错误题数")
    private Integer wrongCount;

    @Schema(description = "是否及格")
    private Boolean isPassed;

    @Schema(description = "答题时长(秒)")
    private Integer duration;

    @Schema(description = "题目详情")
    private List<QuestionResultVO> questions;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;
}