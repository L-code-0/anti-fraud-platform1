package com.anti.fraud.modules.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.anti.fraud.modules.knowledge.entity.KnowledgeContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface KnowledgeContentMapper extends BaseMapper<KnowledgeContent> {
    
    /**
     * 分页查询知识列表
     * @param categoryId 分类ID
     * @param keyword 关键词
     * @param contentType 内容类型
     * @param status 状态
     * @param auditStatus 审核状态
     * @return 知识列表
     */
    List<Map<String, Object>> selectKnowledgePage(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            @Param("contentType") String contentType,
            @Param("status") Integer status,
            @Param("auditStatus") Integer auditStatus);
    
    /**
     * 查询热门知识列表
     * @param limit 限制数量
     * @return 热门知识列表
     */
    List<Map<String, Object>> selectHotKnowledge(@Param("limit") Integer limit);
    
    /**
     * 查询推荐知识列表
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 推荐知识列表
     */
    List<Map<String, Object>> selectRecommendedKnowledge(
            @Param("userId") Long userId,
            @Param("limit") Integer limit);
    
    /**
     * 查询知识详情
     * @param id 知识ID
     * @param userId 用户ID
     * @return 知识详情
     */
    Map<String, Object> selectKnowledgeDetail(
            @Param("id") Long id,
            @Param("userId") Long userId);
    
    /**
     * 更新浏览量
     * @param id 知识ID
     */
    void incrementViewCount(@Param("id") Long id);
    
}
