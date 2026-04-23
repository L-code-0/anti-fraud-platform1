package com.anti.fraud.modules.vr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * VR沉浸式演练实体
 */
@Data
@TableName("vr_simulation")
public class VRSimulation {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 演练名称
     */
    private String name;

    /**
     * 演练描述
     */
    private String description;

    /**
     * 演练场景ID
     */
    private Long scenarioId;

    /**
     * 演练场景名称
     */
    private String scenarioName;

    /**
     * 演练类型：1-电信诈骗，2-网络诈骗，3-金融诈骗，4-其他
     */
    private Integer type;

    /**
     * 难度等级：1-简单，2-中等，3-困难
     */
    private Integer difficulty;

    /**
     * 预计时长（分钟）
     */
    private Integer duration;

    /**
     * 3D模型路径
     */
    private String modelPath;

    /**
     * 场景资源路径
     */
    private String scenePath;

    /**
     * 状态：1-启用，2-禁用
     */
    private Integer status;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 创建者名称
     */
    private String creatorName;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 参与人数
     */
    private Integer participantCount;

    /**
     * 平均评分
     */
    private Double averageScore;

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
