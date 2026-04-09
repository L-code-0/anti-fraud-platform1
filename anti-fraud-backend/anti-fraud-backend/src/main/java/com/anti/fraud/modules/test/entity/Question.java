package com.anti.fraud.modules.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("test_question")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer questionType;

    private Long categoryId;

    private String content;

    private String options;

    private String answer;

    private String analysis;

    private Integer difficulty;

    private String tags;

    private Integer score;

    private Integer status;

    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
