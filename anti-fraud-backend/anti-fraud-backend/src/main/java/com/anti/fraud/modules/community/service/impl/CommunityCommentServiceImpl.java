package com.anti.fraud.modules.community.service.impl;

import com.anti.fraud.modules.community.entity.CommunityComment;
import com.anti.fraud.modules.community.mapper.CommunityCommentMapper;
import com.anti.fraud.modules.community.service.CommunityCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 社区评论服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityCommentServiceImpl extends ServiceImpl<CommunityCommentMapper, CommunityComment> implements CommunityCommentService {

    private final CommunityCommentMapper communityCommentMapper;

    @Override
    @Transactional
    public boolean addComment(CommunityComment comment) {
        try {
            comment.setLikeCount(0);
            comment.setStatus(1);
            comment.setDeleted(0);
            comment.setCreateTime(LocalDateTime.now());
            comment.setUpdateTime(LocalDateTime.now());
            return save(comment);
        } catch (Exception e) {
            log.error("新增评论失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateComment(CommunityComment comment) {
        try {
            comment.setUpdateTime(LocalDateTime.now());
            return updateById(comment);
        } catch (Exception e) {
            log.error("更新评论失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteComment(Long id) {
        try {
            CommunityComment comment = getById(id);
            if (comment != null) {
                comment.setDeleted(1);
                comment.setUpdateTime(LocalDateTime.now());
                return updateById(comment);
            }
            return false;
        } catch (Exception e) {
            log.error("删除评论失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public CommunityComment getCommentById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取评论详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getCommentsByPostId(Long postId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<CommunityComment> comments = communityCommentMapper.selectByPostId(postId, offset, size);
            int total = communityCommentMapper.selectCountByPostId(postId);

            Map<String, Object> result = new HashMap<>();
            result.put("list", comments);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据帖子ID查询评论失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getCommentsByParentId(Long parentId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<CommunityComment> comments = communityCommentMapper.selectByParentId(parentId, offset, size);
            // 计算总数 (这里可以添加一个方法来统计子评论总数)
            int total = comments.size();

            Map<String, Object> result = new HashMap<>();
            result.put("list", comments);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据父评论ID查询子评论失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public boolean increaseLikeCount(Long id) {
        try {
            communityCommentMapper.increaseLikeCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加点赞数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Integer countCommentsByPostId(Long postId) {
        try {
            return communityCommentMapper.selectCountByPostIdGroup(postId);
        } catch (Exception e) {
            log.error("统计帖子评论数失败: {}", e.getMessage(), e);
            return 0;
        }
    }
}
