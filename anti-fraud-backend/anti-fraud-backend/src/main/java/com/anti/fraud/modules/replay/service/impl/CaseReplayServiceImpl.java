package com.anti.fraud.modules.replay.service.impl;

import com.anti.fraud.modules.replay.entity.CaseReplay;
import com.anti.fraud.modules.replay.mapper.CaseReplayMapper;
import com.anti.fraud.modules.replay.service.CaseReplayService;
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
 * 案例回放服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CaseReplayServiceImpl extends ServiceImpl<CaseReplayMapper, CaseReplay> implements CaseReplayService {

    private final CaseReplayMapper caseReplayMapper;

    @Override
    @Transactional
    public boolean addCaseReplay(CaseReplay caseReplay) {
        try {
            caseReplay.setViewCount(0);
            caseReplay.setLikeCount(0);
            caseReplay.setShareCount(0);
            caseReplay.setStatus(1);
            caseReplay.setDeleted(0);
            caseReplay.setCreateTime(LocalDateTime.now());
            caseReplay.setUpdateTime(LocalDateTime.now());
            return save(caseReplay);
        } catch (Exception e) {
            log.error("新增案例回放失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateCaseReplay(CaseReplay caseReplay) {
        try {
            caseReplay.setUpdateTime(LocalDateTime.now());
            return updateById(caseReplay);
        } catch (Exception e) {
            log.error("更新案例回放失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteCaseReplay(Long id) {
        try {
            CaseReplay caseReplay = getById(id);
            if (caseReplay != null) {
                caseReplay.setDeleted(1);
                caseReplay.setUpdateTime(LocalDateTime.now());
                return updateById(caseReplay);
            }
            return false;
        } catch (Exception e) {
            log.error("删除案例回放失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public CaseReplay getCaseReplayById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取案例回放详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getCaseReplayList(Map<String, Object> params, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<CaseReplay> caseReplays = caseReplayMapper.selectByCondition(params, offset, size);
            int total = caseReplayMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", caseReplays);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询案例回放列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public List<CaseReplay> getCaseReplaysByCaseId(Long caseId) {
        try {
            return caseReplayMapper.selectByCaseId(caseId);
        } catch (Exception e) {
            log.error("根据案例ID查询案例回放失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean increaseViewCount(Long id) {
        try {
            caseReplayMapper.increaseViewCount(id);
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
            caseReplayMapper.increaseLikeCount(id);
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
            caseReplayMapper.increaseShareCount(id);
            return true;
        } catch (Exception e) {
            log.error("增加分享数失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<CaseReplay> getHotReplays(int limit) {
        try {
            return caseReplayMapper.selectHotReplays(limit);
        } catch (Exception e) {
            log.error("获取热门案例回放失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<CaseReplay> getLatestReplays(int limit) {
        try {
            return caseReplayMapper.selectLatestReplays(limit);
        } catch (Exception e) {
            log.error("获取最新案例回放失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getCaseReplayStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("typeStats", caseReplayMapper.selectTypeStats());
            return stats;
        } catch (Exception e) {
            log.error("统计案例回放信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> startReplay(Long id) {
        try {
            CaseReplay caseReplay = getById(id);
            if (caseReplay == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "案例回放不存在");
                return result;
            }

            // 增加浏览量
            increaseViewCount(id);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "开始回放成功");
            result.put("replay", caseReplay);
            return result;
        } catch (Exception e) {
            log.error("开始案例回放失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "开始回放失败");
            return result;
        }
    }

    @Override
    public Map<String, Object> getReplayProgress(Long id, Long userId) {
        try {
            // 模拟获取回放进度
            // 实际项目中，这里可以从数据库或缓存中获取用户的回放进度
            Map<String, Object> result = new HashMap<>();
            result.put("progress", 0);
            result.put("lastPlayTime", null);
            return result;
        } catch (Exception e) {
            log.error("获取回放进度失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("progress", 0);
            result.put("lastPlayTime", null);
            return result;
        }
    }

    @Override
    public boolean updateReplayProgress(Long id, Long userId, int progress) {
        try {
            // 模拟更新回放进度
            // 实际项目中，这里可以将用户的回放进度保存到数据库或缓存中
            log.info("更新回放进度: id={}, userId={}, progress={}", id, userId, progress);
            return true;
        } catch (Exception e) {
            log.error("更新回放进度失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
