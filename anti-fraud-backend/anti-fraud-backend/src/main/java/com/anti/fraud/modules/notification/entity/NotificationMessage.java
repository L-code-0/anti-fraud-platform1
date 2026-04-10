package com.anti.fraud.modules.notification.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知消息实体
 */
@Data
@TableName("notification_message")
public class NotificationMessage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 通知类型（1-系统通知 2-活动通知 3-任务通知 4-成绩通知 5-私信）
     */
    private Integer type;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
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
     * 关联业务类型
     */
    private String bizType;
    
    /**
     * 关联业务ID
     */
    private Long bizId;
    
    /**
     * 消息优先级（1-低 2-中 3-高）
     */
    private Integer priority;
    
    /**
     * 是否已读（0-未读 1-已读）
     */
    private Integer isRead;
    
    /**
     * 阅读时间
     */
    private LocalDateTime readTime;
    
    /**
     * 消息状态（0-草稿 1-已发送 2-已撤回）
     */
    private Integer status;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    
    /**
     * 过期时间（为空表示不过期）
     */
    private LocalDateTime expireTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
