package com.anti.fraud.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 风险画像实体
 */
@Data
@TableName("risk_profile")
public class RiskProfile {

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
     * 风险评分
     */
    private Integer riskScore;

    /**
     * 风险等级：1-低风险，2-中风险，3-高风险
     */
    private Integer riskLevel;

    /**
     * 风险标签（JSON格式）
     */
    private String riskTags;

    /**
     * 行为特征（JSON格式）
     */
    private String behaviorFeatures;

    /**
     * 风险分析结果（JSON格式）
     */
    private String riskAnalysis;

    /**
     * 建议措施（JSON格式）
     */
    private String suggestions;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;

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
