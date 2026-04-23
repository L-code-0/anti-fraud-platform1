package com.anti.fraud.modules.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习路径节点实体
 */
@Data
@TableName("learning_path_node")
public class LearningPathNode {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学习路径ID
     */
    private Long pathId;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点类型：1-知识学习，2-视频学习，3-练习测试，4-实战演练
     */
    private Integer nodeType;

    /**
     * 资源ID（根据节点类型对应不同的资源）
     */
    private Long resourceId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 节点顺序
     */
    private Integer nodeOrder;

    /**
     * 预估时长（分钟）
     */
    private Integer estimatedDuration;

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
