package com.anti.fraud.modules.audit.service;

import com.anti.fraud.modules.audit.entity.AuditLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作审计日志服务接口
 */
public interface AuditLogService extends IService<AuditLog> {

    /**
     * 记录审计日志
     * @param auditLog 审计日志信息
     * @return 是否成功
     */
    boolean addAuditLog(AuditLog auditLog);

    /**
     * 批量记录审计日志
     * @param auditLogs 审计日志列表
     * @return 是否成功
     */
    boolean batchAddAuditLogs(List<AuditLog> auditLogs);

    /**
     * 删除审计日志
     * @param id 审计日志ID
     * @return 是否成功
     */
    boolean deleteAuditLog(Long id);

    /**
     * 清理指定时间之前的审计日志
     * @param beforeTime 清理时间
     * @return 清理数量
     */
    int cleanAuditLogs(LocalDateTime beforeTime);

    /**
     * 获取审计日志详情
     * @param id 审计日志ID
     * @return 审计日志详情
     */
    AuditLog getAuditLogById(Long id);

    /**
     * 分页查询审计日志
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 审计日志列表和总数
     */
    Map<String, Object> getAuditLogList(Map<String, Object> params, int page, int size);

    /**
     * 根据用户ID查询审计日志
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 审计日志列表和总数
     */
    Map<String, Object> getAuditLogsByUserId(Long userId, int page, int size);

    /**
     * 获取最近的审计日志
     * @param limit 数量限制
     * @return 最近的审计日志列表
     */
    List<AuditLog> getLatestAuditLogs(int limit);

    /**
     * 获取失败的审计日志
     * @param limit 数量限制
     * @return 失败的审计日志列表
     */
    List<AuditLog> getFailedAuditLogs(int limit);

    /**
     * 统计审计日志信息
     * @return 统计信息
     */
    Map<String, Object> getAuditLogStats();

    /**
     * 按日期统计审计日志
     * @param days 天数
     * @return 日期统计
     */
    List<Map<String, Object>> getDailyAuditLogStats(int days);

    /**
     * 记录登录日志
     * @param userId 用户ID
     * @param username 用户名
     * @param ipAddress IP地址
     * @param browserInfo 浏览器信息
     * @param deviceInfo 设备信息
     * @param result 登录结果
     * @param errorMessage 错误信息
     * @return 是否成功
     */
    boolean recordLoginLog(Long userId, String username, String ipAddress, String browserInfo, String deviceInfo, Integer result, String errorMessage);

    /**
     * 记录操作日志
     * @param userId 用户ID
     * @param username 用户名
     * @param operationType 操作类型
     * @param module 操作模块
     * @param operationContent 操作内容
     * @param ipAddress IP地址
     * @param browserInfo 浏览器信息
     * @param deviceInfo 设备信息
     * @param result 操作结果
     * @param errorMessage 错误信息
     * @return 是否成功
     */
    boolean recordOperationLog(Long userId, String username, Integer operationType, String module, String operationContent, String ipAddress, String browserInfo, String deviceInfo, Integer result, String errorMessage);
}
