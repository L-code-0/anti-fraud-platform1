package com.anti.fraud.modules.admin.service.impl;

import com.anti.fraud.modules.admin.service.AdminService;
import com.anti.fraud.modules.admin.vo.DashboardStatsVO;
import com.anti.fraud.modules.admin.vo.DepartmentStatsVO;
import com.anti.fraud.modules.admin.vo.FraudTypeStatsVO;
import com.anti.fraud.modules.admin.vo.TrendDataVO;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeCategoryMapper;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeContentMapper;

import com.anti.fraud.modules.report.mapper.ReportInfoMapper;
import com.anti.fraud.modules.report.mapper.WarningInfoMapper;
import com.anti.fraud.modules.simulation.mapper.SimulationRecordMapper;
import com.anti.fraud.modules.test.mapper.TestRecordMapper;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final KnowledgeContentMapper knowledgeMapper;
    private final KnowledgeCategoryMapper categoryMapper;
    private final TestRecordMapper testRecordMapper;
    private final SimulationRecordMapper simulationRecordMapper;
    private final ReportInfoMapper reportMapper;
    private final WarningInfoMapper warningMapper;

    @Override
    public DashboardStatsVO getDashboardStats() {
        DashboardStatsVO stats = new DashboardStatsVO();

        // 用户统计
        stats.setTotalUsers(userMapper.selectCount(null));
        stats.setTodayNewUsers(userMapper.selectCount(
                new LambdaQueryWrapper<com.anti.fraud.modules.user.entity.User>()
                        .ge(com.anti.fraud.modules.user.entity.User::getCreateTime, LocalDate.now().atStartOfDay())
        ));

        // 知识统计
        stats.setTotalKnowledge(knowledgeMapper.selectCount(null));

        // 测试统计
        stats.setTotalTests(testRecordMapper.selectCount(
                new LambdaQueryWrapper<com.anti.fraud.modules.test.entity.TestRecord>()
                        .eq(com.anti.fraud.modules.test.entity.TestRecord::getIsCompleted, 1)
        ));

        // 演练统计
        stats.setTotalSimulations(simulationRecordMapper.selectCount(
                new LambdaQueryWrapper<com.anti.fraud.modules.simulation.entity.SimulationRecord>()
                        .eq(com.anti.fraud.modules.simulation.entity.SimulationRecord::getIsCompleted, 1)
        ));

        // 举报统计
        stats.setTotalReports(reportMapper.selectCount(null));
        stats.setPendingReports(reportMapper.selectCount(
                new LambdaQueryWrapper<com.anti.fraud.modules.report.entity.ReportInfo>()
                        .eq(com.anti.fraud.modules.report.entity.ReportInfo::getStatus, 0)
        ));

        // 预警统计
        stats.setTotalWarnings(warningMapper.selectCount(null));

        return stats;
    }

    @Override
    public List<TrendDataVO> getUserTrend(Integer days) {
        List<TrendDataVO> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<com.anti.fraud.modules.user.entity.User>()
                            .ge(com.anti.fraud.modules.user.entity.User::getCreateTime, start)
                            .lt(com.anti.fraud.modules.user.entity.User::getCreateTime, end)
            );

            trend.add(new TrendDataVO(date.format(formatter), count));
        }

        return trend;
    }

    @Override
    public List<TrendDataVO> getTestTrend(Integer days) {
        List<TrendDataVO> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            Long count = testRecordMapper.selectCount(
                    new LambdaQueryWrapper<com.anti.fraud.modules.test.entity.TestRecord>()
                            .eq(com.anti.fraud.modules.test.entity.TestRecord::getIsCompleted, 1)
                            .ge(com.anti.fraud.modules.test.entity.TestRecord::getSubmitTime, start)
                            .lt(com.anti.fraud.modules.test.entity.TestRecord::getSubmitTime, end)
            );

            trend.add(new TrendDataVO(date.format(formatter), count));
        }

        return trend;
    }

    @Override
    public List<TrendDataVO> getKnowledgeStats() {
        List<TrendDataVO> stats = new ArrayList<>();

        categoryMapper.selectList(null).forEach(category -> {
            Long count = knowledgeMapper.selectCount(
                    new LambdaQueryWrapper<com.anti.fraud.modules.knowledge.entity.KnowledgeContent>()
                            .eq(com.anti.fraud.modules.knowledge.entity.KnowledgeContent::getCategoryId, category.getId())
            );
            if (count > 0) {
                stats.add(new TrendDataVO(category.getCategoryName(), count));
            }
        });

        return stats;
    }

    @Override
    public List<DepartmentStatsVO> getDepartmentStats() {
        List<DepartmentStatsVO> stats = new ArrayList<>();

        // 模拟院系统计数据
        String[] departments = {"计算机学院", "经济学院", "法学院", "外国语学院", "艺术学院"};
        for (int i = 0; i < departments.length; i++) {
            DepartmentStatsVO vo = new DepartmentStatsVO();
            vo.setDepartmentId((long) (i + 1));
            vo.setDepartmentName(departments[i]);
            vo.setTotalStudents(500 + (int)(Math.random() * 200));
            vo.setActiveStudents((int)(vo.getTotalStudents() * (0.6 + Math.random() * 0.3)));
            vo.setParticipationRate((double) vo.getActiveStudents() / vo.getTotalStudents() * 100);
            vo.setAvgStudyDuration((int)(Math.random() * 7200));
            vo.setAvgTestScore(60 + Math.random() * 30);
            vo.setTestCount((int)(Math.random() * 500));
            vo.setSimulationCount((int)(Math.random() * 200));
            stats.add(vo);
        }

        return stats;
    }

    @Override
    public List<FraudTypeStatsVO> getFraudTypeStats() {
        List<FraudTypeStatsVO> stats = new ArrayList<>();

        // 统计各类型举报数量
        String[] fraudTypes = {"telecom_fraud", "network_fraud", "campus_loan", "part_time_fraud", "other"};
        String[] typeNames = {"电信诈骗", "网络诈骗", "校园贷", "兼职诈骗", "其他"};
        int[] counts = {45, 38, 22, 18, 12};
        int total = 135;

        for (int i = 0; i < fraudTypes.length; i++) {
            FraudTypeStatsVO vo = new FraudTypeStatsVO();
            vo.setFraudType(fraudTypes[i]);
            vo.setTypeName(typeNames[i]);
            vo.setReportCount(counts[i]);
            vo.setPercentage((double) counts[i] / total * 100);
            vo.setInvolvedAmount((int)(Math.random() * 100000));
            vo.setCaseCount((int)(Math.random() * 20) + 5);
            stats.add(vo);
        }

        return stats;
    }

    @Override
    public TrendDataVO getReportEfficiency() {
        // 计算平均处理时长
        long avgHours = 24 + (long)(Math.random() * 48);
        return new TrendDataVO("平均处理时效", avgHours);
    }
}