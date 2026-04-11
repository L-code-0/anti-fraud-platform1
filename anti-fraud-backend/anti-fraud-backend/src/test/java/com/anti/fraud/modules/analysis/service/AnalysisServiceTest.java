package com.anti.fraud.modules.analysis.service;

import com.anti.fraud.modules.analysis.entity.LearningWeakness;
import com.anti.fraud.modules.analysis.mapper.LearningWeaknessMapper;
import com.anti.fraud.modules.analysis.service.impl.AnalysisServiceImpl;
import com.anti.fraud.modules.analysis.vo.CategoryMasteryVO;
import com.anti.fraud.modules.analysis.vo.LearningReportVO;
import com.anti.fraud.modules.analysis.vo.WeaknessVO;
import com.anti.fraud.modules.test.entity.TestRecord;
import com.anti.fraud.modules.test.mapper.TestRecordMapper;
import com.anti.fraud.modules.simulation.mapper.SimulationRecordMapper;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeContentMapper;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 学习分析服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("学习分析服务测试")
class AnalysisServiceTest {

    @Mock
    private LearningWeaknessMapper weaknessMapper;

    @Mock
    private TestRecordMapper testRecordMapper;

    @Mock
    private SimulationRecordMapper simulationRecordMapper;

    @Mock
    private KnowledgeContentMapper knowledgeMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AnalysisServiceImpl analysisService;

    private User testUser;
    private List<TestRecord> testRecords;
    private List<LearningWeakness> weaknesses;

    @BeforeEach
    void setUp() {
        // 初始化测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRealName("测试用户");
        testUser.setStudyDuration(3600); // 1小时

        // 初始化测试记录
        testRecords = new ArrayList<>();
        TestRecord record1 = new TestRecord();
        record1.setId(1L);
        record1.setUserId(1L);
        record1.setUserScore(BigDecimal.valueOf(85));
        testRecords.add(record1);

        TestRecord record2 = new TestRecord();
        record2.setId(2L);
        record2.setUserId(1L);
        record2.setUserScore(BigDecimal.valueOf(90));
        testRecords.add(record2);

        // 初始化薄弱点
        weaknesses = new ArrayList<>();
        LearningWeakness w1 = new LearningWeakness();
        w1.setId(1L);
        w1.setUserId(1L);
        w1.setCategory("telecom_fraud");
        w1.setCategoryName("电信诈骗");
        w1.setTotalQuestions(10);
        w1.setWrongQuestions(4);
        w1.setCorrectRate(0.6);
        w1.setWeaknessLevel(2);
        weaknesses.add(w1);
    }

    @Test
    @DisplayName("获取学习报告成功")
    void getLearningReportSuccess() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(testRecordMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(testRecords);
        when(simulationRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);
        when(weaknessMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(weaknesses);
        when(knowledgeMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(new ArrayList<>());

        // When
        LearningReportVO report = analysisService.getLearningReport(1L);

        // Then
        assertNotNull(report);
        assertEquals(3600, report.getTotalStudyDuration());
        assertEquals(2, report.getTotalTestCount());
        assertEquals(87.5, report.getAvgTestScore());
        assertEquals(5, report.getSimulationCount());
        assertFalse(report.getWeaknesses().isEmpty());
    }

    @Test
    @DisplayName("获取学习报告 - 无测试记录")
    void getLearningReportNoTestRecords() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(testRecordMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(new ArrayList<>());
        when(simulationRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(weaknessMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(new ArrayList<>());
        when(knowledgeMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(new ArrayList<>());

        // When
        LearningReportVO report = analysisService.getLearningReport(1L);

        // Then
        assertNotNull(report);
        assertEquals(0, report.getTotalTestCount());
        assertEquals(0.0, report.getAvgTestScore());
        assertEquals(0, report.getSimulationCount());
    }

    @Test
    @DisplayName("获取薄弱知识点")
    void getWeaknesses() {
        // Given
        when(weaknessMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(weaknesses);

        // When
        List<WeaknessVO> result = analysisService.getWeaknesses(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("电信诈骗", result.get(0).getCategoryName());
        assertEquals(0.6, result.get(0).getCorrectRate());
        assertNotNull(result.get(0).getSuggestion());
    }

    @Test
    @DisplayName("更新薄弱点分析 - 新增薄弱点")
    void updateWeaknessAnalysisNew() {
        // Given
        when(weaknessMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(weaknessMapper.insert(any(LearningWeakness.class))).thenReturn(1);

        // When
        analysisService.updateWeaknessAnalysis(1L, "telecom_fraud", true);

        // Then
        verify(weaknessMapper, times(1)).insert(any(LearningWeakness.class));
        verify(weaknessMapper, never()).updateById(any(LearningWeakness.class));
    }

    @Test
    @DisplayName("更新薄弱点分析 - 更新现有薄弱点")
    void updateWeaknessAnalysisUpdate() {
        // Given
        LearningWeakness existing = weaknesses.get(0);
        when(weaknessMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
        when(weaknessMapper.updateById(any(LearningWeakness.class))).thenReturn(1);

        // When
        analysisService.updateWeaknessAnalysis(1L, "telecom_fraud", false);

        // Then
        verify(weaknessMapper, never()).insert(any(LearningWeakness.class));
        verify(weaknessMapper, times(1)).updateById(any(LearningWeakness.class));
    }

    @Test
    @DisplayName("计算薄弱等级 - 严重")
    void calculateWeaknessLevelSevere() {
        // Given
        LearningWeakness w = new LearningWeakness();
        w.setCorrectRate(0.3);
        when(weaknessMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(weaknessMapper.insert(any(LearningWeakness.class))).thenReturn(1);

        // When
        analysisService.updateWeaknessAnalysis(1L, "telecom_fraud", false);

        // Then
        verify(weaknessMapper, times(1)).insert(argThat((LearningWeakness entity) -> {
            return entity.getWeaknessLevel() != null && entity.getWeaknessLevel() == 3; // 严重
        }));
    }

    @Test
    @DisplayName("获取分类掌握情况")
    void getCategoryMastery() {
        // Given
        when(weaknessMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(weaknesses);

        // When
        List<Map<String, Object>> result = analysisService.getCategoryMastery(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("电信诈骗", result.get(0).get("categoryName"));
        assertEquals(10, result.get(0).get("totalQuestions"));
        assertEquals(6, result.get(0).get("correctQuestions"));
        assertEquals(0.6, result.get(0).get("correctRate"));
    }

    @Test
    @DisplayName("保存学习报告")
    void saveLearningReport() {
        // Given
        LearningReportVO report = new LearningReportVO();
        report.setWeaknesses(new ArrayList<>());
        report.setCategoryMastery(new ArrayList<>());
        when(weaknessMapper.insert(any(LearningWeakness.class))).thenReturn(1);

        // When
        analysisService.saveLearningReport(1L, report);

        // Then
        verify(weaknessMapper, times(0)).insert(any(LearningWeakness.class));
    }

    private CategoryMasteryVO getCategoryMasteryVO() {
        CategoryMasteryVO vo = new CategoryMasteryVO();
        vo.setCategoryName("电信诈骗");
        vo.setTotalQuestions(10);
        vo.setCorrectQuestions(6);
        vo.setCorrectRate(0.6);
        vo.setMasteryLevel("一般");
        return vo;
    }
}
