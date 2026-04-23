package com.anti.fraud.modules.vr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * VR演练记录实体
 */
@Data
@TableName("vr_simulation_record")
public class VRSimulationRecord {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 演练ID
     */
    private Long simulationId;

    /**
     * 演练名称
     */
    private String simulationName;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 实际时长（分钟）
     */
    private Integer actualDuration;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 正确率
     */
    private Double correctRate;

    /**
     * 完成状态：1-未完成，2-已完成
     */
    private Integer completionStatus;

    /**
     * 操作记录（JSON格式）
     */
    private String operationRecord;

    /**
     * 评估结果（JSON格式）
     */
    private String evaluationResult;

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
