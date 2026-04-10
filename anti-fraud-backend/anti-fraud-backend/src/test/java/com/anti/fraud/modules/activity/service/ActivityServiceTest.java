package com.anti.fraud.modules.activity.service;

import com.anti.fraud.modules.activity.entity.Activity;
import com.anti.fraud.modules.activity.entity.ActivityRegistration;
import com.anti.fraud.modules.activity.mapper.ActivityMapper;
import com.anti.fraud.modules.activity.mapper.ActivityRegistrationMapper;
import com.anti.fraud.modules.activity.service.impl.ActivityServiceImpl;
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
 * 活动服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("活动服务测试")
class ActivityServiceTest {

    @Mock
    private ActivityMapper activityMapper;

    @Mock
    private ActivityRegistrationMapper registrationMapper;

    @InjectMocks
    private ActivityServiceImpl activityService;

    private Activity testActivity;
    private ActivityRegistration testRegistration;

    @BeforeEach
    void setUp() {
        // 初始化测试活动
        testActivity = new Activity();
        testActivity.setId(1L);
        testActivity.setActivityName("反诈知识讲座");
        testActivity.setActivityType(1);
        testActivity.setDescription("学习如何识别和防范电信诈骗");
        testActivity.setStartTime(LocalDateTime.now().plusDays(1));
        testActivity.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        testActivity.setMaxParticipants(100);
        testActivity.setCurrentParticipants(50);
        testActivity.setPointsReward(50);
        testActivity.setStatus(1); // 报名中
        testActivity.setCreateTime(LocalDateTime.now());

        // 初始化报名记录
        testRegistration = new ActivityRegistration();
        testRegistration.setId(1L);
        testRegistration.setActivityId(1L);
        testRegistration.setUserId(1L);
        testRegistration.setStatus(0);
    }

    @Test
    @DisplayName("分页查询活动列表")
    void getActivityPage() {
        // Given
        List<Activity> activities = new ArrayList<>();
        activities.add(testActivity);
        Page<Activity> page = new Page<>(1, 10);
        page.setRecords(activities);
        page.setTotal(1);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            
            when(activityMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(page);

            // When
            Page<?> result = activityService.getActivityPage(1, 10, null, null);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }
    }

