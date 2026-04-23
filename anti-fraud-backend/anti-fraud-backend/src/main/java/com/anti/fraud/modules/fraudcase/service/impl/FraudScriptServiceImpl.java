package com.anti.fraud.modules.fraudcase.service.impl;

import com.anti.fraud.modules.fraudcase.entity.FraudScript;
import com.anti.fraud.modules.fraudcase.mapper.FraudScriptMapper;
import com.anti.fraud.modules.fraudcase.service.FraudScriptService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FraudScriptServiceImpl extends ServiceImpl<FraudScriptMapper, FraudScript> implements FraudScriptService {

    @Override
    public List<FraudScript> getScriptList(String fraudType, String targetGroup, int page, int size) {
        try {
            LambdaQueryWrapper<FraudScript> queryWrapper = new LambdaQueryWrapper<>();
            if (fraudType != null && !fraudType.isEmpty()) {
                queryWrapper.eq(FraudScript::getFraudType, fraudType);
            }
            if (targetGroup != null && !targetGroup.isEmpty()) {
                queryWrapper.eq(FraudScript::getTargetGroup, targetGroup);
            }
            queryWrapper.eq(FraudScript::getStatus, 1);
            queryWrapper.orderByDesc(FraudScript::getCreateTime);

            Page<FraudScript> pageParam = new Page<>(page, size);
            return baseMapper.selectPage(pageParam, queryWrapper).getRecords();
        } catch (Exception e) {
            log.error("获取话术列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<FraudScript> searchScripts(String keyword, int page, int size) {
        try {
            LambdaQueryWrapper<FraudScript> queryWrapper = new LambdaQueryWrapper<>();
            if (keyword != null && !keyword.isEmpty()) {
                queryWrapper.like(FraudScript::getScriptTitle, keyword)
                        .or().like(FraudScript::getScriptContent, keyword)
                        .or().like(FraudScript::getFraudType, keyword);
            }
            queryWrapper.eq(FraudScript::getStatus, 1);
            queryWrapper.orderByDesc(FraudScript::getCreateTime);

            Page<FraudScript> pageParam = new Page<>(page, size);
            return baseMapper.selectPage(pageParam, queryWrapper).getRecords();
        } catch (Exception e) {
            log.error("搜索话术失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean increaseUsageCount(Long id) {
        try {
            FraudScript script = baseMapper.selectById(id);
            if (script != null) {
                script.setUsageCount(script.getUsageCount() + 1);
                baseMapper.updateById(script);
                log.info("话术使用次数增加: id={}, usageCount={}", id, script.getUsageCount());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("增加话术使用次数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<FraudScript> getHotScripts(int count) {
        try {
            LambdaQueryWrapper<FraudScript> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FraudScript::getStatus, 1)
                    .orderByDesc(FraudScript::getUsageCount)
                    .last("LIMIT " + count);
            return baseMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取热门话术失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public Map<String, Object> importScripts(List<FraudScript> scripts) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> failReasons = new ArrayList<>();

        try {
            for (FraudScript script : scripts) {
                try {
                    // 验证必填字段
                    if (script.getScriptTitle() == null || script.getScriptTitle().isEmpty()) {
                        failCount++;
                        failReasons.add("话术标题不能为空");
                        continue;
                    }
                    if (script.getFraudType() == null || script.getFraudType().isEmpty()) {
                        failCount++;
                        failReasons.add("诈骗类型不能为空");
                        continue;
                    }
                    if (script.getScriptContent() == null || script.getScriptContent().isEmpty()) {
                        failCount++;
                        failReasons.add("话术内容不能为空");
                        continue;
                    }

                    // 设置默认值
                    if (script.getStatus() == null) {
                        script.setStatus(1);
                    }
                    if (script.getIsVerified() == null) {
                        script.setIsVerified(0);
                    }
                    if (script.getUsageCount() == null) {
                        script.setUsageCount(0);
                    }

                    baseMapper.insert(script);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("导入失败: " + e.getMessage());
                }
            }

            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("failReasons", failReasons);
            log.info("导入话术完成: 成功{}/失败{}", successCount, failCount);
        } catch (Exception e) {
            log.error("导入话术失败: {}", e.getMessage(), e);
            result.put("successCount", 0);
            result.put("failCount", scripts.size());
            result.put("failReasons", List.of("导入过程出现错误: " + e.getMessage()));
        }

        return result;
    }

    @Override
    public Map<String, Object> exportScripts(List<Long> ids) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<FraudScript> scripts = baseMapper.selectBatchIds(ids);
            result.put("scripts", scripts);
            result.put("count", scripts.size());
            log.info("导出话术完成: 数量{}", scripts.size());
        } catch (Exception e) {
            log.error("导出话术失败: {}", e.getMessage(), e);
            result.put("scripts", new ArrayList<>());
            result.put("count", 0);
            result.put("error", e.getMessage());
        }
        return result;
    }
}
