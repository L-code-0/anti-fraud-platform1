package com.anti.fraud.modules.fraudcase.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fraud_case")
public class FraudCase implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("case_title")
    private String caseTitle;

    @TableField("case_type")
    private String caseType;

    @TableField("occur_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime occurTime;

    @TableField("victim_profile")
    private String victimProfile;

    @TableField("fraud_process")
    private String fraudProcess;

    @TableField("loss_amount")
    private BigDecimal lossAmount;

    @TableField("recovery_amount")
    private BigDecimal recoveryAmount;

    @TableField("case_status")
    private String caseStatus;

    @TableField("police_info")
    private String policeInfo;

    @TableField("warning_level")
    private Integer warningLevel;

    @TableField("prevention_advice")
    private String preventionAdvice;

    private String source;

    @TableField("is_anonymous")
    private Integer isAnonymous;

    @TableField("is_verified")
    private Integer isVerified;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("share_count")
    private Integer shareCount;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}