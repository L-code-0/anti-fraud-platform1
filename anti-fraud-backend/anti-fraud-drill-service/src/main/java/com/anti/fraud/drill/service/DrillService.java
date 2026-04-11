package com.anti.fraud.drill.service;

import com.anti.fraud.drill.entity.DrillScenario;
import com.anti.fraud.drill.entity.DrillRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Map;

public interface DrillService extends IService<DrillScenario> {

    /**
     * 获取演练场景列表
     */
    Map<String, Object> getScenarios();

    /**
     * 获取场景详情
     */
    DrillScenario getScenarioById(Long id);

    /**
     * 提交演练结果
     */
    Map<String, Object> submitDrill(Long userId, Long scenarioId, Map<String, Object> answers);

    /**
     * 获取用户演练记录
     */
    Map<String, Object> getDrillRecords(Long userId);

    /**
     * 获取演练统计
     */
    Map<String, Object> getDrillStats(Long userId);

    /**
     * 生成演练报告
     */
    Map<String, Object> generateDrillReport(Long recordId);
}
