package com.anti.fraud.modules.log.entity;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String username;
    private String actionType; // create, update, delete, login, logout, export
    private String module;
    private String description;
    private String ipAddress;
    private String userAgent;
    private String params; // JSON格式的请求参数
    private String result; // 操作结果
    private Boolean success; // 是否成功
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
