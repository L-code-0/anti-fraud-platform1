package com.anti.fraud.modules.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识图谱节点实体
 */
@Data
@TableName("knowledge_node")
public class KnowledgeNode {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 节点ID（UUID）
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点类型：1-诈骗类型，2-防范措施，3-案例，4-知识，5-其他
     */
    private Integer type;

    /**
     * 节点描述
     */
    private String description;

    /**
     * 关联ID（如诈骗类型ID、案例ID等）
     */
    private Long relatedId;

    /**
     * 节点属性（JSON格式）
     */
    private String properties;

    /**
     * 热度
     */
    private Integer hotness;

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
