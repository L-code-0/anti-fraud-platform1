-- 协作演练参与者表
DROP TABLE IF EXISTS `collaboration_participant`;
CREATE TABLE `collaboration_participant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id` BIGINT NOT NULL COMMENT '会话ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `role` TINYINT NOT NULL COMMENT '角色：1-主持人，2-参与者，3-观察者',
    `join_time` DATETIME COMMENT '加入时间',
    `leave_time` DATETIME COMMENT '离开时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-在线，2-离线',
    `score` INT COMMENT '得分',
    `completion_status` TINYINT NOT NULL DEFAULT 2 COMMENT '完成情况：1-已完成，2-未完成',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='协作演练参与者表';
