package com.anti.fraud.modules.notification.entity;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知实体
 */
@Data
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String type; // system, learning, interaction, activity, warning
    private String title;
    private String content;
    private String icon;
    private String action;
    private String actionText;
    private String extra; // JSON格式的额外信息
    private Boolean read = false;
    private Boolean deleted = false;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
