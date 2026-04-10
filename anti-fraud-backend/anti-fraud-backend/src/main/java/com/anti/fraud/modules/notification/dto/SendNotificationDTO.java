package com.anti.fraud.modules.notification.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 发送通知DTO
 */
@Data
public class SendNotificationDTO {
    
    /**
     * 通知类型（1-系统通知 2-活动通知 3-任务通知 4-成绩通知 5-私信）
     */
    @NotNull(message = "通知类型不能为空")
    private Integer type;
    
    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空")
    private String title;
    
    /**
     * 通知内容
     */
    @NotBlank(message = "通知内容不能为空")
    private String content;
    
    /**
     * 接收者ID（单发）
     */
    private Long receiverId;
    
    /**
     * 接收者ID列表（群发）
     */
    private List<Long> receiverIds;
    
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
     * 过期时间（为空表示不过期）
     */
    private String expireTime;
}
