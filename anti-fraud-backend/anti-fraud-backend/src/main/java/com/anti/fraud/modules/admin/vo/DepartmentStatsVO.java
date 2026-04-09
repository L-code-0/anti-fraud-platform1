package com.anti.fraud.modules.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "院系统计")
public class DepartmentStatsVO {
    @Schema(description = "院系ID")
    private Long departmentId;

    @Schema(description = "院系名称")
    private String departmentName;

    @Schema(description = "学生总数")
    private Integer totalStudents;

    @Schema(description = "学习参与人数")
    private Integer activeStudents;

    @Schema(description = "参与率")
    private Double participationRate;

    @Schema(description = "平均学习时长(秒)")
    private Integer avgStudyDuration;

    @Schema(description = "平均测试分数")
    private Double avgTestScore;

    @Schema(description = "测试完成次数")
    private Integer testCount;

    @Schema(description = "演练完成次数")
    private Integer simulationCount;
}
