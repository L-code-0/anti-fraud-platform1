package com.anti.fraud.modules.websocket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * WebSocket消息存储实体
 */
@Data
@TableName("websocket_message_store")
public class WebSocketMessageStore {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 消息类型：1-心跳消息，2-预警消息，3-连接消息，4-订阅消息，5-协作消息，6-错误消息
     */
    private Integer messageType;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者名称
     */
    private String senderName;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 接收者名称
     */
    private String receiverName;

    /**
     * 消息内容（二进制数据，存储Protobuf序列化后的字节）
     */
    private byte[] messageContent;

    /**
     * 消息状态：1-已发送，2-未发送，3-发送失败
     */
    private Integer status;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 接收时间
     */
    private LocalDateTime receiveTime;

    /**
     * 重试次数
     */
    private Integer retryCount;

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
