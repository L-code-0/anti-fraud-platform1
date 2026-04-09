package com.anti.fraud.modules.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "仪表盘统计VO")
public class DashboardStatsVO {

    @Schema(description = "总用户数")
    private Long totalUsers;

    @Schema(description = "今日新增用户")
    private Long todayNewUsers;

    @Schema(description = "知识文章数")
    private Long totalKnowledge;

    @Schema(description = "测试完成次数")
    private Long totalTests;

    @Schema(description = "演练完成次数")
    private Long totalSimulations;

    @Schema(description = "举报数量")
    private Long totalReports;

    @Schema(description = "待处理举报")
    private Long pendingReports;

    @Schema(description = "预警信息数")
    private Long totalWarnings;

    @Schema(description = "活动数量")
    private Long totalActivities;

    @Schema(description = "进行中活动")
    private Long ongoingActivities;
}
