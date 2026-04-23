package com.anti.fraud.modules.community.mapper;

import com.anti.fraud.modules.community.entity.CommunityComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 社区评论Mapper
 */
@Mapper
public interface CommunityCommentMapper extends BaseMapper<CommunityComment> {

    /**
     * 根据帖子ID查询评论
     * @param postId 帖子ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表
     */
    List<CommunityComment> selectByPostId(@Param("postId") Long postId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 根据帖子ID查询评论总数
     * @param postId 帖子ID
     * @return 评论总数
     */
    Integer selectCountByPostId(@Param("postId") Long postId);

    /**
     * 根据父评论ID查询子评论
     * @param parentId 父评论ID
     * @param page 页码
     * @param size 每页大小
     * @return 子评论列表
     */
    List<CommunityComment> selectByParentId(@Param("parentId") Long parentId, @Param("page") Integer page, @Param("size") Integer size);

    /**
     * 增加点赞数
     * @param id 评论ID
     */
    void increaseLikeCount(@Param("id") Long id);

    /**
     * 按帖子ID统计评论数
     * @param postId 帖子ID
     * @return 评论数
     */
    Integer selectCountByPostIdGroup(@Param("postId") Long postId);
}
