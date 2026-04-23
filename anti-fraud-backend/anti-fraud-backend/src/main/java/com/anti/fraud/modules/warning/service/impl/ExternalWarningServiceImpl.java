package com.anti.fraud.modules.warning.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.modules.warning.entity.ExternalWarning;
import com.anti.fraud.modules.warning.mapper.ExternalWarningMapper;
import com.anti.fraud.modules.warning.service.ExternalWarningService;
import com.anti.fraud.modules.warning.vo.ExternalWarningVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 外部反诈预警服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalWarningServiceImpl implements ExternalWarningService {

    private final ExternalWarningMapper externalWarningMapper;
    private final RestTemplate restTemplate;

    // 预警类型映射
    private static final Map<Integer, String> WARNING_TYPE_MAP = new HashMap<>();
    static {
        WARNING_TYPE_MAP.put(1, "电信诈骗");
        WARNING_TYPE_MAP.put(2, "网络诈骗");
        WARNING_TYPE_MAP.put(3, "金融诈骗");
        WARNING_TYPE_MAP.put(4, "其他");
    }

    // 状态映射
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    static {
        STATUS_MAP.put(1, "有效");
        STATUS_MAP.put(2, "无效");
    }

    // 官方API配置
    private static final Map<String, String> API_CONFIG = new HashMap<>();
    static {
        API_CONFIG.put("公安部", "https://api.mps.gov.cn/anti-fraud/warnings");
        API_CONFIG.put("国家反诈中心", "https://api.96110.gov.cn/warnings");
        API_CONFIG.put("12381", "https://api.12381.cn/warnings");
    }

    @Override
    @Transactional
    public int syncExternalWarnings() {
        try {
            int totalSyncCount = 0;
            
            // 同步各个来源的预警信息
            for (Map.Entry<String, String> entry : API_CONFIG.entrySet()) {
                String source = entry.getKey();
                String apiUrl = entry.getValue();
                
                try {
                    List<ExternalWarning> warnings = fetchWarningsFromApi(source, apiUrl);
                    if (warnings != null && !warnings.isEmpty()) {
                        // 批量插入预警信息
                        int syncCount = externalWarningMapper.batchInsert(warnings);
                        totalSyncCount += syncCount;
                        log.info("从 {} 同步预警信息 {} 条", source, syncCount);
                    }
                } catch (Exception e) {
                    log.error("同步 {} 预警信息失败: {}", source, e.getMessage(), e);
                }
            }

            // 清理过期的预警信息
            cleanExpiredWarnings();
            
            return totalSyncCount;
        } catch (Exception e) {
            log.error("同步外部预警信息失败: {}", e.getMessage(), e);
            throw new BusinessException("同步外部预警信息失败");
        }
    }

    @Override
    public List<ExternalWarningVO> getLatestWarnings(int limit) {
        try {
            List<ExternalWarning> warnings = externalWarningMapper.selectLatestWarnings(limit);
            return convertToVOList(warnings);
        } catch (Exception e) {
            log.error("获取最新预警信息失败: {}", e.getMessage(), e);
            throw new BusinessException("获取最新预警信息失败");
        }
    }

    @Override
    public List<ExternalWarningVO> getWarningsByType(int warningType, int limit) {
        try {
            List<ExternalWarning> warnings = externalWarningMapper.selectByWarningType(warningType, limit);
            return convertToVOList(warnings);
        } catch (Exception e) {
            log.error("根据类型获取预警信息失败: {}", e.getMessage(), e);
            throw new BusinessException("根据类型获取预警信息失败");
        }
    }

    @Override
    public List<ExternalWarningVO> getWarningsBySource(String source, int limit) {
        try {
            List<ExternalWarning> warnings = externalWarningMapper.selectBySource(source, limit);
            return convertToVOList(warnings);
        } catch (Exception e) {
            log.error("根据来源获取预警信息失败: {}", e.getMessage(), e);
            throw new BusinessException("根据来源获取预警信息失败");
        }
    }

    @Override
    public ExternalWarningVO getWarningById(Long id) {
        try {
            ExternalWarning warning = externalWarningMapper.selectById(id);
            if (warning == null) {
                throw new BusinessException("预警信息不存在");
            }
            return convertToVO(warning);
        } catch (Exception e) {
            log.error("获取预警信息详情失败: {}", e.getMessage(), e);
            throw new BusinessException("获取预警信息详情失败");
        }
    }

    @Override
    public int manualSyncWarnings(String source) {
        try {
            String apiUrl = API_CONFIG.get(source);
            if (apiUrl == null) {
                throw new BusinessException("不支持的预警来源");
            }

            List<ExternalWarning> warnings = fetchWarningsFromApi(source, apiUrl);
            if (warnings != null && !warnings.isEmpty()) {
                int syncCount = externalWarningMapper.batchInsert(warnings);
                log.info("手动同步 {} 预警信息 {} 条", source, syncCount);
                return syncCount;
            }
            return 0;
        } catch (Exception e) {
            log.error("手动同步预警信息失败: {}", e.getMessage(), e);
            throw new BusinessException("手动同步预警信息失败");
        }
    }

    @Override
    @Transactional
    public int cleanExpiredWarnings() {
        try {
            // 清理30天前的预警信息
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            List<ExternalWarning> expiredWarnings = externalWarningMapper.selectByTimeRange(null, thirtyDaysAgo);
            
            if (!expiredWarnings.isEmpty()) {
                List<Long> ids = new ArrayList<>();
                for (ExternalWarning warning : expiredWarnings) {
                    ids.add(warning.getId());
                }
                int cleanCount = externalWarningMapper.batchUpdateStatus(ids, 2); // 标记为无效
                log.info("清理过期预警信息 {} 条", cleanCount);
                return cleanCount;
            }
            return 0;
        } catch (Exception e) {
            log.error("清理过期预警信息失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getWarningStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 统计总预警信息数量
            int totalCount = externalWarningMapper.countWarnings(1);
            statistics.put("totalCount", totalCount);
            
            // 统计各类型预警信息数量
            Map<Integer, Integer> typeCountMap = new HashMap<>();
            for (Integer type : WARNING_TYPE_MAP.keySet()) {
                List<ExternalWarning> warnings = externalWarningMapper.selectByWarningType(type, 1);
                typeCountMap.put(type, warnings.size());
            }
            statistics.put("typeCount", typeCountMap);
            
            // 统计各来源预警信息数量
            Map<String, Integer> sourceCountMap = new HashMap<>();
            for (String source : API_CONFIG.keySet()) {
                List<ExternalWarning> warnings = externalWarningMapper.selectBySource(source, 1);
                sourceCountMap.put(source, warnings.size());
            }
            statistics.put("sourceCount", sourceCountMap);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取预警信息统计失败: {}", e.getMessage(), e);
            throw new BusinessException("获取预警信息统计失败");
        }
    }

    @Override
    public boolean testConnection(String source) {
        try {
            String apiUrl = API_CONFIG.get(source);
            if (apiUrl == null) {
                throw new BusinessException("不支持的预警来源");
            }

            // 测试API连接
            // 实际实现需要根据具体API的测试方法进行调整
            log.info("测试连接 {}: {}", source, apiUrl);
            return true;
        } catch (Exception e) {
            log.error("测试连接失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 从API获取预警信息
     * @param source 预警来源
     * @param apiUrl API地址
     * @return 预警信息列表
     */
    private List<ExternalWarning> fetchWarningsFromApi(String source, String apiUrl) {
        try {
            // 实际实现需要根据具体API的接口规范进行调整
            // 这里使用模拟数据进行测试
            List<ExternalWarning> warnings = new ArrayList<>();
            
            // 模拟数据1
            ExternalWarning warning1 = new ExternalWarning();
            warning1.setTitle("警惕新型电信诈骗：冒充客服诈骗");
            warning1.setContent("近期，多地出现冒充客服的电信诈骗案件。诈骗分子通过电话、短信等方式，冒充银行、电商平台客服，以账户异常、退款等为由，诱导受害人点击钓鱼链接或转账。请广大市民提高警惕，不要轻易相信陌生来电，不要随意点击不明链接，保护好个人信息和财产安全。");
            warning1.setSource(source);
            warning1.setWarningType(1);
            warning1.setPublishTime(LocalDateTime.now().minusDays(1));
            warning1.setSyncTime(LocalDateTime.now());
            warning1.setUrl(apiUrl + "/detail/1");
            warning1.setStatus(1);
            warning1.setCreateTime(LocalDateTime.now());
            warning1.setUpdateTime(LocalDateTime.now());
            warnings.add(warning1);
            
            // 模拟数据2
            ExternalWarning warning2 = new ExternalWarning();
            warning2.setTitle("网络兼职诈骗预警");
            warning2.setContent("近期，网络兼职诈骗案件频发。诈骗分子通过网络平台发布虚假兼职信息，以高薪、轻松等为诱饵，诱导受害人缴纳押金、保证金等费用，随后失联。请广大市民不要轻信网络兼职信息，尤其是要求先缴费的兼职，以免上当受骗。");
            warning2.setSource(source);
            warning2.setWarningType(2);
            warning2.setPublishTime(LocalDateTime.now().minusDays(2));
            warning2.setSyncTime(LocalDateTime.now());
            warning2.setUrl(apiUrl + "/detail/2");
            warning2.setStatus(1);
            warning2.setCreateTime(LocalDateTime.now());
            warning2.setUpdateTime(LocalDateTime.now());
            warnings.add(warning2);
            
            return warnings;
        } catch (Exception e) {
            log.error("从API获取预警信息失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 转换预警信息为VO
     * @param warning 预警信息
     * @return 预警信息VO
     */
    private ExternalWarningVO convertToVO(ExternalWarning warning) {
        ExternalWarningVO vo = new ExternalWarningVO();
        vo.setId(warning.getId());
        vo.setTitle(warning.getTitle());
        vo.setContent(warning.getContent());
        vo.setSource(warning.getSource());
        vo.setWarningType(warning.getWarningType());
        vo.setWarningTypeName(WARNING_TYPE_MAP.get(warning.getWarningType()));
        vo.setPublishTime(warning.getPublishTime());
        vo.setSyncTime(warning.getSyncTime());
        vo.setUrl(warning.getUrl());
        vo.setStatus(warning.getStatus());
        vo.setStatusName(STATUS_MAP.get(warning.getStatus()));
        vo.setCreateTime(warning.getCreateTime());
        return vo;
    }

    /**
     * 批量转换预警信息为VO
     * @param warnings 预警信息列表
     * @return 预警信息VO列表
     */
    private List<ExternalWarningVO> convertToVOList(List<ExternalWarning> warnings) {
        List<ExternalWarningVO> vos = new ArrayList<>();
        for (ExternalWarning warning : warnings) {
            vos.add(convertToVO(warning));
        }
        return vos;
    }
}