    @Test
    @DisplayName("获取活动详情")
    void getActivityDetail() {
        // Given
        when(activityMapper.selectById(1L)).thenReturn(testActivity);
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            // When
            var result = activityService.getActivityDetail(1L);

            // Then
            assertNotNull(result);
            assertEquals("反诈知识讲座", result.getActivityName());
        }
    }

    @Test
    @DisplayName("获取活动详情 - 活动不存在")
    void getActivityDetailNotFound() {
        // Given
        when(activityMapper.selectById(99L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            activityService.getActivityDetail(99L);
        });
    }

    @Test
    @DisplayName("创建活动成功")
    void createActivitySuccess() {
        // Given
        var createDTO = new com.anti.fraud.modules.activity.dto.ActivityCreateDTO();
        createDTO.setActivityName("新活动");
        createDTO.setActivityType(1);
        createDTO.setDescription("活动描述");
        createDTO.setStartTime(LocalDateTime.now().plusDays(1));
        createDTO.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        createDTO.setMaxParticipants(50);
        createDTO.setPointsReward(30);

        when(activityMapper.insert(any(Activity.class))).thenReturn(1);

        // When
        activityService.createActivity(createDTO);

        // Then
        verify(activityMapper, times(1)).insert(any(Activity.class));
    }

    @Test
    @DisplayName("创建活动 - 开始时间在当前时间之后，状态应为报名中")
    void createActivitySetsCorrectStatus() {
        // Given
        var createDTO = new com.anti.fraud.modules.activity.dto.ActivityCreateDTO();
        createDTO.setActivityName("未来活动");
        createDTO.setActivityType(1);
        createDTO.setStartTime(LocalDateTime.now().plusDays(5));
        createDTO.setEndTime(LocalDateTime.now().plusDays(5).plusHours(2));
        createDTO.setMaxParticipants(100);
        createDTO.setPointsReward(20);

        when(activityMapper.insert(any(Activity.class))).thenReturn(1);

        // When
        activityService.createActivity(createDTO);

        // Then
        verify(activityMapper, times(1)).insert(argThat(activity -> {
            return activity.getStatus() == 1; // 报名中
        }));
    }

    @Test
    @DisplayName("更新活动成功")
    void updateActivitySuccess() {
        // Given
        when(activityMapper.selectById(1L)).thenReturn(testActivity);
        when(activityMapper.updateById(any(Activity.class))).thenReturn(1);

        var updateDTO = new com.anti.fraud.modules.activity.dto.ActivityCreateDTO();
        updateDTO.setActivityName("更新后的活动名称");
        updateDTO.setActivityType(1);
        updateDTO.setDescription("更新后的描述");
        updateDTO.setStartTime(LocalDateTime.now().plusDays(2));
        updateDTO.setEndTime(LocalDateTime.now().plusDays(2).plusHours(2));
        updateDTO.setMaxParticipants(150);
        updateDTO.setPointsReward(60);

        // When
        activityService.updateActivity(1L, updateDTO);

        // Then
        verify(activityMapper, times(1)).updateById(any(Activity.class));
    }

    @Test
    @DisplayName("更新活动 - 活动不存在")
    void updateActivityNotFound() {
        // Given
        when(activityMapper.selectById(99L)).thenReturn(null);

        var updateDTO = new com.anti.fraud.modules.activity.dto.ActivityCreateDTO();

        // When & Then
        assertThrows(BusinessException.class, () -> {
            activityService.updateActivity(99L, updateDTO);
        });
    }

    @Test
    @DisplayName("删除活动")
    void deleteActivity() {
        // Given
        when(activityMapper.deleteById(1L)).thenReturn(1);

        // When
        activityService.deleteActivity(1L);

        // Then
        verify(activityMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("报名活动成功")
    void registerActivitySuccess() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(activityMapper.selectById(1L)).thenReturn(testActivity);
            when(registrationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(registrationMapper.insert(any(ActivityRegistration.class))).thenReturn(1);
            when(activityMapper.updateById(any(Activity.class))).thenReturn(1);

            // When
            activityService.registerActivity(1L);

            // Then
            verify(registrationMapper, times(1)).insert(any(ActivityRegistration.class));
        }
    }

    @Test
    @DisplayName("报名活动失败 - 未登录")
    void registerActivityNotLoggedIn() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(null);

            // When & Then
            assertThrows(BusinessException.class, () -> {
                activityService.registerActivity(1L);
            });
        }
    }

    @Test
    @DisplayName("报名活动失败 - 活动不存在")
    void registerActivityActivityNotFound() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(activityMapper.selectById(99L)).thenReturn(null);

            // When & Then
            assertThrows(BusinessException.class, () -> {
                activityService.registerActivity(99L);
            });
        }
    }

    @Test
    @DisplayName("报名活动失败 - 不在报名阶段")
    void registerActivityNotInRegistrationPeriod() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            testActivity.setStatus(2); // 进行中
            when(activityMapper.selectById(1L)).thenReturn(testActivity);

            // When & Then
            assertThrows(BusinessException.class, () -> {
                activityService.registerActivity(1L);
            });
        }
    }

    @Test
    @DisplayName("报名活动失败 - 报名人数已满")
    void registerActivityFull() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            testActivity.setCurrentParticipants(100);
            testActivity.setMaxParticipants(100);
            when(activityMapper.selectById(1L)).thenReturn(testActivity);

            // When & Then
            assertThrows(BusinessException.class, () -> {
                activityService.registerActivity(1L);
            });
        }
    }

    @Test
    @DisplayName("报名活动失败 - 重复报名")
    void registerActivityDuplicate() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(activityMapper.selectById(1L)).thenReturn(testActivity);
            when(registrationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // When & Then
            assertThrows(BusinessException.class, () -> {
                activityService.registerActivity(1L);
            });
        }
    }

    @Test
    @DisplayName("取消报名成功")
    void cancelRegistrationSuccess() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(registrationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testRegistration);
            when(activityMapper.selectById(1L)).thenReturn(testActivity);
            when(registrationMapper.updateById(any(ActivityRegistration.class))).thenReturn(1);
            when(activityMapper.updateById(any(Activity.class))).thenReturn(1);

            // When
            activityService.cancelRegistration(1L);

            // Then
            verify(registrationMapper, times(1)).updateById(any(ActivityRegistration.class));
        }
    }

    @Test
    @DisplayName("取消报名 - 未找到报名记录")
    void cancelRegistrationNotFound() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(registrationMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            // When & Then
            assertThrows(BusinessException.class, () -> {
                activityService.cancelRegistration(1L);
            });
        }
    }
}
