package com.anti.fraud.modules.warning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 外部反诈预警信息实体类
 */
@Data
@TableName("external_warning")
public class ExternalWarning {

    /**
     * 预警ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 预警标题
     */
    private String title;

    /**
     * 预警内容
     */
    private String content;

    /**
     * 预警来源（如：公安部、国家反诈中心等）
     */
    private String source;

    /**
     * 预警类型：1-电信诈骗，2-网络诈骗，3-金融诈骗，4-其他
     */
    private Integer warningType;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 同步时间
     */
    private LocalDateTime syncTime;

    /**
     * 预警URL
     */
    private String url;

    /**
     * 状态：1-有效，2-无效
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
