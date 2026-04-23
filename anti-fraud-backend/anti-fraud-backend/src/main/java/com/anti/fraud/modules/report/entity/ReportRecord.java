package com.anti.fraud.modules.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 举报记录实体
 */
@Data
@TableName("report_record")
public class ReportRecord {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 举报用户ID
     */
    private Long userId;

    /**
     * 举报用户名称
     */
    private String username;

    /**
     * 举报类型：1-虚假信息，2-诈骗行为，3-不良内容，4-其他
     */
    private Integer reportType;

    /**
     * 举报对象ID
     */
    private Long targetId;

    /**
     * 举报对象类型：1-用户，2-案例，3-评论，4-其他
     */
    private Integer targetType;

    /**
     * 举报内容
     */
    private String reportContent;

    /**
     * 举报证据（JSON格式）
     */
    private String evidence;

    /**
     * 处理状态：1-待处理，2-已处理，3-已驳回
     */
    private Integer handleStatus;

    /**
     * 处理结果
     */
    private String handleResult;

    /**
     * 处理人
     */
    private String handler;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 积分奖励
     */
    private Integer rewardPoints;

    /**
     * 状态：1-正常，2-异常
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
