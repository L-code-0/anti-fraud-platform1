package com.anti.fraud.modules.community.service;

import com.anti.fraud.modules.community.entity.CommunityComment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 社区评论服务接口
 */
public interface CommunityCommentService extends IService<CommunityComment> {

    /**
     * 新增评论
     * @param comment 评论信息
     * @return 是否成功
     */
    boolean addComment(CommunityComment comment);

    /**
     * 更新评论
     * @param comment 评论信息
     * @return 是否成功
     */
    boolean updateComment(CommunityComment comment);

    /**
     * 删除评论
     * @param id 评论ID
     * @return 是否成功
     */
    boolean deleteComment(Long id);

    /**
     * 获取评论详情
     * @param id 评论ID
     * @return 评论详情
     */
    CommunityComment getCommentById(Long id);

    /**
     * 根据帖子ID查询评论
     * @param postId 帖子ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表和总数
     */
    Map<String, Object> getCommentsByPostId(Long postId, int page, int size);

    /**
     * 根据父评论ID查询子评论
     * @param parentId 父评论ID
     * @param page 页码
     * @param size 每页大小
     * @return 子评论列表和总数
     */
    Map<String, Object> getCommentsByParentId(Long parentId, int page, int size);

    /**
     * 增加点赞数
     * @param id 评论ID
     * @return 是否成功
     */
    boolean increaseLikeCount(Long id);

    /**
     * 统计帖子评论数
     * @param postId 帖子ID
     * @return 评论数
     */
    Integer countCommentsByPostId(Long postId);
}
