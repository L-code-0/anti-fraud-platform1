package com.anti.fraud.modules.checkin.service.impl;

import com.anti.fraud.modules.checkin.entity.CheckinRecord;
import com.anti.fraud.modules.checkin.mapper.CheckinRecordMapper;
import com.anti.fraud.modules.checkin.service.CheckinService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 打卡服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CheckinServiceImpl extends ServiceImpl<CheckinRecordMapper, CheckinRecord> implements CheckinService {

    private final CheckinRecordMapper checkinRecordMapper;

    @Override
    @Transactional
    public Map<String, Object> doCheckin(Long userId, String username, Integer checkinType, String location, String ipAddress) {
        Map<String, Object> result = new HashMap<>();
        LocalDate today = LocalDate.now();

        // 1. 检查当天是否已经打卡
        CheckinRecord existingRecord = checkinRecordMapper.selectTodayCheckin(userId, today, checkinType);
        if (existingRecord != null) {
            result.put("success", false);
            result.put("message", "今日已经打卡，无需重复打卡");
            result.put("record", existingRecord);
            return result;
        }

        // 2. 计算连续打卡天数
        int consecutiveDays = getConsecutiveDays(userId, checkinType) + 1;

        // 3. 计算获得的积分
        int points = calculatePoints(consecutiveDays, checkinType);

        // 4. 创建打卡记录
        CheckinRecord checkinRecord = new CheckinRecord();
        checkinRecord.setUserId(userId);
        checkinRecord.setUsername(username);
        checkinRecord.setCheckinType(checkinType);
        checkinRecord.setCheckinDate(today.atStartOfDay());
        checkinRecord.setCheckinTime(LocalDateTime.now());
        checkinRecord.setStatus(1); // 1-成功
        checkinRecord.setLocation(location);
        checkinRecord.setIpAddress(ipAddress);
        checkinRecord.setConsecutiveDays(consecutiveDays);
        checkinRecord.setPoints(points);

        // 5. 保存打卡记录
        boolean saved = save(checkinRecord);
        if (saved) {
            result.put("success", true);
            result.put("message", "打卡成功");
            result.put("record", checkinRecord);
            result.put("consecutiveDays", consecutiveDays);
            result.put("points", points);
            log.info("用户打卡成功: userId={}, username={}, checkinType={}, consecutiveDays={}, points={}", 
                    userId, username, checkinType, consecutiveDays, points);
        } else {
            result.put("success", false);
            result.put("message", "打卡失败，请稍后重试");
            log.error("用户打卡失败: userId={}, username={}, checkinType={}", userId, username, checkinType);
        }

        return result;
    }

    @Override
    public boolean hasCheckedInToday(Long userId, Integer checkinType) {
        LocalDate today = LocalDate.now();
        CheckinRecord record = checkinRecordMapper.selectTodayCheckin(userId, today, checkinType);
        return record != null;
    }

    @Override
    public int getConsecutiveDays(Long userId, Integer checkinType) {
        try {
            // 从数据库查询连续打卡天数
            Integer consecutiveDays = checkinRecordMapper.selectConsecutiveDays(userId, checkinType);
            if (consecutiveDays != null) {
                return consecutiveDays;
            }

            // 如果数据库没有记录，返回0
            return 0;
        } catch (Exception e) {
            log.error("获取连续打卡天数失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public List<CheckinRecord> getCheckinRecords(Long userId, Integer checkinType, LocalDate startDate, LocalDate endDate, int page, int size) {
        try {
            return checkinRecordMapper.selectCheckinRecords(userId, checkinType, startDate, endDate, page, size);
        } catch (Exception e) {
            log.error("获取打卡记录失败: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public Map<String, Object> getCheckinStats(Long userId, Integer checkinType, LocalDate startDate, LocalDate endDate) {
        try {
            Map<String, Object> stats = checkinRecordMapper.selectCheckinStats(userId, checkinType, startDate, endDate);
            if (stats == null) {
                stats = new HashMap<>();
                stats.put("totalCheckins", 0);
                stats.put("consecutiveDays", 0);
                stats.put("totalPoints", 0);
            }
            return stats;
        } catch (Exception e) {
            log.error("获取打卡统计失败: {}", e.getMessage(), e);
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("totalCheckins", 0);
            defaultStats.put("consecutiveDays", 0);
            defaultStats.put("totalPoints", 0);
            return defaultStats;
        }
    }

    @Override
    public List<CheckinRecord> getMonthCheckinRecords(Long userId, Integer checkinType, int year, int month) {
        try {
            return checkinRecordMapper.selectMonthCheckinRecords(userId, checkinType, year, month);
        } catch (Exception e) {
            log.error("获取当月打卡记录失败: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public int getTodayCheckinCount(Integer checkinType) {
        try {
            LocalDate today = LocalDate.now();
            Integer count = checkinRecordMapper.selectTodayCheckinCount(today, checkinType);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("获取当日打卡用户数失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public List<Map<String, Object>> getCheckinRank(Integer checkinType, LocalDate startDate, LocalDate endDate, int limit) {
        try {
            return checkinRecordMapper.selectCheckinRank(checkinType, startDate, endDate, limit);
        } catch (Exception e) {
            log.error("获取打卡排行榜失败: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public int calculatePoints(int consecutiveDays, Integer checkinType) {
        // 基础积分
        int basePoints = checkinType == 1 ? 10 : 15; // 每日打卡10分，学习打卡15分

        // 连续打卡奖励
        int bonusPoints = 0;
        if (consecutiveDays >= 7) {
            bonusPoints = 5; // 连续7天额外5分
        } else if (consecutiveDays >= 30) {
            bonusPoints = 15; // 连续30天额外15分
        } else if (consecutiveDays >= 100) {
            bonusPoints = 30; // 连续100天额外30分
        }

        return basePoints + bonusPoints;
    }
}
