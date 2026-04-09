package com.anti.fraud.modules.report.service;

import com.anti.fraud.modules.report.dto.ReportSubmitDTO;
import com.anti.fraud.modules.report.vo.ReportVO;
import com.anti.fraud.modules.report.vo.WarningVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface ReportService {

    void submitReport(ReportSubmitDTO submitDTO);

    Page<ReportVO> getMyReports(Integer page, Integer size);

    ReportVO getReportDetail(Long id);

    List<WarningVO> getWarningList();

    WarningVO getWarningDetail(Long id);

    List<WarningVO> getLatestWarnings();
}
