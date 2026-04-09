-- 演练场景表
DROP TABLE IF EXISTS `simulation_scene`;
CREATE TABLE `simulation_scene` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `scene_name` VARCHAR(100) NOT NULL COMMENT '场景名称',
    `scene_type` VARCHAR(50) NOT NULL COMMENT '场景类型: TELEPHONE-电话诈骗, NETWORK-网络诈骗, SMS-短信诈骗, INVESTMENT-投资诈骗, RELATIONSHIP-情感诈骗',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `background` TEXT COMMENT '场景背景描述',
    `script_config` TEXT COMMENT '对话脚本配置(JSON格式)',
    `difficulty` TINYINT NOT NULL DEFAULT 1 COMMENT '难度等级: 1-简单, 2-中等, 3-困难',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
    `play_count` INT NOT NULL DEFAULT 0 COMMENT '演练次数',
    `avg_score` DECIMAL(5,2) DEFAULT 0.00 COMMENT '平均得分',
    `is_recommend` TINYINT NOT NULL DEFAULT 0 COMMENT '是否推荐: 0-否, 1-是',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_scene_type` (`scene_type`),
    KEY `idx_difficulty` (`difficulty`),
    KEY `idx_status` (`status`),
    KEY `idx_is_recommend` (`is_recommend`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='演练场景表';

-- 演练记录表
DROP TABLE IF EXISTS `simulation_record`;
CREATE TABLE `simulation_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `scene_id` BIGINT NOT NULL COMMENT '场景ID',
    `score` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '得分',
    `duration` INT NOT NULL DEFAULT 0 COMMENT '耗时(秒)',
    `is_completed` TINYINT NOT NULL DEFAULT 0 COMMENT '是否完成: 0-未完成, 1-已完成',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_scene_id` (`scene_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='演练记录表';

-- 测试数据：演练场景
INSERT INTO `simulation_scene` (`scene_name`, `scene_type`, `background`, `script_config`, `difficulty`, `is_recommend`, `status`) VALUES
('冒充公检法诈骗', 'TELEPHONE', '你接到一个自称是市公安局民警的电话，对方声称你涉嫌一起洗钱案件，需要配合调查。对方语气严肃，并表示你的银行账户可能被冻结。', '[{"role":"system","content":"您好，我是市公安局刑警大队的王警官。我们接到银行监管部门通报，您的银行账户涉嫌参与一起重大洗钱案件，涉案金额高达500万元。"},{"role":"user","options":["什么？我没参与任何洗钱活动，你们搞错了吧？","请问有什么证据证明我涉嫌洗钱？","好的，我配合调查，请说需要怎么做。"],"correctIndex":1},{"role":"system","content":"这是机密案件，具体细节不方便在电话里透露。但现在情况紧急，你的账户将在两小时内被冻结。为了证明你的清白，需要你将资金转入我们的安全账户进行核查。"},{"role":"user","options":["好的，请告诉我安全账户的账号。","公检法机关不会要求转账到所谓的安全账户，你是骗子吧？","我先核实一下你的身份，能告诉我你的警号吗？"],"correctIndex":1,"critical":true},{"role":"system","content":"你如果不配合，后果自负！我们已经掌握了你所有的信息，不转账就等着坐牢吧！"},{"role":"user","options":["我害怕，请告诉我怎么转账。","我不会转账的，真正的警察不会这样威胁人，我要报警！","那我先咨询一下律师再说。"],"correctIndex":1}]', 1, 1, 1),

('刷单返利诈骗', 'NETWORK', '你在网上看到一则招聘广告，声称可以轻松赚钱，只要完成刷单任务就能获得高额返利。你添加了对方的QQ。', '[{"role":"system","content":"亲，欢迎加入我们的兼职团队！我们是正规电商推广平台，只需要动动手指刷刷单，就能日赚200-500元，无需任何费用。"},{"role":"user","options":["真的能赚这么多钱吗？怎么操作？","天下没有免费的午餐，这不会是骗局吧？","好的，我加入，请告诉我怎么做。"],"correctIndex":1},{"role":"system","content":"放心吧，我们有营业执照的。你先试着刷一单，只需充值100元，完成后立即返还120元，立赚20元。"},{"role":"user","options":["好的，我先试试看。","先交钱的兼职都是骗局，我不做了。","能把营业执照发给我看看吗？"],"correctIndex":1,"critical":true},{"role":"system","content":"你先别急着下定论，你看群里这么多人都在赚钱，而且我们还有任务奖励呢，充值越多返利越高！"},{"role":"user","options":["那我充值5000元试试。","这就是典型的刷单诈骗，我要举报你！","我再考虑考虑。"],"correctIndex":1}]', 2, 1, 1),

