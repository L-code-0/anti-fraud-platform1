package com.anti.fraud.modules.test.service;

import com.anti.fraud.modules.test.dto.PaperCreateDTO;
import com.anti.fraud.modules.test.dto.QuestionCreateDTO;
import com.anti.fraud.modules.test.vo.PaperVO;
import com.anti.fraud.modules.test.vo.QuestionVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TestManageService {

    Page<QuestionVO> getQuestionPage(Integer page, Integer size, Long categoryId, Integer questionType);

    void createQuestion(QuestionCreateDTO createDTO);

    void updateQuestion(Long id, QuestionCreateDTO createDTO);

    void deleteQuestion(Long id);

    Page<PaperVO> getPaperPage(Integer page, Integer size);

    void createPaper(PaperCreateDTO createDTO);

    void updatePaper(Long id, PaperCreateDTO createDTO);

    void deletePaper(Long id);
}
