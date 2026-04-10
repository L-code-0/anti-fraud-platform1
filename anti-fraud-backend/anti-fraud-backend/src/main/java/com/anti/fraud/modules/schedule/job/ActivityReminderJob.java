package com.anti.fraud.modules.schedule.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 活动相关定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityReminderJob {
    
    /**
     * 每天早上8点发送今日活动提醒
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendActivityReminder() {
        log.info("执行活动提醒任务");
        // TODO: 查询今日即将开始的活动，向已报名的用户发送提醒
    }
    
    /**
     * 每小时检查活动状态变化
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkActivityStatus() {
        log.debug("检查活动状态");
        // TODO: 检查活动是否已开始/结束，更新状态
    }
    
    /**
     * 活动开始前1小时发送提醒
     */
    @Scheduled(cron = "0 30 * * * ?")
    public void sendActivityStartReminder() {
        log.debug("检查即将开始的活动");
        // TODO: 查询1小时后开始的活动，发送提醒
    }
}
