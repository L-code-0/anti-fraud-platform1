-- 案例表
DROP TABLE IF EXISTS `case_info`;
CREATE TABLE `case_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '案例标题',
    `type` TINYINT NOT NULL COMMENT '案例类型：1-电信诈骗，2-网络诈骗，3-金融诈骗，4-冒充公检法诈骗，5-刷单诈骗，6-中奖诈骗，7-投资理财诈骗，8-婚恋诈骗',
    `description` TEXT COMMENT '案例描述',
    `details` TEXT COMMENT '案例详情（JSON格式）',
    `images` VARCHAR(500) COMMENT '案例图片',
    `videos` VARCHAR(500) COMMENT '案例视频',
    `source` VARCHAR(100) COMMENT '案例来源',
    `publish_time` DATETIME COMMENT '案例发布时间',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT NOT NULL DEFAULT 0 COMMENT '评论数',
    `share_count` INT NOT NULL DEFAULT 0 COMMENT '分享数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，2-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_publish_time` (`publish_time`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案例表';
