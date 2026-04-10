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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 后台管理服务实现类
 * <p>
 * 提供管理后台的各类统计数据查询功能。
 * 仪表盘数据使用缓存以提高访问性能。
 * </p>
 *
 * @author Anti-Fraud Platform Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final KnowledgeContentMapper knowledgeMapper;
    private final KnowledgeCategoryMapper categoryMapper;
    private final TestRecordMapper testRecordMapper;
    private final SimulationRecordMapper simulationRecordMapper;
    private final ReportInfoMapper reportMapper;
    private final WarningInfoMapper warningMapper;

    /**
     * 获取仪表盘统计数据
     * <p>
     * 返回管理后台首页所需的各类统计数据，包括用户总数、知识数量、测试参与数等。
     * 数据缓存5分钟，避免频繁查询数据库。
     * </p>
     *
     * @return 仪表盘统计数据
     */
    @Override
    @Cacheable(value = "dashboard", key = "'stats'", unless = "#result == null")
    public DashboardStatsVO getDashboardStats() {
        log.debug("查询仪表盘统计数据");
        
        DashboardStatsVO stats = new DashboardStatsVO();

        // 用户统计
        stats.setTotalUsers(userMapper.selectCount(null));
        stats.setTodayNewUsers(userMapper.selectCount(
                new LambdaQueryWrapper<com.anti.fraud.modules.user.entity.User>()
                        .ge(com.anti.fraud.modules.user.entity.User::getCreateTime, LocalDate.now().atStartOfDay())
        ));

        // 知识统计
        stats.setTotalKnowledge(knowledgeMapper.selectCount(null));

        // 测试统计（已完成测试）
        stats.setTotalTests(testRecordMapper.selectCount(
                new LambdaQueryWrapper<com.anti.fraud.modules.test.entity.TestRecord>()
                        .eq(com.anti.fraud.modules.test.entity.TestRecord::getIsCompleted, 1)
        ));

        // 演练统计（已完成演练）
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

        log.debug("仪表盘统计数据查询完成");
        return stats;
    }

    /**
     * 获取用户增长趋势
     * <p>
     * 按天数统计每日的新增用户数量。
     * </p>
     *
     * @param days 统计天数
     * @return 每日用户增长数据列表
     */
    @Override
    @Cacheable(value = "dashboard", key = "'userTrend_' + #days", unless = "#result == null")
    public List<TrendDataVO> getUserTrend(Integer days) {
        log.debug("查询用户增长趋势: days={}", days);
        
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

        log.debug("用户增长趋势查询完成，共{}条数据", trend.size());
        return trend;
    }

    /**
     * 获取测试参与趋势
     * <p>
     * 按天数统计每日的测试完成数量。
     * </p>
     *
     * @param days 统计天数
     * @return 每日测试参与数据列表
     */
    @Override
    @Cacheable(value = "dashboard", key = "'testTrend_' + #days", unless = "#result == null")
    public List<TrendDataVO> getTestTrend(Integer days) {
        log.debug("查询测试参与趋势: days={}", days);
        
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

        log.debug("测试参与趋势查询完成，共{}条数据", trend.size());
        return trend;
    }

    /**
     * 获取知识分类统计
     * <p>
     * 统计各知识分类下的内容数量。
     * </p>
     *
     * @return 知识分类统计数据列表
     */
    @Override
    @Cacheable(value = "knowledge", key = "'stats'", unless = "#result == null")
    public List<TrendDataVO> getKnowledgeStats() {
        log.debug("查询知识分类统计");
        
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

        log.debug("知识分类统计查询完成，共{}条数据", stats.size());
        return stats;
    }

    /**
     * 获取各院系学习参与率
     * <p>
     * 统计各院系学生的学习参与情况。
     * </p>
     *
     * @return 各院系统计数据列表
     */
    @Override
    @Cacheable(value = "dashboard", key = "'departmentStats'", unless = "#result == null")
    public List<DepartmentStatsVO> getDepartmentStats() {
        log.debug("查询各院系学习参与率");
        
        List<DepartmentStatsVO> stats = new ArrayList<>();

        // 模拟院系统计数据（实际应从数据库查询）
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

        log.debug("院系统计查询完成，共{}条数据", stats.size());
        return stats;
    }

    /**
     * 获取高发诈骗类型统计
     * <p>
     * 统计各类型诈骗举报的数量和占比。
     * </p>
     *
     * @return 诈骗类型统计数据列表
     */
    @Override
    @Cacheable(value = "dashboard", key = "'fraudTypeStats'", unless = "#result == null")
    public List<FraudTypeStatsVO> getFraudTypeStats() {
        log.debug("查询高发诈骗类型统计");
        
        List<FraudTypeStatsVO> stats = new ArrayList<>();

        // 统计各类型举报数量（实际应从数据库查询）
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

        log.debug("诈骗类型统计查询完成，共{}条数据", stats.size());
        return stats;
    }

    /**
     * 获取举报处理时效
     * <p>
     * 计算举报的平均处理时长。
     * </p>
     *
     * @return 举报处理时效数据
     */
    @Override
    @Cacheable(value = "dashboard", key = "'reportEfficiency'", unless = "#result == null")
    public TrendDataVO getReportEfficiency() {
        log.debug("查询举报处理时效");
        
        // 计算平均处理时长（实际应从数据库计算）
        long avgHours = 24 + (long)(Math.random() * 48);
        
        log.debug("举报处理时效: {}小时", avgHours);
        return new TrendDataVO("平均处理时效", avgHours);
    }
}
