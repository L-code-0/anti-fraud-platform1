package com.anti.fraud.modules.export.entity;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 导出任务实体
 */
@Data
@TableName("export_task")
public class ExportTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String taskType;
    private String params; // JSON格式的参数
    private String fileName;
    private String filePath;
    private Integer status; // 0-等待中 1-处理中 2-完成 3-失败
    private Integer progress; // 进度百分比
    private Integer totalCount; // 总数据量
    private Integer processedCount; // 已处理数据量
    private String errorMessage;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
