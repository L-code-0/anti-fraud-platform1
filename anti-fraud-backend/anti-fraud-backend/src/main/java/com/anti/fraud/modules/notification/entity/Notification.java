package com.anti.fraud.modules.notification.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知记录实体
 */
@Data
@TableName("notification")
public class Notification {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 通知类型：1-系统通知，2-学习通知，3-演练通知，4-举报通知，5-风险通知，6-其他
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
     * 通知方式：1-站内信，2-短信，3-邮件，4-微信
     */
    private Integer method;

    /**
     * 发送状态：1-待发送，2-已发送，3-发送失败
     */
    private Integer status;

    /**
     * 阅读状态：1-未读，2-已读
     */
    private Integer readStatus;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 失败原因
     */
    private String failureReason;

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
