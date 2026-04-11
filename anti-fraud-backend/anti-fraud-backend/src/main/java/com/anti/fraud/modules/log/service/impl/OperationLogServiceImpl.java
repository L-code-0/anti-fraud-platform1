package com.anti.fraud.modules.log.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.modules.log.entity.OperationLog;
import com.anti.fraud.modules.log.mapper.OperationLogMapper;
import com.anti.fraud.modules.log.service.OperationLogService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作日志服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(OperationLog operationLog) {
        if (operationLog.getCreateTime() == null) {
            operationLog.setCreateTime(LocalDateTime.now());
        }
        operationLogMapper.insert(operationLog);
        log.debug("保存操作日志: {}", operationLog.getRequestUrl());
    }

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLogAsync(OperationLog operationLog) {
        saveLog(operationLog);
    }

    @Override
    public IPage<OperationLog> getLogPage(Integer page, Integer size, Long userId, String operationType,
                                          String moduleName, String keyword, String startTime, String endTime) {
        Page<OperationLog> pageParam = new Page<>(page, size);
        return operationLogMapper.selectLogPage(pageParam, userId, operationType, moduleName, keyword, startTime, endTime);
    }

    @Override
    public OperationLog getLogById(Long id) {
        OperationLog operationLog = operationLogMapper.selectById(id);
        if (operationLog == null) {
            throw new BusinessException("日志不存在");
        }
        return operationLog;
    }

    @Override
    @Transactional
    public void deleteLog(Long id) {
        operationLogMapper.deleteById(id);
        log.info("删除操作日志: {}", id);
    }

    @Override
    @Transactional
    public void deleteLogs(Long[] ids) {
        operationLogMapper.deleteByIds(List.of(ids));
        log.info("批量删除操作日志: {}", ids.length);
    }

    @Override
    @Transactional
    public int cleanOldLogs(int days) {
        int count = operationLogMapper.deleteBeforeDays(days);
        log.info("清理 {} 天前的日志: {} 条", days, count);
        return count;
    }

    @Override
    public Object getOperationStats(int days) {
        Map<String, Object> stats = new HashMap<>();

        // 统计各类型操作数量
        stats.put("login", operationLogMapper.countByOperationType("LOGIN"));
        stats.put("insert", operationLogMapper.countByOperationType("INSERT"));
        stats.put("update", operationLogMapper.countByOperationType("UPDATE"));
        stats.put("delete", operationLogMapper.countByOperationType("DELETE"));
        stats.put("export", operationLogMapper.countByOperationType("EXPORT"));

        return stats;
    }
}
