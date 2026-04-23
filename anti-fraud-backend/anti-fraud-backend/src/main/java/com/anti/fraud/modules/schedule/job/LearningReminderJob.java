package com.anti.fraud.modules.schedule.job;

import com.anti.fraud.modules.notification.service.NotificationService;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.anti.fraud.modules.knowledge.entity.KnowledgeContent;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeContentMapper;
import com.anti.fraud.modules.points.entity.PointsRecord;
import com.anti.fraud.modules.points.mapper.PointsRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习提醒定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LearningReminderJob {
    
    private final UserMapper userMapper;
    private final PointsRecordMapper pointsRecordMapper;
    private final NotificationService notificationService;
    
    /**
     * 每天早上9点发送学习提醒
     * 学习连续中断3天的用户发送提醒
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyLearningReminder() {
        log.info("执行每日学习提醒任务");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysAgo = now.minusDays(3);
        
        // 查询所有用户
        List<User> users = userMapper.selectList(null);
        
        for (User user : users) {
            // 查询用户最近的积分记录（作为学习活动的指标）
            PointsRecord latestRecord = pointsRecordMapper.selectOne(
                    new LambdaQueryWrapper<PointsRecord>()
                            .eq(PointsRecord::getUserId, user.getId())
                            .orderByDesc(PointsRecord::getCreateTime)
                            .last("LIMIT 1")
            );
            
            // 如果没有积分记录或者最后一次积分记录超过3天
            if (latestRecord == null || latestRecord.getCreateTime().isBefore(threeDaysAgo)) {
                // 发送学习提醒
                try {
                    notificationService.sendSystemNotification(
                            "学习提醒", 
                            "您已连续3天未学习，请及时登录学习！", 
                            user.getId()
                    );
                    log.info("向用户 {} 发送学习提醒", user.getUsername());
                } catch (Exception e) {
                    log.error("发送学习提醒失败: {}", e.getMessage(), e);
                }
            }
        }
        
        log.info("每日学习提醒任务完成");
    }
    
    /**
     * 每周一早上9点发送周学习报告
     */
    @Scheduled(cron = "0 0 9 ? * MON")
    public void sendWeeklyReport() {
        log.info("执行周学习报告任务");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.minusWeeks(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime weekEnd = now.withHour(23).withMinute(59).withSecond(59);
        
        // 查询所有用户
        List<User> users = userMapper.selectList(null);
        
        for (User user : users) {
            // 查询用户本周的积分记录
            List<PointsRecord> weekRecords = pointsRecordMapper.selectList(
                    new LambdaQueryWrapper<PointsRecord>()
                            .eq(PointsRecord::getUserId, user.getId())
                            .ge(PointsRecord::getCreateTime, weekStart)
                            .le(PointsRecord::getCreateTime, weekEnd)
            );
            
            // 统计学习数据
            int totalPoints = 0;
            int totalActivities = 0;
            
            for (PointsRecord record : weekRecords) {
                totalPoints += record.getPoints();
                totalActivities++;
            }
            
            // 构建学习报告
            StringBuilder reportContent = new StringBuilder();
            reportContent.append("亲爱的").append(user.getUsername()).append("，以下是您本周的学习报告：\n");
            reportContent.append("\n本周获得积分：").append(totalPoints);
            reportContent.append("\n学习活动次数：").append(totalActivities);
            
            if (totalPoints > 0) {
                reportContent.append("\n\n继续保持良好的学习习惯！");
            } else {
                reportContent.append("\n\n本周未学习，建议您合理安排学习时间。");
            }
            
            // 发送学习报告
            try {
                notificationService.sendSystemNotification(
                        "周学习报告", 
                        reportContent.toString(), 
                        user.getId()
                );
                log.info("向用户 {} 发送周学习报告", user.getUsername());
            } catch (Exception e) {
                log.error("发送周学习报告失败: {}", e.getMessage(), e);
            }
        }
        
        log.info("周学习报告任务完成");
    }
}
