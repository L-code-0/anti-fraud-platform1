package com.anti.fraud.modules.drill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 演练场景实体
 */
@Data
@TableName("drill_scenario")
public class DrillScenario {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String scenarioName;
    private String description;
    private String difficultyLevel;
    private String scenarioContent;
    private String correctAnswer;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
