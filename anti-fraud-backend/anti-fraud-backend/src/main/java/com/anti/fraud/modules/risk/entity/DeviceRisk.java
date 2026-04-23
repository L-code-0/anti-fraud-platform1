package com.anti.fraud.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备风险实体
 */
@Data
@TableName("device_risk")
public class DeviceRisk {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 风险ID（UUID）
     */
    private String riskId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备类型：1-手机，2-平板，3-电脑，4-其他
     */
    private Integer deviceType;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备信息（JSON格式）
     */
    private String deviceInfo;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 地理位置（JSON格式）
     */
    private String location;

    /**
     * 风险分数（0-100）
     */
    private Double riskScore;

    /**
     * 风险等级：1-低风险，2-中风险，3-高风险，4-极高风险
     */
    private Integer riskLevel;

    /**
     * 风险详情（JSON格式）
     */
    private String riskDetails;

    /**
     * 最后使用时间
     */
    private LocalDateTime lastUsedTime;

    /**
     * 状态：1-已分析，2-分析中，3-分析失败
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
