package com.anti.fraud.modules.warning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 智能预警警报实体
 */
@Data
@TableName("warning_alert")
public class WarningAlert {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 风险等级：1-高风险 2-中风险 3-低风险
     */
    private Integer riskLevel;

    /**
     * 预警类型：1-电信诈骗 2-网络诈骗 3-校园贷 4-兼职诈骗
     */
    private Integer warningType;

    /**
     * 预警内容
     */
    private String content;

    /**
     * 风险分数 0-100
     */
    private Integer riskScore;

    /**
     * 风险分析
     */
    private String analysis;

    /**
     * 建议措施
     */
    private String suggestion;

    /**
     * 相关用户ID
     */
    private Long relatedUserId;

    /**
     * 相关用户名
     */
    private String relatedUserName;

    /**
     * 相关知识标签（逗号分隔）
     */
    private String relatedKnowledge;

    /**
     * 预警来源：1-系统自动 2-人工上报
     */
    private Integer source;

    /**
     * 处理状态：0-待处理 1-已处理 2-误报
     */
    private Integer status;

    /**
     * 处理结果
     */
    private String processResult;

    /**
     * 处理人ID
     */
    private Long processedBy;

    /**
     * 处理时间
     */
    private LocalDateTime processedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}