('网络贷款诈骗', 'NETWORK', '你急需用钱，在网上搜索贷款平台，找到一个"快速放款、无抵押、低利息"的贷款广告。', '[{"role":"system","content":"您好，这里是闪电贷平台。看到您申请了贷款，最高可贷20万元，当天放款，月息仅0.3%，无需抵押担保。"},{"role":"user","options":["太好了，我正需要钱，怎么申请？","利息这么低？正规贷款平台年化利率都不可能这么低。","你们是正规持牌机构吗？"],"correctIndex":2},{"role":"system","content":"我们是正规的互联网金融平台。您只需要下载我们的APP，填写资料即可。不过您需要先缴纳1000元保证金，证明您有还款能力。"},{"role":"user","options":["好的，我这就转账。","正规贷款不需要提前缴费，这是骗局！","保证金可以抵扣还款吗？"],"correctIndex":1,"critical":true},{"role":"system","content":"缴纳保证金后，贷款额度会立即到账。如果担心，可以先交500元，剩下的审核通过后再补。"},{"role":"user","options":["那我先交500元试试。","不管交多少钱，提前收费都是违法的，我不贷了！","能提供一下你们的金融许可证号吗？"],"correctIndex":1}]', 2, 1, 1),

('冒充客服退款诈骗', 'TELEPHONE', '你接到一个自称淘宝客服的电话，说你购买的商品有质量问题，可以退款理赔。', '[{"role":"system","content":"您好，我是淘宝客服小王。很抱歉打扰您，您之前购买的洗面奶经检测发现存在质量问题，厂家决定召回并给您三倍赔偿，请问您方便处理吗？"},{"role":"user","options":["三倍赔偿？那太好了，怎么退款？","我先查一下我的订单再说。","请问我的订单号是多少？我怎么验证你是官方客服？"],"correctIndex":2},{"role":"system","content":"因为系统升级，需要您通过支付宝备用金通道退款。请添加我们的理赔QQ，客服会指导您操作。"},{"role":"user","options":["好的，我加QQ。","淘宝退款只需要在订单页面操作，不需要加QQ！","退款为什么要用其他平台？直接退到原账户不行吗？"],"correctIndex":1,"critical":true},{"role":"system","content":"您放心，我们是为了快速处理。只需要您打开支付宝，按客服指导操作就能收到退款。"},{"role":"user","options":["好，我现在就打开支付宝。","我不需要退款了，原来的产品也没问题。","退款流程不会需要我转账或提供验证码吧？如果是那就是诈骗！"],"correctIndex":2}]', 1, 1, 1),

('虚假投资理财诈骗', 'INVESTMENT', '你在微信群看到有人推荐一个稳赚不赔的投资平台，声称月收益可达30%。', '[{"role":"system","content":"您好，我是专业的理财顾问。我们平台有专业的投资团队，月收益稳定在30%以上，而且有专业老师指导，稳赚不赔。"},{"role":"user","options":["月收益30%？这收益也太高了，正规投资不可能有这种收益。","真的稳赚不赔吗？我想试试。","你们平台有金融牌照吗？"],"correctIndex":0},{"role":"system","content":"您看我们群里其他会员的收益截图，都是真实到账的。您可以先小额试水，投资1000元试试，保证一周后连本带利返还。"},{"role":"user","options":["那我投资1000元试试看。","高收益必然伴随高风险，这可能是庞氏骗局！","能把平台的公司信息和监管牌照发给我看看吗？"],"correctIndex":1,"critical":true},{"role":"system","content":"您放心，我们是正规平台。现在有个限时活动，充值满10000元送2000元，错过就没有了！"},{"role":"user","options":["那我充值10000元！","正规投资平台不会有这种充送活动，这是典型的传销式诈骗！","我先观望一下再说。"],"correctIndex":1}]', 3, 1, 1),

