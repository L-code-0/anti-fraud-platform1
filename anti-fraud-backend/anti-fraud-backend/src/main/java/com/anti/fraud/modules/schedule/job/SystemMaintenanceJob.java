package com.anti.fraud.modules.schedule.job;

import com.anti.fraud.modules.log.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 系统维护定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemMaintenanceJob {
    
    private final OperationLogService operationLogService;
    
    /**
     * 每天凌晨2点清理7天前的操作日志
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanOldLogs() {
        log.info("执行日志清理任务");
        try {
            int count = operationLogService.cleanOldLogs(7);
            log.info("清理 {} 天前的日志完成，删除 {} 条", 7, count);
        } catch (Exception e) {
            log.error("日志清理失败", e);
        }
    }
    
    /**
     * 每天凌晨3点统计昨日数据
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void generateDailyStats() {
        log.info("执行每日统计任务");
        // TODO: 统计昨日关键指标，生成日报
    }
    
    /**
     * 每月1日凌晨4点清理过期数据
     */
    @Scheduled(cron = "0 0 4 1 * ?")
    public void cleanExpiredData() {
        log.info("执行过期数据清理任务");
        // TODO: 清理过期通知、临时文件等
    }
}
