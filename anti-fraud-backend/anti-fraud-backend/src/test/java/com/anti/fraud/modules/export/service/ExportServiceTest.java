package com.anti.fraud.modules.export.service;

import com.anti.fraud.modules.export.entity.ExportTask;
import com.anti.fraud.modules.export.mapper.ExportTaskMapper;
import com.anti.fraud.modules.export.service.impl.ExportServiceImpl;
import com.anti.fraud.modules.export.vo.UserStatisticsExportVO;
import com.anti.fraud.modules.user.entity.User;
import com.anti.fraud.modules.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExportServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private ExportTaskMapper exportTaskMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ExportServiceImpl exportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExportUserStatistics() {
        // 准备测试数据
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("test1");
        user1.setRealName("测试用户1");
        user1.setDepartment("计算机系");
        user1.setPoints(100);
        user1.setLevel(1);
        users.add(user1);

        // 模拟UserMapper的行为
        when(userMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(users);

        // 执行测试
        Map<String, Object> params = new HashMap<>();
        List<UserStatisticsExportVO> result = exportService.exportUserStatistics(params);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test1", result.get(0).getUsername());
        assertEquals("测试用户1", result.get(0).getRealName());
        assertEquals("计算机系", result.get(0).getDepartment());
        assertEquals(100, result.get(0).getPoints());
        assertEquals(1, result.get(0).getLevel());
    }

    @Test
    void testCreateExportTask() throws Exception {
        // 准备测试数据
        Long userId = 1L;
        String taskType = "users";
        Map<String, Object> params = new HashMap<>();
        params.put("department", "计算机系");

        ExportTask task = new ExportTask();
        task.setId(1L);
        task.setUserId(userId);
        task.setTaskType(taskType);
        task.setParams("{\"department\":\"计算机系\"}");
        task.setStatus(0);
        task.setProgress(0);

        // 模拟objectMapper的行为
        when(objectMapper.writeValueAsString(params)).thenReturn("{\"department\":\"计算机系\"}");

        // 模拟exportTaskMapper的行为
        when(exportTaskMapper.insert(any(ExportTask.class))).thenAnswer(invocation -> {
            ExportTask t = invocation.getArgument(0);
            t.setId(1L);
            return 1;
        });

        // 执行测试
        Long taskId = exportService.createExportTask(userId, taskType, params);

        // 验证结果
        assertNotNull(taskId);
        assertEquals(1L, taskId);
    }

    @Test
    void testGetExportTask() {
        // 准备测试数据
        Long taskId = 1L;
        ExportTask task = new ExportTask();
        task.setId(taskId);
        task.setUserId(1L);
        task.setTaskType("users");
        task.setStatus(2);

        // 模拟exportTaskMapper的行为
        when(exportTaskMapper.selectById(taskId)).thenReturn(task);

        // 执行测试
        ExportTask result = exportService.getExportTask(taskId);

        // 验证结果
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("users", result.getTaskType());
        assertEquals(2, result.getStatus());
    }
}
