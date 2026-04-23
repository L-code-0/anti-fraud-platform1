package com.anti.fraud.modules.multimedia.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 多媒体资源实体
 */
@Data
@TableName("multimedia_resource")
public class MultimediaResource {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资源标题
     */
    private String title;

    /**
     * 资源类型：1-视频，2-音频，3-图片，4-文档
     */
    private Integer type;

    /**
     * 资源URL
     */
    private String url;

    /**
     * 资源封面
     */
    private String cover;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 资源时长（秒）
     */
    private Integer duration;

    /**
     * 资源大小（字节）
     */
    private Long size;

    /**
     * 资源格式
     */
    private String format;

    /**
     * 知识点
     */
    private String knowledgePoint;

    /**
     * 标签
     */
    private String tags;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

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
