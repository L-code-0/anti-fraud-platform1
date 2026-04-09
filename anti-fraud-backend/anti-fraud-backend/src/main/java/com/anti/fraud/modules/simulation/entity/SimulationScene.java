package com.anti.fraud.modules.simulation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("simulation_scene")
public class SimulationScene {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String sceneName;

    private String sceneType;

    private Long categoryId;

    private String background;

    private String scriptConfig;

    private Integer difficulty;

    private String coverImage;

    private Integer playCount;

    private BigDecimal avgScore;

    private Integer isRecommend;

    private Integer status;

    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
