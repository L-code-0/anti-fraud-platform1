package com.anti.fraud.modules.works.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 作品实体
 */
@Data
@TableName("works")
public class Works {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String worksType; // ESSAY-征文, VIDEO-短视频
    private Long activityId;
    private String activityName;
    private Long authorId;
    private String authorName;
    private String authorPhone;
    private String department;
    private String content; // 征文内容
    private String fileUrl; // 文件/视频URL
    private String coverImage;
    private String description;
    private Integer status; // 0-待审核, 1-已通过, 2-已拒绝
    private String auditRemark;
    private Long auditorId;
    private String auditorName;
    private LocalDateTime auditTime;
    private Integer viewCount;
    private Integer likeCount;
    private Integer isExcellent; // 是否优秀作品
    private Integer rank; // 排名
    private Integer points; // 奖励积分
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}