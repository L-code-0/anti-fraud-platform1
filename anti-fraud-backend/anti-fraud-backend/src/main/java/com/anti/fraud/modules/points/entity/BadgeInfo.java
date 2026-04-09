package com.anti.fraud.modules.points.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("badge_info")
public class BadgeInfo {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String badgeName;

    private Integer badgeType;

    private String badgeIcon;

    private String description;

    private Integer pointsReward;

    private Integer status;

    private LocalDateTime createTime;
}
