package com.anti.fraud.modules.log.service.impl;

import com.anti.fraud.modules.log.entity.OperationLog;
import com.anti.fraud.modules.log.mapper.OperationLogMapper;
import com.anti.fraud.modules.log.service.OperationLogService;
import com.anti.fraud.common.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 操作日志服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {
    
    private final OperationLogMapper operationLogMapper;
    
    @Override
    public void recordLog(String actionType, String module, String description, 
                         String params, String result, boolean success) {
        // 从当前线程获取请求信息
        HttpServletRequest request = SecurityUtils.getCurrentRequest();
        recordLog(request, actionType, module, description, params, result, success);
    }
    
    @Override
    public void recordLog(HttpServletRequest request, String actionType, String module, 
                         String description, String params, String result, boolean success) {
        try {
            OperationLog log = new OperationLog();
            
            // 设置用户信息
            try {
                log.setUserId(SecurityUtils.getCurrentUserId());
                log.setUsername(SecurityUtils.getCurrentUsername());
            } catch (Exception e) {
                // 匿名用户或未登录用户
                log.setUserId(0L);
                log.setUsername("anonymous");
            }
            
            // 设置操作信息
            log.setActionType(actionType);
            log.setModule(module);
            log.setDescription(description);
            log.setParams(params);
            log.setResult(result);
            log.setSuccess(success);
            
            // 设置请求信息
            if (request != null) {
                log.setIpAddress(SecurityUtils.getClientIp(request));
                log.setUserAgent(request.getHeader("User-Agent"));
            }
            
            // 保存日志
            operationLogMapper.insert(log);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }
    
    @Override
    public IPage<OperationLog> getOperationLogs(int page, int size, String username, 
                                               String actionType, String startTime, String endTime) {
        IPage<OperationLog> pageInfo = new Page<>(page, size);
        return operationLogMapper.selectPage(pageInfo, username, actionType, startTime, endTime);
    }
    
    @Override
    public OperationLog getOperationLogById(Long id) {
        return operationLogMapper.selectById(id);
    }
    
    @Override
    public int cleanOldLogs(int days) {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minus(days, ChronoUnit.DAYS);
            // 这里可以实现删除旧日志的逻辑
            // 例如：operationLogMapper.deleteOldLogs(cutoffTime);
            return 0; // 暂时返回0，实际实现时需要根据删除的记录数返回
        } catch (Exception e) {
            log.error("清理旧日志失败", e);
            return 0;
        }
    }
}
