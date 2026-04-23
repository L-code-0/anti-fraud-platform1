package com.anti.fraud.modules.irt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * IRT模型实体
 */
@Data
@TableName("irt_model")
public class IrtModel {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模型ID（UUID）
     */
    private String modelId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型类型：1-1PL，2-2PL，3-3PL
     */
    private Integer modelType;

    /**
     * 模型参数（JSON格式）
     */
    private String modelParams;

    /**
     * 模型状态：1-启用，2-禁用，3-待训练
     */
    private Integer status;

    /**
     * 训练数据量
     */
    private Integer trainingDataSize;

    /**
     * 训练精度
     */
    private Double trainingAccuracy;

    /**
     * 训练时间
     */
    private LocalDateTime trainingTime;

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
