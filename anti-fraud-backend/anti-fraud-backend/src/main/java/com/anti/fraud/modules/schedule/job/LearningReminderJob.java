package com.anti.fraud.modules.schedule.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 学习提醒定时任务
 */
@Slf4j
@Component
public class LearningReminderJob {
    
    /**
     * 每天早上9点发送学习提醒
     * 学习连续中断3天的用户发送提醒
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyLearningReminder() {
        log.info("执行每日学习提醒任务");
        // TODO: 查询学习连续中断3天的用户，发送提醒
        // notificationService.sendSystemNotification("学习提醒", "您已连续3天未学习，请及时登录学习！", userId);
    }
    
    /**
     * 每周一早上9点发送周学习报告
     */
    @Scheduled(cron = "0 0 9 ? * MON")
    public void sendWeeklyReport() {
        log.info("执行周学习报告任务");
        // TODO: 统计用户本周学习情况，发送报告
    }
}
