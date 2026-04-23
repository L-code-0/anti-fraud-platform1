package com.anti.fraud.modules.checkin.mapper;

import com.anti.fraud.modules.checkin.entity.CheckinRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 打卡记录Mapper
 */
@Mapper
public interface CheckinRecordMapper extends BaseMapper<CheckinRecord> {

    /**
     * 查询用户当天是否已经打卡
     * @param userId 用户ID
     * @param checkinDate 打卡日期
     * @param checkinType 打卡类型
     * @return 打卡记录
     */
    CheckinRecord selectTodayCheckin(@Param("userId") Long userId, @Param("checkinDate") LocalDate checkinDate, @Param("checkinType") Integer checkinType);

    /**
     * 查询用户连续打卡天数
     * @param userId 用户ID
     * @param checkinType 打卡类型
     * @return 连续打卡天数
     */
    Integer selectConsecutiveDays(@Param("userId") Long userId, @Param("checkinType") Integer checkinType);

    /**
     * 查询用户打卡记录列表
     * @param userId 用户ID
     * @param checkinType 打卡类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 页码
     * @param size 每页大小
     * @return 打卡记录列表
     */
    List<CheckinRecord> selectCheckinRecords(
            @Param("userId") Long userId,
            @Param("checkinType") Integer checkinType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("page") Integer page,
            @Param("size") Integer size
    );

    /**
     * 查询用户打卡统计
     * @param userId 用户ID
     * @param checkinType 打卡类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 打卡统计
     */
    Map<String, Object> selectCheckinStats(
            @Param("userId") Long userId,
            @Param("checkinType") Integer checkinType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 查询用户当月打卡记录
     * @param userId 用户ID
     * @param checkinType 打卡类型
     * @param year 年份
     * @param month 月份
     * @return 当月打卡记录列表
     */
    List<CheckinRecord> selectMonthCheckinRecords(
            @Param("userId") Long userId,
            @Param("checkinType") Integer checkinType,
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    /**
     * 查询当日打卡用户数
     * @param checkinDate 打卡日期
     * @param checkinType 打卡类型
     * @return 打卡用户数
     */
    Integer selectTodayCheckinCount(@Param("checkinDate") LocalDate checkinDate, @Param("checkinType") Integer checkinType);

    /**
     * 查询打卡排行榜
     * @param checkinType 打卡类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 限制数量
     * @return 打卡排行榜
     */
    List<Map<String, Object>> selectCheckinRank(
            @Param("checkinType") Integer checkinType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") Integer limit
    );
}
