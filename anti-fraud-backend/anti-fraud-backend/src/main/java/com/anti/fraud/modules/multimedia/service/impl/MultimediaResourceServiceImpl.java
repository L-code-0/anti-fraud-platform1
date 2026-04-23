package com.anti.fraud.modules.multimedia.service.impl;

import com.anti.fraud.modules.multimedia.entity.MultimediaResource;
import com.anti.fraud.modules.multimedia.mapper.MultimediaResourceMapper;
import com.anti.fraud.modules.multimedia.service.MultimediaResourceService;
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
 * 多媒体资源服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MultimediaResourceServiceImpl extends ServiceImpl<MultimediaResourceMapper, MultimediaResource> implements MultimediaResourceService {

    private final MultimediaResourceMapper multimediaResourceMapper;

    @Override
    @Transactional
    public boolean addMultimediaResource(MultimediaResource multimediaResource) {
        try {
            multimediaResource.setViewCount(0);
            multimediaResource.setLikeCount(0);
            multimediaResource.setShareCount(0);
            multimediaResource.setStatus(1);
            multimediaResource.setDeleted(0);
            multimediaResource.setCreateTime(LocalDateTime.now());
            multimediaResource.setUpdateTime(LocalDateTime.now());
            return save(multimediaResource);
        } catch (Exception e) {
            log.error("新增多媒体资源失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateMultimediaResource(MultimediaResource multimediaResource) {
        try {
            multimediaResource.setUpdateTime(LocalDateTime.now());
            return updateById(multimediaResource);
        } catch (Exception e) {
            log.error("更新多媒体资源失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteMultimediaResource(Long id) {
        try {
            MultimediaResource multimediaResource = getById(id);
            if (multimediaResource != null) {
                multimediaResource.setDeleted(1);
                multimediaResource.setUpdateTime(LocalDateTime.now());
                return updateById(multimediaResource);
            }
            return false;
        } catch (Exception e) {
            log.error("删除多媒体资源失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public MultimediaResource getMultimediaResourceById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取多媒体资源详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getMultimediaResourceList(Map<String, Object> params, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<MultimediaResource> multimediaResources = multimediaResourceMapper.selectByCondition(params, offset, size);
            int total = multimediaResourceMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", multimediaResources);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询多媒体资源列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getMultimediaResourcesByType(Integer type, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<MultimediaResource> multimediaResources = multimediaResourceMapper.selectByType(type, offset, size);
            // 计算总数
            Map<String, Object> params = new HashMap<>();
            params.put("type", type);
            int total = multimediaResourceMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", multimediaResources);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据资源类型查询多媒体资源失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getMultimediaResourcesByKnowledgePoint(String knowledgePoint, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<MultimediaResource> multimediaResources = multimediaResourceMapper.selectByKnowledgePoint(knowledgePoint, offset, size);
            // 计算总数
            Map<String, Object> params = new HashMap<>();
            params.put("knowledgePoint", knowledgePoint);
            int total = multimediaResourceMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", multimediaResources);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据知识点查询多媒体资源失败: {}", e.getMessage(), e);
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
            multimediaResourceMapper.increaseViewCount(id);
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
            multimediaResourceMapper.increaseLikeCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加点赞数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean increaseShareCount(Long id) {
        try {
            multimediaResourceMapper.increaseShareCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加分享数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<MultimediaResource> getHotResources(int limit) {
        try {
            return multimediaResourceMapper.selectHotResources(limit);
        } catch (Exception e) {
            log.error("获取热门多媒体资源失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<MultimediaResource> getLatestResources(int limit) {
        try {
            return multimediaResourceMapper.selectLatestResources(limit);
        } catch (Exception e) {
            log.error("获取最新多媒体资源失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getMultimediaResourceStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("typeStats", multimediaResourceMapper.selectTypeStats());
            stats.put("knowledgePointStats", multimediaResourceMapper.selectKnowledgePointStats());
            return stats;
        } catch (Exception e) {
            log.error("统计多媒体资源信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> searchMultimediaResources(String keyword, int page, int size) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("keyword", keyword);
            int offset = (page - 1) * size;
            List<MultimediaResource> multimediaResources = multimediaResourceMapper.selectByCondition(params, offset, size);
            int total = multimediaResourceMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", multimediaResources);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("搜索多媒体资源失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public List<MultimediaResource> batchUploadMultimediaResources(List<MultimediaResource> resources) {
        try {
            List<MultimediaResource> uploadedResources = new ArrayList<>();
            for (MultimediaResource resource : resources) {
                resource.setViewCount(0);
                resource.setLikeCount(0);
                resource.setShareCount(0);
                resource.setStatus(1);
                resource.setDeleted(0);
                resource.setCreateTime(LocalDateTime.now());
                resource.setUpdateTime(LocalDateTime.now());
                if (save(resource)) {
                    uploadedResources.add(resource);
                }
            }
            return uploadedResources;
        } catch (Exception e) {
            log.error("批量上传多媒体资源失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
