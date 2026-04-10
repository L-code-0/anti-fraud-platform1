package com.anti.fraud.modules.log.mapper;

import com.anti.fraud.modules.log.entity.OperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 操作日志Mapper
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    
    /**
     * 分页查询操作日志
     */
    IPage<OperationLog> selectLogPage(
            Page<OperationLog> page,
            @Param("userId") Long userId,
            @Param("operationType") String operationType,
            @Param("moduleName") String moduleName,
            @Param("keyword") String keyword,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );
    
    /**
     * 清理N天前的日志
     */
    int deleteBeforeDays(@Param("days") int days);
    
    /**
     * 统计操作类型数量
     */
    long countByOperationType(@Param("operationType") String operationType);
}
