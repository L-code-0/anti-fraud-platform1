package com.anti.fraud.modules.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识图谱边实体
 */
@Data
@TableName("knowledge_edge")
public class KnowledgeEdge {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 边ID（UUID）
     */
    private String edgeId;

    /**
     * 源节点ID
     */
    private String sourceNodeId;

    /**
     * 目标节点ID
     */
    private String targetNodeId;

    /**
     * 边类型：1-属于，2-包含，3-关联，4-推荐，5-其他
     */
    private Integer type;

    /**
     * 边名称
     */
    private String name;

    /**
     * 边描述
     */
    private String description;

    /**
     * 权重
     */
    private Double weight;

    /**
     * 状态：1-启用，2-禁用
     */
    private Integer status;

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
