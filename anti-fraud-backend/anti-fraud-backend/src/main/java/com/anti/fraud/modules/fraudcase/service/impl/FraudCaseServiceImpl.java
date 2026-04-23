package com.anti.fraud.modules.fraudcase.service.impl;

import com.anti.fraud.modules.fraudcase.entity.FraudCase;
import com.anti.fraud.modules.fraudcase.mapper.FraudCaseMapper;
import com.anti.fraud.modules.fraudcase.service.FraudCaseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FraudCaseServiceImpl extends ServiceImpl<FraudCaseMapper, FraudCase> implements FraudCaseService {

    @Override
    public boolean createCase(FraudCase fraudCase) {
        return this.save(fraudCase);
    }

    @Override
    public boolean updateCase(FraudCase fraudCase) {
        return this.updateById(fraudCase);
    }

    @Override
    public boolean deleteCase(Long id) {
        return this.removeById(id);
    }

    @Override
    public FraudCase getCaseById(Long id) {
        return this.getById(id);
    }

    @Override
    public List<FraudCase> getCaseList(String category, String caseType, String difficulty, int page, int size) {
        LambdaQueryWrapper<FraudCase> queryWrapper = new LambdaQueryWrapper<>();
        if (caseType != null) {
            queryWrapper.eq(FraudCase::getCaseType, caseType);
        }
        queryWrapper.eq(FraudCase::getStatus, 1);
        queryWrapper.orderByDesc(FraudCase::getCreateTime);
        return this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size), queryWrapper).getRecords();
    }

    @Override
    public List<FraudCase> searchCases(String keyword, int page, int size) {
        LambdaQueryWrapper<FraudCase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(FraudCase::getCaseTitle, keyword)
                .or().like(FraudCase::getFraudProcess, keyword)
                .or().like(FraudCase::getVictimProfile, keyword);
        queryWrapper.eq(FraudCase::getStatus, 1);
        queryWrapper.orderByDesc(FraudCase::getCreateTime);
        return this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size), queryWrapper).getRecords();
    }

    @Override
    public boolean increaseViewCount(Long id) {
        FraudCase fraudCase = this.getById(id);
        if (fraudCase != null) {
            fraudCase.setViewCount(fraudCase.getViewCount() + 1);
            return this.updateById(fraudCase);
        }
        return false;
    }

    @Override
    public boolean increaseLikeCount(Long id) {
        // 这里可以根据需要实现点赞功能
        return true;
    }

    @Override
    public List<FraudCase> getHotCases(int count) {
        LambdaQueryWrapper<FraudCase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FraudCase::getStatus, 1);
        queryWrapper.orderByDesc(FraudCase::getViewCount);
        return this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, count), queryWrapper).getRecords();
    }

    @Override
    public List<FraudCase> getRecommendedCases(Long userId, int count) {
        LambdaQueryWrapper<FraudCase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FraudCase::getStatus, 1);
        queryWrapper.orderByDesc(FraudCase::getCreateTime);
        return this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, count), queryWrapper).getRecords();
    }

    @Override
    public Map<String, Object> getCaseStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCases", this.count());
        stats.put("verifiedCases", this.count(new LambdaQueryWrapper<FraudCase>().eq(FraudCase::getIsVerified, 1)));
        return stats;
    }

    @Override
    public Map<String, Object> importCases(List<FraudCase> cases) {
        Map<String, Object> result = new HashMap<>();
        try {
            this.saveBatch(cases);
            result.put("success", true);
            result.put("imported", cases.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> exportCases(List<Long> ids) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<FraudCase> cases = this.listByIds(ids);
            result.put("success", true);
            result.put("cases", cases);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
