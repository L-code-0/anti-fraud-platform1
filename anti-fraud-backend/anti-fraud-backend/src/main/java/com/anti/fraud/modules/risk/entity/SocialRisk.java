package com.anti.fraud.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社交风险实体
 */
@Data
@TableName("social_risk")
public class SocialRisk {

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
     * 社交类型：1-好友关系，2-群组关系，3-关注关系，4-其他
     */
    private Integer socialType;

    /**
     * 社交对象ID
     */
    private String targetUserId;

    /**
     * 社交对象名称
     */
    private String targetUsername;

    /**
     * 社交关系强度（0-1）
     */
    private Double socialStrength;

    /**
     * 社交内容（JSON格式）
     */
    private String socialContent;

    /**
     * 社交时间
     */
    private LocalDateTime socialTime;

    /**
     * 风险分数（0-100）
     */
    private Double riskScore;

    /**
     * 风险等级：1-低风险，2-中风险，3-高风险，4-极高风险
     */
    private Integer riskLevel;

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
