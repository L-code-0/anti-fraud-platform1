package com.anti.fraud.modules.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对话消息实体
 */
@Data
@TableName("chat_message")
public class ChatMessage {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 消息ID（UUID）
     */
    private String messageId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 发送者类型：1-用户，2-AI
     */
    private Integer senderType;

    /**
     * 消息类型：1-文本，2-图片，3-语音，4-视频
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息状态：1-已发送，2-已读，3-已删除
     */
    private Integer status;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

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
