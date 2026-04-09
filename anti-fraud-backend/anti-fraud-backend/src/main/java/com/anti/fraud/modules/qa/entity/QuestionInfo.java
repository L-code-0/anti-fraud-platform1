package com.anti.fraud.modules.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 问题实体
 */
@Data
@TableName("question_info")
public class QuestionInfo {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String content;
    private Long askerId;
    private String askerName;
    private Integer status; // 0-待回答, 1-已回答
    private Integer viewCount;
    private Integer answerCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}