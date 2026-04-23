-- 社区帖子表
DROP TABLE IF EXISTS `community_post`;
CREATE TABLE `community_post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `title` VARCHAR(200) NOT NULL COMMENT '帖子标题',
    `content` TEXT COMMENT '帖子内容',
    `post_type` TINYINT NOT NULL COMMENT '帖子类型：1-反诈知识，2-可疑信息，3-求助问答，4-经验分享，5-其他',
    `images` VARCHAR(500) COMMENT '帖子图片',
    `videos` VARCHAR(500) COMMENT '帖子视频',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT NOT NULL DEFAULT 0 COMMENT '评论数',
    `share_count` INT NOT NULL DEFAULT 0 COMMENT '分享数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，2-审核中，3-已下架',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_post_type` (`post_type`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区帖子表';
