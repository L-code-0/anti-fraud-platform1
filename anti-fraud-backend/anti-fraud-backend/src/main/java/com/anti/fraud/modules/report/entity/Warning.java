package com.anti.fraud.modules.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预警实体
 */
@Data
@TableName("warning")
public class Warning {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String userId;
    private String warningType;
    private String warningContent;
    private String riskLevel;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
