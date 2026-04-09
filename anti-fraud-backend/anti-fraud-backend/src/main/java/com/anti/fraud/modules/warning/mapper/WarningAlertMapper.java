package com.anti.fraud.modules.warning.mapper;

import com.anti.fraud.modules.warning.entity.WarningAlert;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 预警警报 Mapper
 */
@Mapper
public interface WarningAlertMapper extends BaseMapper<WarningAlert> {

    /**
     * 分页查询预警列表
     */
    IPage<Map<String, Object>> selectWarningPage(
            Page<Map<String, Object>> page,
            @Param("keyword") String keyword,
            @Param("riskLevel") Integer riskLevel,
            @Param("warningType") Integer warningType,
            @Param("status") Integer status
    );

    /**
     * 统计各风险等级数量
     */
    @Select("SELECT risk_level, COUNT(*) as count FROM warning_alert WHERE deleted = 0 AND status = 0 GROUP BY risk_level")
    java.util.List<Map<String, Object>> countByRiskLevel();

    /**
     * 获取预警详情（包含用户信息）
     */
    Map<String, Object> selectWarningDetail(@Param("id") Long id);
}
