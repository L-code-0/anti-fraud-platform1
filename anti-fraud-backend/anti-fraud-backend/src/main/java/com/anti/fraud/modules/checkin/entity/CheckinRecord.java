package com.anti.fraud.modules.checkin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 打卡记录实体
 */
@Data
@TableName("checkin_record")
public class CheckinRecord {

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
     * 打卡类型：1-每日打卡，2-学习打卡
     */
    private Integer checkinType;

    /**
     * 打卡日期
     */
    private LocalDateTime checkinDate;

    /**
     * 打卡时间
     */
    private LocalDateTime checkinTime;

    /**
     * 打卡状态：1-成功，2-失败
     */
    private Integer status;

    /**
     * 打卡地点（可选）
     */
    private String location;

    /**
     * 打卡IP
     */
    private String ipAddress;

    /**
     * 连续打卡天数
     */
    private Integer consecutiveDays;

    /**
     * 获得积分
     */
    private Integer points;

    /**
     * 备注
     */
    private String remark;

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
}
