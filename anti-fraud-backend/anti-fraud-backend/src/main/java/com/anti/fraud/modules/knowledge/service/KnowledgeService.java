package com.anti.fraud.modules.knowledge.service;

import com.anti.fraud.modules.knowledge.dto.KnowledgeQueryDTO;
import com.anti.fraud.modules.knowledge.vo.CategoryVO;
import com.anti.fraud.modules.knowledge.vo.KnowledgeDetailVO;
import com.anti.fraud.modules.knowledge.vo.KnowledgeListVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface KnowledgeService {

    List<CategoryVO> getCategories();

    Page<KnowledgeListVO> getKnowledgePage(KnowledgeQueryDTO queryDTO);

    KnowledgeDetailVO getKnowledgeDetail(Long id);

    void collectKnowledge(Long id);

    void uncollectKnowledge(Long id);

    void recordProgress(Long id, Integer duration);

    void likeKnowledge(Long id);

    List<KnowledgeListVO> getRecommendKnowledge();

    List<KnowledgeListVO> getHotKnowledge();
}

