package com.anti.fraud.modules.fraudcase.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("fraud_script")
public class FraudScript implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("script_title")
    private String scriptTitle;

    @TableField("fraud_type")
    private String fraudType;

    @TableField("script_content")
    private String scriptContent;

    @TableField("target_group")
    private String targetGroup;

    @TableField("common_responses")
    private String commonResponses;

    @TableField("warning_signs")
    private String warningSigns;

    @TableField("prevention_tips")
    private String preventionTips;

    private String source;

    private String version;

    @TableField("is_verified")
    private Integer isVerified;

    @TableField("usage_count")
    private Integer usageCount;

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