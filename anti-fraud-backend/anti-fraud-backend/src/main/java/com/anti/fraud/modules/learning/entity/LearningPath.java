package com.anti.fraud.modules.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习路径实体
 */
@Data
@TableName("learning_path")
public class LearningPath {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 路径名称
     */
    private String name;

    /**
     * 路径描述
     */
    private String description;

    /**
     * 目标用户类型：1-新手，2-进阶，3-专家
     */
    private Integer targetUserType;

    /**
     * 预估时长（小时）
     */
    private Integer estimatedDuration;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    /**
     * 路径状态：1-启用，2-禁用
     */
    private Integer status;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 创建者名称
     */
    private String creatorName;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 完成人数
     */
    private Integer completionCount;

    /**
     * 平均评分
     */
    private Double averageScore;

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
