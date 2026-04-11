package com.anti.fraud.report.service;

import com.anti.fraud.report.entity.Warning;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 预警服务接口
 */
public interface WarningService extends IService<Warning> {

    /**
     * 发布预警
     * @param warning 预警信息
     * @return 预警ID
     */
    Long publishWarning(Warning warning);

    /**
     * 获取最新预警
     * @param page 页码
     * @param size 每页大小
     * @return 预警列表
     */
    List<Warning> getLatestWarnings(int page, int size);

    /**
     * 根据类型获取预警
     * @param type 诈骗类型
     * @param page 页码
     * @param size 每页大小
     * @return 预警列表
     */
    List<Warning> getWarningsByType(String type, int page, int size);

    /**
     * 根据等级获取预警
     * @param level 预警等级
     * @param page 页码
     * @param size 每页大小
     * @return 预警列表
     */
    List<Warning> getWarningsByLevel(String level, int page, int size);

    /**
     * 获取预警详情
     * @param warningId 预警ID
     * @return 预警信息
     */
    Warning getWarningDetail(Long warningId);

    /**
     * 更新预警状态
     * @param warningId 预警ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateWarningStatus(Long warningId, String status);

    /**
     * 获取预警统计
     * @return 统计信息
     */
    WarningStatistics getWarningStatistics();

    /**
     * 预警统计信息
     */
    class WarningStatistics {
        private int todayWarnings; // 今日预警数
        private int weekWarnings; // 本周预警数
        private int monthWarnings; // 本月预警数
        private double accuracyRate; // 预警准确率

        // getter和setter方法
        public int getTodayWarnings() {
            return todayWarnings;
        }

        public void setTodayWarnings(int todayWarnings) {
            this.todayWarnings = todayWarnings;
        }

        public int getWeekWarnings() {
            return weekWarnings;
        }

        public void setWeekWarnings(int weekWarnings) {
            this.weekWarnings = weekWarnings;
        }

        public int getMonthWarnings() {
            return monthWarnings;
        }

        public void setMonthWarnings(int monthWarnings) {
            this.monthWarnings = monthWarnings;
        }

        public double getAccuracyRate() {
            return accuracyRate;
        }

        public void setAccuracyRate(double accuracyRate) {
            this.accuracyRate = accuracyRate;
        }
    }
}
