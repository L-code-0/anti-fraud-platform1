package com.anti.fraud.modules.log.service;

import com.anti.fraud.modules.log.entity.OperationLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 操作日志服务
 */
public interface OperationLogService {
    
    /**
     * 记录操作日志
     * @param actionType 操作类型
     * @param module 操作模块
     * @param description 操作描述
     * @param params 请求参数
     * @param result 操作结果
     * @param success 是否成功
     */
    void recordLog(String actionType, String module, String description, 
                  String params, String result, boolean success);
    
    /**
     * 记录操作日志（自动获取请求信息）
     * @param request HTTP请求
     * @param actionType 操作类型
     * @param module 操作模块
     * @param description 操作描述
     * @param params 请求参数
     * @param result 操作结果
     * @param success 是否成功
     */
    void recordLog(HttpServletRequest request, String actionType, String module, 
                  String description, String params, String result, boolean success);
    
    /**
     * 分页查询操作日志
     * @param page 页码
     * @param size 每页大小
     * @param username 用户名
     * @param actionType 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志列表
     */
    IPage<OperationLog> getOperationLogs(int page, int size, String username, 
                                        String actionType, String startTime, String endTime);
    
    /**
     * 根据ID获取操作日志详情
     * @param id 日志ID
     * @return 操作日志
     */
    OperationLog getOperationLogById(Long id);
    
    /**
     * 清理指定时间之前的操作日志
     * @param days 天数
     * @return 清理的日志数量
     */
    int cleanOldLogs(int days);
}
