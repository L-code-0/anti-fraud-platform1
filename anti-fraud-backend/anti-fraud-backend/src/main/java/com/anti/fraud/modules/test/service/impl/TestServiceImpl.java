package com.anti.fraud.modules.test.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.test.dto.AnswerDTO;
import com.anti.fraud.modules.test.dto.AnswerSubmitDTO;
import com.anti.fraud.modules.test.entity.Paper;
import com.anti.fraud.modules.test.entity.Question;
import com.anti.fraud.modules.test.entity.TestRecord;
import com.anti.fraud.modules.test.mapper.PaperMapper;
import com.anti.fraud.modules.test.mapper.QuestionMapper;
import com.anti.fraud.modules.test.mapper.TestRecordMapper;
import com.anti.fraud.modules.test.service.TestService;
import com.anti.fraud.modules.test.vo.PaperVO;
import com.anti.fraud.modules.test.vo.QuestionResultVO;
import com.anti.fraud.modules.test.vo.QuestionVO;
import com.anti.fraud.modules.test.vo.TestResultVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final PaperMapper paperMapper;
    private final QuestionMapper questionMapper;
    private final TestRecordMapper testRecordMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<PaperVO> getAvailablePapers() {
        List<Paper> papers = paperMapper.selectList(
                new LambdaQueryWrapper<Paper>()
                        .eq(Paper::getStatus, 1)
                        .orderByDesc(Paper::getCreateTime)
        );

        return papers.stream()
                .map(this::convertToPaperVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionVO> getPaperQuestions(Long paperId) {
        Paper paper = paperMapper.selectById(paperId);
        if (paper == null) {
            throw new BusinessException("试卷不存在");
        }

        List<Question> questions;
        if (paper.getPaperType() == 1) {
            // 随机组卷
            questions = questionMapper.selectRandomQuestions(null, paper.getQuestionCount());
        } else {
            // 固定试卷 - 从题库随机抽取
            questions = questionMapper.selectList(
                    new LambdaQueryWrapper<Question>()
                            .eq(Question::getStatus, 1)
                            .last("LIMIT " + paper.getQuestionCount())
            );
        }

        return questions.stream()
                .map(this::convertToQuestionVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long startTest(Long paperId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        Paper paper = paperMapper.selectById(paperId);
        if (paper == null) {
            throw new BusinessException("试卷不存在");
        }

        TestRecord record = new TestRecord();
        record.setUserId(userId);
        record.setPaperId(paperId);
        record.setTotalScore(paper.getTotalScore());
        record.setIsCompleted(0);
        record.setStartTime(LocalDateTime.now());

        testRecordMapper.insert(record);

        return record.getId();
    }

    @Override
    @Transactional
    public TestResultVO submitAnswers(AnswerSubmitDTO submitDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        TestRecord record = testRecordMapper.selectById(submitDTO.getRecordId());
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException("测试记录不存在");
        }

        if (record.getIsCompleted() == 1) {
            throw new BusinessException("该测试已提交");
        }

        Paper paper = paperMapper.selectById(record.getPaperId());
        List<Question> questions = questionMapper.selectBatchIds(
                submitDTO.getAnswers().stream()
                        .map(AnswerDTO::getQuestionId)
                        .collect(Collectors.toList())
        );

        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        List<QuestionResultVO> questionResults = new ArrayList<>();
        int correctCount = 0;
        BigDecimal totalScore = BigDecimal.ZERO;

        for (AnswerDTO answer : submitDTO.getAnswers()) {
            Question question = questionMap.get(answer.getQuestionId());
            if (question == null) continue;

            QuestionResultVO result = new QuestionResultVO();
            result.setQuestionId(question.getId());
            result.setContent(question.getContent());
            result.setUserAnswer(answer.getAnswer());
            result.setCorrectAnswer(question.getAnswer());
            result.setAnalysis(question.getAnalysis());
            result.setScore(question.getScore());

            boolean isCorrect = question.getAnswer().equalsIgnoreCase(answer.getAnswer());
            result.setIsCorrect(isCorrect);

            if (isCorrect) {
                correctCount++;
                result.setEarnedScore(question.getScore());
                totalScore = totalScore.add(BigDecimal.valueOf(question.getScore()));
            } else {
                result.setEarnedScore(0);
            }

            questionResults.add(result);
        }

        record.setUserScore(totalScore);
        record.setCorrectCount(correctCount);
        record.setWrongCount(submitDTO.getAnswers().size() - correctCount);
        record.setDuration(submitDTO.getDuration());
        record.setIsCompleted(1);
        record.setIsPassed(totalScore.compareTo(BigDecimal.valueOf(paper.getPassScore())) >= 0 ? 1 : 0);
        record.setSubmitTime(LocalDateTime.now());

        testRecordMapper.updateById(record);

        TestResultVO resultVO = new TestResultVO();
        resultVO.setRecordId(record.getId());
        resultVO.setPaperId(record.getPaperId());
        resultVO.setPaperName(paper.getPaperName());
        resultVO.setTotalScore(record.getTotalScore());
        resultVO.setUserScore(record.getUserScore());
        resultVO.setCorrectCount(record.getCorrectCount());
        resultVO.setWrongCount(record.getWrongCount());
        resultVO.setIsPassed(record.getIsPassed() == 1);
        resultVO.setDuration(record.getDuration());
        resultVO.setQuestions(questionResults);
        resultVO.setSubmitTime(record.getSubmitTime());

        return resultVO;
    }

    @Override
    public TestResultVO getTestResult(Long recordId) {
        Long userId = SecurityUtils.getCurrentUserId();
        TestRecord record = testRecordMapper.selectById(recordId);

        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException("测试记录不存在");
        }

        Paper paper = paperMapper.selectById(record.getPaperId());

        TestResultVO resultVO = new TestResultVO();
        resultVO.setRecordId(record.getId());
        resultVO.setPaperId(record.getPaperId());
        resultVO.setPaperName(paper.getPaperName());
        resultVO.setTotalScore(record.getTotalScore());
        resultVO.setUserScore(record.getUserScore());
        resultVO.setCorrectCount(record.getCorrectCount());
        resultVO.setWrongCount(record.getWrongCount());
        resultVO.setIsPassed(record.getIsPassed() == 1);
        resultVO.setDuration(record.getDuration());
        resultVO.setSubmitTime(record.getSubmitTime());

        return resultVO;
    }

    @Override
    public Page<TestResultVO> getMyTestRecords(Integer page, Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        Page<TestRecord> recordPage = new Page<>(page, size);
        testRecordMapper.selectPage(recordPage,
                new LambdaQueryWrapper<TestRecord>()
                        .eq(TestRecord::getUserId, userId)
                        .eq(TestRecord::getIsCompleted, 1)
                        .orderByDesc(TestRecord::getSubmitTime)
        );

        // 手动转换避免类型不兼容
        Page<TestResultVO> result = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        result.setRecords(recordPage.getRecords().stream().map(record -> {
            Paper paper = paperMapper.selectById(record.getPaperId());
            TestResultVO vo = new TestResultVO();
            vo.setRecordId(record.getId());
            vo.setPaperId(record.getPaperId());
            vo.setPaperName(paper != null ? paper.getPaperName() : "");
            vo.setTotalScore(record.getTotalScore());
            vo.setUserScore(record.getUserScore());
            vo.setCorrectCount(record.getCorrectCount());
            vo.setWrongCount(record.getWrongCount());
            vo.setIsPassed(record.getIsPassed() == 1);
            vo.setDuration(record.getDuration());
            vo.setSubmitTime(record.getSubmitTime());
            return vo;
        }).collect(Collectors.toList()));
        return result;
    }

    @Override
    public List<TestResultVO> getRankingList(Long paperId) {
        List<TestRecord> records = testRecordMapper.selectList(
                new LambdaQueryWrapper<TestRecord>()
                        .eq(TestRecord::getPaperId, paperId)
                        .eq(TestRecord::getIsCompleted, 1)
                        .orderByDesc(TestRecord::getUserScore)
                        .last("LIMIT 100")
        );

        return records.stream()
                .map(record -> {
                    TestResultVO vo = new TestResultVO();
                    vo.setRecordId(record.getId());
                    vo.setUserScore(record.getUserScore());
                    vo.setDuration(record.getDuration());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private PaperVO convertToPaperVO(Paper paper) {
        PaperVO vo = new PaperVO();
        vo.setId(paper.getId());
        vo.setPaperName(paper.getPaperName());
        vo.setPaperType(paper.getPaperType());
        vo.setTotalScore(paper.getTotalScore());
        vo.setPassScore(paper.getPassScore());
        vo.setDuration(paper.getDuration());
        vo.setQuestionCount(paper.getQuestionCount());
        return vo;
    }

    private QuestionVO convertToQuestionVO(Question question) {
        QuestionVO vo = new QuestionVO();
        vo.setId(question.getId());
        vo.setQuestionType(question.getQuestionType());
        vo.setContent(question.getContent());
        vo.setScore(question.getScore());
        vo.setDifficulty(question.getDifficulty());

        try {
            List<String> options = objectMapper.readValue(question.getOptions(), new TypeReference<List<String>>() {});
            vo.setOptions(options);
        } catch (Exception e) {
            vo.setOptions(new ArrayList<>());
        }

        return vo;
    }
}