-- 智能预警警报表
CREATE TABLE IF NOT EXISTS `warning_alert` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `risk_level` TINYINT NOT NULL DEFAULT 1 COMMENT '风险等级：1-高风险 2-中风险 3-低风险',
    `warning_type` TINYINT NOT NULL DEFAULT 1 COMMENT '预警类型：1-电信诈骗 2-网络诈骗 3-校园贷 4-兼职诈骗',
    `content` VARCHAR(500) NOT NULL COMMENT '预警内容',
    `risk_score` INT NOT NULL DEFAULT 0 COMMENT '风险分数 0-100',
    `analysis` TEXT COMMENT '风险分析',
    `suggestion` TEXT COMMENT '建议措施',
    `related_user_id` BIGINT COMMENT '相关用户ID',
    `related_user_name` VARCHAR(50) COMMENT '相关用户名',
    `related_knowledge` VARCHAR(500) COMMENT '相关知识标签（逗号分隔）',
    `source` TINYINT NOT NULL DEFAULT 1 COMMENT '预警来源：1-系统自动 2-人工上报',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态：0-待处理 1-已处理 2-误报',
    `process_result` VARCHAR(500) COMMENT '处理结果',
    `processed_by` BIGINT COMMENT '处理人ID',
    `processed_at` DATETIME COMMENT '处理时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_risk_level` (`risk_level`),
    INDEX `idx_warning_type` (`warning_type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_related_user` (`related_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能预警警报表';

-- 插入测试数据
INSERT INTO `warning_alert` (`risk_level`, `warning_type`, `content`, `risk_score`, `analysis`, `suggestion`, `related_user_name`, `related_knowledge`, `status`, `create_time`) VALUES
(1, 1, '检测到可疑电信诈骗电话，号码为138****5678', 85, '系统检测到该电话号码与已知诈骗号码特征匹配，近期有多人举报类似号码。该号码通过语音机器人进行诈骗，内容涉及冒充公检法、虚假中奖等。', '1. 立即联系相关用户，告知风险\n2. 建议用户不要接听该号码\n3. 引导用户学习相关防范知识\n4. 如已遭受损失，建议报警', '张三', '电信诈骗防范指南,冒充公检法诈骗识别,诈骗电话应对技巧', 0, NOW()),
(1, 2, '检测到可疑钓鱼网站链接，域名类似于银行官网', 92, '该链接使用与正规银行高度相似的域名，使用HTTP协议，存在窃取用户银行卡信息的风险。已有多名用户反馈在该网站输入信息后银行卡被盗刷。', '1. 立即通知相关用户\n2. 建议用户修改银行密码\n3. 检查银行卡交易记录\n4. 提交网络安全部门处理', '李四', '钓鱼网站识别,网络安全知识,银行卡安全', 0, NOW()),
(2, 3, '检测到可疑校园贷短信推广', 65, '检测到向用户发送校园贷推广短信，承诺低息放款，实际可能涉及高利贷、暴力催收等违法行为。', '1. 提醒用户警惕校园贷陷阱\n2. 推送正规金融知识\n3. 引导用户向学校或相关部门举报', '王五', '校园贷危害,金融诈骗防范,大学生权益保护', 0, NOW()),
(2, 4, '检测到可疑网络兼职信息', 58, '检测到用户浏览了疑似刷单兼职网站，该类兼职通常要求先付款或提供个人信息，存在诈骗风险。', '1. 提醒用户谨慎对待\n2. 推送兼职诈骗案例\n3. 建议通过正规渠道找工作', '赵六', '兼职诈骗识别,刷单骗局防范,求职安全指南', 0, NOW()),
(3, 1, '检测到可疑来电频繁呼叫', 45, '检测到某号码短时间内多次呼叫用户，可能是骚扰电话或诈骗电话尝试。', '建议用户开启来电拦截功能，注意接听陌生号码时的个人信息保护。', '钱七', '骚扰电话防范,个人信息保护', 0, NOW()),
(1, 2, '检测到用户访问非法博彩网站', 88, '系统检测到用户访问了被标记为非法博彩的网站，该网站可能涉及诈骗、洗钱等违法行为。', '1. 立即通知用户风险\n2. 建议用户停止访问\n3. 如已充值，建议报警\n4. 检查设备安全', '孙八', '网络博彩危害,网络诈骗案例,法律知识', 0, NOW()),
(2, 1, '检测到冒充客服诈骗电话', 70, '检测到有来电自称电商平台客服，要求用户提供银行卡信息进行退款，符合诈骗特征。', '提醒用户正规客服不会索要银行卡密码，建议通过官方渠道核实。', '周九', '电商诈骗识别,客服真假辨别,退款诈骗防范', 0, NOW()),
(3, 4, '检测到可疑兼职培训广告', 40, '检测到兼职广告要求先支付培训费用，存在诈骗风险。', '提醒用户正规兼职不需要预付费用，建议通过学校就业中心寻找兼职。', '吴十', '兼职陷阱识别,培训诈骗防范', 1, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 更新处理信息
UPDATE `warning_alert` SET 
    `process_result` = '已联系用户确认，确认为培训推销，非诈骗行为',
    `processed_by` = 1,
    `processed_at` = NOW()
WHERE `id` = 8;
