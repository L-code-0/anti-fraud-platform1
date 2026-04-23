package com.anti.fraud.modules.offline.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 离线下载实体
 */
@Data
@TableName("offline_download")
public class OfflineDownload {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源类型：1-知识文章，2-视频，3-音频，4-文档，5-其他
     */
    private Integer resourceType;

    /**
     * 资源路径
     */
    private String resourcePath;

    /**
     * 存储路径
     */
    private String storagePath;

    /**
     * 下载状态：1-待下载，2-下载中，3-已完成，4-下载失败
     */
    private Integer status;

    /**
     * 下载进度（0-100）
     */
    private Integer progress;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 已下载大小（字节）
     */
    private Long downloadedSize;

    /**
     * 下载开始时间
     */
    private LocalDateTime startTime;

    /**
     * 下载完成时间
     */
    private LocalDateTime endTime;

    /**
     * 失败原因
     */
    private String failureReason;

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
