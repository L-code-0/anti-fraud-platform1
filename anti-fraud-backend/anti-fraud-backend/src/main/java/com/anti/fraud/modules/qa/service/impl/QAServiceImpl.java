package com.anti.fraud.modules.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.qa.dto.AnswerDTO;
import com.anti.fraud.modules.qa.dto.QuestionDTO;
import com.anti.fraud.modules.qa.entity.AnswerInfo;
import com.anti.fraud.modules.qa.entity.QuestionInfo;
import com.anti.fraud.modules.qa.mapper.AnswerInfoMapper;
import com.anti.fraud.modules.qa.mapper.QuestionInfoMapper;
import com.anti.fraud.modules.qa.service.QAService;
import com.anti.fraud.modules.qa.vo.AnswerVO;
import com.anti.fraud.modules.qa.vo.QuestionVO;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QAServiceImpl implements QAService {

    private final QuestionInfoMapper questionMapper;
    private final AnswerInfoMapper answerMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Long askQuestion(QuestionDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);

        QuestionInfo question = new QuestionInfo();
        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        question.setAskerId(userId);
        question.setAskerName(user.getRealName());
        question.setStatus(0);
        question.setViewCount(0);
        question.setAnswerCount(0);
        question.setCreateTime(LocalDateTime.now());

        questionMapper.insert(question);
        return question.getId();
    }

    @Override
    public Page<QuestionVO> getQuestions(Integer page, Integer size, Integer status) {
        Page<QuestionInfo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<QuestionInfo> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(QuestionInfo::getStatus, status);
        }

        wrapper.orderByDesc(QuestionInfo::getCreateTime);

        Page<QuestionInfo> result = questionMapper.selectPage(pageParam, wrapper);

        Page<QuestionVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public QuestionVO getQuestionDetail(Long id) {
        QuestionInfo question = questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException("问题不存在");
        }

        // 增加浏览量
        question.setViewCount(question.getViewCount() + 1);
        questionMapper.updateById(question);

        QuestionVO vo = convertToVO(question);

        // 获取回答列表
        List<AnswerInfo> answers = answerMapper.selectList(
                new LambdaQueryWrapper<AnswerInfo>()
                        .eq(AnswerInfo::getQuestionId, id)
                        .orderByDesc(AnswerInfo::getIsAccepted)
                        .orderByDesc(AnswerInfo::getCreateTime)
        );

        vo.setAnswers(answers.stream()
                .map(this::convertAnswerToVO)
                .collect(Collectors.toList()));

        return vo;
    }

    @Override
    @Transactional
    public Long answerQuestion(AnswerDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);

        QuestionInfo question = questionMapper.selectById(dto.getQuestionId());
        if (question == null) {
            throw new BusinessException("问题不存在");
        }

        AnswerInfo answer = new AnswerInfo();
        answer.setQuestionId(dto.getQuestionId());
        answer.setContent(dto.getContent());
        answer.setAnswererId(userId);
        answer.setAnswererName(user.getRealName());
        answer.setAnswererTitle(user.getRoleId() == 4 ? "反诈专家" : "");
        answer.setIsAccepted(0);
        answer.setLikeCount(0);
        answer.setCreateTime(LocalDateTime.now());

        answerMapper.insert(answer);

        // 更新问题状态
        question.setStatus(1);
        question.setAnswerCount(question.getAnswerCount() + 1);
        questionMapper.updateById(question);

        return answer.getId();
    }

    @Override
    @Transactional
    public void acceptAnswer(Long answerId) {
        AnswerInfo answer = answerMapper.selectById(answerId);
        if (answer == null) {
            throw new BusinessException("回答不存在");
        }

        Long userId = SecurityUtils.getCurrentUserId();
        QuestionInfo question = questionMapper.selectById(answer.getQuestionId());

        if (!question.getAskerId().equals(userId)) {
            throw new BusinessException("只能采纳自己问题的回答");
        }

        // 取消之前的采纳
        answerMapper.selectList(
                new LambdaQueryWrapper<AnswerInfo>()
                        .eq(AnswerInfo::getQuestionId, answer.getQuestionId())
                        .eq(AnswerInfo::getIsAccepted, 1)
        ).forEach(a -> {
            a.setIsAccepted(0);
            answerMapper.updateById(a);
        });

        answer.setIsAccepted(1);
        answerMapper.updateById(answer);
    }

    @Override
    @Transactional
    public void likeAnswer(Long answerId) {
        AnswerInfo answer = answerMapper.selectById(answerId);
        if (answer == null) {
            throw new BusinessException("回答不存在");
        }

        answer.setLikeCount(answer.getLikeCount() + 1);
        answerMapper.updateById(answer);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        QuestionInfo question = questionMapper.selectById(id);

        if (question == null) {
            throw new BusinessException("问题不存在");
        }

        if (!question.getAskerId().equals(userId)) {
            throw new BusinessException("只能删除自己的问题");
        }

        questionMapper.deleteById(id);
    }

    private QuestionVO convertToVO(QuestionInfo question) {
        QuestionVO vo = new QuestionVO();
        vo.setId(question.getId());
        vo.setTitle(question.getTitle());
        vo.setContent(question.getContent());
        vo.setAskerId(question.getAskerId());
        vo.setAskerName(question.getAskerName());
        vo.setStatus(question.getStatus());
        vo.setStatusName(question.getStatus() == 0 ? "待回答" : "已回答");
        vo.setViewCount(question.getViewCount());
        vo.setAnswerCount(question.getAnswerCount());
        vo.setCreateTime(question.getCreateTime());
        return vo;
    }

    private AnswerVO convertAnswerToVO(AnswerInfo answer) {
        AnswerVO vo = new AnswerVO();
        vo.setId(answer.getId());
        vo.setQuestionId(answer.getQuestionId());
        vo.setContent(answer.getContent());
        vo.setAnswererId(answer.getAnswererId());
        vo.setAnswererName(answer.getAnswererName());
        vo.setAnswererTitle(answer.getAnswererTitle());
        vo.setIsAccepted(answer.getIsAccepted() == 1);
        vo.setLikeCount(answer.getLikeCount());
        vo.setCreateTime(answer.getCreateTime());
        return vo;
    }
}