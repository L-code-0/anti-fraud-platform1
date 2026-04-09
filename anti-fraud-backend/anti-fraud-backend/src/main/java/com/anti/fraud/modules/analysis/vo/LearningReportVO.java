package com.anti.fraud.modules.analysis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "学习报告")
public class LearningReportVO {
    @Schema(description = "总学习时长(秒)")
    private Integer totalStudyDuration;

    @Schema(description = "总学习时长(格式化)")
    private String totalStudyDurationStr;

    @Schema(description = "总测试次数")
    private Integer totalTestCount;

    @Schema(description = "平均测试分数")
    private Double avgTestScore;

    @Schema(description = "演练完成数")
    private Integer simulationCount;

    @Schema(description = "知识点掌握情况")
    private List<CategoryMasteryVO> categoryMastery;

    @Schema(description = "薄弱知识点")
    private List<WeaknessVO> weaknesses;

    @Schema(description = "学习推荐")
    private List<RecommendationVO> recommendations;
}
