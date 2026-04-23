package com.anti.fraud.modules.replay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 案例回放实体
 */
@Data
@TableName("case_replay")
public class CaseReplay {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 案例ID
     */
    private Long caseId;

    /**
     * 案例标题
     */
    private String caseTitle;

    /**
     * 回放标题
     */
    private String replayTitle;

    /**
     * 回放描述
     */
    private String replayDescription;

    /**
     * 回放类型：1-文字回放，2-语音回放，3-视频回放，4-互动回放
     */
    private Integer replayType;

    /**
     * 回放内容（JSON格式）
     */
    private String replayContent;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 时长（秒）
     */
    private Integer duration;

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
