package com.anti.fraud.modules.irt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目参数实体
 */
@Data
@TableName("question_param")
public class QuestionParam {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 参数ID（UUID）
     */
    private String paramId;

    /**
     * 题目ID
     */
    private String questionId;

    /**
     * 题目名称
     */
    private String questionName;

    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 难度参数（b）
     */
    private Double difficulty;

    /**
     * 区分度参数（a）
     */
    private Double discrimination;

    /**
     * 猜测参数（c）
     */
    private Double guessing;

    /**
     * 参数估计方法：1-极大似然估计，2-贝叶斯估计，3-EM算法
     */
    private Integer estimationMethod;

    /**
     * 估计精度
     */
    private Double estimationAccuracy;

    /**
     * 样本量
     */
    private Integer sampleSize;

    /**
     * 状态：1-已标定，2-待标定，3-标定失败
     */
    private Integer status;

    /**
     * 标定时间
     */
    private LocalDateTime calibrationTime;

    /**
     * 描述
     */
    private String description;

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
