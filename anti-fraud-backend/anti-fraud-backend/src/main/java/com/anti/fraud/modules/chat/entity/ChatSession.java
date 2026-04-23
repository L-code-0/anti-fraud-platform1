package com.anti.fraud.modules.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 对话会话实体
 */
@Data
@TableName("chat_session")
public class ChatSession {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID（UUID）
     */
    private String sessionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 对话标题
     */
    private String title;

    /**
     * 对话类型：1-普通对话，2-反诈咨询，3-学习指导
     */
    private Integer type;

    /**
     * 对话状态：1-活跃，2-已结束
     */
    private Integer status;

    /**
     * 最后交互时间
     */
    private LocalDateTime lastInteractionTime;

    /**
     * 对话历史（JSON格式）
     */
    private String chatHistory;

    /**
     * 对话上下文（JSON格式）
     */
    private String context;

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
