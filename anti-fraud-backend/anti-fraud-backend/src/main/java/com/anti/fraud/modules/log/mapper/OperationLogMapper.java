package com.anti.fraud.modules.log.mapper;

import com.anti.fraud.modules.log.entity.OperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

/**
 * 操作日志Mapper
 */
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    
    /**
     * 分页查询操作日志
     * @param page 分页参数
     * @param username 用户名
     * @param actionType 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作日志列表
     */
    IPage<OperationLog> selectPage(
            IPage<OperationLog> page,
            @Param("username") String username,
            @Param("actionType") String actionType,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );
}
