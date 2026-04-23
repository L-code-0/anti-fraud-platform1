package com.anti.fraud.modules.audit.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作审计日志实体
 */
@Data
@TableName("audit_log")
public class AuditLog {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作人ID
     */
    private Long userId;

    /**
     * 操作人名称
     */
    private String username;

    /**
     * 操作类型：1-登录，2-登出，3-新增，4-修改，5-删除，6-查询，7-其他
     */
    private Integer operationType;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作内容
     */
    private String operationContent;

    /**
     * 操作结果：1-成功，2-失败
     */
    private Integer operationResult;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 操作IP
     */
    private String ipAddress;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

    /**
     * 浏览器信息
     */
    private String browserInfo;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 状态：1-正常，2-异常
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 逻辑删除
     */
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private Integer deleted;
}
