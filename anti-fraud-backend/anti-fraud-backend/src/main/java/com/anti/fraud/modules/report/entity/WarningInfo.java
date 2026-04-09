package com.anti.fraud.modules.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("warning_info")
public class WarningInfo {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private Integer warningLevel;

    private String fraudType;

    private String content;

    private String preventionTips;

    private Integer viewCount;

    private Integer status;

    private Long publisherId;

    private LocalDateTime publishTime;

    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
