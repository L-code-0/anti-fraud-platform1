package com.anti.fraud.modules.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 回答实体
 */
@Data
@TableName("answer_info")
public class AnswerInfo {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionId;
    private String content;
    private Long answererId;
    private String answererName;
    private String answererTitle; // 专家头衔
    private Integer isAccepted; // 是否被采纳
    private Integer likeCount;
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
