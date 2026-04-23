package com.anti.fraud.modules.exercise.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 演练事件实体
 */
@Data
@TableName("exercise_event")
public class ExerciseEvent {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 事件ID（UUID）
     */
    private String eventId;

    /**
     * 演练ID
     */
    private String exerciseId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 事件类型：1-开始演练，2-结束演练，3-选择答案，4-查看解析，5-获得分数，6-其他
     */
    private Integer eventType;

    /**
     * 事件内容（JSON格式）
     */
    private String content;

    /**
     * 事件时间
     */
    private LocalDateTime eventTime;

    /**
     * 事件状态：1-已处理，2-处理中，3-处理失败
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
