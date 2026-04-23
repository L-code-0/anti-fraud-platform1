package com.anti.fraud.modules.case.service.impl;

import com.anti.fraud.modules.case.entity.Case;
import com.anti.fraud.modules.case.mapper.CaseMapper;
import com.anti.fraud.modules.case.service.CaseService;
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
 * 案例服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CaseServiceImpl extends ServiceImpl<CaseMapper, Case> implements CaseService {

    private final CaseMapper caseMapper;

    @Override
    @Transactional
    public boolean addCase(Case caseInfo) {
        try {
            caseInfo.setViewCount(0);
            caseInfo.setLikeCount(0);
            caseInfo.setCommentCount(0);
            caseInfo.setShareCount(0);
            caseInfo.setStatus(1);
            caseInfo.setDeleted(0);
            caseInfo.setCreateTime(LocalDateTime.now());
            caseInfo.setUpdateTime(LocalDateTime.now());
            return save(caseInfo);
        } catch (Exception e) {
            log.error("新增案例失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateCase(Case caseInfo) {
        try {
            caseInfo.setUpdateTime(LocalDateTime.now());
            return updateById(caseInfo);
        } catch (Exception e) {
            log.error("更新案例失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteCase(Long id) {
        try {
            Case caseInfo = getById(id);
            if (caseInfo != null) {
                caseInfo.setDeleted(1);
                caseInfo.setUpdateTime(LocalDateTime.now());
                return updateById(caseInfo);
            }
            return false;
        } catch (Exception e) {
            log.error("删除案例失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Case getCaseById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取案例详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getCaseList(Map<String, Object> params, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<Case> cases = caseMapper.selectByCondition(params, offset, size);
            int total = caseMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", cases);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询案例列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getCasesByType(Integer type, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<Case> cases = caseMapper.selectByType(type, offset, size);
            // 计算总数
            Map<String, Object> params = new HashMap<>();
            params.put("type", type);
            int total = caseMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", cases);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据案例类型查询案例失败: {}", e.getMessage(), e);
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
            caseMapper.increaseViewCount(id);
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
            caseMapper.increaseLikeCount(id);
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
            caseMapper.increaseCommentCount(id);
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
            caseMapper.increaseShareCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加分享数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Case> getHotCases(int limit) {
        try {
            return caseMapper.selectHotCases(limit);
        } catch (Exception e) {
            log.error("获取热门案例失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Case> getLatestCases(int limit) {
        try {
            return caseMapper.selectLatestCases(limit);
        } catch (Exception e) {
            log.error("获取最新案例失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getCaseStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("typeStats", caseMapper.selectTypeStats());
            return stats;
        } catch (Exception e) {
            log.error("统计案例信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> searchCases(String keyword, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<Case> cases = caseMapper.selectByKeyword(keyword, offset, size);
            int total = caseMapper.selectCountByKeyword(keyword);

            Map<String, Object> result = new HashMap<>();
            result.put("list", cases);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("搜索案例失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    @Transactional
    public List<Case> batchImportCases(List<Case> cases) {
        try {
            List<Case> importedCases = new ArrayList<>();
            for (Case caseInfo : cases) {
                caseInfo.setViewCount(0);
                caseInfo.setLikeCount(0);
                caseInfo.setCommentCount(0);
                caseInfo.setShareCount(0);
                caseInfo.setStatus(1);
                caseInfo.setDeleted(0);
                caseInfo.setCreateTime(LocalDateTime.now());
                caseInfo.setUpdateTime(LocalDateTime.now());
                if (save(caseInfo)) {
                    importedCases.add(caseInfo);
                }
            }
            return importedCases;
        } catch (Exception e) {
            log.error("批量导入案例失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
