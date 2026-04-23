package com.anti.fraud.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户风险画像实体
 */
@Data
@TableName("user_risk_profile")
public class UserRiskProfile {

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
     * 综合风险分数（0-100）
     */
    private Double overallRiskScore;

    /**
     * 风险等级：1-低风险，2-中风险，3-高风险，4-极高风险
     */
    private Integer riskLevel;

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
     * 风险标签（JSON格式）
     */
    private String riskLabels;

    /**
     * 风险趋势（JSON格式）
     */
    private String riskTrend;

    /**
     * 风险因素（JSON格式）
     */
    private String riskFactors;

    /**
     * 建议措施（JSON格式）
     */
    private String recommendations;

    /**
     * 风险画像数据（JSON格式）
     */
    private String profileData;

    /**
     * 评估时间
     */
    private LocalDateTime assessmentTime;

    /**
     * 状态：1-已评估，2-评估中，3-评估失败
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
