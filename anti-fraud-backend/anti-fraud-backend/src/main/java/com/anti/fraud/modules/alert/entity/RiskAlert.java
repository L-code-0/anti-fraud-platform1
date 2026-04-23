package com.anti.fraud.modules.alert.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 风险预警实体
 */
@Data
@TableName("risk_alert")
public class RiskAlert {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 预警ID（UUID）
     */
    private String alertId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 预警类型：1-行为风险预警，2-设备风险预警，3-社交风险预警，4-综合风险预警
     */
    private Integer alertType;

    /**
     * 风险等级：1-低风险，2-中风险，3-高风险，4-极高风险
     */
    private Integer riskLevel;

    /**
     * 预警标题
     */
    private String alertTitle;

    /**
     * 预警内容
     */
    private String alertContent;

    /**
     * 预警详情（JSON格式）
     */
    private String alertDetails;

    /**
     * 通知方式：1-WebSocket，2-短信，3-邮件，4-全部
     */
    private Integer notificationMethod;

    /**
     * WebSocket状态：1-已推送，2-推送失败，3-未推送
     */
    private Integer webSocketStatus;

    /**
     * 短信状态：1-已发送，2-发送失败，3-未发送
     */
    private Integer smsStatus;

    /**
     * 邮件状态：1-已发送，2-发送失败，3-未发送
     */
    private Integer emailStatus;

    /**
     * 接收人手机号
     */
    private String phoneNumber;

    /**
     * 接收人邮箱
     */
    private String email;

    /**
     * 预警时间
     */
    private LocalDateTime alertTime;

    /**
     * 状态：1-已处理，2-处理中，3-处理失败
     */
    private Integer status;

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
