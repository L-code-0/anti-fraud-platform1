package com.anti.fraud.modules.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("report_info")
public class ReportInfo {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String reportNo;

    private Long userId;

    private Integer reportType;

    private String fraudType;

    private String title;

    private String description;

    private String phoneNumber;

    private String linkUrl;

    private String images;

    private String contactName;

    private String contactPhone;

    private Integer isAnonymous;

    private Integer status;

    private Integer riskLevel;

    private Long handlerId;

    private LocalDateTime handleTime;

    private String handleResult;

    private Integer rewardPoints;

    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}