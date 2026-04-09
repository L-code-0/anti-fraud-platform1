-- 作品表
DROP TABLE IF EXISTS `works`;
CREATE TABLE `works` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '作品标题',
    `works_type` VARCHAR(20) NOT NULL COMMENT '作品类型: ESSAY-征文, VIDEO-短视频',
    `activity_id` BIGINT DEFAULT NULL COMMENT '关联活动ID',
    `activity_name` VARCHAR(100) DEFAULT NULL COMMENT '活动名称',
    `author_id` BIGINT NOT NULL COMMENT '作者ID',
    `author_name` VARCHAR(50) NOT NULL COMMENT '作者姓名',
    `author_phone` VARCHAR(20) DEFAULT NULL COMMENT '作者电话',
    `department` VARCHAR(100) DEFAULT NULL COMMENT '院系',
    `content` TEXT DEFAULT NULL COMMENT '征文内容',
    `file_url` VARCHAR(500) DEFAULT NULL COMMENT '文件/视频URL',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '作品描述',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝',
    `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
    `auditor_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
    `auditor_name` VARCHAR(50) DEFAULT NULL COMMENT '审核人姓名',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `is_excellent` TINYINT NOT NULL DEFAULT 0 COMMENT '是否优秀作品: 0-否, 1-是',
    `rank` INT DEFAULT NULL COMMENT '排名',
    `points` INT DEFAULT NULL COMMENT '奖励积分',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_status` (`status`),
    KEY `idx_works_type` (`works_type`),
    KEY `idx_is_excellent` (`is_excellent`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作品表';

-- 测试数据
INSERT INTO `works` (`title`, `works_type`, `activity_id`, `activity_name`, `author_id`, `author_name`, `author_phone`, `department`, `content`, `description`, `status`, `view_count`, `like_count`, `is_excellent`, `rank`, `points`) VALUES
('防范电信诈骗，从我做起', 'ESSAY', 1, '反诈征文大赛', 2, '张三', '13800138001', '计算机学院', '随着互联网的快速发展，电信网络诈骗案件层出不穷。作为一名大学生，我们应该提高警惕，增强防范意识。首先，要保护个人信息，不随意泄露身份证号、银行卡号等敏感信息。其次，对于陌生来电和短信要保持警惕，不轻信"中奖"、"退款"等诱惑性信息。最后，遇到可疑情况及时向辅导员或警方报告。让我们一起筑牢反诈防线，守护好自己的"钱袋子"！', '一篇关于防范电信诈骗的心得体会', 1, 1256, 89, 1, 1, 100),
('识别诈骗套路，保护财产安全', 'ESSAY', 1, '反诈征文大赛', 3, '李四', '13800138002', '经济管理学院', '近年来，校园诈骗案件频发，给同学们的财产安全带来了严重威胁。本文总结了常见的几种诈骗类型：一是冒充公检法诈骗，骗子冒充公安机关工作人员，以"涉嫌洗钱"为由要求转账；二是网络兼职诈骗，以"刷单返利"为诱饵骗取钱财；三是冒充熟人诈骗，通过盗取QQ、微信账号借钱。我们要牢记：公检法不会通过电话办案，网上刷单都是骗局，转账汇款前务必核实身份。', '总结常见诈骗类型及防范方法', 1, 987, 67, 1, 2, 80),
('校园贷的危害与防范', 'ESSAY', 1, '反诈征文大赛', 4, '王五', '13800138003', '法学院', '校园贷是一种针对大学生的非法借贷行为，其特点是门槛低、放款快、利息高。很多同学因为一时消费冲动，陷入校园贷陷阱，最终背负巨额债务。防范校园贷，首先要树立正确的消费观，量入为出；其次要提高法律意识，了解相关法律法规；最后遇到经济困难要及时向学校求助，不要轻信网络借贷广告。珍爱信用，远离校园贷！', '分析校园贷危害并提出防范建议', 1, 756, 45, 0, NULL, NULL),
('反诈小课堂', 'VIDEO', 2, '反诈短视频大赛', 5, '赵六', '13800138004', '新闻传播学院', NULL, '通过情景再现的方式，展示常见诈骗手法，并给出防范建议。视频时长3分钟，包含三个典型案例。', 1, 2345, 156, 1, 1, 120),
('防诈骗情景剧', 'VIDEO', 2, '反诈短视频大赛', 6, '钱七', '13800138005', '艺术学院', NULL, '原创防诈骗情景剧，讲述大学生小王差点被骗，最后成功识破骗局的故事。', 1, 1567, 98, 0, NULL, NULL),
('我的反诈经历', 'ESSAY', NULL, NULL, 7, '孙八', '13800138006', '外语学院', '上个月，我接到一个陌生电话，对方自称是某网购平台的客服，说我购买的商品有质量问题可以退款。对方让我加了QQ，发来一个链接让我填写银行卡信息。好在我之前参加过学校的反诈宣传活动，立刻意识到这是诈骗。我直接挂断电话，并向辅导员报告了此事。通过这次经历，我深刻认识到反诈知识的重要性，希望同学们也能提高警惕。', '分享亲身经历的诈骗案例', 0, 0, 0, 0, NULL, NULL);
