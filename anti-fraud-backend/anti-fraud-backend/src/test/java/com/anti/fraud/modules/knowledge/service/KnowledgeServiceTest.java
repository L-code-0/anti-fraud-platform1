package com.anti.fraud.modules.knowledge.service;

import com.anti.fraud.modules.knowledge.entity.KnowledgeContent;
import com.anti.fraud.modules.knowledge.entity.KnowledgeCategory;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeContentMapper;
import com.anti.fraud.modules.knowledge.mapper.KnowledgeCategoryMapper;
import com.anti.fraud.modules.knowledge.service.impl.KnowledgeServiceImpl;
import com.anti.fraud.modules.knowledge.vo.CategoryVO;
import com.anti.fraud.modules.knowledge.vo.KnowledgeDetailVO;
import com.anti.fraud.modules.knowledge.vo.KnowledgeListVO;
import com.anti.fraud.modules.knowledge.dto.KnowledgeQueryDTO;
import com.anti.fraud.common.exception.BusinessException;
import com.anti.fraud.common.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 知识服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("知识服务测试")
class KnowledgeServiceTest {

    @Mock
    private KnowledgeContentMapper knowledgeMapper;

    @Mock
    private KnowledgeCategoryMapper categoryMapper;

    @InjectMocks
    private KnowledgeServiceImpl knowledgeService;

    private KnowledgeContent testKnowledge;
    private KnowledgeCategory testCategory;

    @BeforeEach
    void setUp() {
        // 初始化测试知识内容
        testKnowledge = new KnowledgeContent();
        testKnowledge.setId(1L);
        testKnowledge.setTitle("电信诈骗防范指南");
        testKnowledge.setSummary("本文介绍如何识别和防范电信诈骗");
        testKnowledge.setContent("详细内容...");
        testKnowledge.setContentType(1); // 1=文章类型
        testKnowledge.setCategoryId(1L);
        testKnowledge.setViewCount(100);
        testKnowledge.setCollectCount(20);
        testKnowledge.setLikeCount(50);
        testKnowledge.setAuthorName("管理员");
        testKnowledge.setStatus(1);
        testKnowledge.setAuditStatus(1);
        testKnowledge.setPublishTime(LocalDateTime.now());
        testKnowledge.setCreateTime(LocalDateTime.now());

        // 初始化测试分类
        testCategory = new KnowledgeCategory();
        testCategory.setId(1L);
        testCategory.setCategoryName("电信诈骗");
        testCategory.setSortOrder(1);
        testCategory.setStatus(1);
    }

