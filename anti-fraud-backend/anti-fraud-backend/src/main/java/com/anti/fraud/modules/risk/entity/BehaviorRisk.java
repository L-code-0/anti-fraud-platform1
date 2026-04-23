package com.anti.fraud.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 行为风险实体
 */
@Data
@TableName("behavior_risk")
public class BehaviorRisk {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 风险ID（UUID）
     */
    private String riskId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 行为类型：1-登录行为，2-交易行为，3-搜索行为，4-浏览行为，5-其他
     */
    private Integer behaviorType;

    /**
     * 行为内容（JSON格式）
     */
    private String behaviorContent;

    /**
     * 行为时间
     */
    private LocalDateTime behaviorTime;

    /**
     * 风险分数（0-100）
     */
    private Double riskScore;

    /**
     * 风险等级：1-低风险，2-中风险，3-高风险，4-极高风险
     */
    private Integer riskLevel;

    /**
     * 时间衰减因子（0-1）
     */
    private Double timeDecayFactor;

    /**
     * 风险详情（JSON格式）
     */
    private String riskDetails;

    /**
     * 状态：1-已分析，2-分析中，3-分析失败
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
