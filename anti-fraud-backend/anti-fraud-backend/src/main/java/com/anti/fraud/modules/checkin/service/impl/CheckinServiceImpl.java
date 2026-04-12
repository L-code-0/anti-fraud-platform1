package com.anti.fraud.modules.checkin.service.impl;

import com.anti.fraud.modules.checkin.entity.Checkin;
import com.anti.fraud.modules.checkin.mapper.CheckinMapper;
import com.anti.fraud.modules.checkin.service.CheckinService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckinServiceImpl implements CheckinService {

    private final CheckinMapper checkinMapper;

    @Override
    public Map<String, Object> checkin(Long userId, String checkinType) {
        try {
            LocalDate today = LocalDate.now();
            
            // 检查今日是否已打卡
            Checkin todayCheckin = getTodayCheckin(userId);
            if (todayCheckin != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "今日已打卡");
                result.put("checkin", todayCheckin);
                return result;
            }

            // 计算连续打卡天数
            int continuousDays = calculateContinuousDays(userId);
            
            // 计算奖励积分
            int rewardPoints = calculateRewardPoints(continuousDays);
            
            // 创建打卡记录
            Checkin checkin = new Checkin();
            checkin.setUserId(userId);
            checkin.setUserName("用户" + userId);
            checkin.setCheckinDate(today);
            checkin.setCheckinStatus(1);
            checkin.setContinuousDays(continuousDays);
            checkin.setRewardPoints(rewardPoints);
            checkin.setCheckinType(checkinType);
            
            checkinMapper.insert(checkin);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "打卡成功");
            result.put("checkin", checkin);
            result.put("continuousDays", continuousDays);
            result.put("rewardPoints", rewardPoints);
            
            log.info("用户打卡成功: userId={}, continuousDays={}, rewardPoints={}", 
                    userId, continuousDays, rewardPoints);
            
            return result;
        } catch (Exception e) {
            log.error("打卡失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "打卡失败，请稍后再试");
            return result;
        }
    }

    @Override
    public Checkin getTodayCheckin(Long userId) {
        try {
            LocalDate today = LocalDate.now();
            LambdaQueryWrapper<Checkin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Checkin::getUserId, userId)
                    .eq(Checkin::getCheckinDate, today);
            return checkinMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("获取今日打卡状态失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Checkin> getCheckinHistory(Long userId, LocalDate startDate, LocalDate endDate) {
        try {
            LambdaQueryWrapper<Checkin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Checkin::getUserId, userId)
                    .between(Checkin::getCheckinDate, startDate, endDate)
                    .orderByDesc(Checkin::getCheckinDate);
            return checkinMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取打卡历史失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Integer getContinuousDays(Long userId) {
        return calculateContinuousDays(userId);
    }

    @Override
    public Integer getTotalCheckinDays(Long userId) {
        try {
            LambdaQueryWrapper<Checkin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Checkin::getUserId, userId);
            return checkinMapper.selectCount(queryWrapper).intValue();
        } catch (Exception e) {
            log.error("获取总打卡天数失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public List<Checkin> getMonthCheckin(Long userId, int year, int month) {
        try {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            
            LambdaQueryWrapper<Checkin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Checkin::getUserId, userId)
                    .between(Checkin::getCheckinDate, startDate, endDate)
                    .orderByAsc(Checkin::getCheckinDate);
            return checkinMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取本月打卡记录失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> claimReward(Long userId, int days) {
        try {
            // 检查是否符合领取条件
            int continuousDays = getContinuousDays(userId);
            if (continuousDays < days) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "连续打卡天数不足");
                return result;
            }

            // 计算奖励
            int rewardPoints = calculateRewardPoints(days);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "领取奖励成功");
            result.put("rewardPoints", rewardPoints);
            result.put("continuousDays", days);
            
            log.info("用户领取连续打卡奖励: userId={}, days={}, rewardPoints={}", 
                    userId, days, rewardPoints);
            
            return result;
        } catch (Exception e) {
            log.error("领取奖励失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "领取奖励失败，请稍后再试");
            return result;
        }
    }

    /**
     * 计算连续打卡天数
     */
    private int calculateContinuousDays(Long userId) {
        try {
            LocalDate today = LocalDate.now();
            int continuousDays = 1; // 当天打卡算1天
            
            // 查询最近的打卡记录
            LambdaQueryWrapper<Checkin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Checkin::getUserId, userId)
                    .orderByDesc(Checkin::getCheckinDate);
            List<Checkin> checkins = checkinMapper.selectList(queryWrapper);
            
            if (checkins.isEmpty()) {
                return 1;
            }
            
            // 从昨天开始检查连续打卡
            LocalDate checkDate = today.minusDays(1);
            for (Checkin checkin : checkins) {
                if (checkin.getCheckinDate().equals(checkDate)) {
                    continuousDays++;
                    checkDate = checkDate.minusDays(1);
                } else if (checkin.getCheckinDate().isBefore(checkDate)) {
                    break;
                }
            }
            
            return continuousDays;
        } catch (Exception e) {
            log.error("计算连续打卡天数失败: {}", e.getMessage(), e);
            return 1;
        }
    }

    /**
     * 计算奖励积分
     */
    private int calculateRewardPoints(int continuousDays) {
        if (continuousDays >= 30) {
            return 50;
        } else if (continuousDays >= 21) {
            return 30;
        } else if (continuousDays >= 14) {
            return 20;
        } else if (continuousDays >= 7) {
            return 10;
        } else if (continuousDays >= 3) {
            return 5;
        } else {
            return 1;
        }
    }
}
