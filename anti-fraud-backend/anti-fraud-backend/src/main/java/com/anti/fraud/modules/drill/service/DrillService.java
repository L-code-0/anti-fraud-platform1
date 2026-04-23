package com.anti.fraud.modules.drill.service;

import com.anti.fraud.modules.drill.entity.DrillRecord;
import com.anti.fraud.modules.drill.entity.DrillScenario;
import java.util.List;

/**
 * 演练服务
 */
public interface DrillService {

    /**
     * 获取演练场景列表
     */
    List<DrillScenario> getScenarioList();

    /**
     * 保存演练记录
     */
    void saveDrillRecord(DrillRecord record);

    /**
     * 获取用户演练记录列表
     */
    List<DrillRecord> getDrillRecordList(String userId);
}
