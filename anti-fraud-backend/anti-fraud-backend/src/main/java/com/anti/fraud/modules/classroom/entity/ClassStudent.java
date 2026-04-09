package com.anti.fraud.modules.classroom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级学生关联实体
 */
@Data
@TableName("class_student")
public class ClassStudent {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     学号
     */
    private String studentNo;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 学习进度
     */
    private Integer learningProgress;

    /**
     * 平均分
     */
    private Integer averageScore;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 加入时间
     */
    private LocalDateTime joinTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
