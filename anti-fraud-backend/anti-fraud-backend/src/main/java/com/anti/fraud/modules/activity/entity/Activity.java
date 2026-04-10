package com.anti.fraud.modules.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动实体类
 * <p>
 * 对应数据库中的 activity_info 表，存储活动的基本信息。
 * 支持逻辑删除，使用 status 字段表示活动状态。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 * @see com.baomidou.mybatisplus.annotation.TableName
 */
@Data
@TableName("activity_info")
public class Activity {

    /**
     * 活动ID（主键，自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动类型
     * <p>
     * 用于分类不同类型的活动，如讲座、竞赛、宣传活动等。
     * 具体取值含义需参考业务文档或枚举定义。
     * </p>
     */
    private Integer activityType;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 最大参与人数
     * <p>
     * nullable字段，null表示不限制参与人数。
     * </p>
     */
    private Integer maxParticipants;

    /**
     * 当前参与人数
     */
    private Integer currentParticipants;

    /**
     * 积分奖励
     * <p>
     * 用户参与活动后可获得的积分奖励数量。
     * </p>
     */
    private Integer pointsReward;

    /**
     * 活动状态
     * <p>
     * 取值范围：
     * <ul>
     *   <li>1 - 报名中</li>
     *   <li>2 - 进行中</li>
     *   <li>3 - 已结束</li>
     * </ul>
     * </p>
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 逻辑删除标记
     * <p>
     * 使用Mybatis-Plus的逻辑删除功能。
     * 值为1表示已删除，0或null表示未删除。
     * </p>
     */
    @TableLogic
    private Integer deleted;
}
