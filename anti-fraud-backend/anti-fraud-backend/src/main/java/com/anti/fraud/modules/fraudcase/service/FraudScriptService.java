package com.anti.fraud.modules.fraudcase.service;

import com.anti.fraud.modules.fraudcase.entity.FraudScript;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface FraudScriptService extends IService<FraudScript> {
    List<FraudScript> getScriptList(String fraudType, String targetGroup, int page, int size);
    List<FraudScript> searchScripts(String keyword, int page, int size);
    boolean increaseUsageCount(Long id);
    List<FraudScript> getHotScripts(int count);
    Map<String, Object> importScripts(List<FraudScript> scripts);
    Map<String, Object> exportScripts(List<Long> ids);
}
