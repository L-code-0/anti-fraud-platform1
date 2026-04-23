package com.anti.fraud.modules.statistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统计数据实体
 */
@Data
@TableName("statistics_data")
public class StatisticsData {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 统计ID（UUID）
     */
    private String statisticsId;

    /**
     * 统计类型：1-用户活跃度，2-演练参与度，3-诈骗类型分布，4-知识掌握度，5-其他
     */
    private Integer type;

    /**
     * 统计维度：1-日，2-周，3-月，4-年，5-自定义
     */
    private Integer dimension;

    /**
     * 统计值
     */
    private Double value;

    /**
     * 统计数据（JSON格式）
     */
    private String data;

    /**
     * 统计时间
     */
    private LocalDateTime statisticsTime;

    /**
     * 统计周期开始时间
     */
    private LocalDateTime periodStartTime;

    /**
     * 统计周期结束时间
     */
    private LocalDateTime periodEndTime;

    /**
     * 状态：1-已统计，2-统计中，3-统计失败
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private Integer deleted;
}
