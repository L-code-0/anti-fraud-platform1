-- 举报记录表
DROP TABLE IF EXISTS `report_record`;
CREATE TABLE `report_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '举报用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '举报用户名称',
    `report_type` TINYINT NOT NULL COMMENT '举报类型：1-虚假信息，2-诈骗行为，3-不良内容，4-其他',
    `target_id` BIGINT NOT NULL COMMENT '举报对象ID',
    `target_type` TINYINT NOT NULL COMMENT '举报对象类型：1-用户，2-案例，3-评论，4-其他',
    `report_content` TEXT NOT NULL COMMENT '举报内容',
    `evidence` TEXT COMMENT '举报证据（JSON格式）',
    `handle_status` TINYINT NOT NULL DEFAULT 1 COMMENT '处理状态：1-待处理，2-已处理，3-已驳回',
    `handle_result` TEXT COMMENT '处理结果',
    `handler` VARCHAR(50) COMMENT '处理人',
    `handle_time` DATETIME COMMENT '处理时间',
    `reward_points` INT NOT NULL DEFAULT 0 COMMENT '积分奖励',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，2-异常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_report_type` (`report_type`),
    KEY `idx_target_id` (`target_id`),
    KEY `idx_target_type` (`target_type`),
    KEY `idx_handle_status` (`handle_status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='举报记录表';
