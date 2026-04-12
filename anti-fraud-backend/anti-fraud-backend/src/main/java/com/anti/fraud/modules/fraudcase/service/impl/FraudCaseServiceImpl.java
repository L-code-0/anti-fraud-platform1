package com.anti.fraud.modules.fraudcase.service.impl;

import com.anti.fraud.modules.fraudcase.entity.FraudCase;
import com.anti.fraud.modules.fraudcase.mapper.FraudCaseMapper;
import com.anti.fraud.modules.fraudcase.service.FraudCaseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudCaseServiceImpl implements FraudCaseService {

    private final FraudCaseMapper fraudCaseMapper;

    @Override
    public boolean createCase(FraudCase fraudCase) {
        try {
            fraudCase.setViewCount(0);
            fraudCase.setLikeCount(0);
            fraudCase.setCommentCount(0);
            fraudCase.setStatus(1); // 1: 发布
            fraudCaseMapper.insert(fraudCase);
            log.info("创建案例成功: id={}, title={}", fraudCase.getId(), fraudCase.getTitle());
            return true;
        } catch (Exception e) {
            log.error("创建案例失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateCase(FraudCase fraudCase) {
        try {
            fraudCaseMapper.updateById(fraudCase);
            log.info("更新案例成功: id={}", fraudCase.getId());
            return true;
        } catch (Exception e) {
            log.error("更新案例失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteCase(Long id) {
        try {
            fraudCaseMapper.deleteById(id);
            log.info("删除案例成功: id={}", id);
            return true;
        } catch (Exception e) {
            log.error("删除案例失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public FraudCase getCaseById(Long id) {
        try {
            return fraudCaseMapper.selectById(id);
        } catch (Exception e) {
            log.error("获取案例详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<FraudCase> getCaseList(String category, String caseType, String difficulty, int page, int size) {
        try {
            LambdaQueryWrapper<FraudCase> queryWrapper = new LambdaQueryWrapper<>();
            if (category != null && !category.isEmpty()) {
                queryWrapper.eq(FraudCase::getCategory, category);
            }
            if (caseType != null && !caseType.isEmpty()) {
                queryWrapper.eq(FraudCase::getCaseType, caseType);
            }
            if (difficulty != null && !difficulty.isEmpty()) {
                queryWrapper.eq(FraudCase::getDifficulty, difficulty);
            }
            queryWrapper.eq(FraudCase::getStatus, 1) // 只查询已发布的案例
                    .orderByDesc(FraudCase::getCreateTime);

            IPage<FraudCase> pageInfo = new Page<>(page, size);
            fraudCaseMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取案例列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<FraudCase> searchCases(String keyword, int page, int size) {
        try {
            LambdaQueryWrapper<FraudCase> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FraudCase::getStatus, 1) // 只查询已发布的案例
                    .and(wrapper -> wrapper
                            .like(FraudCase::getTitle, keyword)
                            .or()
                            .like(FraudCase::getDescription, keyword)
                            .or()
                            .like(FraudCase::getKeywords, keyword)
                    )
                    .orderByDesc(FraudCase::getCreateTime);

            IPage<FraudCase> pageInfo = new Page<>(page, size);
            fraudCaseMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("搜索案例失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean increaseViewCount(Long id) {
        try {
            FraudCase fraudCase = fraudCaseMapper.selectById(id);
            if (fraudCase != null) {
                fraudCase.setViewCount(fraudCase.getViewCount() + 1);
                fraudCaseMapper.updateById(fraudCase);
                log.info("增加案例浏览次数成功: id={}, viewCount={}", id, fraudCase.getViewCount());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("增加案例浏览次数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean increaseLikeCount(Long id) {
        try {
            FraudCase fraudCase = fraudCaseMapper.selectById(id);
            if (fraudCase != null) {
                fraudCase.setLikeCount(fraudCase.getLikeCount() + 1);
                fraudCaseMapper.updateById(fraudCase);
                log.info("增加案例点赞次数成功: id={}, likeCount={}", id, fraudCase.getLikeCount());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("增加案例点赞次数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean increaseCommentCount(Long id) {
        try {
            FraudCase fraudCase = fraudCaseMapper.selectById(id);
            if (fraudCase != null) {
                fraudCase.setCommentCount(fraudCase.getCommentCount() + 1);
                fraudCaseMapper.updateById(fraudCase);
                log.info("增加案例评论次数成功: id={}, commentCount={}", id, fraudCase.getCommentCount());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("增加案例评论次数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<FraudCase> getHotCases(int count) {
        try {
            LambdaQueryWrapper<FraudCase> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FraudCase::getStatus, 1) // 只查询已发布的案例
                    .orderByDesc(FraudCase::getViewCount)
                    .last("LIMIT " + count);

            return fraudCaseMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取热门案例失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<FraudCase> getRecommendedCases(Long userId, int count) {
        try {
            // 简单的推荐逻辑：随机选择案例
            LambdaQueryWrapper<FraudCase> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FraudCase::getStatus, 1) // 只查询已发布的案例
                    .orderByAsc("RAND()")
                    .last("LIMIT " + count);

            return fraudCaseMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取推荐案例失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> getCaseStats() {
        try {
            // 统计案例总数
            long totalCases = fraudCaseMapper.selectCount(null);

            // 统计已发布案例数
            LambdaQueryWrapper<FraudCase> publishedQuery = new LambdaQueryWrapper<>();
            publishedQuery.eq(FraudCase::getStatus, 1);
            long publishedCases = fraudCaseMapper.selectCount(publishedQuery);

            // 按分类统计
            Map<String, Long> categoryStats = new HashMap<>();
            List<FraudCase> cases = fraudCaseMapper.selectList(null);
            for (FraudCase fraudCase : cases) {
                String category = fraudCase.getCategory();
                categoryStats.put(category, categoryStats.getOrDefault(category, 0L) + 1);
            }

            // 统计总浏览次数
            int totalViews = 0;
            for (FraudCase fraudCase : cases) {
                totalViews += fraudCase.getViewCount();
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCases", totalCases);
            stats.put("publishedCases", publishedCases);
            stats.put("categoryStats", categoryStats);
            stats.put("totalViews", totalViews);

            log.info("获取案例统计信息成功: totalCases={}, publishedCases={}", totalCases, publishedCases);
            return stats;
        } catch (Exception e) {
            log.error("获取案例统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> importCases(List<FraudCase> cases) {
        Map<String, Object> result = new HashMap<>();
        try {
            int successCount = 0;
            int failCount = 0;

            for (FraudCase fraudCase : cases) {
                try {
                    fraudCase.setViewCount(0);
                    fraudCase.setLikeCount(0);
                    fraudCase.setCommentCount(0);
                    fraudCase.setStatus(1); // 1: 发布
                    fraudCaseMapper.insert(fraudCase);
                    successCount++;
                } catch (Exception e) {
                    log.error("导入案例失败: {}", e.getMessage(), e);
                    failCount++;
                }
            }

            result.put("success", true);
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("message", "导入案例完成");

            log.info("导入案例成功: successCount={}, failCount={}", successCount, failCount);
            return result;
        } catch (Exception e) {
            log.error("导入案例失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "导入案例失败");
            return result;
        }
    }

    @Override
    public Map<String, Object> exportCases(List<Long> ids) {
        try {
            List<FraudCase> cases = new ArrayList<>();
            for (Long id : ids) {
                FraudCase fraudCase = fraudCaseMapper.selectById(id);
                if (fraudCase != null) {
                    cases.add(fraudCase);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("cases", cases);
            result.put("count", cases.size());

            log.info("导出案例成功: count={}", cases.size());
            return result;
        } catch (Exception e) {
            log.error("导出案例失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "导出案例失败");
            return result;
        }
    }
}
