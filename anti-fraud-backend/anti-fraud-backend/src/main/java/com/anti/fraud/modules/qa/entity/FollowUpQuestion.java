package com.anti.fraud.modules.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 追问实体
 */
@Data
@TableName("follow_up_question")
public class FollowUpQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionId; // 原问题ID
    private Long answerId; // 被追问的回答ID
    private Long askerId; // 追问人ID
    private String askerName; // 追问人名称
    private String content; // 追问内容
    private Integer status; // 0-待回答, 1-已回答
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
