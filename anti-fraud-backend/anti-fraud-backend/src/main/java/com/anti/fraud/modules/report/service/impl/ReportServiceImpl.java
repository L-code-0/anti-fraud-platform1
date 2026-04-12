package com.anti.fraud.modules.report.service.impl;

import com.anti.fraud.modules.report.entity.Report;
import com.anti.fraud.modules.report.entity.ReportProgress;
import com.anti.fraud.modules.report.entity.ReportPoint;
import com.anti.fraud.modules.report.mapper.ReportMapper;
import com.anti.fraud.modules.report.mapper.ReportProgressMapper;
import com.anti.fraud.modules.report.mapper.ReportPointMapper;
import com.anti.fraud.modules.report.service.ReportService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;
    private final ReportProgressMapper reportProgressMapper;
    private final ReportPointMapper reportPointMapper;

    @Override
    public boolean createReport(Report report) {
        try {
            report.setStatus("pending"); // 初始状态：待处理
            reportMapper.insert(report);
            
            // 添加初始进度
            ReportProgress progress = new ReportProgress();
            progress.setReportId(report.getId());
            progress.setStatus("pending");
            progress.setDescription("举报已提交，等待处理");
            reportProgressMapper.insert(progress);
            
            log.info("创建举报成功: id={}, userId={}, reportType={}", report.getId(), report.getUserId(), report.getReportType());
            return true;
        } catch (Exception e) {
            log.error("创建举报失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateReport(Report report) {
        try {
            reportMapper.updateById(report);
            log.info("更新举报成功: id={}", report.getId());
            return true;
        } catch (Exception e) {
            log.error("更新举报失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteReport(Long id) {
        try {
            // 删除举报进度
            LambdaQueryWrapper<ReportProgress> progressQuery = new LambdaQueryWrapper<>();
            progressQuery.eq(ReportProgress::getReportId, id);
            reportProgressMapper.delete(progressQuery);
            
            // 删除举报
            reportMapper.deleteById(id);
            log.info("删除举报成功: id={}", id);
            return true;
        } catch (Exception e) {
            log.error("删除举报失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Report getReportById(Long id) {
        try {
            return reportMapper.selectById(id);
        } catch (Exception e) {
            log.error("获取举报详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Report> getReportList(String status, String reportType, int page, int size) {
        try {
            LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
            if (status != null) {
                queryWrapper.eq(Report::getStatus, status);
            }
            if (reportType != null) {
                queryWrapper.eq(Report::getReportType, reportType);
            }
            queryWrapper.orderByDesc(Report::getCreateTime);

            IPage<Report> pageInfo = new Page<>(page, size);
            reportMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取举报列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Report> getUserReportHistory(Long userId, int page, int size) {
        try {
            LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Report::getUserId, userId)
                    .orderByDesc(Report::getCreateTime);

            IPage<Report> pageInfo = new Page<>(page, size);
            reportMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取用户举报历史失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean handleReport(Long id, String status, String feedback, Long handlerId, String handlerName) {
        try {
            // 更新举报状态
            Report report = reportMapper.selectById(id);
            if (report == null) {
                return false;
            }
            report.setStatus(status);
            report.setFeedback(feedback);
            report.setHandlerId(handlerId);
            report.setHandlerName(handlerName);
            reportMapper.updateById(report);
            
            // 添加处理进度
            ReportProgress progress = new ReportProgress();
            progress.setReportId(id);
            progress.setStatus(status);
            progress.setDescription(feedback);
            progress.setHandlerId(handlerId);
            progress.setHandlerName(handlerName);
            reportProgressMapper.insert(progress);
            
            // 如果举报成功处理，计算积分
            if ("success".equals(status)) {
                calculateReportPoints(id, report.getUserId(), report.getUserName());
            }
            
            log.info("处理举报成功: id={}, status={}, handlerId={}", id, status, handlerId);
            return true;
        } catch (Exception e) {
            log.error("处理举报失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<ReportProgress> getReportProgress(Long reportId) {
        try {
            LambdaQueryWrapper<ReportProgress> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ReportProgress::getReportId, reportId)
                    .orderByAsc(ReportProgress::getCreateTime);
            return reportProgressMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取举报进度失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean addReportProgress(ReportProgress progress) {
        try {
            reportProgressMapper.insert(progress);
            log.info("添加举报进度成功: reportId={}, status={}", progress.getReportId(), progress.getStatus());
            return true;
        } catch (Exception e) {
            log.error("添加举报进度失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> calculateReportPoints(Long reportId, Long userId, String userName) {
        try {
            // 模拟积分计算
            int points = 10; // 基础积分
            
            // 根据举报类型和处理结果调整积分
            Report report = reportMapper.selectById(reportId);
            if (report != null) {
                if ("success".equals(report.getStatus())) {
                    points += 5; // 成功处理额外加5分
                }
                if ("电信诈骗".equals(report.getReportType())) {
                    points += 3; // 电信诈骗类型额外加3分
                }
            }
            
            // 保存积分记录
            ReportPoint reportPoint = new ReportPoint();
            reportPoint.setUserId(userId);
            reportPoint.setUserName(userName);
            reportPoint.setPoints(points);
            reportPoint.setType("report");
            reportPoint.setDescription("举报奖励积分");
            reportPoint.setReportId(reportId);
            reportPointMapper.insert(reportPoint);
            
            // 返回积分信息
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("points", points);
            result.put("message", "积分计算成功");
            
            log.info("计算举报积分成功: reportId={}, userId={}, points={}", reportId, userId, points);
            return result;
        } catch (Exception e) {
            log.error("计算举报积分失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "积分计算失败");
            return result;
        }
    }

    @Override
    public Map<String, Object> getUserReportPoints(Long userId) {
        try {
            // 模拟用户积分信息
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalPoints", 150);
            stats.put("reportCount", 10);
            stats.put("successCount", 8);
            stats.put("pointHistory", Arrays.asList(
                    Map.of("date", "2026-04-10", "points", 15),
                    Map.of("date", "2026-04-05", "points", 12),
                    Map.of("date", "2026-04-01", "points", 10)
            ));
            
            log.info("获取用户举报积分成功: userId={}", userId);
            return stats;
        } catch (Exception e) {
            log.error("获取用户举报积分失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getReportAnalysis(String startTime, String endTime, String reportType) {
        try {
            // 模拟举报数据分析
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("totalReports", 100);
            analysis.put("successRate", 85.5);
            analysis.put("averageProcessingTime", 24); // 平均处理时间（小时）
            analysis.put("reportTypeDistribution", Map.of(
                    "电信诈骗", 40,
                    "网络诈骗", 30,
                    "金融诈骗", 20,
                    "其他", 10
            ));
            analysis.put("statusDistribution", Map.of(
                    "pending", 15,
                    "processing", 25,
                    "success", 50,
                    "rejected", 10
            ));
            analysis.put("trend", Arrays.asList(
                    Map.of("date", "2026-04-01", "count", 10),
                    Map.of("date", "2026-04-02", "count", 15),
                    Map.of("date", "2026-04-03", "count", 8),
                    Map.of("date", "2026-04-04", "count", 12),
                    Map.of("date", "2026-04-05", "count", 10),
                    Map.of("date", "2026-04-06", "count", 15),
                    Map.of("date", "2026-04-07", "count", 20)
            ));
            
            log.info("获取举报数据分析成功");
            return analysis;
        } catch (Exception e) {
            log.error("获取举报数据分析失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public List<Report> searchReports(String keyword, int page, int size) {
        try {
            LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(Report::getReportContent, keyword)
                    .or()
                    .like(Report::getReportType, keyword)
                    .orderByDesc(Report::getCreateTime);

            IPage<Report> pageInfo = new Page<>(page, size);
            reportMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("搜索举报失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> batchHandleReports(List<Long> ids, String status, String feedback, Long handlerId, String handlerName) {
        try {
            int successCount = 0;
            int failCount = 0;
            
            for (Long id : ids) {
                boolean success = handleReport(id, status, feedback, handlerId, handlerName);
                if (success) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "批量处理举报成功");
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            
            log.info("批量处理举报成功: total={}, success={}, fail={}", ids.size(), successCount, failCount);
            return result;
        } catch (Exception e) {
            log.error("批量处理举报失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "批量处理举报失败");
            return result;
        }
    }
}
