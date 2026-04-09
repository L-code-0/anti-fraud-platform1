package com.anti.fraud.modules.knowledge.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.knowledge.dto.KnowledgeCreateDTO;
import com.anti.fraud.modules.knowledge.entity.KnowledgeContent;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeContentMapper;
import com.anti.fraud.modules.knowledge.service.KnowledgeManageService;
import com.anti.fraud.modules.knowledge.vo.KnowledgeListVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgeManageServiceImpl implements KnowledgeManageService {

    private final KnowledgeContentMapper knowledgeMapper;

    @Override
    public Page<KnowledgeListVO> getKnowledgePage(Integer page, Integer size, Long categoryId, Integer status, Integer auditStatus) {
        Page<KnowledgeContent> contentPage = new Page<>(page, size);

        LambdaQueryWrapper<KnowledgeContent> wrapper = new LambdaQueryWrapper<>();

        if (categoryId != null) {
            wrapper.eq(KnowledgeContent::getCategoryId, categoryId);
        }

        if (status != null) {
            wrapper.eq(KnowledgeContent::getStatus, status);
        }

        if (auditStatus != null) {
            wrapper.eq(KnowledgeContent::getAuditStatus, auditStatus);
        }

        wrapper.orderByDesc(KnowledgeContent::getCreateTime);

        knowledgeMapper.selectPage(contentPage, wrapper);

        // 手动转换避免类型不兼容
        Page<KnowledgeListVO> result = new Page<>(contentPage.getCurrent(), contentPage.getSize(), contentPage.getTotal());
        result.setRecords(contentPage.getRecords().stream().map(this::convertToListVO).collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional
    public void createKnowledge(KnowledgeCreateDTO createDTO) {
        KnowledgeContent knowledge = new KnowledgeContent();
        knowledge.setTitle(createDTO.getTitle());
        knowledge.setCategoryId(createDTO.getCategoryId());
        knowledge.setContentType(createDTO.getContentType());
        knowledge.setSummary(createDTO.getSummary());
        knowledge.setContent(createDTO.getContent());
        knowledge.setCoverImage(createDTO.getCoverImage());
        knowledge.setVideoUrl(createDTO.getVideoUrl());
        knowledge.setVideoDuration(createDTO.getVideoDuration());
        knowledge.setTags(createDTO.getTags());
        knowledge.setSource(createDTO.getSource());
        knowledge.setPublishTime(createDTO.getPublishTime() != null ? createDTO.getPublishTime() : LocalDateTime.now());
        knowledge.setIsTop(createDTO.getIsTop() != null && createDTO.getIsTop() ? 1 : 0);
        knowledge.setIsHot(createDTO.getIsHot() != null && createDTO.getIsHot() ? 1 : 0);
        knowledge.setIsRecommend(createDTO.getIsRecommend() != null && createDTO.getIsRecommend() ? 1 : 0);
        knowledge.setViewCount(0);
        knowledge.setLikeCount(0);
        knowledge.setCollectCount(0);
        knowledge.setShareCount(0);
        knowledge.setStatus(1);
        knowledge.setAuditStatus(0);

        // 设置作者信息
        Long userId = SecurityUtils.getCurrentUserId();
        String username = SecurityUtils.getCurrentUsername();
        knowledge.setAuthorId(userId);
        knowledge.setAuthorName(username);

        knowledgeMapper.insert(knowledge);
    }

    @Override
    @Transactional
    public void updateKnowledge(Long id, KnowledgeCreateDTO createDTO) {
        KnowledgeContent knowledge = knowledgeMapper.selectById(id);
        if (knowledge == null) {
            throw new BusinessException("知识内容不存在");
        }

        knowledge.setTitle(createDTO.getTitle());
        knowledge.setCategoryId(createDTO.getCategoryId());
        knowledge.setContentType(createDTO.getContentType());
        knowledge.setSummary(createDTO.getSummary());
        knowledge.setContent(createDTO.getContent());
        knowledge.setCoverImage(createDTO.getCoverImage());
        knowledge.setVideoUrl(createDTO.getVideoUrl());
        knowledge.setVideoDuration(createDTO.getVideoDuration());
        knowledge.setTags(createDTO.getTags());
        knowledge.setSource(createDTO.getSource());

        if (createDTO.getPublishTime() != null) {
            knowledge.setPublishTime(createDTO.getPublishTime());
        }

        if (createDTO.getIsTop() != null) {
            knowledge.setIsTop(createDTO.getIsTop() ? 1 : 0);
        }

        if (createDTO.getIsHot() != null) {
            knowledge.setIsHot(createDTO.getIsHot() ? 1 : 0);
        }

        if (createDTO.getIsRecommend() != null) {
            knowledge.setIsRecommend(createDTO.getIsRecommend() ? 1 : 0);
        }

        knowledgeMapper.updateById(knowledge);
    }

    @Override
    @Transactional
    public void deleteKnowledge(Long id) {
        knowledgeMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void auditKnowledge(Long id, Integer auditStatus) {
        KnowledgeContent knowledge = knowledgeMapper.selectById(id);
        if (knowledge == null) {
            throw new BusinessException("知识内容不存在");
        }

        knowledge.setAuditStatus(auditStatus);
        knowledgeMapper.updateById(knowledge);
    }

    @Override
    @Transactional
    public void updateKnowledgeStatus(Long id, Integer status) {
        KnowledgeContent knowledge = knowledgeMapper.selectById(id);
        if (knowledge == null) {
            throw new BusinessException("知识内容不存在");
        }

        knowledge.setStatus(status);
        knowledgeMapper.updateById(knowledge);
    }

    @Override
    @Transactional
    public void setRecommend(Long id, Boolean isHot, Boolean isRecommend) {
        KnowledgeContent knowledge = knowledgeMapper.selectById(id);
        if (knowledge == null) {
            throw new BusinessException("知识内容不存在");
        }

        if (isHot != null) {
            knowledge.setIsHot(isHot ? 1 : 0);
        }

        if (isRecommend != null) {
            knowledge.setIsRecommend(isRecommend ? 1 : 0);
        }

        knowledgeMapper.updateById(knowledge);
    }

    private KnowledgeListVO convertToListVO(KnowledgeContent content) {
        KnowledgeListVO vo = new KnowledgeListVO();
        vo.setId(content.getId());
        vo.setTitle(content.getTitle());
        vo.setCategoryId(content.getCategoryId());
        vo.setContentType(content.getContentType());
        vo.setSummary(content.getSummary());
        vo.setCoverImage(content.getCoverImage());
        vo.setViewCount(content.getViewCount());
        vo.setLikeCount(content.getLikeCount());
        vo.setCollectCount(content.getCollectCount());
        vo.setPublishTime(content.getPublishTime());
        vo.setAuthorName(content.getAuthorName());
        return vo;
    }
}

