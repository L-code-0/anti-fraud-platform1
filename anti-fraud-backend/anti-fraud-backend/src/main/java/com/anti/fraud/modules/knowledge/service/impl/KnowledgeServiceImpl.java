package com.anti.fraud.modules.knowledge.service.impl;

import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.anti.fraud.modules.knowledge.dto.KnowledgeQueryDTO;
import com.anti.fraud.modules.knowledge.entity.KnowledgeCategory;
import com.anti.fraud.modules.knowledge.entity.KnowledgeContent;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeCategoryMapper;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeContentMapper;
import com.anti.fraud.modules.knowledge.service.KnowledgeService;
import com.anti.fraud.modules.knowledge.vo.CategoryVO;
import com.anti.fraud.modules.knowledge.vo.KnowledgeDetailVO;
import com.anti.fraud.modules.knowledge.vo.KnowledgeListVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 知识服务实现类
 *
 * 缓存策略说明：
 * - 知识分类：缓存1小时，分类变化时清除
 * - 热门知识：缓存30分钟，保持热度数据新鲜
 * - 推荐知识：缓存30分钟
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeContentMapper knowledgeMapper;
    private final KnowledgeCategoryMapper categoryMapper;

    /**
     * 获取知识分类列表（带缓存）
     * 缓存时间：1小时
     */
    @Override
    @Cacheable(value = "knowledge:categories", key = "'all'", unless = "#result == null")
    public List<CategoryVO> getCategories() {
        log.debug("从数据库加载知识分类列表");
        List<KnowledgeCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<KnowledgeCategory>()
                        .eq(KnowledgeCategory::getStatus, 1)
                        .orderByAsc(KnowledgeCategory::getSortOrder)
        );

        return categories.stream()
                .map(this::convertToCategoryVO)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询知识列表
     * 不缓存分页数据，因为查询条件多变
     */
    @Override
    public Page<KnowledgeListVO> getKnowledgePage(KnowledgeQueryDTO queryDTO) {
        // 参数校验
        int page = Math.max(1, queryDTO.getPage());
        int size = Math.min(100, Math.max(1, queryDTO.getSize()));

        Page<KnowledgeContent> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<KnowledgeContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeContent::getStatus, 1);

        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(KnowledgeContent::getCategoryId, queryDTO.getCategoryId());
        }

        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            wrapper.like(KnowledgeContent::getTitle, queryDTO.getKeyword());
        }

        if (queryDTO.getContentType() != null) {
            wrapper.eq(KnowledgeContent::getContentType, queryDTO.getContentType());
        }

        wrapper.orderByDesc(KnowledgeContent::getIsTop)
                .orderByDesc(KnowledgeContent::getPublishTime);

        Page<KnowledgeContent> contentPage = knowledgeMapper.selectPage(pageParam, wrapper);

        // 手动转换避免类型不兼容
        Page<KnowledgeListVO> result = new Page<>(contentPage.getCurrent(), contentPage.getSize(), contentPage.getTotal());
        result.setRecords(contentPage.getRecords().stream().map(this::convertToListVO).collect(Collectors.toList()));
        return result;
    }

    /**
     * 获取知识详情
     * 缓存单个知识详情，更新时清除
     */
    @Override
    @Cacheable(value = "knowledge:detail", key = "#id", unless = "#result == null")
    @Transactional
    public KnowledgeDetailVO getKnowledgeDetail(Long id) {
        log.debug("从数据库加载知识详情: {}", id);
        KnowledgeContent knowledge = knowledgeMapper.selectById(id);
        if (knowledge == null) {
            throw new BusinessException("知识内容不存在");
        }

        // 异步更新浏览量，不影响查询性能
        updateViewCountAsync(id);

        KnowledgeDetailVO vo = convertToDetailVO(knowledge);

        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            vo.setIsCollected(false);
        }

        return vo;
    }

    /**
     * 异步更新浏览量
     */
    private void updateViewCountAsync(Long id) {
        // 使用Redis计数器或异步更新，避免每次都写数据库
        // 这里简化处理，实际生产环境可以使用Redis incr
        try {
            KnowledgeContent knowledge = new KnowledgeContent();
            knowledge.setId(id);
            // knowledgeMapper.incrementViewCount(id);
        } catch (Exception e) {
            log.warn("更新浏览量失败: {}", e.getMessage());
        }
    }

    /**
     * 收藏知识
     * 清除该知识的缓存
     */
    @Override
    @Transactional
    @CacheEvict(value = "knowledge:detail", key = "#id")
    public void collectKnowledge(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        KnowledgeContent knowledge = knowledgeMapper.selectById(id);
        if (knowledge == null) {
            throw new BusinessException("知识内容不存在");
        }

        knowledge.setCollectCount(knowledge.getCollectCount() + 1);
        knowledgeMapper.updateById(knowledge);

        log.info("用户 {} 收藏了知识 {}", userId, id);
    }

    /**
     * 取消收藏
     * 清除该知识的缓存
     */
    @Override
    @Transactional
    @CacheEvict(value = "knowledge:detail", key = "#id")
    public void uncollectKnowledge(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        KnowledgeContent knowledge = knowledgeMapper.selectById(id);
        if (knowledge == null) {
            throw new BusinessException("知识内容不存在");
        }

        if (knowledge.getCollectCount() > 0) {
            knowledge.setCollectCount(knowledge.getCollectCount() - 1);
            knowledgeMapper.updateById(knowledge);
        }

        log.info("用户 {} 取消收藏知识 {}", userId, id);
    }

    @Override
    public void recordProgress(Long id, Integer duration) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return;
        }
        // 记录学习进度到Redis，使用hash存储用户学习进度
        // 格式: learning:progress:{userId}:{knowledgeId} -> {startTime, duration, lastVisitTime}
        try {
            // 这里使用RedisUtils记录学习进度
            // String redisKey = String.format("learning:progress:%d:%d", userId, id);
            // RedisUtils.hset(redisKey, "duration", duration);
            // RedisUtils.hset(redisKey, "lastVisitTime", System.currentTimeMillis());
            log.debug("用户 {} 学习知识 {} 时长 {} 秒", userId, id, duration);
            
            // 如果学习时长超过一定时间（比如30秒），可以增加用户积分
            if (duration >= 30) {
                // 异步增加积分
                // pointsService.addPoints(userId, 1, "learning", id, "学习知识奖励");
            }
        } catch (Exception e) {
            log.warn("记录学习进度失败: {}", e.getMessage());
        }
    }

    /**
     * 点赞知识
     * 清除该知识的缓存
     */
    @Override
    @Transactional
    @CacheEvict(value = "knowledge:detail", key = "#id")
    public void likeKnowledge(Long id) {
        KnowledgeContent knowledge = knowledgeMapper.selectById(id);
        if (knowledge == null) {
            throw new BusinessException("知识内容不存在");
        }

        knowledge.setLikeCount(knowledge.getLikeCount() + 1);
        knowledgeMapper.updateById(knowledge);

        log.info("用户点赞知识 {}", id);
    }

    /**
     * 获取推荐知识（带缓存）
     * 缓存时间：30分钟
     */
    @Override
    @Cacheable(value = "knowledge:recommend", key = "'top10'", unless = "#result == null || #result.isEmpty()")
    public List<KnowledgeListVO> getRecommendKnowledge() {
        log.debug("从数据库加载推荐知识");
        List<KnowledgeContent> list = knowledgeMapper.selectList(
                new LambdaQueryWrapper<KnowledgeContent>()
                        .eq(KnowledgeContent::getStatus, 1)
                        .eq(KnowledgeContent::getIsRecommend, 1)
                        .orderByDesc(KnowledgeContent::getPublishTime)
                        .last("LIMIT 10")
        );

        return list.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取热门知识（带缓存）
     * 缓存时间：30分钟
     */
    @Override
    @Cacheable(value = "knowledge:hot", key = "'top10'", unless = "#result == null || #result.isEmpty()")
    public List<KnowledgeListVO> getHotKnowledge() {
        log.debug("从数据库加载热门知识");
        List<KnowledgeContent> list = knowledgeMapper.selectList(
                new LambdaQueryWrapper<KnowledgeContent>()
                        .eq(KnowledgeContent::getStatus, 1)
                        .eq(KnowledgeContent::getIsHot, 1)
                        .orderByDesc(KnowledgeContent::getViewCount)
                        .last("LIMIT 10")
        );

        return list.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
    }

    /**
     * 清除所有知识相关缓存
     * 在管理后台更新知识时调用
     */
    @CacheEvict(value = {"knowledge:categories", "knowledge:recommend", "knowledge:hot"}, allEntries = true)
    public void clearAllCache() {
        log.info("清除知识模块所有缓存");
    }

    private CategoryVO convertToCategoryVO(KnowledgeCategory category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setCategoryName(category.getCategoryName());
        vo.setParentId(category.getParentId());
        vo.setLevel(category.getLevel());
        vo.setIcon(category.getIcon());
        return vo;
    }

    private KnowledgeListVO convertToListVO(KnowledgeContent content) {
        KnowledgeListVO vo = new KnowledgeListVO();
        vo.setId(content.getId());
        vo.setTitle(content.getTitle());
        vo.setCategoryId(content.getCategoryId());
        vo.setContentType(content.getContentType());
        vo.setSummary(content.getSummary());
        vo.setCoverImage(content.getCoverImage());
        vo.setViewCount(content.getViewCount());
        vo.setLikeCount(content.getLikeCount());
        vo.setCollectCount(content.getCollectCount());
        vo.setPublishTime(content.getPublishTime());
        vo.setAuthorName(content.getAuthorName());
        return vo;
    }

    private KnowledgeDetailVO convertToDetailVO(KnowledgeContent content) {
        KnowledgeDetailVO vo = new KnowledgeDetailVO();
        vo.setId(content.getId());
        vo.setTitle(content.getTitle());
        vo.setCategoryId(content.getCategoryId());
        vo.setContentType(content.getContentType());
        vo.setSummary(content.getSummary());
        vo.setContent(content.getContent());
        vo.setCoverImage(content.getCoverImage());
        vo.setVideoUrl(content.getVideoUrl());
        vo.setVideoDuration(content.getVideoDuration());
        vo.setTags(content.getTags());
        vo.setViewCount(content.getViewCount());
        vo.setLikeCount(content.getLikeCount());
        vo.setCollectCount(content.getCollectCount());
        vo.setShareCount(content.getShareCount());
        vo.setPublishTime(content.getPublishTime());
        vo.setAuthorName(content.getAuthorName());
        vo.setSource(content.getSource());
        return vo;
    }
}