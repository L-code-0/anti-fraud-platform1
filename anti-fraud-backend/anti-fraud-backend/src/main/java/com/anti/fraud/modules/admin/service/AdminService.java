package com.anti.fraud.modules.admin.service;

import com.anti.fraud.modules.admin.vo.DashboardStatsVO;
import com.anti.fraud.modules.admin.vo.TrendDataVO;
import com.anti.fraud.modules.admin.vo.DepartmentStatsVO;
import com.anti.fraud.modules.admin.vo.FraudTypeStatsVO;

import java.util.List;

public interface AdminService {

    DashboardStatsVO getDashboardStats();

    List<TrendDataVO> getUserTrend(Integer days);

    List<TrendDataVO> getTestTrend(Integer days);

    List<TrendDataVO> getKnowledgeStats();

    /**
     * 各院系学习参与率统计
     */
    List<DepartmentStatsVO> getDepartmentStats();

    /**
     * 高发诈骗类型统计
     */
    List<FraudTypeStatsVO> getFraudTypeStats();

    /**
     * 举报处理时效统计
     */
    TrendDataVO getReportEfficiency();
}