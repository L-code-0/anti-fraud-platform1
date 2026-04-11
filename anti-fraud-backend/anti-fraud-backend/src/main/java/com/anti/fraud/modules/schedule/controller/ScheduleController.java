package com.anti.fraud.modules.schedule.controller;

import com.anti.fraud.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务控制器
 */
@Tag(name = "定时任务", description = "定时任务管理接口")
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Operation(summary = "获取所有定时任务列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getScheduledTasks() {
        List<Map<String, Object>> tasks = new ArrayList<>();
        
        // 扫描所有Job类，获取@Scheduled注解的方法
        // 这里简化处理，实际可以使用反射或自定义注解扫描
        tasks.add(createTaskInfo("LearningReminderJob", "sendDailyLearningReminder", 
                "每天早上9点发送学习提醒", "0 0 9 * * ?"));
        tasks.add(createTaskInfo("LearningReminderJob", "sendWeeklyReport", 
                "每周一早上9点发送周学习报告", "0 0 9 ? * MON"));
        tasks.add(createTaskInfo("SystemMaintenanceJob", "cleanOldLogs", 
                "每天凌晨2点清理7天前的操作日志", "0 0 2 * * ?"));
        tasks.add(createTaskInfo("SystemMaintenanceJob", "generateDailyStats", 
                "每天凌晨3点统计昨日数据", "0 0 3 * * ?"));
        tasks.add(createTaskInfo("SystemMaintenanceJob", "cleanExpiredData", 
                "每月1日凌晨4点清理过期数据", "0 0 4 1 * ?"));
        tasks.add(createTaskInfo("ActivityReminderJob", "sendActivityReminder", 
                "每天早上8点发送今日活动提醒", "0 0 8 * * ?"));
        tasks.add(createTaskInfo("ActivityReminderJob", "checkActivityStatus", 
                "每小时检查活动状态变化", "0 0 * * * ?"));
        tasks.add(createTaskInfo("ActivityReminderJob", "sendActivityStartReminder", 
                "每小时检查即将开始的活动并提醒", "0 30 * * * ?"));
        
        return Result.success(tasks);
    }
    
    @Operation(summary = "获取服务器当前时间")
    @GetMapping("/now")
    public Result<Map<String, Object>> getNow() {
        Map<String, Object> result = new HashMap<>();
        result.put("serverTime", LocalDateTime.now().format(FORMATTER));
        result.put("timestamp", System.currentTimeMillis());
        return Result.success(result);
    }
    
    /**
     * 创建任务信息
     */
    private Map<String, Object> createTaskInfo(String className, String methodName, 
                                                String description, String cron) {
        Map<String, Object> task = new HashMap<>();
        task.put("className", className);
        task.put("methodName", methodName);
        task.put("description", description);
        task.put("cron", cron);
        task.put("status", "RUNNING");
        return task;
    }
}
