package com.anti.fraud.modules.checkin.service;

import com.anti.fraud.modules.checkin.entity.Checkin;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CheckinService {

    /**
     * 每日打卡
     * @param userId 用户ID
     * @param checkinType 打卡类型
     * @return 打卡结果
     */
    Map<String, Object> checkin(Long userId, String checkinType);

    /**
     * 获取用户今日打卡状态
     * @param userId 用户ID
     * @return 打卡状态
     */
    Checkin getTodayCheckin(Long userId);

    /**
     * 获取用户打卡历史
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 打卡历史列表
     */
    List<Checkin> getCheckinHistory(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取用户连续打卡天数
     * @param userId 用户ID
     * @return 连续打卡天数
     */
    Integer getContinuousDays(Long userId);

    /**
     * 获取用户总打卡天数
     * @param userId 用户ID
     * @return 总打卡天数
     */
    Integer getTotalCheckinDays(Long userId);

    /**
     * 获取用户本月打卡记录
     * @param userId 用户ID
     * @param year 年份
     * @param month 月份
     * @return 本月打卡记录
     */
    List<Checkin> getMonthCheckin(Long userId, int year, int month);

    /**
     * 领取连续打卡奖励
     * @param userId 用户ID
     * @param days 连续天数
     * @return 领取结果
     */
    Map<String, Object> claimReward(Long userId, int days);
}
