package com.anti.fraud.modules.report.service;

import com.anti.fraud.modules.file.entity.FileInfo;
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

    /**
     * 获取举报详情
     */
    Report getReportDetail(Long id);

    /**
     * 获取举报证据文件
     */
    List<FileInfo> getReportFiles(Long id);

    /**
     * 为举报添加证据文件
     */
    void addReportFiles(Long id, List<Long> fileIds);

    /**
     * 移除举报证据文件
     */
    void removeReportFile(Long id, Long fileId);
}
