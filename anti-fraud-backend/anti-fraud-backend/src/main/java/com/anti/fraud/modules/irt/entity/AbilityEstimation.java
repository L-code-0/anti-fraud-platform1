package com.anti.fraud.modules.irt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户能力估计实体
 */
@Data
@TableName("ability_estimation")
public class AbilityEstimation {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 估计ID（UUID）
     */
    private String estimationId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 能力值（θ）
     */
    private Double ability;

    /**
     * 能力估计标准差
     */
    private Double abilityStd;

    /**
     * 估计方法：1-极大似然估计，2-贝叶斯估计，3-加权似然估计
     */
    private Integer estimationMethod;

    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 测试ID
     */
    private String testId;

    /**
     * 测试状态：1-进行中，2-已完成，3-已取消
     */
    private Integer testStatus;

    /**
     * 已答题数量
     */
    private Integer answeredCount;

    /**
     * 正确答题数量
     */
    private Integer correctCount;

    /**
     * 估计时间
     */
    private LocalDateTime estimationTime;

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
