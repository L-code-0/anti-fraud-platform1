package com.anti.fraud.modules.checkin.service;

import com.anti.fraud.modules.checkin.entity.CheckinRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 打卡服务接口
 */
public interface CheckinService extends IService<CheckinRecord> {

    /**
     * 执行打卡
     * @param userId 用户ID
     * @param username 用户名
     * @param checkinType 打卡类型
     * @param location 打卡地点
     * @param ipAddress 打卡IP
     * @return 打卡结果
     */
    Map<String, Object> doCheckin(Long userId, String username, Integer checkinType, String location, String ipAddress);

    /**
     * 检查用户当天是否已经打卡
     * @param userId 用户ID
     * @param checkinType 打卡类型
     * @return 是否已经打卡
     */
    boolean hasCheckedInToday(Long userId, Integer checkinType);

    /**
     * 获取用户连续打卡天数
     * @param userId 用户ID
     * @param checkinType 打卡类型
     * @return 连续打卡天数
     */
    int getConsecutiveDays(Long userId, Integer checkinType);

    /**
     * 获取用户打卡记录列表
     * @param userId 用户ID
     * @param checkinType 打卡类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 页码
     * @param size 每页大小
     * @return 打卡记录列表
     */
    List<CheckinRecord> getCheckinRecords(Long userId, Integer checkinType, LocalDate startDate, LocalDate endDate, int page, int size);

    /**
     * 获取用户打卡统计
     * @param userId 用户ID
     * @param checkinType 打卡类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 打卡统计
     */
    Map<String, Object> getCheckinStats(Long userId, Integer checkinType, LocalDate startDate, LocalDate endDate);

    /**
     * 获取用户当月打卡记录
     * @param userId 用户ID
     * @param checkinType 打卡类型
     * @param year 年份
     * @param month 月份
     * @return 当月打卡记录列表
     */
    List<CheckinRecord> getMonthCheckinRecords(Long userId, Integer checkinType, int year, int month);

    /**
     * 获取当日打卡用户数
     * @param checkinType 打卡类型
     * @return 打卡用户数
     */
    int getTodayCheckinCount(Integer checkinType);

    /**
     * 获取打卡排行榜
     * @param checkinType 打卡类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 限制数量
     * @return 打卡排行榜
     */
    List<Map<String, Object>> getCheckinRank(Integer checkinType, LocalDate startDate, LocalDate endDate, int limit);

    /**
     * 计算打卡获得的积分
     * @param consecutiveDays 连续打卡天数
     * @param checkinType 打卡类型
     * @return 获得的积分
     */
    int calculatePoints(int consecutiveDays, Integer checkinType);
}
