package com.anti.fraud.modules.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("test_paper")
public class Paper {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String paperName;

    private Integer paperType;

    private Integer totalScore;

    private Integer passScore;

    private Integer duration;

    private Integer questionCount;

    private Integer status;

    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
