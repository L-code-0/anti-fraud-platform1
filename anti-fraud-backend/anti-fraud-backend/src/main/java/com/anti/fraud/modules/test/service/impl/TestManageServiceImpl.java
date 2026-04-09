package com.anti.fraud.modules.test.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.modules.test.dto.PaperCreateDTO;
import com.anti.fraud.modules.test.dto.QuestionCreateDTO;
import com.anti.fraud.modules.test.entity.Paper;
import com.anti.fraud.modules.test.entity.Question;
import com.anti.fraud.modules.test.mapper.PaperMapper;
import com.anti.fraud.modules.test.mapper.QuestionMapper;
import com.anti.fraud.modules.test.service.TestManageService;
import com.anti.fraud.modules.test.vo.PaperVO;
import com.anti.fraud.modules.test.vo.QuestionVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestManageServiceImpl implements TestManageService {

    private final QuestionMapper questionMapper;
    private final PaperMapper paperMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Page<QuestionVO> getQuestionPage(Integer page, Integer size, Long categoryId, Integer questionType) {
        Page<Question> questionPage = new Page<>(page, size);

        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        if (categoryId != null) {
            wrapper.eq(Question::getCategoryId, categoryId);
        }

        if (questionType != null) {
            wrapper.eq(Question::getQuestionType, questionType);
        }

        wrapper.orderByDesc(Question::getCreateTime);

        questionMapper.selectPage(questionPage, wrapper);

        // 手动转换避免类型不兼容
        Page<QuestionVO> result = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        result.setRecords(questionPage.getRecords().stream().map(this::convertToQuestionVO).collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional
    public void createQuestion(QuestionCreateDTO createDTO) {
        Question question = new Question();
        question.setQuestionType(createDTO.getQuestionType());
        question.setCategoryId(createDTO.getCategoryId());
        question.setContent(createDTO.getContent());
        question.setAnswer(createDTO.getAnswer());
        question.setAnalysis(createDTO.getAnalysis());
        question.setDifficulty(createDTO.getDifficulty());
        question.setTags(createDTO.getTags());
        question.setScore(createDTO.getScore() != null ? createDTO.getScore() : 10);
        question.setStatus(1);

        if (createDTO.getOptions() != null) {
            try {
                question.setOptions(objectMapper.writeValueAsString(createDTO.getOptions()));
            } catch (JsonProcessingException e) {
                question.setOptions("[]");
            }
        }

        questionMapper.insert(question);
    }

    @Override
    @Transactional
    public void updateQuestion(Long id, QuestionCreateDTO createDTO) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException("题目不存在");
        }

        question.setQuestionType(createDTO.getQuestionType());
        question.setCategoryId(createDTO.getCategoryId());
        question.setContent(createDTO.getContent());
        question.setAnswer(createDTO.getAnswer());
        question.setAnalysis(createDTO.getAnalysis());
        question.setDifficulty(createDTO.getDifficulty());
        question.setTags(createDTO.getTags());

        if (createDTO.getScore() != null) {
            question.setScore(createDTO.getScore());
        }

        if (createDTO.getOptions() != null) {
            try {
                question.setOptions(objectMapper.writeValueAsString(createDTO.getOptions()));
            } catch (JsonProcessingException e) {
                question.setOptions("[]");
            }
        }

        questionMapper.updateById(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        questionMapper.deleteById(id);
    }

    @Override
    public Page<PaperVO> getPaperPage(Integer page, Integer size) {
        Page<Paper> paperPage = new Page<>(page, size);

        paperMapper.selectPage(paperPage,
                new LambdaQueryWrapper<Paper>()
                        .orderByDesc(Paper::getCreateTime)
        );

        // 手动转换避免类型不兼容
        Page<PaperVO> result = new Page<>(paperPage.getCurrent(), paperPage.getSize(), paperPage.getTotal());
        result.setRecords(paperPage.getRecords().stream().map(this::convertToPaperVO).collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional
    public void createPaper(PaperCreateDTO createDTO) {
        Paper paper = new Paper();
        paper.setPaperName(createDTO.getPaperName());
        paper.setPaperType(createDTO.getPaperType());
        paper.setTotalScore(createDTO.getTotalScore() != null ? createDTO.getTotalScore() : 100);
        paper.setPassScore(createDTO.getPassScore() != null ? createDTO.getPassScore() : 60);
        paper.setDuration(createDTO.getDuration() != null ? createDTO.getDuration() : 60);
        paper.setQuestionCount(createDTO.getQuestionCount());
        paper.setStatus(1);

        paperMapper.insert(paper);
    }

    @Override
    @Transactional
    public void updatePaper(Long id, PaperCreateDTO createDTO) {
        Paper paper = paperMapper.selectById(id);
        if (paper == null) {
            throw new BusinessException("试卷不存在");
        }

        paper.setPaperName(createDTO.getPaperName());
        paper.setPaperType(createDTO.getPaperType());

        if (createDTO.getTotalScore() != null) {
            paper.setTotalScore(createDTO.getTotalScore());
        }

        if (createDTO.getPassScore() != null) {
            paper.setPassScore(createDTO.getPassScore());
        }

        if (createDTO.getDuration() != null) {
            paper.setDuration(createDTO.getDuration());
        }

        if (createDTO.getQuestionCount() != null) {
            paper.setQuestionCount(createDTO.getQuestionCount());
        }

        paperMapper.updateById(paper);
    }

    @Override
    @Transactional
    public void deletePaper(Long id) {
        paperMapper.deleteById(id);
    }

    private QuestionVO convertToQuestionVO(Question question) {
        QuestionVO vo = new QuestionVO();
        vo.setId(question.getId());
        vo.setQuestionType(question.getQuestionType());
        vo.setContent(question.getContent());
        vo.setScore(question.getScore());
        vo.setDifficulty(question.getDifficulty());

        try {
            vo.setOptions(objectMapper.readValue(question.getOptions(), new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() {}));
        } catch (Exception e) {
            vo.setOptions(new java.util.ArrayList<>());
        }

        return vo;
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
}