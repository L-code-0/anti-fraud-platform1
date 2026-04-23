package com.anti.fraud.modules.warning.service;

import com.anti.fraud.modules.warning.entity.ExternalWarning;
import com.anti.fraud.modules.warning.vo.ExternalWarningVO;

import java.util.List;

/**
 * 外部反诈预警服务接口
 */
public interface ExternalWarningService {

    /**
     * 同步官方反诈预警信息
     * @return 同步的预警信息数量
     */
    int syncExternalWarnings();

    /**
     * 获取最新的预警信息
     * @param limit 数量限制
     * @return 预警信息列表
     */
    List<ExternalWarningVO> getLatestWarnings(int limit);

    /**
     * 根据预警类型获取预警信息
     * @param warningType 预警类型
     * @param limit 数量限制
     * @return 预警信息列表
     */
    List<ExternalWarningVO> getWarningsByType(int warningType, int limit);

    /**
     * 根据来源获取预警信息
     * @param source 预警来源
     * @param limit 数量限制
     * @return 预警信息列表
     */
    List<ExternalWarningVO> getWarningsBySource(String source, int limit);

    /**
     * 获取预警信息详情
     * @param id 预警ID
     * @return 预警信息详情
     */
    ExternalWarningVO getWarningById(Long id);

    /**
     * 手动同步预警信息
     * @param source 预警来源
     * @return 同步的预警信息数量
     */
    int manualSyncWarnings(String source);

    /**
     * 清理过期的预警信息
     * @return 清理的预警信息数量
     */
    int cleanExpiredWarnings();

    /**
     * 获取预警信息统计
     * @return 统计信息
     */
    java.util.Map<String, Object> getWarningStatistics();

    /**
     * 测试预警信息接口连接
     * @param source 预警来源
     * @return 测试结果
     */
    boolean testConnection(String source);
}
