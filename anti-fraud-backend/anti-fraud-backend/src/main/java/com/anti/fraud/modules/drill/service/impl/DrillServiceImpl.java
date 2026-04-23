package com.anti.fraud.modules.drill.service.impl;

import com.anti.fraud.modules.drill.entity.DrillRecord;
import com.anti.fraud.modules.drill.entity.DrillScenario;
import com.anti.fraud.modules.drill.mapper.DrillRecordMapper;
import com.anti.fraud.modules.drill.mapper.DrillScenarioMapper;
import com.anti.fraud.modules.drill.service.DrillService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 演练服务实现
 */
@Service
@RequiredArgsConstructor
public class DrillServiceImpl implements DrillService {

    private final DrillScenarioMapper drillScenarioMapper;
    private final DrillRecordMapper drillRecordMapper;

    @Override
    public List<DrillScenario> getScenarioList() {
        return drillScenarioMapper.selectList(null);
    }

    @Override
    public void saveDrillRecord(DrillRecord record) {
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        drillRecordMapper.insert(record);
    }

    @Override
    public List<DrillRecord> getDrillRecordList(String userId) {
        QueryWrapper<DrillRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return drillRecordMapper.selectList(wrapper);
    }
}
