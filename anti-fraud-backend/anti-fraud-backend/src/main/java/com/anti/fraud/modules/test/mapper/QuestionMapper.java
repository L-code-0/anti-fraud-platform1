package com.anti.fraud.modules.test.mapper;

import com.anti.fraud.modules.test.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    List<Question> selectRandomQuestions(@Param("categoryId") Long categoryId,
                                         @Param("count") Integer count);
}