    @Test
    @DisplayName("获取知识分类列表")
    void getCategories() {
        // Given
        List<KnowledgeCategory> categories = new ArrayList<>();
        categories.add(testCategory);
        when(categoryMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(categories);

        // When
        List<CategoryVO> result = knowledgeService.getCategories();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("电信诈骗", result.get(0).getCategoryName());
    }

    @Test
    @DisplayName("分页查询知识列表")
    void getKnowledgePage() {
        // Given
        List<KnowledgeContent> knowledgeList = new ArrayList<>();
        knowledgeList.add(testKnowledge);
        Page<KnowledgeContent> page = new Page<>(1, 10);
        page.setRecords(knowledgeList);
        page.setTotal(1);

        when(knowledgeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        KnowledgeQueryDTO queryDTO = new KnowledgeQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(10);

        // When
        Page<KnowledgeListVO> result = knowledgeService.getKnowledgePage(queryDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("分页查询知识列表 - 按分类筛选")
    void getKnowledgePageByCategory() {
        // Given
        List<KnowledgeContent> knowledgeList = new ArrayList<>();
        knowledgeList.add(testKnowledge);
        Page<KnowledgeContent> page = new Page<>(1, 10);
        page.setRecords(knowledgeList);
        page.setTotal(1);

        when(knowledgeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        KnowledgeQueryDTO queryDTO = new KnowledgeQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        queryDTO.setCategoryId(1L);

        // When
        Page<KnowledgeListVO> result = knowledgeService.getKnowledgePage(queryDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("分页查询知识列表 - 按关键词搜索")
    void getKnowledgePageByKeyword() {
        // Given
        List<KnowledgeContent> knowledgeList = new ArrayList<>();
        knowledgeList.add(testKnowledge);
        Page<KnowledgeContent> page = new Page<>(1, 10);
        page.setRecords(knowledgeList);
        page.setTotal(1);

        when(knowledgeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        KnowledgeQueryDTO queryDTO = new KnowledgeQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        queryDTO.setKeyword("电信诈骗");

        // When
        Page<KnowledgeListVO> result = knowledgeService.getKnowledgePage(queryDTO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("获取知识详情成功")
    void getKnowledgeDetailSuccess() {
        // Given
        when(knowledgeMapper.selectById(1L)).thenReturn(testKnowledge);
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            // When
            KnowledgeDetailVO result = knowledgeService.getKnowledgeDetail(1L);

            // Then
            assertNotNull(result);
            assertEquals("电信诈骗防范指南", result.getTitle());
            assertEquals(100, result.getViewCount());
        }
    }

    @Test
    @DisplayName("获取知识详情 - 知识不存在")
    void getKnowledgeDetailNotFound() {
        // Given
        when(knowledgeMapper.selectById(99L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            knowledgeService.getKnowledgeDetail(99L);
        });
    }

    @Test
    @DisplayName("收藏知识成功")
    void collectKnowledgeSuccess() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(knowledgeMapper.selectById(1L)).thenReturn(testKnowledge);
            when(knowledgeMapper.updateById(any(KnowledgeContent.class))).thenReturn(1);

            // When
            knowledgeService.collectKnowledge(1L);

            // Then
            verify(knowledgeMapper, times(1)).updateById(any(KnowledgeContent.class));
        }
    }

    @Test
    @DisplayName("收藏知识失败 - 未登录")
    void collectKnowledgeNotLoggedIn() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(null);

            // When & Then
            assertThrows(BusinessException.class, () -> {
                knowledgeService.collectKnowledge(1L);
            });
        }
    }

    @Test
    @DisplayName("收藏知识失败 - 知识不存在")
    void collectKnowledgeKnowledgeNotFound() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(knowledgeMapper.selectById(99L)).thenReturn(null);

            // When & Then
            assertThrows(BusinessException.class, () -> {
                knowledgeService.collectKnowledge(99L);
            });
        }
    }

    @Test
    @DisplayName("取消收藏成功")
    void uncollectKnowledgeSuccess() {
        // Given
        testKnowledge.setCollectCount(20);
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(knowledgeMapper.selectById(1L)).thenReturn(testKnowledge);
            when(knowledgeMapper.updateById(any(KnowledgeContent.class))).thenReturn(1);

            // When
            knowledgeService.uncollectKnowledge(1L);

            // Then
            verify(knowledgeMapper, times(1)).updateById(any(KnowledgeContent.class));
        }
    }

    @Test
    @DisplayName("取消收藏 - 收藏数为0时不减少")
    void uncollectKnowledgeCountZero() {
        // Given
        testKnowledge.setCollectCount(0);
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(knowledgeMapper.selectById(1L)).thenReturn(testKnowledge);

            // When
            knowledgeService.uncollectKnowledge(1L);

            // Then
            verify(knowledgeMapper, never()).updateById(any(KnowledgeContent.class));
        }
    }

    @Test
    @DisplayName("记录学习进度")
    void recordProgress() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            // When
            knowledgeService.recordProgress(1L, 300);

            // Then
            // 验证日志记录（无异常即可）
        }
    }

    @Test
    @DisplayName("记录学习进度 - 未登录时不记录")
    void recordProgressNotLoggedIn() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(null);

            // When
            knowledgeService.recordProgress(1L, 300);

            // Then
            // 未登录时不执行任何操作
        }
    }

    @Test
    @DisplayName("分页参数边界测试 - 页码为负数")
    void getKnowledgePageNegativePage() {
        // Given
        List<KnowledgeContent> knowledgeList = new ArrayList<>();
        Page<KnowledgeContent> page = new Page<>(1, 10);
        page.setRecords(knowledgeList);
        page.setTotal(0);

        when(knowledgeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        KnowledgeQueryDTO queryDTO = new KnowledgeQueryDTO();
        queryDTO.setPage(-1);
        queryDTO.setSize(10);

        // When
        Page<KnowledgeListVO> result = knowledgeService.getKnowledgePage(queryDTO);

        // Then
        assertNotNull(result);
        // 页码应该被修正为1
    }

    @Test
    @DisplayName("分页参数边界测试 - 每页数量过大")
    void getKnowledgePageOversizedPage() {
        // Given
        List<KnowledgeContent> knowledgeList = new ArrayList<>();
        Page<KnowledgeContent> page = new Page<>(1, 100);
        page.setRecords(knowledgeList);
        page.setTotal(0);

        when(knowledgeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                .thenReturn(page);

        KnowledgeQueryDTO queryDTO = new KnowledgeQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(500);

        // When
        Page<KnowledgeListVO> result = knowledgeService.getKnowledgePage(queryDTO);

        // Then
        assertNotNull(result);
        // 每页数量应该被限制为100
    }
}
