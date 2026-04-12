package com.anti.fraud.modules.fraudcase.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("fraud_case")
public class FraudCase implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private String category;

    private String difficulty;

    private String caseType;

    private String videoUrl;

    private String audioUrl;

    private String imageUrl;

    private String caseData;

    private Integer duration;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private String keywords;

    private Integer status;

    private String createdBy;

    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}