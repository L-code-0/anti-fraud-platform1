package com.anti.fraud.modules.analysis.service.impl;

import com.anti.fraud.modules.analysis.entity.DrillAnalysis;
import com.anti.fraud.modules.analysis.mapper.DrillAnalysisMapper;
import com.anti.fraud.modules.analysis.service.DrillAnalysisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DrillAnalysisServiceImpl implements DrillAnalysisService {

    private final DrillAnalysisMapper drillAnalysisMapper;

    @Override
    public boolean createAnalysis(DrillAnalysis analysis) {
        try {
            drillAnalysisMapper.insert(analysis);
            log.info("创建演练分析成功: id={}, drillName={}", analysis.getId(), analysis.getDrillName());
            return true;
        } catch (Exception e) {
            log.error("创建演练分析失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateAnalysis(DrillAnalysis analysis) {
        try {
            drillAnalysisMapper.updateById(analysis);
            log.info("更新演练分析成功: id={}", analysis.getId());
            return true;
        } catch (Exception e) {
            log.error("更新演练分析失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public DrillAnalysis getAnalysisById(Long id) {
        try {
            return drillAnalysisMapper.selectById(id);
        } catch (Exception e) {
            log.error("获取分析详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DrillAnalysis> getUserAnalysisList(Long userId, String drillType, int page, int size) {
        try {
            LambdaQueryWrapper<DrillAnalysis> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DrillAnalysis::getUserId, userId);
            if (drillType != null && !drillType.isEmpty()) {
                queryWrapper.eq(DrillAnalysis::getDrillType, drillType);
            }
            queryWrapper.orderByDesc(DrillAnalysis::getCreateTime);

            IPage<DrillAnalysis> pageInfo = new Page<>(page, size);
            drillAnalysisMapper.selectPage(pageInfo, queryWrapper);

            return pageInfo.getRecords();
        } catch (Exception e) {
            log.error("获取用户演练分析列表失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> analyzeDrillData(Long userId, Long drillId, String drillType, String drillName, int score) {
        try {
            // 计算平均分
            int averageScore = calculateAverageScore(drillType);
            
            // 计算排名
            int rank = calculateRank(drillType, score);
            
            // 计算进步
            int improvement = calculateImprovement(userId, drillType, score);
            
            // 生成分析结果
            String analysisResult = generateAnalysisResult(score, averageScore, improvement);
            
            // 保存分析数据
            DrillAnalysis analysis = new DrillAnalysis();
            analysis.setUserId(userId);
            analysis.setDrillId(drillId);
            analysis.setDrillType(drillType);
            analysis.setDrillName(drillName);
            analysis.setScore(score);
            analysis.setRank(rank);
            analysis.setAverageScore(averageScore);
            analysis.setImprovement(improvement);
            analysis.setAnalysisResult(analysisResult);
            drillAnalysisMapper.insert(analysis);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("analysisId", analysis.getId());
            result.put("score", score);
            result.put("averageScore", averageScore);
            result.put("rank", rank);
            result.put("improvement", improvement);
            result.put("analysisResult", analysisResult);
            
            log.info("分析演练数据成功: userId={}, drillType={}, score={}", userId, drillType, score);
            return result;
        } catch (Exception e) {
            log.error("分析演练数据失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "分析演练数据失败");
            return result;
        }
    }

    @Override
    public Map<String, Object> compareWithOthers(Long userId, String drillType) {
        try {
            // 获取用户最近的演练数据
            LambdaQueryWrapper<DrillAnalysis> userQuery = new LambdaQueryWrapper<>();
            userQuery.eq(DrillAnalysis::getUserId, userId)
                    .eq(DrillAnalysis::getDrillType, drillType)
                    .orderByDesc(DrillAnalysis::getCreateTime)
                    .last("LIMIT 1");
            DrillAnalysis userAnalysis = drillAnalysisMapper.selectOne(userQuery);
            
            if (userAnalysis == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户暂无演练数据");
                return result;
            }
            
            // 计算平均分
            int averageScore = calculateAverageScore(drillType);
            
            // 计算排名
            int rank = calculateRank(drillType, userAnalysis.getScore());
            
            // 计算超越百分比
            double exceedPercentage = calculateExceedPercentage(drillType, userAnalysis.getScore());
            
            // 获取前10名用户
            List<Map<String, Object>> topUsers = getTopUsers(drillType, 10);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("userScore", userAnalysis.getScore());
            result.put("averageScore", averageScore);
            result.put("rank", rank);
            result.put("exceedPercentage", exceedPercentage);
            result.put("topUsers", topUsers);
            
            log.info("与他人对比分析成功: userId={}, drillType={}", userId, drillType);
            return result;
        } catch (Exception e) {
            log.error("与他人对比分析失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "与他人对比分析失败");
            return result;
        }
    }

    @Override
    public Map<String, Object> analyzeProgress(Long userId, String drillType, String period) {
        try {
            // 计算时间范围
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate;
            switch (period) {
                case "week":
                    startDate = now.minus(1, ChronoUnit.WEEKS);
                    break;
                case "month":
                    startDate = now.minus(1, ChronoUnit.MONTHS);
                    break;
                case "year":
                    startDate = now.minus(1, ChronoUnit.YEARS);
                    break;
                default:
                    startDate = now.minus(1, ChronoUnit.MONTHS);
            }
            
            // 获取用户在时间范围内的演练数据
            LambdaQueryWrapper<DrillAnalysis> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DrillAnalysis::getUserId, userId)
                    .eq(DrillAnalysis::getDrillType, drillType)
                    .ge(DrillAnalysis::getCreateTime, startDate)
                    .orderByAsc(DrillAnalysis::getCreateTime);
            List<DrillAnalysis> analyses = drillAnalysisMapper.selectList(queryWrapper);
            
            if (analyses.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "暂无进步数据");
                return result;
            }
            
            // 计算进步数据
            List<Map<String, Object>> progressData = new ArrayList<>();
            int initialScore = analyses.get(0).getScore();
            int finalScore = analyses.get(analyses.size() - 1).getScore();
            int totalImprovement = finalScore - initialScore;
            double improvementRate = initialScore > 0 ? (double) totalImprovement / initialScore * 100 : 0;
            
            for (DrillAnalysis analysis : analyses) {
                Map<String, Object> data = new HashMap<>();
                data.put("date", analysis.getCreateTime());
                data.put("score", analysis.getScore());
                data.put("improvement", analysis.getImprovement());
                progressData.add(data);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("progressData", progressData);
            result.put("initialScore", initialScore);
            result.put("finalScore", finalScore);
            result.put("totalImprovement", totalImprovement);
            result.put("improvementRate", improvementRate);
            
            log.info("进步分析成功: userId={}, drillType={}, period={}", userId, drillType, period);
            return result;
        } catch (Exception e) {
            log.error("进步分析失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "进步分析失败");
            return result;
        }
    }

    @Override
    public Map<String, Object> getDrillStats(Long userId) {
        try {
            // 统计用户演练总数
            LambdaQueryWrapper<DrillAnalysis> totalQuery = new LambdaQueryWrapper<>();
            totalQuery.eq(DrillAnalysis::getUserId, userId);
            long totalDrills = drillAnalysisMapper.selectCount(totalQuery);
            
            // 统计不同类型的演练数量
            Map<String, Long> typeStats = new HashMap<>();
            List<DrillAnalysis> analyses = drillAnalysisMapper.selectList(totalQuery);
            for (DrillAnalysis analysis : analyses) {
                String drillType = analysis.getDrillType();
                typeStats.put(drillType, typeStats.getOrDefault(drillType, 0L) + 1);
            }
            
            // 计算平均分
            double averageScore = 0;
            if (!analyses.isEmpty()) {
                int totalScore = 0;
                for (DrillAnalysis analysis : analyses) {
                    totalScore += analysis.getScore();
                }
                averageScore = (double) totalScore / analyses.size();
            }
            
            // 计算最高分
            int highestScore = 0;
            if (!analyses.isEmpty()) {
                highestScore = analyses.stream().mapToInt(DrillAnalysis::getScore).max().orElse(0);
            }
            
            // 计算进步最多的演练
            int maxImprovement = 0;
            DrillAnalysis maxImprovementAnalysis = null;
            for (DrillAnalysis analysis : analyses) {
                if (analysis.getImprovement() > maxImprovement) {
                    maxImprovement = analysis.getImprovement();
                    maxImprovementAnalysis = analysis;
                }
            }
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalDrills", totalDrills);
            stats.put("typeStats", typeStats);
            stats.put("averageScore", averageScore);
            stats.put("highestScore", highestScore);
            if (maxImprovementAnalysis != null) {
                stats.put("maxImprovement", maxImprovement);
                stats.put("maxImprovementDrill", maxImprovementAnalysis.getDrillName());
            }
            
            log.info("获取演练统计信息成功: userId={}, totalDrills={}", userId, totalDrills);
            return stats;
        } catch (Exception e) {
            log.error("获取演练统计信息失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> generateAnalysisReport(Long userId, String drillType) {
        try {
            // 获取用户演练分析列表
            List<DrillAnalysis> analyses = getUserAnalysisList(userId, drillType, 1, 100);
            
            if (analyses.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "暂无演练数据");
                return result;
            }
            
            // 计算统计数据
            int totalDrills = analyses.size();
            int totalScore = 0;
            int highestScore = 0;
            int lowestScore = 100;
            for (DrillAnalysis analysis : analyses) {
                int score = analysis.getScore();
                totalScore += score;
                if (score > highestScore) highestScore = score;
                if (score < lowestScore) lowestScore = score;
            }
            double averageScore = (double) totalScore / totalDrills;
            
            // 与他人对比
            Map<String, Object> comparisonResult = compareWithOthers(userId, drillType);
            
            // 进步分析
            Map<String, Object> progressResult = analyzeProgress(userId, drillType, "month");
            
            Map<String, Object> report = new HashMap<>();
            report.put("success", true);
            report.put("totalDrills", totalDrills);
            report.put("averageScore", averageScore);
            report.put("highestScore", highestScore);
            report.put("lowestScore", lowestScore);
            report.put("comparison", comparisonResult);
            report.put("progress", progressResult);
            report.put("recentDrills", analyses.subList(0, Math.min(5, analyses.size())));
            
            log.info("生成分析报告成功: userId={}, drillType={}", userId, drillType);
            return report;
        } catch (Exception e) {
            log.error("生成分析报告失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "生成分析报告失败");
            return result;
        }
    }

    @Override
    public List<Map<String, Object>> getRanking(String drillType, int limit) {
        try {
            // 模拟排名数据
            List<Map<String, Object>> ranking = new ArrayList<>();
            ranking.add(Map.of("rank", 1, "userName", "张三", "score", 95, "drillType", drillType));
            ranking.add(Map.of("rank", 2, "userName", "李四", "score", 90, "drillType", drillType));
            ranking.add(Map.of("rank", 3, "userName", "王五", "score", 85, "drillType", drillType));
            ranking.add(Map.of("rank", 4, "userName", "赵六", "score", 80, "drillType", drillType));
            ranking.add(Map.of("rank", 5, "userName", "钱七", "score", 75, "drillType", drillType));
            
            return ranking.subList(0, Math.min(limit, ranking.size()));
        } catch (Exception e) {
            log.error("获取排名信息失败: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Integer getHighestScore(Long userId, String drillType) {
        try {
            LambdaQueryWrapper<DrillAnalysis> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DrillAnalysis::getUserId, userId)
                    .eq(DrillAnalysis::getDrillType, drillType)
                    .orderByDesc(DrillAnalysis::getScore)
                    .last("LIMIT 1");
            DrillAnalysis analysis = drillAnalysisMapper.selectOne(queryWrapper);
            return analysis != null ? analysis.getScore() : 0;
        } catch (Exception e) {
            log.error("获取用户历史最高分失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public Double getAverageScore(Long userId, String drillType) {
        try {
            LambdaQueryWrapper<DrillAnalysis> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DrillAnalysis::getUserId, userId)
                    .eq(DrillAnalysis::getDrillType, drillType);
            List<DrillAnalysis> analyses = drillAnalysisMapper.selectList(queryWrapper);
            if (analyses.isEmpty()) {
                return 0.0;
            }
            int totalScore = 0;
            for (DrillAnalysis analysis : analyses) {
                totalScore += analysis.getScore();
            }
            return (double) totalScore / analyses.size();
        } catch (Exception e) {
            log.error("获取用户历史平均分失败: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    // 计算平均分
    private int calculateAverageScore(String drillType) {
        // 模拟数据
        return 75;
    }

    // 计算排名
    private int calculateRank(String drillType, int score) {
        // 模拟数据
        if (score >= 90) return 1;
        if (score >= 80) return 5;
        if (score >= 70) return 10;
        if (score >= 60) return 20;
        return 30;
    }

    // 计算进步
    private int calculateImprovement(Long userId, String drillType, int score) {
        LambdaQueryWrapper<DrillAnalysis> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DrillAnalysis::getUserId, userId)
                .eq(DrillAnalysis::getDrillType, drillType)
                .orderByDesc(DrillAnalysis::getCreateTime)
                .last("LIMIT 1");
        DrillAnalysis previousAnalysis = drillAnalysisMapper.selectOne(queryWrapper);
        if (previousAnalysis == null) {
            return 0;
        }
        return score - previousAnalysis.getScore();
    }

    // 生成分析结果
    private String generateAnalysisResult(int score, int averageScore, int improvement) {
        StringBuilder result = new StringBuilder();
        result.append("您的得分为：").append(score).append("分\n");
        result.append("平均得分为：").append(averageScore).append("分\n");
        
        if (score > averageScore) {
            result.append("您的表现优于平均水平！\n");
        } else if (score < averageScore) {
            result.append("您的表现低于平均水平，需要继续努力！\n");
        } else {
            result.append("您的表现达到了平均水平。\n");
        }
        
        if (improvement > 0) {
            result.append("相比上次，您进步了").append(improvement).append("分，继续加油！\n");
        } else if (improvement < 0) {
            result.append("相比上次，您退步了").append(Math.abs(improvement)).append("分，需要加强练习！\n");
        } else {
            result.append("相比上次，您的表现保持稳定。\n");
        }
        
        return result.toString();
    }

    // 计算超越百分比
    private double calculateExceedPercentage(String drillType, int score) {
        // 模拟数据
        return 75.5;
    }

    // 获取前N名用户
    private List<Map<String, Object>> getTopUsers(String drillType, int limit) {
        return getRanking(drillType, limit);
    }
}
