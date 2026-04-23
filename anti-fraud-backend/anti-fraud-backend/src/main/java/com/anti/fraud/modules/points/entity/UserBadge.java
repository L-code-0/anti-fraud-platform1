package com.anti.fraud.modules.points.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户勋章实体类
 */
@Data
@TableName("user_badge")
public class UserBadge {

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
     * 勋章ID
     */
    private Long badgeId;

    /**
     * 获得时间
     */
    private LocalDateTime acquireTime;

    /**
     * 获得原因
     */
    private String acquireReason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
