package com.anti.fraud.modules.classroom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级实体
 */
@Data
@TableName("class_info")
public class ClassInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 班级码（唯一）
     */
    private String classCode;

    /**
     * 班主任ID
     */
    private Long teacherId;

    /**
     * 班主任姓名
     */
    private String teacherName;

    /**
     * 班级描述
     */
    private String description;

    /**
     * 学生人数
     */
    private Integer studentCount;

    /**
     * 学习进度（平均）
     */
    private Integer learningProgress;

    /**
     * 知识掌握率
     */
    private Integer masteryRate;

    /**
     * 平均分
     */
    private Integer averageScore;

    /**
     * 状态：0-已关闭 1-正常
     */
    private Integer status;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
