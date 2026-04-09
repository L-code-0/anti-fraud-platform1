package com.anti.fraud.modules.knowledge.service;

import com.anti.fraud.modules.knowledge.dto.KnowledgeCreateDTO;
import com.anti.fraud.modules.knowledge.vo.KnowledgeListVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface KnowledgeManageService {

    Page<KnowledgeListVO> getKnowledgePage(Integer page, Integer size, Long categoryId, Integer status, Integer auditStatus);

    void createKnowledge(KnowledgeCreateDTO createDTO);

    void updateKnowledge(Long id, KnowledgeCreateDTO createDTO);

    void deleteKnowledge(Long id);

    void auditKnowledge(Long id, Integer auditStatus);

    void updateKnowledgeStatus(Long id, Integer status);

    void setRecommend(Long id, Boolean isHot, Boolean isRecommend);
}
