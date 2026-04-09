package com.anti.fraud.modules.knowledge.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_content")
public class KnowledgeContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private Long categoryId;

    private Integer contentType;

    private String summary;

    private String content;

    private String coverImage;

    private String videoUrl;

    private Integer videoDuration;

    private String tags;

    private Long authorId;

    private String authorName;

    private String source;

    private Integer viewCount;

    private Integer likeCount;

    private Integer collectCount;

    private Integer shareCount;

    private Integer isTop;

    private Integer isHot;

    private Integer isRecommend;

    private Integer status;

    private Integer auditStatus;

    private Long auditUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;

    private String auditRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
