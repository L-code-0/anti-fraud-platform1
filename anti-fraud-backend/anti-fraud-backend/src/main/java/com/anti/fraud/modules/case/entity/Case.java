package com.anti.fraud.modules.case.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 案例实体
 */
@Data
@TableName("case_info")
public class Case {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 案例标题
     */
    private String title;

    /**
     * 案例类型：1-电信诈骗，2-网络诈骗，3-金融诈骗，4-冒充公检法诈骗，5-刷单诈骗，6-中奖诈骗，7-投资理财诈骗，8-婚恋诈骗
     */
    private Integer type;

    /**
     * 案例描述
     */
    private String description;

    /**
     * 案例详情（JSON格式）
     */
    private String details;

    /**
     * 案例图片
     */
    private String images;

    /**
     * 案例视频
     */
    private String videos;

    /**
     * 案例来源
     */
    private String source;

    /**
     * 案例发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 状态：1-启用，2-禁用
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
