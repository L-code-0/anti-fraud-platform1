package com.anti.fraud.modules.qa.mapper;

import com.anti.fraud.modules.qa.entity.FollowUpQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

/**
 * 追问Mapper
 */
public interface FollowUpQuestionMapper extends BaseMapper<FollowUpQuestion> {

    /**
     * 根据问题ID获取追问列表
     */
    List<FollowUpQuestion> selectByQuestionId(Long questionId);

    /**
     * 根据回答ID获取追问列表
     */
    List<FollowUpQuestion> selectByAnswerId(Long answerId);

    /**
     * 获取用户的追问列表
     */
    List<FollowUpQuestion> selectByAskerId(Long askerId);
}
