package com.anti.fraud.modules.report.service;

import com.anti.fraud.modules.report.dto.ReportHandleDTO;
import com.anti.fraud.modules.report.dto.WarningCreateDTO;
import com.anti.fraud.modules.report.vo.ReportVO;
import com.anti.fraud.modules.report.vo.WarningVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ReportManageService {

    // 举报管理
    Page<ReportVO> getReportPage(Integer page, Integer size, Integer status, Integer riskLevel);

    ReportVO getReportDetail(Long id);

    void handleReport(Long id, ReportHandleDTO handleDTO);

    // 预警管理
    Page<WarningVO> getWarningPage(Integer page, Integer size, Integer warningLevel, Integer status);

    void createWarning(WarningCreateDTO createDTO);

    void updateWarning(Long id, WarningCreateDTO createDTO);

    void deleteWarning(Long id);

    void publishWarning(Long id);
}