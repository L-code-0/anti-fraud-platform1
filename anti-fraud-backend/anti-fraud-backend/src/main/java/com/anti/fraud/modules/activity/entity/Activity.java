package com.anti.fraud.modules.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity_info")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String activityName;

    private Integer activityType;

    private String coverImage;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer maxParticipants;

    private Integer currentParticipants;

    private Integer pointsReward;

    private Integer status;

    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}