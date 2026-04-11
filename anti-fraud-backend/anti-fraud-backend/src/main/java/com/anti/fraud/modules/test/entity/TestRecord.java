package com.anti.fraud.modules.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("test_record")
public class TestRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long paperId;

    private Integer totalScore;

    /**
     * 得分（整数）
     */
    private Integer score;

    private BigDecimal userScore;

    private Integer correctCount;

    private Integer wrongCount;

    private Integer duration;

    private Integer isCompleted;

    private Integer isPassed;

    private LocalDateTime startTime;

    private LocalDateTime submitTime;

    private LocalDateTime createTime;
}