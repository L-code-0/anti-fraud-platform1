package com.anti.fraud.modules.export.service;

import com.anti.fraud.modules.export.vo.ReportExportVO;
import com.anti.fraud.modules.export.vo.TestScoreExportVO;
import com.anti.fraud.modules.export.vo.UserStatisticsExportVO;

import java.util.List;
import java.util.Map;

/**
 * 数据导出服务接口
 */
public interface ExportService {
    
    /**
     * 导出用户统计数据
     * @param params 查询参数
     * @return 用户统计数据列表
     */
    List<UserStatisticsExportVO> exportUserStatistics(Map<String, Object> params);
    
    /**
     * 导出测验成绩
     * @param params 查询参数
     * @return 测验成绩列表
     */
    List<TestScoreExportVO> exportTestScores(Map<String, Object> params);
    
    /**
     * 导出举报记录
     * @param params 查询参数
     * @return 举报记录列表
     */
    List<ReportExportVO> exportReports(Map<String, Object> params);
    
    /**
     * 导出知识库内容
     * @param params 查询参数
     * @return 知识库内容列表
     */
    List<Map<String, Object>> exportKnowledge(Map<String, Object> params);
    
    /**
     * 导出活动参与记录
     * @param params 查询参数
     * @return 活动参与记录列表
     */
    List<Map<String, Object>> exportActivities(Map<String, Object> params);
    
    /**
     * 导出积分记录
     * @param params 查询参数
     * @return 积分记录列表
     */
    List<Map<String, Object>> exportPointsRecords(Map<String, Object> params);
    
    /**
     * 生成导出文件名
     * @param type 导出类型
     * @return 文件名
     */
    String generateFileName(String type);
}
