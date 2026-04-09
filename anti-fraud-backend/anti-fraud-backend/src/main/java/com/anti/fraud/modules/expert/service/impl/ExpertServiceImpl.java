package com.anti.fraud.modules.expert.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.modules.expert.dto.AdviceCreateDTO;
import com.anti.fraud.modules.expert.dto.AnalysisCreateDTO;
import com.anti.fraud.modules.expert.entity.ExpertAdvice;
import com.anti.fraud.modules.expert.entity.ExpertAnalysis;
import com.anti.fraud.modules.expert.mapper.ExpertAdviceMapper;
import com.anti.fraud.modules.expert.mapper.ExpertAnalysisMapper;
import com.anti.fraud.modules.expert.service.ExpertService;
import com.anti.fraud.modules.expert.vo.AdviceVO;
import com.anti.fraud.modules.expert.vo.AnalysisVO;
import com.anti.fraud.modules.expert.vo.ExpertStatsVO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 专家咨询服务实现
 */
@Service
@RequiredArgsConstructor
public class ExpertServiceImpl implements ExpertService {

    private final ExpertAnalysisMapper analysisMapper;
    private final ExpertAdviceMapper adviceMapper;

    @Override
    public ExpertStatsVO getStats(Long expertId) {
        ExpertStatsVO stats = new ExpertStatsVO();

        if (expertId != null) {
            // 获取指定专家的统计
            stats.setAnalysisCount(analysisMapper.countByExpertId(expertId));
            stats.setAdviceCount(adviceMapper.countByExpertId(expertId));
            stats.setViewCount(analysisMapper.sumViewCountByExpertId(expertId) +
                    adviceMapper.sumViewCountByExpertId(expertId));
            stats.setThumbCount(analysisMapper.sumThumbCountByExpertId(expertId) +
                    adviceMapper.sumThumbCountByExpertId(expertId));
        } else {
            // 获取全局统计
            stats.setAnalysisCount(Math.toIntExact(analysisMapper.selectCount(null)));
            stats.setAdviceCount(Math.toIntExact(adviceMapper.selectCount(null)));
            stats.setViewCount(0L);
            stats.setThumbCount(0L);
        }

        return stats;
    }

