package com.anti.fraud.modules.report.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.modules.file.entity.FileInfo;
import com.anti.fraud.modules.file.service.FileService;
import com.anti.fraud.modules.report.entity.Report;
import com.anti.fraud.modules.report.mapper.ReportMapper;
import com.anti.fraud.modules.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 举报服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;
    private final FileService fileService;

    @Override
    public void submitReport(Report report) {
        report.setStatus("待处理");
        report.setCreateTime(LocalDateTime.now());
        report.setUpdateTime(LocalDateTime.now());
        reportMapper.insert(report);
    }

    @Override
    public List<Report> getReportList() {
        return reportMapper.selectList(null);
    }

    @Override
    public Report getReportDetail(Long id) {
        Report report = reportMapper.selectById(id);
        if (report == null) {
            throw new BusinessException("举报不存在");
        }
        return report;
    }

    @Override
    public List<FileInfo> getReportFiles(Long id) {
        Report report = getReportDetail(id);
        List<FileInfo> fileInfos = new ArrayList<>();
        
        if (report.getEvidenceFileIds() != null && !report.getEvidenceFileIds().isEmpty()) {
            String[] fileIdStrs = report.getEvidenceFileIds().split(",");
            for (String fileIdStr : fileIdStrs) {
                try {
                    Long fileId = Long.parseLong(fileIdStr.trim());
                    FileInfo fileInfo = fileService.getFileById(fileId);
                    fileInfos.add(fileInfo);
                } catch (Exception e) {
                    log.warn("获取文件信息失败: {}", e.getMessage());
                }
            }
        }
        
        return fileInfos;
    }

    @Override
    @Transactional
    public void addReportFiles(Long id, List<Long> fileIds) {
        Report report = getReportDetail(id);
        
        // 构建新的文件ID列表
        List<Long> existingFileIds = new ArrayList<>();
        if (report.getEvidenceFileIds() != null && !report.getEvidenceFileIds().isEmpty()) {
            String[] fileIdStrs = report.getEvidenceFileIds().split(",");
            for (String fileIdStr : fileIdStrs) {
                try {
                    existingFileIds.add(Long.parseLong(fileIdStr.trim()));
                } catch (Exception e) {
                    log.warn("解析文件ID失败: {}", e.getMessage());
                }
            }
        }
        
        // 添加新的文件ID
        for (Long fileId : fileIds) {
            if (!existingFileIds.contains(fileId)) {
                existingFileIds.add(fileId);
            }
        }
        
        // 更新文件ID列表
        String newFileIds = String.join(",", existingFileIds.stream().map(String::valueOf).toArray(String[]::new));
        report.setEvidenceFileIds(newFileIds);
        report.setUpdateTime(LocalDateTime.now());
        reportMapper.updateById(report);
        
        log.info("为举报 {} 添加了 {} 个证据文件", id, fileIds.size());
    }

    @Override
    @Transactional
    public void removeReportFile(Long id, Long fileId) {
        Report report = getReportDetail(id);
        
        if (report.getEvidenceFileIds() == null || report.getEvidenceFileIds().isEmpty()) {
            throw new BusinessException("没有证据文件可移除");
        }
        
        // 移除指定的文件ID
        List<Long> existingFileIds = new ArrayList<>();
        String[] fileIdStrs = report.getEvidenceFileIds().split(",");
        for (String fileIdStr : fileIdStrs) {
            try {
                Long existingFileId = Long.parseLong(fileIdStr.trim());
                if (!existingFileId.equals(fileId)) {
                    existingFileIds.add(existingFileId);
                }
            } catch (Exception e) {
                log.warn("解析文件ID失败: {}", e.getMessage());
            }
        }
        
        // 更新文件ID列表
        String newFileIds = existingFileIds.isEmpty() ? null : String.join(",", existingFileIds.stream().map(String::valueOf).toArray(String[]::new));
        report.setEvidenceFileIds(newFileIds);
        report.setUpdateTime(LocalDateTime.now());
        reportMapper.updateById(report);
        
        log.info("从举报 {} 中移除了证据文件 {}", id, fileId);
    }
}
