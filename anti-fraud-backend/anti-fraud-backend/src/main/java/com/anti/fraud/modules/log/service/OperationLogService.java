package com.anti.fraud.modules.log.service;

import com.anti.fraud.modules.log.entity.OperationLog;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {
    
    /**
     * 保存操作日志
     * @param log 日志信息
     */
    void saveLog(OperationLog log);
    
    /**
     * 异步保存操作日志
     * @param log 日志信息
     */
    void saveLogAsync(OperationLog log);
    
    /**
     * 分页查询操作日志
     * @param page 页码
     * @param size 每页数量
     * @param userId 用户ID
     * @param operationType 操作类型
     * @param moduleName 模块名称
     * @param keyword 关键词
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志分页列表
     */
    IPage<OperationLog> getLogPage(Integer page, Integer size, Long userId, String operationType,
                                   String moduleName, String keyword, String startTime, String endTime);
    
    /**
     * 获取日志详情
     * @param id 日志ID
     * @return 日志详情
     */
    OperationLog getLogById(Long id);
    
    /**
     * 删除日志
     * @param id 日志ID
     */
    void deleteLog(Long id);
    
    /**
     * 批量删除日志
     * @param ids 日志ID列表
     */
    void deleteLogs(Long[] ids);
    
    /**
     * 清理N天前的日志
     * @param days 天数
     * @return 删除数量
     */
    int cleanOldLogs(int days);
    
    /**
     * 获取操作统计
     * @param days 统计天数
     * @return 统计数据
     */
    Object getOperationStats(int days);
}
