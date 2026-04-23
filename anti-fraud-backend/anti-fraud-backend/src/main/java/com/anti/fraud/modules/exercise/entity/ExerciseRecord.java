package com.anti.fraud.modules.exercise.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 演练记录实体
 */
@Data
@TableName("exercise_record")
public class ExerciseRecord {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 演练类型：1-模拟演练，2-实战演练
     */
    private Integer exerciseType;

    /**
     * 演练名称
     */
    private String exerciseName;

    /**
     * 演练内容（JSON格式）
     */
    private String exerciseContent;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 用时（秒）
     */
    private Integer duration;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 正确率
     */
    private Double correctRate;

    /**
     * 完成情况：1-完成，2-未完成
     */
    private Integer completionStatus;

    /**
     * 状态：1-正常，2-异常
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private Integer deleted;
}
