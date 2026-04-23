package com.anti.fraud.modules.schedule.job;

import com.anti.fraud.modules.activity.entity.Activity;
import com.anti.fraud.modules.activity.entity.ActivityRegistration;
import com.anti.fraud.modules.activity.mapper.ActivityMapper;
import com.anti.fraud.modules.activity.mapper.ActivityRegistrationMapper;
import com.anti.fraud.modules.notification.entity.Notification;
import com.anti.fraud.modules.notification.service.NotificationService;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 活动相关定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityReminderJob {
    
    private final ActivityMapper activityMapper;
    private final ActivityRegistrationMapper registrationMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;
    
    /**
     * 每天早上8点发送今日活动提醒
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendActivityReminder() {
        log.info("执行今日活动提醒任务");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.withHour(23).withMinute(59).withSecond(59);
        
        // 查询今日即将开始的活动
        List<Activity> todayActivities = activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .ge(Activity::getStartTime, now)
                        .le(Activity::getStartTime, endOfDay)
                        .eq(Activity::getStatus, 1)
        );
        
        for (Activity activity : todayActivities) {
            // 查询已报名的用户
            List<ActivityRegistration> registrations = registrationMapper.selectList(
                    new LambdaQueryWrapper<ActivityRegistration>()
                            .eq(ActivityRegistration::getActivityId, activity.getId())
                            .eq(ActivityRegistration::getStatus, 1)
            );
            
            for (ActivityRegistration registration : registrations) {
                User user = userMapper.selectById(registration.getUserId());
                if (user != null) {
                    sendReminder(user, activity, "今日活动提醒", 
                            "亲爱的" + user.getUsername() + "，您报名的活动\"" + activity.getActivityName() + "\"将在今日开始，请准时参加！");
                }
            }
        }
        
        log.info("今日活动提醒任务完成，共处理{}个活动", todayActivities.size());
    }
    
    /**
     * 每小时检查活动状态变化
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkActivityStatus() {
        log.debug("检查活动状态");
        LocalDateTime now = LocalDateTime.now();
        
        // 检查活动是否已开始/结束，更新状态
        List<Activity> activities = activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .in(Activity::getStatus, 1, 2)
        );
        
        for (Activity activity : activities) {
            if (activity.getStatus() == 1 && activity.getStartTime().isBefore(now)) {
                activity.setStatus(2); // 报名中 -> 进行中
                activityMapper.updateById(activity);
                log.debug("活动状态更新为进行中: id={}", activity.getId());
            } else if (activity.getStatus() == 2 && activity.getEndTime().isBefore(now)) {
                activity.setStatus(3); // 进行中 -> 已结束
                activityMapper.updateById(activity);
                log.debug("活动状态更新为已结束: id={}", activity.getId());
            }
        }
    }
    
    /**
     * 活动开始前30分钟发送提醒
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void sendActivityStartReminder() {
        log.debug("检查即将开始的活动");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesLater = now.plusMinutes(30);
        
        // 查询30分钟后开始的活动
        List<Activity> startingActivities = activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .ge(Activity::getStartTime, now)
                        .le(Activity::getStartTime, thirtyMinutesLater)
                        .eq(Activity::getStatus, 1)
        );
        
        for (Activity activity : startingActivities) {
            // 查询已报名的用户
            List<ActivityRegistration> registrations = registrationMapper.selectList(
                    new LambdaQueryWrapper<ActivityRegistration>()
                            .eq(ActivityRegistration::getActivityId, activity.getId())
                            .eq(ActivityRegistration::getStatus, 1)
            );
            
            for (ActivityRegistration registration : registrations) {
                User user = userMapper.selectById(registration.getUserId());
                if (user != null) {
                    sendReminder(user, activity, "活动开始提醒", 
                            "亲爱的" + user.getUsername() + "，您报名的活动\"" + activity.getActivityName() + "\"将在30分钟后开始，请准时参加！");
                }
            }
        }
    }
    
    /**
     * 活动前一天发送提醒
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void sendActivityDayBeforeReminder() {
        log.info("执行活动前一天提醒任务");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
        
        // 查询明天开始的活动
        List<Activity> tomorrowActivities = activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .ge(Activity::getStartTime, tomorrow)
                        .le(Activity::getStartTime, tomorrow.plusDays(1))
                        .eq(Activity::getStatus, 1)
        );
        
        for (Activity activity : tomorrowActivities) {
            // 查询已报名的用户
            List<ActivityRegistration> registrations = registrationMapper.selectList(
                    new LambdaQueryWrapper<ActivityRegistration>()
                            .eq(ActivityRegistration::getActivityId, activity.getId())
                            .eq(ActivityRegistration::getStatus, 1)
            );
            
            for (ActivityRegistration registration : registrations) {
                User user = userMapper.selectById(registration.getUserId());
                if (user != null) {
                    sendReminder(user, activity, "活动前一天提醒", 
                            "亲爱的" + user.getUsername() + "，您报名的活动\"" + activity.getActivityName() + "\"将在明天开始，请提前安排时间！");
                }
            }
        }
        
        log.info("活动前一天提醒任务完成，共处理{}个活动", tomorrowActivities.size());
    }
    
    /**
     * 发送提醒通知
     */
    private void sendReminder(User user, Activity activity, String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(user.getId());
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(1); // 活动提醒
        notification.setMethod(1); // 系统通知
        notification.setStatus(0); // 未读
        notification.setCreateTime(LocalDateTime.now());
        
        notificationService.sendNotification(notification);
        log.debug("向用户 {} 发送活动提醒: {}", user.getUsername(), title);
    }
}
