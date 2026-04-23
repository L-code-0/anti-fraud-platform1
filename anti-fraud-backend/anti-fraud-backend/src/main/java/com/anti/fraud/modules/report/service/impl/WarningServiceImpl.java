package com.anti.fraud.modules.report.service.impl;

import com.anti.fraud.modules.report.entity.Warning;
import com.anti.fraud.modules.report.mapper.WarningMapper;
import com.anti.fraud.modules.report.service.WarningService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预警服务实现
 */
@Service
@RequiredArgsConstructor
public class WarningServiceImpl implements WarningService {

    private final WarningMapper warningMapper;

    @Override
    public void createWarning(Warning warning) {
        warning.setStatus("未处理");
        warning.setCreateTime(LocalDateTime.now());
        warning.setUpdateTime(LocalDateTime.now());
        warningMapper.insert(warning);
    }

    @Override
    public List<Warning> getUserWarnings(String userId) {
        QueryWrapper<Warning> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return warningMapper.selectList(wrapper);
    }
}
