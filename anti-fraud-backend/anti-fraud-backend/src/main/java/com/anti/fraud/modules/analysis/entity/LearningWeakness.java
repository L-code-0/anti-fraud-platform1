package com.anti.fraud.modules.analysis.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学习薄弱点实体
 */
@Data
@TableName("learning_weakness")
public class LearningWeakness {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String category; // 知识分类
    private String categoryName;
    private Integer totalQuestions; // 该分类总题数
    private Integer wrongQuestions; // 错题数
    private Double correctRate; // 正确率
    private Integer weaknessLevel; // 薄弱等级 1-轻微 2-中等 3-严重
    private LocalDateTime updateTime;
}
