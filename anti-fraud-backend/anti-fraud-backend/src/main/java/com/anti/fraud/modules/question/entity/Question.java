package com.anti.fraud.modules.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目实体
 */
@Data
@TableName("question")
public class Question {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 题目类型：1-单选题，2-多选题，3-判断题，4-填空题，5-简答题
     */
    private Integer type;

    /**
     * 题目难度：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    /**
     * 知识点
     */
    private String knowledgePoint;

    /**
     * 选项（JSON格式）
     */
    private String options;

    /**
     * 正确答案
     */
    private String correctAnswer;

    /**
     * 解析
     */
    private String explanation;

    /**
     * 来源
     */
    private String source;

    /**
     * 标签
     */
    private String tags;

    /**
     * 使用率
     */
    private Integer usageCount;

    /**
     * 正确率
     */
    private Double correctRate;

    /**
     * 状态：1-启用，2-禁用
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
