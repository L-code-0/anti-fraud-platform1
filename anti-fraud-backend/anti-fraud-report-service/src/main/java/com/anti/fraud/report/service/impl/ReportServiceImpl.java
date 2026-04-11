package com.anti.fraud.report.service.impl;

import com.anti.fraud.report.entity.Report;
import com.anti.fraud.report.mapper.ReportMapper;
import com.anti.fraud.report.service.ReportService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 举报服务实现类
 */
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    @Override
    public Long submitReport(Report report) {
        // 生成举报编号
        String reportNo = "RPT" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                String.format("%03d", new Random().nextInt(1000));
        report.setReportNo(reportNo);
        report.setStatus("pending");
        report.setCreateTime(LocalDateTime.now());
        report.setUpdateTime(LocalDateTime.now());
        report.setDeleted(0);
        save(report);
        return report.getId();
    }

    @Override
    public List<Report> getUserReportHistory(Long userId, int page, int size) {
        IPage<Report> reportPage = new Page<>(page, size);
        LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Report::getUserId, userId)
                .eq(Report::getDeleted, 0)
                .orderByDesc(Report::getCreateTime);
        page(reportPage, queryWrapper);
        return reportPage.getRecords();
    }

    @Override
    public String autoClassify(String description) {
        // 基于关键词的简单分类
        description = description.toLowerCase();
        if (description.contains("电话") || description.contains("来电") || description.contains("客服")) {
            return "phone";
        } else if (description.contains("网站") || description.contains("app") || description.contains("网购") || description.contains("投资")) {
            return "online";
        } else if (description.contains("短信") || description.contains("验证码") || description.contains("链接")) {
            return "sms";
        } else if (description.contains("微信") || description.contains("qq") || description.contains("交友") || description.contains("聊天")) {
            return "social";
        } else {
            return "other";
        }
    }

    @Override
    public boolean processReport(Long reportId, Long handlerId, String result) {
        Report report = getById(reportId);
        if (report == null) {
            return false;
        }
        report.setStatus("resolved");
        report.setProcessingResult(result);
        report.setHandlerId(handlerId);
        report.setProcessingTime(LocalDateTime.now());
        report.setUpdateTime(LocalDateTime.now());
        return updateById(report);
    }

    @Override
    public ReportStatistics getReportStatistics(Long userId) {
        ReportStatistics statistics = new ReportStatistics();
        LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Report::getUserId, userId)
                .eq(Report::getDeleted, 0);
        statistics.setTotalReports((int) count(queryWrapper));

        LambdaQueryWrapper<Report> processedQuery = new LambdaQueryWrapper<>();
        processedQuery.eq(Report::getUserId, userId)
                .eq(Report::getStatus, "resolved")
                .eq(Report::getDeleted, 0);
        statistics.setProcessedReports((int) count(processedQuery));

        LambdaQueryWrapper<Report> pendingQuery = new LambdaQueryWrapper<>();
        pendingQuery.eq(Report::getUserId, userId)
                .eq(Report::getStatus, "pending")
                .eq(Report::getDeleted, 0);
        statistics.setPendingReports((int) count(pendingQuery));

        // 假设每个举报获得30积分
        statistics.setPointsEarned(statistics.getTotalReports() * 30);

        return statistics;
    }
}
