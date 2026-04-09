package com.anti.fraud.modules.classroom.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 班级统计 VO
 */
@Data
@Schema(description = "班级统计")
public class ClassStatsVO {

    @Schema(description = "班级总数")
    private Integer totalClasses;

    @Schema(description = "学生总数")
    private Integer totalStudents;

    @Schema(description = "平均学习进度")
    private Integer avgProgress;
}
