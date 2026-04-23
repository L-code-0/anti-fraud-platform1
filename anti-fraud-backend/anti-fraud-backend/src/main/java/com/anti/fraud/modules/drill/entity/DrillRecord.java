package com.anti.fraud.modules.drill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 演练记录实体
 */
@Data
@TableName("drill_record")
public class DrillRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String userId;
    private Long scenarioId;
    private String scenarioName;
    private String result;
    private Integer score;
    private String feedback;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
