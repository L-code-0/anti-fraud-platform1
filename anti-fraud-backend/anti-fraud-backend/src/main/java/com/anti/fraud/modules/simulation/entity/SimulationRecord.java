package com.anti.fraud.modules.simulation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("simulation_record")
public class SimulationRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long sceneId;

    private BigDecimal score;

    private Integer duration;

    private Integer isCompleted;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createTime;
}