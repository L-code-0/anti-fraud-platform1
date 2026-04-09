package com.anti.fraud.modules.test.service;

import com.anti.fraud.modules.test.dto.AnswerSubmitDTO;
import com.anti.fraud.modules.test.vo.PaperVO;
import com.anti.fraud.modules.test.vo.QuestionVO;
import com.anti.fraud.modules.test.vo.TestResultVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface TestService {

    List<PaperVO> getAvailablePapers();

    List<QuestionVO> getPaperQuestions(Long paperId);

    Long startTest(Long paperId);

    TestResultVO submitAnswers(AnswerSubmitDTO submitDTO);

    TestResultVO getTestResult(Long recordId);

    Page<TestResultVO> getMyTestRecords(Integer page, Integer size);

    List<TestResultVO> getRankingList(Long paperId);
}
