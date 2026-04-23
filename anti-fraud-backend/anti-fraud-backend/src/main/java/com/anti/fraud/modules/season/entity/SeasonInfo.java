package com.anti.fraud.modules.season.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 赛季信息实体类
 */
@Data
@TableName("season_info")
public class SeasonInfo {

    /**
     * 赛季ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 赛季名称
     */
    private String seasonName;

    /**
     * 赛季描述
     */
    private String description;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 赛季状态：1-未开始，2-进行中，3-已结束
     */
    private Integer status;

    /**
     * 赛季主题
     */
    private String theme;

    /**
     * 赛季皮肤ID
     */
    private Long skinId;

    /**
     * 赛季称号ID
     */
    private Long titleId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
