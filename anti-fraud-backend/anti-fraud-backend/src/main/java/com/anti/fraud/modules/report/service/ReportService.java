package com.anti.fraud.modules.report.service;

import com.anti.fraud.modules.report.entity.Report;
import java.util.List;

/**
 * 举报服务
 */
public interface ReportService {

    /**
     * 提交举报
     */
    void submitReport(Report report);

    /**
     * 获取举报列表
     */
    List<Report> getReportList();
}
