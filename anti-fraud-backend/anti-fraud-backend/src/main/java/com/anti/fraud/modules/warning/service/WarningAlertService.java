package com.anti.fraud.modules.warning.service;

import com.anti.fraud.modules.warning.dto.WarningProcessDTO;
import com.anti.fraud.modules.warning.dto.WarningQueryDTO;
import com.anti.fraud.modules.warning.vo.WarningAlertVO;
import com.anti.fraud.modules.warning.vo.WarningStatsVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 预警警报服务接口
 */
public interface WarningAlertService {

    /**
     * 获取预警统计
     */
    WarningStatsVO getStats();

    /**
     * 分页查询预警列表
     */
    Page<WarningAlertVO> getWarningPage(WarningQueryDTO queryDTO);

    /**
     * 获取预警详情
     */
    WarningAlertVO getWarningDetail(Long id);

    /**
     * 处理预警
     */
    void processWarning(WarningProcessDTO processDTO);

    /**
     * 批量处理预警
     */
    void batchProcess(java.util.List<Long> ids, Integer status, String result);
}
