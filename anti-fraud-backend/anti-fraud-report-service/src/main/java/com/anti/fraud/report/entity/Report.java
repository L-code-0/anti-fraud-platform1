package com.anti.fraud.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 举报实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("report")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 举报ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 举报编号
     */
    private String reportNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 诈骗类型：phone(电话诈骗)、online(网络诈骗)、sms(短信诈骗)、social(社交诈骗)、other(其他)
     */
    private String type;

    /**
     * 诈骗方式
     */
    private String method;

    /**
     * 发生时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime occurrenceTime;

    /**
     * 诈骗金额
     */
    private BigDecimal amount;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 证据图片URL，多个以逗号分隔
     */
    private String evidenceUrls;

    /**
     * 处理状态：pending(待处理)、processing(处理中)、resolved(已处理)、rejected(已驳回)
     */
    private String status;

    /**
     * 处理结果
     */
    private String processingResult;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processingTime;

    /**
     * 智能分类结果
     */
    private String autoClassification;

    /**
     * 分类置信度
     */
    private Double classificationConfidence;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 删除标志：0-未删除，1-已删除
     */
    private Integer deleted;
}
