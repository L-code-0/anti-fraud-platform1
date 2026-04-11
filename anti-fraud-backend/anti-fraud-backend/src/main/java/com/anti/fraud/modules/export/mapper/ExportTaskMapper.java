package com.anti.fraud.modules.export.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.anti.fraud.modules.export.entity.ExportTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 导出任务Mapper
 */
@Mapper
public interface ExportTaskMapper extends BaseMapper<ExportTask> {
    
    /**
     * 根据用户ID查询导出任务列表
     * @param userId 用户ID
     * @return 任务列表
     */
    List<ExportTask> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 查询等待中的任务
     * @return 任务列表
     */
    List<ExportTask> selectPendingTasks();
}
