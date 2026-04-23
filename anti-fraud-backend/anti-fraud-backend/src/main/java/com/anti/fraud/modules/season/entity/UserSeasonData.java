package com.anti.fraud.modules.season.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户赛季数据实体类
 */
@Data
@TableName("user_season_data")
public class UserSeasonData {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 赛季ID
     */
    private Long seasonId;

    /**
     * 赛季积分
     */
    private Integer seasonPoints;

    /**
     * 赛季排名
     */
    private Integer seasonRank;

    /**
     * 完成任务数
     */
    private Integer completedTasks;

    /**
     * 获得勋章数
     */
    private Integer acquiredBadges;

    /**
     * 赛季皮肤ID
     */
    private Long skinId;

    /**
     * 赛季称号ID
     */
    private Long titleId;

    /**
     * 赛季状态：1-进行中，2-已结算
     */
    private Integer status;

    /**
     * 结算时间
     */
    private LocalDateTime settleTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
