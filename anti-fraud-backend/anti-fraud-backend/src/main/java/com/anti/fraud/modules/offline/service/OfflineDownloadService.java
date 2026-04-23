package com.anti.fraud.modules.offline.service;

import com.anti.fraud.modules.offline.entity.OfflineDownload;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 离线下载服务接口
 */
public interface OfflineDownloadService extends IService<OfflineDownload> {

    /**
     * 开始下载
     * @param offlineDownload 下载信息
     * @return 是否成功
     */
    boolean startDownload(OfflineDownload offlineDownload);

    /**
     * 暂停下载
     * @param id 下载记录ID
     * @return 是否成功
     */
    boolean pauseDownload(Long id);

    /**
     * 恢复下载
     * @param id 下载记录ID
     * @return 是否成功
     */
    boolean resumeDownload(Long id);

    /**
     * 取消下载
     * @param id 下载记录ID
     * @return 是否成功
     */
    boolean cancelDownload(Long id);

    /**
     * 删除下载记录
     * @param id 下载记录ID
     * @return 是否成功
     */
    boolean deleteDownload(Long id);

    /**
     * 获取下载详情
     * @param id 下载记录ID
     * @return 下载详情
     */
    OfflineDownload getDownloadById(Long id);

    /**
     * 分页查询下载记录
     * @param userId 用户ID
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 下载记录列表和总数
     */
    Map<String, Object> getDownloadList(Long userId, Integer status, int page, int size);

    /**
     * 按状态统计下载记录
     * @param userId 用户ID
     * @return 状态统计
     */
    List<Map<String, Object>> getStatusStats(Long userId);

    /**
     * 按资源类型统计下载记录
     * @param userId 用户ID
     * @return 类型统计
     */
    List<Map<String, Object>> getResourceTypeStats(Long userId);

    /**
     * 更新下载进度
     * @param id 下载记录ID
     * @param progress 进度
     * @param downloadedSize 已下载大小
     * @return 是否成功
     */
    boolean updateProgress(Long id, Integer progress, Long downloadedSize);

    /**
     * 完成下载
     * @param id 下载记录ID
     * @return 是否成功
     */
    boolean completeDownload(Long id);

    /**
     * 标记下载失败
     * @param id 下载记录ID
     * @param failureReason 失败原因
     * @return 是否成功
     */
    boolean markDownloadFailed(Long id, String failureReason);

    /**
     * 获取待下载的记录
     * @param limit 数量限制
     * @return 待下载记录列表
     */
    List<OfflineDownload> getPendingDownloads(int limit);

    /**
     * 获取下载中的记录
     * @param limit 数量限制
     * @return 下载中记录列表
     */
    List<OfflineDownload> getDownloadingRecords(int limit);

    /**
     * 统计用户下载总量
     * @param userId 用户ID
     * @return 下载总量（字节）
     */
    Long getTotalDownloadSize(Long userId);

    /**
     * 统计用户已完成的下载数量
     * @param userId 用户ID
     * @return 已完成下载数量
     */
    Integer getCompletedCount(Long userId);

    /**
     * 清理过期的下载记录
     * @param days 天数
     * @return 清理的记录数量
     */
    int cleanExpiredDownloads(int days);

    /**
     * 批量开始下载
     * @param downloads 下载记录列表
     * @return 成功数量
     */
    int batchStartDownload(List<OfflineDownload> downloads);

    /**
     * 检查资源是否已下载
     * @param userId 用户ID
     * @param resourceId 资源ID
     * @return 是否已下载
     */
    boolean isResourceDownloaded(Long userId, Long resourceId);

    /**
     * 获取已下载的资源列表
     * @param userId 用户ID
     * @param resourceType 资源类型
     * @param page 页码
     * @param size 每页大小
     * @return 资源列表和总数
     */
    Map<String, Object> getDownloadedResources(Long userId, Integer resourceType, int page, int size);
}
