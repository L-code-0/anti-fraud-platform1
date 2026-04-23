package com.anti.fraud.modules.report.service;

import com.anti.fraud.modules.report.entity.Warning;
import java.util.List;

/**
 * 预警服务
 */
public interface WarningService {

    /**
     * 创建预警
     */
    void createWarning(Warning warning);

    /**
     * 获取用户预警列表
     */
    List<Warning> getUserWarnings(String userId);
}
