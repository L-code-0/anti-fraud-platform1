package com.anti.fraud.modules.community.service.impl;

import com.anti.fraud.modules.community.entity.CommunityPost;
import com.anti.fraud.modules.community.mapper.CommunityPostMapper;
import com.anti.fraud.modules.community.service.CommunityPostService;
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
 * 社区帖子服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityPostServiceImpl extends ServiceImpl<CommunityPostMapper, CommunityPost> implements CommunityPostService {

    private final CommunityPostMapper communityPostMapper;

    @Override
    @Transactional
    public boolean addPost(CommunityPost post) {
        try {
            post.setViewCount(0);
            post.setLikeCount(0);
            post.setCommentCount(0);
            post.setShareCount(0);
            post.setStatus(1);
            post.setDeleted(0);
            post.setCreateTime(LocalDateTime.now());
            post.setUpdateTime(LocalDateTime.now());
            return save(post);
        } catch (Exception e) {
            log.error("新增帖子失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updatePost(CommunityPost post) {
        try {
            post.setUpdateTime(LocalDateTime.now());
            return updateById(post);
        } catch (Exception e) {
            log.error("更新帖子失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deletePost(Long id) {
        try {
            CommunityPost post = getById(id);
            if (post != null) {
                post.setDeleted(1);
                post.setUpdateTime(LocalDateTime.now());
                return updateById(post);
            }
            return false;
        } catch (Exception e) {
            log.error("删除帖子失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public CommunityPost getPostById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取帖子详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getPostList(Map<String, Object> params, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<CommunityPost> posts = communityPostMapper.selectByCondition(params, offset, size);
            int total = communityPostMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", posts);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询帖子列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getPostsByType(Integer postType, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<CommunityPost> posts = communityPostMapper.selectByPostType(postType, offset, size);
            // 计算总数
            Map<String, Object> params = new HashMap<>();
            params.put("postType", postType);
            int total = communityPostMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", posts);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据帖子类型查询帖子失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public boolean increaseViewCount(Long id) {
        try {
            communityPostMapper.increaseViewCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加浏览量失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean increaseLikeCount(Long id) {
        try {
            communityPostMapper.increaseLikeCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加点赞数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean increaseCommentCount(Long id) {
        try {
            communityPostMapper.increaseCommentCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加评论数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean increaseShareCount(Long id) {
        try {
            communityPostMapper.increaseShareCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加分享数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<CommunityPost> getHotPosts(int limit) {
        try {
            return communityPostMapper.selectHotPosts(limit);
        } catch (Exception e) {
            log.error("获取热门帖子失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<CommunityPost> getLatestPosts(int limit) {
        try {
            return communityPostMapper.selectLatestPosts(limit);
        } catch (Exception e) {
            log.error("获取最新帖子失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getPostStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("typeStats", communityPostMapper.selectPostTypeStats());
            return stats;
        } catch (Exception e) {
            log.error("统计帖子信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> searchPosts(String keyword, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<CommunityPost> posts = communityPostMapper.selectByKeyword(keyword, offset, size);
            int total = communityPostMapper.selectCountByKeyword(keyword);

            Map<String, Object> result = new HashMap<>();
            result.put("list", posts);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("搜索帖子失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }
}
