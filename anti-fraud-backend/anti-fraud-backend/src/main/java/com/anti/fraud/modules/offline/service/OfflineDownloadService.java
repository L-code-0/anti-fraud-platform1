package com.anti.fraud.modules.offline.service;

import com.anti.fraud.modules.offline.entity.OfflineDownload;
import java.util.List;
import java.util.Map;

public interface OfflineDownloadService {

    /**
     * 开始离线下载
     * @param userId 用户ID
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 下载任务信息
     */
    OfflineDownload startDownload(Long userId, Long contentId, String contentType);

    /**
     * 获取下载任务状态
     * @param id 任务ID
     * @param userId 用户ID
     * @return 任务状态
     */
    OfflineDownload getDownloadStatus(Long id, Long userId);

    /**
     * 获取用户的下载任务列表
     * @param userId 用户ID
     * @param status 状态过滤
     * @return 下载任务列表
     */
    List<OfflineDownload> getDownloadList(Long userId, Integer status);

    /**
     * 取消下载任务
     * @param id 任务ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean cancelDownload(Long id, Long userId);

    /**
     * 删除下载任务
     * @param id 任务ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteDownload(Long id, Long userId);

    /**
     * 清理过期下载任务
     * @param days 过期天数
     * @return 清理数量
     */
    int cleanExpiredDownloads(int days);

    /**
     * 获取下载文件
     * @param id 任务ID
     * @param userId 用户ID
     * @return 文件信息
     */
    Map<String, Object> getDownloadFile(Long id, Long userId);
}
