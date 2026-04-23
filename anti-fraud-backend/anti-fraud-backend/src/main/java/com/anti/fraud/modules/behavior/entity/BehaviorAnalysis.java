package com.anti.fraud.modules.behavior.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 行为分析实体
 */
@Data
@TableName("behavior_analysis")
public class BehaviorAnalysis {

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
     * 行为类型：1-浏览，2-点击，3-停留，4-搜索，5-答题，6-演练，7-其他
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
     * 分析结果（JSON格式）
     */
    private String analysisResult;

    /**
     * 反馈内容（JSON格式）
     */
    private String feedbackContent;

    /**
     * 分析状态：1-已分析，2-分析中，3-分析失败
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
