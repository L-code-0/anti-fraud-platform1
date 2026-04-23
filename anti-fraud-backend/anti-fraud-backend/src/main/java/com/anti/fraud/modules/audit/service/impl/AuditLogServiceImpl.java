package com.anti.fraud.modules.audit.service.impl;

import com.anti.fraud.modules.audit.entity.AuditLog;
import com.anti.fraud.modules.audit.mapper.AuditLogMapper;
import com.anti.fraud.modules.audit.service.AuditLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作审计日志服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements AuditLogService {

    private final AuditLogMapper auditLogMapper;

    @Override
    @Transactional
    public boolean addAuditLog(AuditLog auditLog) {
        try {
            auditLog.setOperationTime(LocalDateTime.now());
            auditLog.setStatus(1);
            auditLog.setDeleted(0);
            auditLog.setCreateTime(LocalDateTime.now());
            return save(auditLog);
        } catch (Exception e) {
            log.error("记录审计日志失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean batchAddAuditLogs(List<AuditLog> auditLogs) {
        try {
            for (AuditLog auditLog : auditLogs) {
                auditLog.setOperationTime(LocalDateTime.now());
                auditLog.setStatus(1);
                auditLog.setDeleted(0);
                auditLog.setCreateTime(LocalDateTime.now());
            }
            return saveBatch(auditLogs);
        } catch (Exception e) {
            log.error("批量记录审计日志失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteAuditLog(Long id) {
        try {
            AuditLog auditLog = getById(id);
            if (auditLog != null) {
                auditLog.setDeleted(1);
                return updateById(auditLog);
            }
            return false;
        } catch (Exception e) {
            log.error("删除审计日志失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public int cleanAuditLogs(LocalDateTime beforeTime) {
        try {
            return auditLogMapper.deleteBeforeTime(beforeTime);
        } catch (Exception e) {
            log.error("清理审计日志失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public AuditLog getAuditLogById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取审计日志详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getAuditLogList(Map<String, Object> params, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<AuditLog> auditLogs = auditLogMapper.selectByCondition(params, offset, size);
            int total = auditLogMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", auditLogs);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询审计日志列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getAuditLogsByUserId(Long userId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<AuditLog> auditLogs = auditLogMapper.selectByUserId(userId, offset, size);
            int total = auditLogMapper.selectCountByUserId(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("list", auditLogs);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据用户ID查询审计日志失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public List<AuditLog> getLatestAuditLogs(int limit) {
        try {
            return auditLogMapper.selectLatestLogs(limit);
        } catch (Exception e) {
            log.error("获取最近的审计日志失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<AuditLog> getFailedAuditLogs(int limit) {
        try {
            return auditLogMapper.selectFailedLogs(limit);
        } catch (Exception e) {
            log.error("获取失败的审计日志失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getAuditLogStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("operationTypeStats", auditLogMapper.selectOperationTypeStats());
            stats.put("moduleStats", auditLogMapper.selectModuleStats());
            stats.put("operationResultStats", auditLogMapper.selectOperationResultStats());
            stats.put("dailyStats", auditLogMapper.selectDailyStats(7));
            return stats;
        } catch (Exception e) {
            log.error("统计审计日志信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<Map<String, Object>> getDailyAuditLogStats(int days) {
        try {
            return auditLogMapper.selectDailyStats(days);
        } catch (Exception e) {
            log.error("按日期统计审计日志失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean recordLoginLog(Long userId, String username, String ipAddress, String browserInfo, String deviceInfo, Integer result, String errorMessage) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(userId);
            auditLog.setUsername(username);
            auditLog.setOperationType(1); // 登录
            auditLog.setModule("用户管理");
            auditLog.setOperationContent("用户登录");
            auditLog.setOperationResult(result);
            auditLog.setErrorMessage(errorMessage);
            auditLog.setIpAddress(ipAddress);
            auditLog.setBrowserInfo(browserInfo);
            auditLog.setDeviceInfo(deviceInfo);
            return addAuditLog(auditLog);
        } catch (Exception e) {
            log.error("记录登录日志失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean recordOperationLog(Long userId, String username, Integer operationType, String module, String operationContent, String ipAddress, String browserInfo, String deviceInfo, Integer result, String errorMessage) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(userId);
            auditLog.setUsername(username);
            auditLog.setOperationType(operationType);
            auditLog.setModule(module);
            auditLog.setOperationContent(operationContent);
            auditLog.setOperationResult(result);
            auditLog.setErrorMessage(errorMessage);
            auditLog.setIpAddress(ipAddress);
            auditLog.setBrowserInfo(browserInfo);
            auditLog.setDeviceInfo(deviceInfo);
            return addAuditLog(auditLog);
        } catch (Exception e) {
            log.error("记录操作日志失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
