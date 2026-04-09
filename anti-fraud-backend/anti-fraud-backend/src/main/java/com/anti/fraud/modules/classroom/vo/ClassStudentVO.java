package com.anti.fraud.modules.classroom.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级学生 VO
 */
@Data
@Schema(description = "班级学生信息")
public class ClassStudentVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学号")
    private String studentNo;

    @Schema(description = "学生姓名")
    private String name;

    @Schema(description = "学习进度")
    private Integer learningProgress;

    @Schema(description = "平均分")
    private Integer score;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLogin;

    @Schema(description = "加入时间")
    private LocalDateTime joinTime;
}
