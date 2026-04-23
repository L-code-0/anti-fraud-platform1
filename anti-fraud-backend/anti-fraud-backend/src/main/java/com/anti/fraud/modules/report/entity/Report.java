package com.anti.fraud.modules.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 举报实体
 */
@Data
@TableName("report")
public class Report {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String reporterName;
    private String reporterEmail;
    private String reporterPhone;
    private String fraudType;
    private String description;
    private String evidenceUrl;
    private String evidenceFileIds; // 存储证据文件ID，逗号分隔
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
