package com.anti.fraud.modules.report.service;

import com.anti.fraud.modules.report.entity.Report;
import com.anti.fraud.modules.report.entity.ReportProgress;
import com.anti.fraud.modules.report.entity.ReportPoint;
import java.util.List;
import java.util.Map;

public interface ReportService {

    /**
     * 创建举报
     * @param report 举报信息
     * @return 是否成功
     */
    boolean createReport(Report report);

    /**
     * 更新举报
     * @param report 举报信息
     * @return 是否成功
     */
    boolean updateReport(Report report);

    /**
     * 删除举报
     * @param id 举报ID
     * @return 是否成功
     */
    boolean deleteReport(Long id);

    /**
     * 获取举报详情
     * @param id 举报ID
     * @return 举报详情
     */
    Report getReportById(Long id);

    /**
     * 获取举报列表
     * @param status 状态
     * @param reportType 举报类型
     * @param page 页码
     * @param size 每页大小
     * @return 举报列表
     */
    List<Report> getReportList(String status, String reportType, int page, int size);

    /**
     * 获取用户举报历史
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 举报历史
     */
    List<Report> getUserReportHistory(Long userId, int page, int size);

    /**
     * 处理举报
     * @param id 举报ID
     * @param status 处理状态
     * @param feedback 处理反馈
     * @param handlerId 处理人ID
     * @param handlerName 处理人姓名
     * @return 是否成功
     */
    boolean handleReport(Long id, String status, String feedback, Long handlerId, String handlerName);

    /**
     * 获取举报进度
     * @param reportId 举报ID
     * @return 进度列表
     */
    List<ReportProgress> getReportProgress(Long reportId);

    /**
     * 添加举报进度
     * @param progress 进度信息
     * @return 是否成功
     */
    boolean addReportProgress(ReportProgress progress);

    /**
     * 计算举报积分
     * @param reportId 举报ID
     * @param userId 用户ID
     * @param userName 用户名
     * @return 积分信息
     */
    Map<String, Object> calculateReportPoints(Long reportId, Long userId, String userName);

    /**
     * 获取用户举报积分
     * @param userId 用户ID
     * @return 积分信息
     */
    Map<String, Object> getUserReportPoints(Long userId);

    /**
     * 获取举报数据分析
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param reportType 举报类型
     * @return 分析结果
     */
    Map<String, Object> getReportAnalysis(String startTime, String endTime, String reportType);

    /**
     * 搜索举报
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 举报列表
     */
    List<Report> searchReports(String keyword, int page, int size);

    /**
     * 批量处理举报
     * @param ids 举报ID列表
     * @param status 处理状态
     * @param feedback 处理反馈
     * @param handlerId 处理人ID
     * @param handlerName 处理人姓名
     * @return 处理结果
     */
    Map<String, Object> batchHandleReports(List<Long> ids, String status, String feedback, Long handlerId, String handlerName);
}
