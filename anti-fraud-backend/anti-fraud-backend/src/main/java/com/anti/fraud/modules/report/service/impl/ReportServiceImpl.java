package com.anti.fraud.modules.report.service.impl;

import com.anti.fraud.modules.report.entity.Report;
import com.anti.fraud.modules.report.mapper.ReportMapper;
import com.anti.fraud.modules.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 举报服务实现
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;

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
}
