package com.anti.fraud.modules.dashboard.service;

import java.util.Map;
import java.util.List;

/**
 * 数据大屏服务接口
 */
public interface DashboardService {

    /**
     * 获取实时数据大屏首页数据
     * @return 首页数据
     */
    Map<String, Object> getDashboardHomeData();

    /**
     * 获取用户统计数据
     * @return 用户统计数据
     */
    Map<String, Object> getUserStats();

    /**
     * 获取打卡统计数据
     * @return 打卡统计数据
     */
    Map<String, Object> getCheckinStats();

    /**
     * 获取演练统计数据
     * @return 演练统计数据
     */
    Map<String, Object> getExerciseStats();

    /**
     * 获取举报统计数据
     * @return 举报统计数据
     */
    Map<String, Object> getReportStats();

    /**
     * 获取案例统计数据
     * @return 案例统计数据
     */
    Map<String, Object> getCaseStats();

    /**
     * 获取题目统计数据
     * @return 题目统计数据
     */
    Map<String, Object> getQuestionStats();

    /**
     * 获取访问统计数据
     * @param days 天数
     * @return 访问统计数据
     */
    List<Map<String, Object>> getAccessStats(int days);

    /**
     * 获取趋势统计数据
     * @param type 统计类型
     * @param days 天数
     * @return 趋势统计数据
     */
    List<Map<String, Object>> getTrendStats(String type, int days);

    /**
     * 获取排行榜数据
     * @param type 排行榜类型
     * @param limit 数量限制
     * @return 排行榜数据
     */
    List<Map<String, Object>> getRankingData(String type, int limit);

    /**
     * 获取系统运行状态
     * @return 系统运行状态
     */
    Map<String, Object> getSystemStatus();
}
