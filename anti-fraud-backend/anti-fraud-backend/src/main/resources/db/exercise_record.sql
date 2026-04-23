-- 演练记录表
DROP TABLE IF EXISTS `exercise_record`;
CREATE TABLE `exercise_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `exercise_type` TINYINT NOT NULL COMMENT '演练类型：1-模拟演练，2-实战演练',
    `exercise_name` VARCHAR(200) NOT NULL COMMENT '演练名称',
    `exercise_content` TEXT COMMENT '演练内容（JSON格式）',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `duration` INT NOT NULL COMMENT '用时（秒）',
    `score` INT NOT NULL COMMENT '得分',
    `correct_rate` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '正确率',
    `completion_status` TINYINT NOT NULL COMMENT '完成情况：1-完成，2-未完成',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，2-异常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_exercise_type` (`exercise_type`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_score` (`score`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='演练记录表';
