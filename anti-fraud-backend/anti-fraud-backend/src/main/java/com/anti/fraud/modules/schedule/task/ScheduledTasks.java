package com.anti.fraud.modules.schedule.task;

import com.anti.fraud.modules.log.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 定时任务
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledTasks {
    
    private final OperationLogService operationLogService;
    
    /**
     * 每日数据统计任务
     * 每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void dailyDataStatistics() {
        log.info("开始执行每日数据统计任务: {}", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        try {
            // 这里可以实现数据统计逻辑
            // 例如：统计用户注册数、举报数、知识访问量等
            
            log.info("每日数据统计任务执行完成");
        } catch (Exception e) {
            log.error("每日数据统计任务执行失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 每周日志清理任务
     * 每周日凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 ? * SUN")
    public void weeklyLogCleanup() {
        log.info("开始执行每周日志清理任务: {}", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        try {
            // 清理30天前的操作日志
            int count = operationLogService.cleanOldLogs(30);
            log.info("清理了 {} 条旧日志", count);
        } catch (Exception e) {
            log.error("每周日志清理任务执行失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 每月数据备份任务
     * 每月1号凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    public void monthlyDataBackup() {
        log.info("开始执行每月数据备份任务: {}", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        try {
            // 这里可以实现数据备份逻辑
            // 例如：备份数据库、导出重要数据等
            
            log.info("每月数据备份任务执行完成");
        } catch (Exception e) {
            log.error("每月数据备份任务执行失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 每10分钟检查系统状态
     */
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void checkSystemStatus() {
        log.debug("执行系统状态检查: {}", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        try {
            // 这里可以实现系统状态检查逻辑
            // 例如：检查数据库连接、Redis连接等
            
        } catch (Exception e) {
            log.error("系统状态检查失败: {}", e.getMessage(), e);
        }
    }
}
