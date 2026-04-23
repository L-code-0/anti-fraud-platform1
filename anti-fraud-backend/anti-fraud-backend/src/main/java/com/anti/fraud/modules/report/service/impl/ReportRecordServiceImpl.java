package com.anti.fraud.modules.report.service.impl;

import com.anti.fraud.modules.report.entity.ReportRecord;
import com.anti.fraud.modules.report.mapper.ReportRecordMapper;
import com.anti.fraud.modules.report.service.ReportRecordService;
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
 * 举报记录服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReportRecordServiceImpl extends ServiceImpl<ReportRecordMapper, ReportRecord> implements ReportRecordService {

    private final ReportRecordMapper reportRecordMapper;

    @Override
    @Transactional
    public boolean addReportRecord(ReportRecord reportRecord) {
        try {
            reportRecord.setHandleStatus(1); // 待处理
            reportRecord.setRewardPoints(0);
            reportRecord.setStatus(1);
            reportRecord.setDeleted(0);
            reportRecord.setCreateTime(LocalDateTime.now());
            reportRecord.setUpdateTime(LocalDateTime.now());
            return save(reportRecord);
        } catch (Exception e) {
            log.error("新增举报记录失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateReportRecord(ReportRecord reportRecord) {
        try {
            reportRecord.setUpdateTime(LocalDateTime.now());
            return updateById(reportRecord);
        } catch (Exception e) {
            log.error("更新举报记录失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteReportRecord(Long id) {
        try {
            ReportRecord reportRecord = getById(id);
            if (reportRecord != null) {
                reportRecord.setDeleted(1);
                reportRecord.setUpdateTime(LocalDateTime.now());
                return updateById(reportRecord);
            }
            return false;
        } catch (Exception e) {
            log.error("删除举报记录失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public ReportRecord getReportRecordById(Long id) {
        try {
            return getById(id);
        } catch (Exception e) {
            log.error("获取举报记录详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getReportRecordList(Map<String, Object> params, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<ReportRecord> reportRecords = reportRecordMapper.selectByCondition(params, offset, size);
            int total = reportRecordMapper.selectCountByCondition(params);

            Map<String, Object> result = new HashMap<>();
            result.put("list", reportRecords);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("查询举报记录列表失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public Map<String, Object> getReportRecordsByUserId(Long userId, int page, int size) {
        try {
            int offset = (page - 1) * size;
            List<ReportRecord> reportRecords = reportRecordMapper.selectByUserId(userId, offset, size);
            int total = reportRecordMapper.selectCountByUserId(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("list", reportRecords);
            result.put("total", total);
            return result;
        } catch (Exception e) {
            log.error("根据用户ID查询举报记录失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("list", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    @Override
    public List<ReportRecord> getPendingReports(int limit) {
        try {
            return reportRecordMapper.selectPendingReports(limit);
        } catch (Exception e) {
            log.error("获取待处理的举报记录失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean handleReport(Long id, Integer handleStatus, String handleResult, String handler, Integer rewardPoints) {
        try {
            ReportRecord reportRecord = getById(id);
            if (reportRecord == null) {
                return false;
            }

            reportRecord.setHandleStatus(handleStatus);
            reportRecord.setHandleResult(handleResult);
            reportRecord.setHandler(handler);
            reportRecord.setHandleTime(LocalDateTime.now());
            reportRecord.setRewardPoints(rewardPoints);
            reportRecord.setUpdateTime(LocalDateTime.now());

            return updateById(reportRecord);
        } catch (Exception e) {
            log.error("处理举报记录失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getReportRecordStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("typeStats", reportRecordMapper.selectTypeStats());
            stats.put("statusStats", reportRecordMapper.selectStatusStats());
            stats.put("monthlyStats", reportRecordMapper.selectMonthlyStats(6));
            return stats;
        } catch (Exception e) {
            log.error("统计举报记录信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getUserReportStats(Long userId) {
        try {
            return reportRecordMapper.selectUserReportStats(userId);
        } catch (Exception e) {
            log.error("获取用户举报统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Integer getUserRewardPoints(Long userId) {
        try {
            return reportRecordMapper.selectUserRewardPoints(userId);
        } catch (Exception e) {
            log.error("获取用户获得的积分奖励失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public List<Map<String, Object>> getMonthlyStats(int months) {
        try {
            return reportRecordMapper.selectMonthlyStats(months);
        } catch (Exception e) {
            log.error("按月份统计举报记录失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Integer calculateRewardPoints(Integer reportType, String reportContent, String evidence) {
        try {
            // 基础积分
            int basePoints = 10;

            // 根据举报类型增加积分
            switch (reportType) {
                case 1: // 虚假信息
                    basePoints += 5;
                    break;
                case 2: // 诈骗行为
                    basePoints += 15;
                    break;
                case 3: // 不良内容
                    basePoints += 8;
                    break;
                case 4: // 其他
                    basePoints += 3;
                    break;
            }

            // 根据举报内容长度增加积分
            if (reportContent != null && reportContent.length() > 100) {
                basePoints += 5;
            } else if (reportContent != null && reportContent.length() > 50) {
                basePoints += 3;
            }

            // 根据证据数量增加积分
            if (evidence != null && evidence.length() > 0) {
                basePoints += 10;
            }

            // 最高积分限制
            return Math.min(basePoints, 50);
        } catch (Exception e) {
            log.error("计算举报积分奖励失败: {}", e.getMessage(), e);
            return 0;
        }
    }
}
