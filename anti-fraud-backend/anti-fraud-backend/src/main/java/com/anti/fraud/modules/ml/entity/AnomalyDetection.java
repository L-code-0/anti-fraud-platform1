package com.anti.fraud.modules.ml.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 异常检测实体
 */
@Data
@TableName("anomaly_detection")
public class AnomalyDetection {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 检测ID（UUID）
     */
    private String detectionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 检测类型：1-行为异常，2-设备异常，3-社交异常，4-综合异常
     */
    private Integer detectionType;

    /**
     * 检测方法：1-Isolation Forest，2-LOF，3-One-Class SVM，4-其他
     */
    private Integer detectionMethod;

    /**
     * 特征数据（JSON格式）
     */
    private String featureData;

    /**
     * 异常分数（0-1）
     */
    private Double anomalyScore;

    /**
     * 异常等级：1-正常，2-轻微异常，3-中度异常，4-严重异常
     */
    private Integer anomalyLevel;

    /**
     * 是否为异常
     */
    private Boolean isAnomaly;

    /**
     * 置信度（0-1）
     */
    private Double confidence;

    /**
     * 检测结果（JSON格式）
     */
    private String detectionResult;

    /**
     * 检测时间
     */
    private LocalDateTime detectionTime;

    /**
     * 状态：1-已检测，2-检测中，3-检测失败
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;

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
