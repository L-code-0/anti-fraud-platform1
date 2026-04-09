package com.anti.fraud.modules.expert.service;

import com.anti.fraud.modules.expert.dto.AdviceCreateDTO;
import com.anti.fraud.modules.expert.dto.AnalysisCreateDTO;
import com.anti.fraud.modules.expert.vo.AdviceVO;
import com.anti.fraud.modules.expert.vo.AnalysisVO;
import com.anti.fraud.modules.expert.vo.ExpertStatsVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 专家咨询服务接口
 */
public interface ExpertService {

    /**
     * 获取专家统计数据
     */
    ExpertStatsVO getStats(Long expertId);

    /**
     * 分页查询案例分析列表
     */
    Page<AnalysisVO> getAnalysisPage(Integer page, Integer size, Long expertId, Integer type);

    /**
     * 获取案例分析详情
     */
    AnalysisVO getAnalysisDetail(Long id);

    /**
     * 发布案例分析
     */
    Long createAnalysis(AnalysisCreateDTO createDTO, Long expertId, String expertName);

    /**
     * 删除案例分析
     */
    void deleteAnalysis(Long id, Long expertId);

    /**
     * 分页查询专家建议列表
     */
    Page<AdviceVO> getAdvicePage(Integer page, Integer size, Long expertId, String category);

    /**
     * 获取专家建议详情
     */
    AdviceVO getAdviceDetail(Long id);

    /**
     * 发布专家建议
     */
    Long createAdvice(AdviceCreateDTO createDTO, Long expertId, String expertName);

    /**
     * 删除专家建议
     */
    void deleteAdvice(Long id, Long expertId);

    /**
     * 点赞
     */
    void thumb(Long type, Long id);
}
