package com.anti.fraud.modules.offline.service.impl;

import com.anti.fraud.modules.knowledge.entity.KnowledgeContent;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeContentMapper;
import com.anti.fraud.modules.offline.entity.OfflineDownload;
import com.anti.fraud.modules.offline.mapper.OfflineDownloadMapper;
import com.anti.fraud.modules.offline.service.OfflineDownloadService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfflineDownloadServiceImpl implements OfflineDownloadService {

    private final OfflineDownloadMapper offlineDownloadMapper;
    private final KnowledgeContentMapper knowledgeContentMapper;

    private static final String DOWNLOAD_DIR = "d:/anti-fraud-downloads";

    @Override
    public OfflineDownload startDownload(Long userId, Long contentId, String contentType) {
        try {
            // 创建下载目录
            createDownloadDir();

            // 获取内容信息
            KnowledgeContent content = knowledgeContentMapper.selectById(contentId);
            if (content == null) {
                throw new Exception("内容不存在");
            }

            // 创建下载任务
            OfflineDownload download = new OfflineDownload();
            download.setUserId(userId);
            download.setUserName("用户" + userId);
            download.setContentId(contentId);
            download.setContentTitle(content.getTitle());
            download.setContentType(contentType);
            download.setDownloadStatus(1); // 下载中
            download.setDownloadProgress(0);
            offlineDownloadMapper.insert(download);

            // 模拟下载过程（实际项目中应该使用线程或异步任务）
            simulateDownload(download);

            return download;
        } catch (Exception e) {
            log.error("开始离线下载失败: {}", e.getMessage(), e);
            // 创建失败的任务记录
            OfflineDownload download = new OfflineDownload();
            download.setUserId(userId);
            download.setUserName("用户" + userId);
            download.setContentId(contentId);
            download.setContentType(contentType);
            download.setDownloadStatus(3); // 下载失败
            download.setDownloadProgress(0);
            download.setErrorMessage(e.getMessage());
            offlineDownloadMapper.insert(download);
            return download;
        }
    }

    @Override
    public OfflineDownload getDownloadStatus(Long id, Long userId) {
        try {
            LambdaQueryWrapper<OfflineDownload> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OfflineDownload::getId, id)
                    .eq(OfflineDownload::getUserId, userId);
            return offlineDownloadMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("获取下载任务状态失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<OfflineDownload> getDownloadList(Long userId, Integer status) {
        try {
            LambdaQueryWrapper<OfflineDownload> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OfflineDownload::getUserId, userId)
                    .orderByDesc(OfflineDownload::getCreateTime);
            
            if (status != null) {
                queryWrapper.eq(OfflineDownload::getDownloadStatus, status);
            }
            
            return offlineDownloadMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取下载任务列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean cancelDownload(Long id, Long userId) {
        try {
            LambdaQueryWrapper<OfflineDownload> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OfflineDownload::getId, id)
                    .eq(OfflineDownload::getUserId, userId);
            
            OfflineDownload download = offlineDownloadMapper.selectOne(queryWrapper);
            if (download != null && download.getDownloadStatus() == 1) {
                download.setDownloadStatus(4); // 已取消
                offlineDownloadMapper.updateById(download);
                log.info("取消下载任务成功: id={}, userId={}", id, userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("取消下载任务失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteDownload(Long id, Long userId) {
        try {
            LambdaQueryWrapper<OfflineDownload> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OfflineDownload::getId, id)
                    .eq(OfflineDownload::getUserId, userId);
            
            OfflineDownload download = offlineDownloadMapper.selectOne(queryWrapper);
            if (download != null) {
                // 删除文件
                if (download.getDownloadPath() != null) {
                    File file = new File(download.getDownloadPath());
                    if (file.exists()) {
                        file.delete();
                    }
                }
                // 删除记录
                offlineDownloadMapper.deleteById(id);
                log.info("删除下载任务成功: id={}, userId={}", id, userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除下载任务失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int cleanExpiredDownloads(int days) {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minus(days, ChronoUnit.DAYS);
            
            LambdaQueryWrapper<OfflineDownload> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.lt(OfflineDownload::getCreateTime, cutoffTime);
            
            List<OfflineDownload> expiredDownloads = offlineDownloadMapper.selectList(queryWrapper);
            int count = 0;
            
            for (OfflineDownload download : expiredDownloads) {
                // 删除文件
                if (download.getDownloadPath() != null) {
                    File file = new File(download.getDownloadPath());
                    if (file.exists()) {
                        file.delete();
                    }
                }
                // 删除记录
                offlineDownloadMapper.deleteById(download.getId());
                count++;
            }
            
            log.info("清理过期下载任务: {}个", count);
            return count;
        } catch (Exception e) {
            log.error("清理过期下载任务失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getDownloadFile(Long id, Long userId) {
        try {
            LambdaQueryWrapper<OfflineDownload> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OfflineDownload::getId, id)
                    .eq(OfflineDownload::getUserId, userId);
            
            OfflineDownload download = offlineDownloadMapper.selectOne(queryWrapper);
            if (download == null) {
                throw new Exception("下载任务不存在");
            }
            
            if (download.getDownloadStatus() != 2) {
                throw new Exception("下载未完成");
            }
            
            File file = new File(download.getDownloadPath());
            if (!file.exists()) {
                throw new Exception("文件不存在");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("filePath", download.getDownloadPath());
            result.put("fileName", file.getName());
            result.put("fileSize", download.getFileSize());
            result.put("contentType", download.getContentType());
            
            return result;
        } catch (Exception e) {
            log.error("获取下载文件失败: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 创建下载目录
     */
    private void createDownloadDir() throws IOException {
        Path path = Paths.get(DOWNLOAD_DIR);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    /**
     * 模拟下载过程
     */
    private void simulateDownload(OfflineDownload download) {
        // 模拟下载进度
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i += 10) {
                    Thread.sleep(1000); // 模拟下载延迟
                    download.setDownloadProgress(i);
                    offlineDownloadMapper.updateById(download);
                }
                
                // 模拟下载完成
                String fileName = "knowledge_" + download.getContentId() + ".html";
                String filePath = DOWNLOAD_DIR + File.separator + fileName;
                
                // 创建模拟文件
                File file = new File(filePath);
                file.createNewFile();
                
                download.setDownloadPath(filePath);
                download.setFileSize("1024KB");
                download.setDownloadStatus(2); // 下载完成
                download.setDownloadProgress(100);
                offlineDownloadMapper.updateById(download);
                
                log.info("下载任务完成: id={}, contentId={}", download.getId(), download.getContentId());
            } catch (Exception e) {
                log.error("模拟下载失败: {}", e.getMessage(), e);
                download.setDownloadStatus(3); // 下载失败
                download.setErrorMessage(e.getMessage());
                offlineDownloadMapper.updateById(download);
            }
        }).start();
    }
}
