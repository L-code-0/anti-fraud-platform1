package com.anti.fraud.modules.emotion.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 情感分析实体
 */
@Data
@TableName("emotion_analysis")
public class EmotionAnalysis {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分析ID（UUID）
     */
    private String analysisId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 分析文本
     */
    private String text;

    /**
     * 情感类型：1-积极，2-消极，3-中性
     */
    private Integer emotionType;

    /**
     * 情感得分（-1到1，-1表示最消极，1表示最积极）
     */
    private Double emotionScore;

    /**
     * 情绪标签（JSON格式）
     */
    private String emotionTags;

    /**
     * 分析结果（JSON格式）
     */
    private String analysisResult;

    /**
     * 分析时间
     */
    private LocalDateTime analysisTime;

    /**
     * 状态：1-已分析，2-分析中，3-分析失败
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMessage;

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
