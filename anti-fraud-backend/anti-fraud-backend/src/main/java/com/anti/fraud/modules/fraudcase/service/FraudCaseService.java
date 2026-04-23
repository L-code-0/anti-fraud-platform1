package com.anti.fraud.modules.fraudcase.service;

import com.anti.fraud.modules.fraudcase.entity.FraudCase;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface FraudCaseService extends IService<FraudCase> {
    boolean createCase(FraudCase fraudCase);
    boolean updateCase(FraudCase fraudCase);
    boolean deleteCase(Long id);
    FraudCase getCaseById(Long id);
    List<FraudCase> getCaseList(String category, String caseType, String difficulty, int page, int size);
    List<FraudCase> searchCases(String keyword, int page, int size);
    boolean increaseViewCount(Long id);
    boolean increaseLikeCount(Long id);
    List<FraudCase> getHotCases(int count);
    List<FraudCase> getRecommendedCases(Long userId, int count);
    Map<String, Object> getCaseStats();
    Map<String, Object> importCases(List<FraudCase> cases);
    Map<String, Object> exportCases(List<Long> ids);
}