('杀猪盘诈骗', 'RELATIONSHIP', '你在交友软件上认识了一个很聊得来的异性朋友，对方自称是投资分析师，想带你一起赚钱。', '[{"role":"system","content":"亲爱的，最近工作忙吗？我跟你说，最近我发现了一个投资平台，我利用专业知识分析，稳赚不赔。想带你一起赚钱，以后我们就可以早点见面了。"},{"role":"user","options":["真的吗？我也想赚点钱，快告诉我！","谢谢你这么为我着想，但我对投资不太懂。","认识没多久就推荐投资，有点奇怪吧？"],"correctIndex":2},{"role":"system","content":"我怎么会骗你呢？你还不相信我吗？我每天都会给你看我赚了多少，从不骗人。你先投5000试试，赚了我们正好可以见面旅游。"},{"role":"user","options":["好吧，我相信你，我投5000。","网上认识的人推荐投资要警惕，我需要先了解你的真实身份！","投资有风险，我需要考虑一下。"],"correctIndex":1,"critical":true},{"role":"system","content":"你还在犹豫什么？错过这个机会就没了。我们这么投缘，我怎么能害你呢？"},{"role":"user","options":["那我就投吧！","这不符合正常的交友逻辑，突然让我投资很可疑，我要报警举报！","我得和家人商量一下。"],"correctIndex":1}]', 3, 0, 1),

('冒充熟人借钱诈骗', 'SMS', '你收到一条短信，自称是你的同学小王，急需借钱周转。', '[{"role":"system","content":"在吗？我是小王，我手机掉水里坏了，借个朋友手机给你发消息。我现在在外地出差，急需用钱周转一下，能借我3000块钱吗？"},{"role":"user","options":["小王啊，好的，卡号发给我。","你怎么换了手机号？先让我核实一下，你是哪个小王？","3000块钱是吧？我马上转给你。"],"correctIndex":1},{"role":"system","content":"就是我啊，咱们大学同班的。我现在真的很急，银行卡被锁住了，你先转到这个账户：6222...户名张三。"},{"role":"user","options":["好的，我马上转账！","你借别人手机为什么不直接打电话？而且户名也不是你，这很可疑！","我给你打电话确认一下吧。"],"correctIndex":1,"critical":true},{"role":"system","content":"我现在不方便接电话，你相信我，明天我就还你！"},{"role":"user","options":["好吧，我相信你。","不核实清楚绝不能转账，我要亲自给小王本人打电话确认！","那你发个语音证明一下身份。"],"correctIndex":1}]', 1, 1, 1),

('兼职代购诈骗', 'NETWORK', '你在网上看到一则招聘代购的广告，声称可以免费出国旅游还能赚钱。', '[{"role":"system","content":"您好，我们公司招聘海外代购专员，可以免费出国旅游，还能赚取高额佣金。只需帮忙带一些商品回国，一趟可赚5000-10000元。"},{"role":"user","options":["免费旅游还能赚钱？这么好的机会！","代购商品会不会有违禁品？我需要谨慎。","你们公司的具体信息能介绍一下吗？"],"correctIndex":1},{"role":"system","content":"放心，都是正规商品，主要是化妆品、保健品等。您只需要提供护照信息，我们帮您办理签证和机票，费用全包。"},{"role":"user","options":["好的，我把护照信息发给你。","提供护照等个人信息要谨慎，可能涉及信息泄露或违法犯罪！","能先签合同吗？"],"correctIndex":1,"critical":true},{"role":"system","content":"您放心，我们有很多员工都走过这个流程。您提供的护照信息仅用于办理签证，绝对保密。"},{"role":"user","options":["那我就放心了，我提供信息。","这很可能是骗取个人信息或者让我携带违禁品，是严重的违法行为，我拒绝参与！","我再考虑一下。"],"correctIndex":1}]', 2, 0, 1);

-- 测试数据：演练记录
INSERT INTO `simulation_record` (`user_id`, `scene_id`, `score`, `duration`, `is_completed`, `start_time`, `end_time`) VALUES
(2, 1, 85.00, 180, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 2, 70.00, 210, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 1, 90.00, 150, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 3, 65.00, 240, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, 2, 55.00, 300, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 1, 95.00, 120, 1, NOW(), NOW()),
(5, 4, 80.00, 200, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));
