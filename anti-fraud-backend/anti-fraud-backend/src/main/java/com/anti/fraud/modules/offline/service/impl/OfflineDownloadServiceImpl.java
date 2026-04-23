package com.anti.fraud.modules.offline.service.impl;

import com.anti.fraud.modules.offline.entity.OfflineDownload;
import com.anti.fraud.modules.offline.mapper.OfflineDownloadMapper;
import com.anti.fraud.modules.offline.service.OfflineDownloadService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 离线下载服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OfflineDownloadServiceImpl extends ServiceImpl<OfflineDownloadMapper, OfflineDownload> implements OfflineDownloadService {

    private final OfflineDownloadMapper offlineDownloadMapper;

    // 下载目录
    private static final String DOWNLOAD_DIR = "D:\\anti-fraud-platform\\downloads\\";

    @Override
    @Transactional
    public boolean startDownload(OfflineDownload offlineDownload) {
        try {
            // 检查资源是否已下载
            if (isResourceDownloaded(offlineDownload.getUserId(), offlineDownload.getResourceId())) {
                log.info("资源已下载: userId={}, resourceId={}", offlineDownload.getUserId(), offlineDownload.getResourceId());
                return false;
            }

            // 初始化下载记录
            offlineDownload.setStatus(1); // 待下载
            offlineDownload.setProgress(0);
            offlineDownload.setDownloadedSize(0L);
            offlineDownload.setDeleted(0);
            offlineDownload.setCreateTime(LocalDateTime.now());
            offlineDownload.setUpdateTime(LocalDateTime.now());

            // 生成存储路径
            String storagePath = generateStoragePath(offlineDownload);
            offlineDownload.setStoragePath(storagePath);

            boolean success = save(offlineDownload);
            if (success) {
                // 异步开始下载
                new Thread(() -> {
                    try {
                        doDownload(offlineDownload.getId());
                    } catch (Exception e) {
                        log.error("下载失败: {}", e.getMessage(), e);
                        markDownloadFailed(offlineDownload.getId(), e.getMessage());
                    }
                }).start();
            }
            return success;
        } catch (Exception e) {
            log.error("开始下载失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean pauseDownload(Long id) {
        try {
            OfflineDownload download = getById(id);
            if (download != null && download.getStatus() == 2) { // 下载中
                download.setStatus(1); // 待下载
                download.setUpdateTime(LocalDateTime.now());
                return updateById(download);
            }
            return false;
        } catch (Exception e) {
            log.error("暂停下载失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean resumeDownload(Long id) {
        try {
            OfflineDownload download = getById(id);
            if (download != null && download.getStatus() == 1) { // 待下载
                download.setStatus(2); // 下载中
                download.setUpdateTime(LocalDateTime.now());
                boolean success = updateById(download);
                if (success) {
                    // 异步继续下载
                    new Thread(() -> {
                        try {
                            doDownload(id);
                        } catch (Exception e) {
                            log.error("下载失败: {}", e.getMessage(), e);
                            markDownloadFailed(id, e.getMessage());
                        }
                    }).start();
                }
                return success;
            }
            return false;
        } catch (Exception e) {
            log.error("恢复下载失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean cancelDownload(Long id) {
        try {
            OfflineDownload download = getById(id);
            if (download != null && (download.getStatus() == 1 || download.getStatus() == 2)) { // 待下载或下载中
                download.setStatus(4); // 下载失败
                download.setFailureReason("用户取消下载");
                download.setUpdateTime(LocalDateTime.now());
                boolean success = updateById(download);
                if (success) {
                    // 删除已下载的文件
                    deleteDownloadedFile(download.getStoragePath());
                }
                return success;
            }
            return false;
        } catch (Exception e) {
            log.error("取消下载失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteDownload(Long id) {
        try {
            OfflineDownload download = getById(id);
            if (download != null) {
                download.setDeleted(1);
                download.setUpdateTime(LocalDateTime.now());
                boolean success = updateById(download);
                if (success) {
                    // 删除已下载的文件
                    if (download.getStatus() == 3) { // 已完成
                        deleteDownloadedFile(download.getStoragePath());
                    }
                }
                return success;
            }
            return false;
        } catch (Exception e) {
            log.error("删除下载记录失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public OfflineDownload getDownloadById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取下载详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getDownloadList(Long userId, Integer status, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<OfflineDownload> downloads = offlineDownloadMapper.selectByUserId(userId, status, offset, size);
            // 计算总数
            int total = downloads.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", downloads);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询下载记录列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public List<Map<String, Object>> getStatusStats(Long userId) {
        try {
            return offlineDownloadMapper.selectStatusStats(userId);
        } catch (Exception e) {
            log.error("按状态统计下载记录失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getResourceTypeStats(Long userId) {
        try {
            return offlineDownloadMapper.selectResourceTypeStats(userId);
        } catch (Exception e) {
            log.error("按资源类型统计下载记录失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean updateProgress(Long id, Integer progress, Long downloadedSize) {
        try {
            int rows = offlineDownloadMapper.updateProgress(id, progress, downloadedSize);
            return rows > 0;
        } catch (Exception e) {
            log.error("更新下载进度失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean completeDownload(Long id) {
        try {
            int rows = offlineDownloadMapper.completeDownload(id, 3, 100, LocalDateTime.now());
            return rows > 0;
        } catch (Exception e) {
            log.error("完成下载失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean markDownloadFailed(Long id, String failureReason) {
        try {
            int rows = offlineDownloadMapper.markDownloadFailed(id, 4, failureReason);
            return rows > 0;
        } catch (Exception e) {
            log.error("标记下载失败失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<OfflineDownload> getPendingDownloads(int limit) {
        try {
            return offlineDownloadMapper.selectPendingDownloads(limit);
        } catch (Exception e) {
            log.error("获取待下载的记录失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<OfflineDownload> getDownloadingRecords(int limit) {
        try {
            return offlineDownloadMapper.selectDownloadingRecords(limit);
        } catch (Exception e) {
            log.error("获取下载中的记录失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Long getTotalDownloadSize(Long userId) {
        try {
            return offlineDownloadMapper.selectTotalDownloadSize(userId);
        } catch (Exception e) {
            log.error("统计用户下载总量失败: {}", e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public Integer getCompletedCount(Long userId) {
        try {
            return offlineDownloadMapper.selectCompletedCount(userId);
        } catch (Exception e) {
            log.error("统计用户已完成的下载数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    @Transactional
    public int cleanExpiredDownloads(int days) {
        try {
            // 这里简化处理，实际应该根据创建时间筛选过期记录
            // 并删除对应的文件
            return 0;
        } catch (Exception e) {
            log.error("清理过期的下载记录失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    @Transactional
    public int batchStartDownload(List<OfflineDownload> downloads) {
        try {
            int successCount = 0;
            for (OfflineDownload download : downloads) {
                if (startDownload(download)) {
                    successCount++;
                }
            }
            return successCount;
        } catch (Exception e) {
            log.error("批量开始下载失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public boolean isResourceDownloaded(Long userId, Long resourceId) {
        try {
            List<OfflineDownload> downloads = offlineDownloadMapper.selectByResourceId(resourceId, 3);
            for (OfflineDownload download : downloads) {
                if (download.getUserId().equals(userId)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("检查资源是否已下载失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getDownloadedResources(Long userId, Integer resourceType, int page, int size) {
        try {
            // 这里简化处理，实际应该根据资源类型筛选已完成的下载记录
            List<OfflineDownload> downloads = offlineDownloadMapper.selectByUserId(userId, 3, (page - 1) * size, size);
            int total = downloads.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", downloads);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("获取已下载的资源列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    // 执行下载
    private void doDownload(Long id) throws Exception {
        OfflineDownload download = getById(id);
        if (download == null) {
            throw new Exception("下载记录不存在");
        }

        // 更新状态为下载中
        download.setStatus(2);
        download.setStartTime(LocalDateTime.now());
        updateById(download);

        // 确保下载目录存在
        File dir = new File(DOWNLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 开始下载
        URL url = new URL(download.getResourcePath());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP请求失败: " + responseCode);
        }

        // 获取文件大小
        long fileSize = connection.getContentLengthLong();
        download.setFileSize(fileSize);
        updateById(download);

        // 读取输入流并写入文件
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = new java.io.FileOutputStream(download.getStoragePath())) {

            byte[] buffer = new byte[1024 * 1024]; // 1MB缓冲区
            int bytesRead;
            long totalRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalRead += bytesRead;

                // 更新下载进度
                int progress = (int) (totalRead * 100 / fileSize);
                updateProgress(id, progress, totalRead);

                // 检查是否被暂停或取消
                OfflineDownload currentDownload = getById(id);
                if (currentDownload != null && currentDownload.getStatus() != 2) {
                    // 下载被暂停或取消
                    return;
                }
            }

            // 下载完成
            completeDownload(id);
        } finally {
            connection.disconnect();
        }
    }

    // 生成存储路径
    private String generateStoragePath(OfflineDownload offlineDownload) {
        // 按用户ID和资源类型创建目录
        String userDir = DOWNLOAD_DIR + offlineDownload.getUserId() + "/";
        String typeDir = userDir + offlineDownload.getResourceType() + "/";

        File dir = new File(typeDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成文件名
        String fileName = System.currentTimeMillis() + "_" + offlineDownload.getResourceName();
        return typeDir + fileName;
    }

    // 删除已下载的文件
    private void deleteDownloadedFile(String storagePath) {
        if (storagePath != null) {
            File file = new File(storagePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    log.warn("删除文件失败: {}", storagePath);
                }
            }
        }
    }
}