    @Override
    public Page<AnalysisVO> getAnalysisPage(Integer page, Integer size, Long expertId, Integer type) {
        Page<Map<String, Object>> pageParam = new Page<>(page, size);

        Page<Map<String, Object>> resultPage = analysisMapper.selectAnalysisPage(
                pageParam, expertId, type
        );

        Page<AnalysisVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        voPage.setRecords(resultPage.getRecords().stream()
                .map(this::convertToAnalysisVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public AnalysisVO getAnalysisDetail(Long id) {
        ExpertAnalysis analysis = analysisMapper.selectById(id);
        if (analysis == null) {
            throw new BusinessException("案例分析不存在");
        }

        // 增加阅读量
        LambdaUpdateWrapper<ExpertAnalysis> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ExpertAnalysis::getId, id)
                .setSql("view_count = view_count + 1");
        analysisMapper.update(null, updateWrapper);

        return convertToAnalysisDetailVO(analysis);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAnalysis(AnalysisCreateDTO createDTO, Long expertId, String expertName) {
        ExpertAnalysis analysis = new ExpertAnalysis();
        analysis.setTitle(createDTO.getTitle());
        analysis.setType(createDTO.getType());
        analysis.setSummary(createDTO.getSummary());
        analysis.setContent(createDTO.getContent());
        analysis.setExpertId(expertId);
        analysis.setExpertName(expertName);
        analysis.setViewCount(0);
        analysis.setThumbCount(0);
        analysis.setStatus(1);
        analysis.setCreateTime(LocalDateTime.now());

        analysisMapper.insert(analysis);
        return analysis.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnalysis(Long id, Long expertId) {
        ExpertAnalysis analysis = analysisMapper.selectById(id);
        if (analysis == null) {
            throw new BusinessException("案例分析不存在");
        }

        if (expertId != null && !analysis.getExpertId().equals(expertId)) {
            throw new BusinessException("无权删除他人的案例分析");
        }

        analysisMapper.deleteById(id);
    }

    @Override
    public Page<AdviceVO> getAdvicePage(Integer page, Integer size, Long expertId, String category) {
        Page<Map<String, Object>> pageParam = new Page<>(page, size);

        Page<Map<String, Object>> resultPage = adviceMapper.selectAdvicePage(
                pageParam, expertId, category
        );

        Page<AdviceVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        voPage.setRecords(resultPage.getRecords().stream()
                .map(this::convertToAdviceVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public AdviceVO getAdviceDetail(Long id) {
        ExpertAdvice advice = adviceMapper.selectById(id);
        if (advice == null) {
            throw new BusinessException("专家建议不存在");
        }

        // 增加阅读量
        LambdaUpdateWrapper<ExpertAdvice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ExpertAdvice::getId, id)
                .setSql("view_count = view_count + 1");
        adviceMapper.update(null, updateWrapper);

        return convertToAdviceDetailVO(advice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAdvice(AdviceCreateDTO createDTO, Long expertId, String expertName) {
        ExpertAdvice advice = new ExpertAdvice();
        advice.setTitle(createDTO.getTitle());
        advice.setCategory(createDTO.getCategory());
        advice.setContent(createDTO.getContent());
        advice.setExpertId(expertId);
        advice.setExpertName(expertName);
        advice.setViewCount(0);
        advice.setThumbCount(0);
        advice.setStatus(1);
        advice.setCreateTime(LocalDateTime.now());

        adviceMapper.insert(advice);
        return advice.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdvice(Long id, Long expertId) {
        ExpertAdvice advice = adviceMapper.selectById(id);
        if (advice == null) {
            throw new BusinessException("专家建议不存在");
        }

        if (expertId != null && !advice.getExpertId().equals(expertId)) {
            throw new BusinessException("无权删除他人的专家建议");
        }

        adviceMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void thumb(Long type, Long id) {
        if (type == 1) {
            // 案例分析点赞
            LambdaUpdateWrapper<ExpertAnalysis> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ExpertAnalysis::getId, id)
                    .setSql("thumb_count = thumb_count + 1");
            analysisMapper.update(null, updateWrapper);
        } else if (type == 2) {
            // 专家建议点赞
            LambdaUpdateWrapper<ExpertAdvice> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ExpertAdvice::getId, id)
                    .setSql("thumb_count = thumb_count + 1");
            adviceMapper.update(null, updateWrapper);
        }
    }

    /**
     * 转换为案例分析 VO
     */
    private AnalysisVO convertToAnalysisVO(Map<String, Object> map) {
        AnalysisVO vo = new AnalysisVO();
        vo.setId(((Number) map.get("id")).longValue());
        vo.setTitle((String) map.get("title"));
        vo.setType((Integer) map.get("type"));
        vo.setSummary((String) map.get("summary"));
        vo.setExpertName((String) map.get("expertName"));
        vo.setViewCount((Integer) map.get("viewCount"));
        vo.setThumbCount((Integer) map.get("thumbCount"));
        vo.setCreateTime((LocalDateTime) map.get("createTime"));
        return vo;
    }

    /**
     * 转换为案例分析详情 VO
     */
    private AnalysisVO convertToAnalysisDetailVO(ExpertAnalysis analysis) {
        AnalysisVO vo = new AnalysisVO();
        vo.setId(analysis.getId());
        vo.setTitle(analysis.getTitle());
        vo.setType(analysis.getType());
        vo.setSummary(analysis.getSummary());
        vo.setContent(analysis.getContent());
        vo.setExpertName(analysis.getExpertName());
        vo.setViewCount(analysis.getViewCount() + 1);
        vo.setThumbCount(analysis.getThumbCount());
        vo.setCreateTime(analysis.getCreateTime());
        return vo;
    }

    /**
     * 转换为专家建议 VO
     */
    private AdviceVO convertToAdviceVO(Map<String, Object> map) {
        AdviceVO vo = new AdviceVO();
        vo.setId(((Number) map.get("id")).longValue());
        vo.setTitle((String) map.get("title"));
        vo.setCategory((String) map.get("category"));
        vo.setContent((String) map.get("content"));
        vo.setAuthor((String) map.get("author"));
        vo.setViewCount((Integer) map.get("viewCount"));
        vo.setThumbCount((Integer) map.get("thumbCount"));
        vo.setCreateTime((LocalDateTime) map.get("createTime"));
        return vo;
    }

    /**
     * 转换为专家建议详情 VO
     */
    private AdviceVO convertToAdviceDetailVO(ExpertAdvice advice) {
        AdviceVO vo = new AdviceVO();
        vo.setId(advice.getId());
        vo.setTitle(advice.getTitle());
        vo.setCategory(advice.getCategory());
        vo.setContent(advice.getContent());
        vo.setAuthor(advice.getExpertName());
        vo.setViewCount(advice.getViewCount() + 1);
        vo.setThumbCount(advice.getThumbCount());
        vo.setCreateTime(advice.getCreateTime());
        return vo;
    }
}
