package com.anti.fraud.report.service;

import com.anti.fraud.report.entity.Report;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 举报服务接口
 */
public interface ReportService extends IService<Report> {

    /**
     * 提交举报
     * @param report 举报信息
     * @return 举报ID
     */
    Long submitReport(Report report);

    /**
     * 获取用户举报历史
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 举报列表
     */
    List<Report> getUserReportHistory(Long userId, int page, int size);

    /**
     * 智能分类举报
     * @param description 举报描述
     * @return 分类结果
     */
    String autoClassify(String description);

    /**
     * 处理举报
     * @param reportId 举报ID
     * @param handlerId 处理人ID
     * @param result 处理结果
     * @return 是否成功
     */
    boolean processReport(Long reportId, Long handlerId, String result);

    /**
     * 获取举报统计
     * @param userId 用户ID
     * @return 统计信息
     */
    ReportStatistics getReportStatistics(Long userId);

    /**
     * 举报统计信息
     */
    class ReportStatistics {
        private int totalReports; // 总举报数
        private int processedReports; // 已处理数
        private int pendingReports; // 待处理数
        private int pointsEarned; // 获得积分

        // getter和setter方法
        public int getTotalReports() {
            return totalReports;
        }

        public void setTotalReports(int totalReports) {
            this.totalReports = totalReports;
        }

        public int getProcessedReports() {
            return processedReports;
        }

        public void setProcessedReports(int processedReports) {
            this.processedReports = processedReports;
        }

        public int getPendingReports() {
            return pendingReports;
        }

        public void setPendingReports(int pendingReports) {
            this.pendingReports = pendingReports;
        }

        public int getPointsEarned() {
            return pointsEarned;
        }

        public void setPointsEarned(int pointsEarned) {
            this.pointsEarned = pointsEarned;
        }
    }
}
