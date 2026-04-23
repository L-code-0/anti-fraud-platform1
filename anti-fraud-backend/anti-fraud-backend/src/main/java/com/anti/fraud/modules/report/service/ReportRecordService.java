package com.anti.fraud.modules.report.service;

import com.anti.fraud.modules.report.entity.ReportRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 举报记录服务接口
 */
public interface ReportRecordService extends IService<ReportRecord> {

    /**
     * 新增举报记录
     * @param reportRecord 举报记录信息
     * @return 是否成功
     */
    boolean addReportRecord(ReportRecord reportRecord);

    /**
     * 更新举报记录
     * @param reportRecord 举报记录信息
     * @return 是否成功
     */
    boolean updateReportRecord(ReportRecord reportRecord);

    /**
     * 删除举报记录
     * @param id 举报记录ID
     * @return 是否成功
     */
    boolean deleteReportRecord(Long id);

    /**
     * 获取举报记录详情
     * @param id 举报记录ID
     * @return 举报记录详情
     */
    ReportRecord getReportRecordById(Long id);

    /**
     * 分页查询举报记录
     * @param params 查询参数
     * @param page 页码
     * @param size 每页大小
     * @return 举报记录列表和总数
     */
    Map<String, Object> getReportRecordList(Map<String, Object> params, int page, int size);

    /**
     * 根据用户ID查询举报记录
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 举报记录列表和总数
     */
    Map<String, Object> getReportRecordsByUserId(Long userId, int page, int size);

    /**
     * 获取待处理的举报记录
     * @param limit 数量限制
     * @return 待处理的举报记录列表
     */
    List<ReportRecord> getPendingReports(int limit);

    /**
     * 处理举报记录
     * @param id 举报记录ID
     * @param handleStatus 处理状态
     * @param handleResult 处理结果
     * @param handler 处理人
     * @param rewardPoints 积分奖励
     * @return 是否成功
     */
    boolean handleReport(Long id, Integer handleStatus, String handleResult, String handler, Integer rewardPoints);

    /**
     * 统计举报记录信息
     * @return 统计信息
     */
    Map<String, Object> getReportRecordStats();

    /**
     * 获取用户举报统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getUserReportStats(Long userId);

    /**
     * 获取用户获得的积分奖励
     * @param userId 用户ID
     * @return 积分奖励
     */
    Integer getUserRewardPoints(Long userId);

    /**
     * 按月份统计举报记录
     * @param months 月份数
     * @return 月度统计
     */
    List<Map<String, Object>> getMonthlyStats(int months);

    /**
     * 计算举报积分奖励
     * @param reportType 举报类型
     * @param reportContent 举报内容
     * @param evidence 举报证据
     * @return 积分奖励
     */
    Integer calculateRewardPoints(Integer reportType, String reportContent, String evidence);
}
