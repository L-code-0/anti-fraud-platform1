package com.anti.fraud.modules.classroom.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级列表 VO
 */
@Data
@Schema(description = "班级信息")
public class ClassVO {

    @Schema(description = "班级ID")
    private Long id;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "班级码")
    private String classCode;

    @Schema(description = "班主任姓名")
    private String teacherName;

    @Schema(description = "班级描述")
    private String description;

    @Schema(description = "学生人数")
    private Integer studentCount;

    @Schema(description = "学习进度")
    private Integer learningProgress;

    @Schema(description = "平均分")
    private Integer averageScore;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
