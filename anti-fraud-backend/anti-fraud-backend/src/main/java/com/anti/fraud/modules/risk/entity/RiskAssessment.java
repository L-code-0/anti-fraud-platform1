package com.anti.fraud.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 风险评估实体
 */
@Data
@TableName("risk_assessment")
public class RiskAssessment {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 评估ID（UUID）
     */
    private String assessmentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 行为风险分数（0-100）
     */
    private Double behaviorRiskScore;

    /**
     * 设备风险分数（0-100）
     */
    private Double deviceRiskScore;

    /**
     * 社交风险分数（0-100）
     */
    private Double socialRiskScore;

    /**
     * 综合风险分数（0-100）
     */
    private Double overallRiskScore;

    /**
     * 风险等级：1-低风险，2-中风险，3-高风险，4-极高风险
     */
    private Integer riskLevel;

    /**
     * 行为风险详情（JSON格式）
     */
    private String behaviorRiskDetails;

    /**
     * 设备风险详情（JSON格式）
     */
    private String deviceRiskDetails;

    /**
     * 社交风险详情（JSON格式）
     */
    private String socialRiskDetails;

    /**
     * 评估时间
     */
    private LocalDateTime assessmentTime;

    /**
     * 评估状态：1-已评估，2-评估中，3-评估失败
     */
    private Integer status;

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
