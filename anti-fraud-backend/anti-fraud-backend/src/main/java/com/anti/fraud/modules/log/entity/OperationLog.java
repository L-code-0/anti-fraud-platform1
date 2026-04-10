package com.anti.fraud.modules.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("sys_operation_log")
public class OperationLog {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 操作类型（INSERT/UPDATE/DELETE/SELECT/LOGIN/LOGOUT/EXPORT/OTHER）
     */
    private String operationType;
    
    /**
     * 请求方法
     */
    private String requestMethod;
    
    /**
     * 请求URL
     */
    private String requestUrl;
    
    /**
     * 请求参数
     */
    private String requestParams;
    
    /**
     * 响应结果
     */
    private String responseResult;
    
    /**
     * 响应状态（0-失败 1-成功）
     */
    private Integer responseStatus;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 执行时长（毫秒）
     */
    private Long executionTime;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 用户IP
     */
    private String ip;
    
    /**
     * 操作地点
     */
    private String location;
    
    /**
     * 浏览器
     */
    private String browser;
    
    /**
     * 操作系统
     */
    private String os;
    
    /**
     * 操作描述
     */
    private String description;
    
    /**
     * 模块名称
     */
    private String moduleName;
    
    /**
     * 业务类型
     */
    private String bizType;
    
    /**
     * 业务ID
     */
    private Long bizId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
