package com.anti.fraud.modules.warning.task;

import com.anti.fraud.modules.warning.service.ExternalWarningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 反诈预警信息同步定时任务
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WarningSyncTask {

    private final ExternalWarningService externalWarningService;

    /**
     * 每6小时同步一次反诈预警信息
     * 表达式含义：0 0 0/6 * * ?
     * 即：每6小时的0分0秒执行
     */
    @Scheduled(cron = "0 0 0/6 * * ?")
    public void syncExternalWarnings() {
        try {
            log.info("开始同步外部反诈预警信息");
            int syncCount = externalWarningService.syncExternalWarnings();
            log.info("同步外部反诈预警信息完成，共同步 {} 条", syncCount);
        } catch (Exception e) {
            log.error("同步外部反诈预警信息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每天凌晨1点清理过期的预警信息
     * 表达式含义：0 0 1 * * ?
     * 即：每天的1点0分0秒执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanExpiredWarnings() {
        try {
            log.info("开始清理过期的反诈预警信息");
            int cleanCount = externalWarningService.cleanExpiredWarnings();
            log.info("清理过期的反诈预警信息完成，共清理 {} 条", cleanCount);
        } catch (Exception e) {
            log.error("清理过期的反诈预警信息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 系统启动时同步一次反诈预警信息
     */
    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)
    public void syncOnStartup() {
        try {
            log.info("系统启动，开始同步外部反诈预警信息");
            int syncCount = externalWarningService.syncExternalWarnings();
            log.info("系统启动同步外部反诈预警信息完成，共同步 {} 条", syncCount);
        } catch (Exception e) {
            log.error("系统启动同步外部反诈预警信息失败: {}", e.getMessage(), e);
        }
    }
}
