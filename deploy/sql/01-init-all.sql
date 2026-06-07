-- ============================================
-- 校园综合服务平台 — 全部数据库一键创建
-- 执行方式: mysql -u root -p < 00-all-in-one.sql
-- ============================================

-- 注意: 此文件汇总了所有服务的数据库创建脚本
-- 如果你的 MySQL 不支持多数据库同时创建，请按 01-07 顺序依次执行

SOURCE 01-user-service.sql;
SOURCE 02-news-service.sql;
SOURCE 03-forum-service.sql;
SOURCE 04-lost-found-service.sql;
SOURCE 05-second-hand-service.sql;
SOURCE 06-complaint-service.sql;
SOURCE 07-message-service.sql;
-- ============================================
-- 校园综合服务平台 — 用户服务数据库
-- 数据库: campus_user
-- ============================================

CREATE DATABASE IF NOT EXISTS `campus_user`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `campus_user`;

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username`    VARCHAR(32)  NOT NULL COMMENT '用户名（学号/工号）',
  `password`    VARCHAR(128) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '昵称',
  `phone`       VARCHAR(16)  NOT NULL DEFAULT '' COMMENT '手机号',
  `email`       VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '邮箱',
  `avatar_url`  VARCHAR(255) NOT NULL DEFAULT '' COMMENT '头像URL',
  `role`        VARCHAR(16)  NOT NULL DEFAULT 'student' COMMENT '角色: student/teacher/admin',
  `college`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '学院',
  `major`       VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '专业',
  `grade`       VARCHAR(8)   NOT NULL DEFAULT '' COMMENT '年级，如2023',
  `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1正常',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone`    (`phone`),
  KEY `idx_role`       (`role`),
  KEY `idx_college`    (`college`),
  KEY `idx_status`     (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ----------------------------
-- 初始化测试数据
-- 密码均为 123456 经 BCrypt 加密
-- ----------------------------
INSERT INTO `t_user` (`username`, `password`, `nickname`, `phone`, `email`, `role`, `college`, `major`, `grade`) VALUES
('admin',    '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', '系统管理员', '13800000000', 'admin@campus.edu',   'admin',   '信息中心', '',       ''),
('20210001', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', '张三',       '13800000001', 'zhangsan@campus.edu', 'student', '计算机学院', '软件工程', '2021'),
('20210002', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', '李四',       '13800000002', 'lisi@campus.edu',     'student', '计算机学院', '软件工程', '2021'),
('20210003', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', '王五',       '13800000003', 'wangwu@campus.edu',   'student', '计算机学院', '计算机科学', '2021'),
('T001',     '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', '刘老师',     '13800000004', 'liuteacher@campus.edu','teacher', '计算机学院', '',       ''),
('T002',     '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', '陈老师',     '13800000005', 'chenteacher@campus.edu','teacher', '外国语学院', '',       ''),
('20210004', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', '赵六',       '13800000006', 'zhaoliu@campus.edu',  'student', '外国语学院', '英语',     '2021'),
('20210005', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', '孙七',       '13800000007', 'sunqi@campus.edu',    'student', '电子信息学院', '通信工程', '2021');
-- ============================================
-- 校园综合服务平台 — 资讯服务数据库
-- 数据库: campus_news
-- ============================================

CREATE DATABASE IF NOT EXISTS `campus_news`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `campus_news`;

-- ----------------------------
-- 资讯表
-- ----------------------------
DROP TABLE IF EXISTS `t_news`;
CREATE TABLE `t_news` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title`        VARCHAR(128) NOT NULL COMMENT '标题',
  `content`      LONGTEXT     NOT NULL COMMENT '正文（富文本HTML）',
  `summary`      VARCHAR(512) NOT NULL DEFAULT '' COMMENT '摘要',
  `category`     VARCHAR(32)  NOT NULL DEFAULT 'notice' COMMENT '分类: notice/lecture/activity/job/other',
  `cover_image`  VARCHAR(255) NOT NULL DEFAULT '' COMMENT '封面图URL',
  `author_id`    BIGINT       NOT NULL COMMENT '发布者ID（关联t_user）',
  `author_name`  VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '发布者姓名（冗余，防跨服务查询）',
  `view_count`   INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
  `status`       VARCHAR(16)  NOT NULL DEFAULT 'draft' COMMENT '状态: draft/published/offline',
  `is_top`       TINYINT      NOT NULL DEFAULT 0 COMMENT '是否置顶: 0否 1是',
  `publish_time` DATETIME     NULL     DEFAULT NULL COMMENT '发布时间',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_category`    (`category`),
  KEY `idx_status`      (`status`),
  KEY `idx_author_id`   (`author_id`),
  KEY `idx_publish_time`(`publish_time`),
  KEY `idx_is_top_status` (`is_top`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资讯表';

-- ----------------------------
-- 初始化测试数据
-- ----------------------------
INSERT INTO `t_news` (`title`, `content`, `summary`, `category`, `author_id`, `author_name`, `status`, `publish_time`) VALUES
('关于举办2024年校园春季招聘会的通知',
 '<p>各位同学：</p><p>我校定于<strong>2024年3月15日</strong>在体育馆举办春季校园招聘会，届时将有100余家企业参会。请同学们提前准备好简历，积极参加。</p><p>时间：2024年3月15日 9:00-16:00</p><p>地点：校体育馆</p>',
 '3月15日在体育馆举办春季招聘会，100+企业参会。',
 'job', 5, '刘老师', 'published', '2024-01-10 10:00:00'),

('学术讲座：大语言模型的前沿与应用',
 '<p>主讲人：王教授（清华大学计算机系）</p><p>时间：2024年1月20日 14:00</p><p>地点：信息楼报告厅</p><p>内容简介：本讲座将介绍大语言模型（LLM）的最新研究进展、关键技术以及在教育、医疗等领域的应用前景。</p>',
 '清华大学王教授主讲大语言模型前沿讲座。',
 'lecture', 5, '刘老师', 'published', '2024-01-08 14:00:00'),

('关于期末考试安排的通知',
 '<p>各学院：</p><p>本学期期末考试定于<strong>2024年1月22日至1月26日</strong>进行，请各位同学登录教务系统查看具体考试安排，并严格遵守考场纪律。</p>',
 '期末考试1月22日至26日进行，请查看教务系统。',
 'notice', 6, '陈老师', 'published', '2024-01-05 09:00:00'),

('计科院"代码马拉松"编程比赛报名通知',
 '<p>计算机学院将于<strong>2024年3月1日</strong>举办第三届"代码马拉松"编程比赛。</p><p>参赛对象：全校本科生</p><p>比赛形式：3人一组，8小时极限编程</p><p>报名截止：2024年2月20日</p><p>报名链接：http://example.com/signup</p>',
 '3月1日第三届代码马拉松，3人组队，8小时极限编程。',
 'activity', 5, '刘老师', 'published', '2024-01-12 16:00:00'),

('关于校园一卡通系统升级的通知',
 '<p>信息技术中心将于<strong>1月14日（周日）凌晨2:00-6:00</strong>对校园一卡通系统进行升级维护，届时一卡通充值、消费等功能将暂停使用，请大家提前做好充值准备。</p>',
 '1月14日凌晨一卡通系统升级，暂停使用4小时。',
 'notice', 1, '系统管理员', 'published', '2024-01-13 08:00:00'),

('英语角活动通知',
 '<p>每周四晚7:00-9:00，外国语学院在图书馆咖啡厅举办英语角活动，欢迎全校同学参加！</p><p>本期主题：Travel & Culture</p>',
 '每周四晚7点图书馆咖啡厅英语角活动。',
 'activity', 6, '陈老师', 'published', '2024-01-07 11:00:00');
-- ============================================
-- 校园综合服务平台 — 论坛服务数据库
-- 数据库: campus_forum
-- ============================================

CREATE DATABASE IF NOT EXISTS `campus_forum`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `campus_forum`;

-- ----------------------------
-- 帖子表
-- ----------------------------
DROP TABLE IF EXISTS `t_post`;
CREATE TABLE `t_post` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title`         VARCHAR(128) NOT NULL COMMENT '帖子标题',
  `content`       TEXT         NOT NULL COMMENT '帖子正文（Markdown）',
  `board`         VARCHAR(32)  NOT NULL DEFAULT 'life' COMMENT '板块: study/job/life/tech/other',
  `author_id`     BIGINT       NOT NULL COMMENT '发帖人ID',
  `author_name`   VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '发帖人昵称（冗余）',
  `view_count`    INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count`    INT          NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT          NOT NULL DEFAULT 0 COMMENT '回复数',
  `is_pinned`     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否置顶: 0否 1是',
  `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0删除 1正常 2隐藏',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_board`        (`board`),
  KEY `idx_author_id`    (`author_id`),
  KEY `idx_create_time`  (`create_time`),
  KEY `idx_status_board` (`status`, `board`),
  FULLTEXT KEY `ft_title_content` (`title`, `content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='帖子表';

-- ----------------------------
-- 回复/评论表
-- ----------------------------
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id`     BIGINT   NOT NULL COMMENT '帖子ID',
  `parent_id`   BIGINT   NOT NULL DEFAULT 0 COMMENT '父评论ID（0=一级回复）',
  `reply_to_id` BIGINT   NOT NULL DEFAULT 0 COMMENT '回复的目标评论ID',
  `reply_to_uid`BIGINT   NOT NULL DEFAULT 0 COMMENT '回复的目标用户ID',
  `content`     TEXT     NOT NULL COMMENT '回复内容',
  `author_id`   BIGINT   NOT NULL COMMENT '回复人ID',
  `author_name` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '回复人昵称（冗余）',
  `like_count`  INT      NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status`      TINYINT  NOT NULL DEFAULT 1 COMMENT '状态: 0删除 1正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id`    (`post_id`),
  KEY `idx_author_id`  (`author_id`),
  KEY `idx_parent_id`  (`parent_id`),
  KEY `idx_create_time`(`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='回复/评论表';

-- ----------------------------
-- 帖子点赞记录表
-- ----------------------------
DROP TABLE IF EXISTS `t_post_like`;
CREATE TABLE `t_post_like` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id`     BIGINT   NOT NULL COMMENT '帖子ID',
  `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='帖子点赞记录表';

-- ----------------------------
-- 帖子收藏记录表
-- ----------------------------
DROP TABLE IF EXISTS `t_post_favorite`;
CREATE TABLE `t_post_favorite` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id`     BIGINT   NOT NULL COMMENT '帖子ID',
  `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='帖子收藏记录表';

-- ----------------------------
-- 初始化测试数据
-- ----------------------------
INSERT INTO `t_post` (`title`, `content`, `board`, `author_id`, `author_name`, `view_count`, `like_count`, `comment_count`) VALUES
('期末考试复习资料分享，需要的自取', '整理了这学期《操作系统》《数据结构》《计算机网络》的复习笔记和历年真题，PDF版本，需要的同学评论区留言邮箱。\n\n> 赠人玫瑰，手有余香 🌹', 'study', 2, '张三', 356, 28, 5),
('有没有一起准备春招面试的？', '2024春招马上开始了，想找几个同学一起刷 LeetCode、模拟面试，有没有感兴趣的小伙伴？\n\n计划：\n- 每天 3 道 LeetCode\n- 每周一次模拟面试\n- 互相 review 简历', 'study', 2, '张三', 189, 15, 3),
('今天食堂的麻辣香锅太好吃了！', '二食堂二楼新开的麻辣香锅窗口，价格实惠量又足，人均15块吃到撑。强烈推荐！\n\n![香锅照片]', 'life', 3, '李四', 423, 42, 8),
('推荐一个非常好用的笔记工具', '最近发现了一个叫 **Obsidian** 的笔记软件，本地存储 Markdown，支持双向链接和知识图谱，比 Notion 更轻量。\n\n非常适合用来做课程笔记和知识管理，安利给大家！', 'tech', 4, '王五', 267, 19, 4),
('大三了，考研还是就业，好纠结', '本人软件工程大三，成绩中等偏上，家里建议考研，但自己更想早点工作积累经验。有没有过来人给点建议？', 'life', 7, '赵六', 512, 8, 12),
('分享几个实用的 GitHub 学生包福利', 'GitHub Student Developer Pack 里的免费资源：\n1. GitHub Copilot（AI编程助手）\n2. DigitalOcean $200额度\n3. JetBrains 全家桶\n4. Canva Pro\n5. Namecheap 域名\n\n用edu邮箱就能申请！', 'tech', 8, '孙七', 634, 38, 6);

INSERT INTO `t_comment` (`post_id`, `parent_id`, `content`, `author_id`, `author_name`) VALUES
(1, 0, '感谢分享！求发一份操作系统资料，邮箱 test1@qq.com', 3, '李四'),
(1, 0, '好人一生平安！三科都要，邮箱 test2@qq.com', 4, '王五'),
(1, 3, '已发，注意查收', 2, '张三'),
(2, 0, '带我一个！我建了个刷题群，加微信 coding123', 7, '赵六'),
(2, 0, '+1，同大三找队友', 8, '孙七'),
(3, 0, '真的假的，改天去试试', 2, '张三'),
(3, 0, '上次去吃了，确实不错，就是排队有点长...', 8, '孙七'),
(4, 0, 'Obsidian 确实好用，配合 git 同步完美', 2, '张三'),
(5, 0, '建议考研，现在就业形势不太好，有研究生学历更稳', 4, '王五'),
(5, 0, '看你自己喜欢什么，喜欢写代码就直接就业，喜欢搞科研就考研', 3, '李四'),
(6, 0, '补充一个：MongoDB Atlas 也有免费额度', 3, '李四');
-- ============================================
-- 校园综合服务平台 — 失物招领服务数据库
-- 数据库: campus_lostfound
-- ============================================

CREATE DATABASE IF NOT EXISTS `campus_lostfound`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `campus_lostfound`;

-- ----------------------------
-- 失物招领表
-- ----------------------------
DROP TABLE IF EXISTS `t_lost_found`;
CREATE TABLE `t_lost_found` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type`        VARCHAR(8)   NOT NULL DEFAULT 'lost' COMMENT '类型: lost(寻物)/found(招领)',
  `item_name`   VARCHAR(64)  NOT NULL COMMENT '物品名称',
  `category`    VARCHAR(32)  NOT NULL DEFAULT 'other' COMMENT '分类: digital/stationery/card/clothing/key/book/other',
  `description` TEXT         NOT NULL COMMENT '物品描述',
  `images`      JSON         NULL     DEFAULT NULL COMMENT '图片URL数组，JSON格式: ["url1","url2"]',
  `location`    VARCHAR(128) NOT NULL DEFAULT '' COMMENT '丢失/拾取地点',
  `contact`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '联系方式',
  `status`      VARCHAR(16)  NOT NULL DEFAULT 'active' COMMENT '状态: active(进行中)/found(已找到/已归还)',
  `user_id`     BIGINT       NOT NULL COMMENT '发布者ID',
  `user_name`   VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '发布者昵称（冗余）',
  `view_count`  INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_type`        (`type`),
  KEY `idx_category`    (`category`),
  KEY `idx_status`      (`status`),
  KEY `idx_user_id`     (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_type_status` (`type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='失物招领表';

-- ----------------------------
-- 初始化测试数据
-- ----------------------------
INSERT INTO `t_lost_found` (`type`, `item_name`, `category`, `description`, `location`, `contact`, `status`, `user_id`, `user_name`) VALUES
('lost', '黑色双肩包', 'clothing', '1月10日下午在图书馆3楼自习室遗失一个黑色双肩包，内有《计算机网络》教材一本、白色充电宝一个、笔袋一个。包外侧有白色logo，请拾到者联系我，非常感谢！', '图书馆3楼自习室', '电话: 13800000001', 'active', 2, '张三'),
('found', '校园卡', 'card', '1月12日在教学楼A座一楼走廊捡到一张校园卡，卡号后四位3721，失主请速联系。', '教学楼A座一楼', '微信: lisi_wx', 'active', 3, '李四'),
('lost', 'AirPods Pro', 'digital', '白色 AirPods Pro（第一代），外壳有轻微的磕碰痕迹，在操场跑步时可能掉落的。请捡到的好心人联系我！', '操场', 'QQ: 123456789', 'active', 4, '王五'),
('found', '钥匙串', 'key', '二食堂门口捡到一串钥匙，共5把钥匙+一个小熊挂件，已放在食堂一楼失物招领处。', '二食堂门口', '已交至食堂失物招领处', 'active', 7, '赵六'),
('lost', 'U盘', 'digital', '丢失一个64G闪迪U盘，银灰色外壳，内有重要的课程设计和简历文件。U盘上贴有"W.W"标签。', '信息楼机房', '电话: 13800000006', 'active', 8, '孙七'),
('found', '《数据结构》教材', 'book', '在图书馆2楼还书车上发现一本被遗忘的《数据结构》（严蔚敏版），书内有很多笔记，失主看到请联系。', '图书馆2楼', '微信: liuteacher', 'active', 5, '刘老师'),
('lost', '眼镜', 'other', '丢失一副黑色半框眼镜，在体育馆打篮球时取下放旁边，走的时候忘记拿了。', '体育馆篮球场', 'QQ: 987654321', 'found', 4, '王五'),
('found', '校园卡', 'card', '食堂一楼捡到校园卡一张，卡号后四位8841，已交给食堂工作人员，失主可去食堂服务台领取。', '食堂一楼', '已交至食堂服务台', 'found', 2, '张三');
-- ============================================
-- 校园综合服务平台 — 二手交易服务数据库
-- 数据库: campus_secondhand
-- ============================================

CREATE DATABASE IF NOT EXISTS `campus_secondhand`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `campus_secondhand`;

-- ----------------------------
-- 二手商品表
-- ----------------------------
DROP TABLE IF EXISTS `t_second_hand`;
CREATE TABLE `t_second_hand` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title`       VARCHAR(128) NOT NULL COMMENT '商品标题',
  `description` TEXT         NOT NULL COMMENT '商品描述',
  `category`    VARCHAR(32)  NOT NULL DEFAULT 'other' COMMENT '分类: textbook/digital/living/clothing/other',
  `images`      JSON         NULL     DEFAULT NULL COMMENT '图片URL数组，JSON格式',
  `price`       DECIMAL(10,2) NOT NULL COMMENT '价格（元）',
  `original_price` DECIMAL(10,2) NULL DEFAULT NULL COMMENT '原价（元）',
  `goods_condition` VARCHAR(16) NOT NULL DEFAULT 'good' COMMENT '成色: new(全新)/like_new(几乎全新)/good(良好)/fair(一般)',
  `status`      VARCHAR(16)  NOT NULL DEFAULT 'active' COMMENT '状态: active(在售)/sold(已售)/removed(已下架)',
  `seller_id`   BIGINT       NOT NULL COMMENT '卖家ID',
  `seller_name` VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '卖家昵称（冗余）',
  `contact`     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '联系方式',
  `view_count`  INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
  `fav_count`   INT          NOT NULL DEFAULT 0 COMMENT '收藏量',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_category`    (`category`),
  KEY `idx_status`      (`status`),
  KEY `idx_price`       (`price`),
  KEY `idx_seller_id`   (`seller_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_status_cat`  (`status`, `category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='二手商品表';

-- ----------------------------
-- 商品收藏记录表
-- ----------------------------
DROP TABLE IF EXISTS `t_goods_favorite`;
CREATE TABLE `t_goods_favorite` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `goods_id`    BIGINT   NOT NULL COMMENT '商品ID',
  `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_goods_user` (`goods_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品收藏记录表';

-- ----------------------------
-- 初始化测试数据
-- ----------------------------
INSERT INTO `t_second_hand` (`title`, `description`, `category`, `price`, `original_price`, `condition`, `status`, `seller_id`, `seller_name`, `contact`) VALUES
('《操作系统概念》第九版 九成新', '大二教材，就期末考试翻了几次，几乎全新，没有任何笔记划痕。', 'textbook', 25.00, 69.00, 'like_new', 'active', 2, '张三', '微信: zhangsan_wx'),
('罗技 K380 蓝牙键盘', '用了一年多，换了机械键盘所以出掉。功能正常，电池仓盖有一处小划痕。送两节电池。', 'digital', 45.00, 149.00, 'good', 'active', 4, '王五', 'QQ: 123456789'),
('床上小桌子 折叠桌', '大一时买的，毕业用不上了，9成新，可折叠，带杯托和iPad支架槽。', 'living', 15.00, 39.00, 'good', 'active', 3, '李四', '电话: 13800000002'),
('大学英语四级真题 2023版 全新未拆', '买错了版本，全新未拆封，内含10套真题+解析+听力光盘。', 'textbook', 20.00, 49.80, 'new', 'active', 7, '赵六', '微信: zhaoliu666'),
('九阳迷你电煮锅 1.5L', '宿舍用小电煮锅，煮面煮粥神器，功率不大不会跳闸。用了半年，功能完好。', 'living', 30.00, 89.00, 'good', 'active', 8, '孙七', 'QQ: 55556666'),
('iPad 第九代保护壳 + 钢化膜', 'iPad换新了所以出配件，壳是磁吸分离款，全新钢化膜一张。打包出。', 'digital', 18.00, 59.00, 'like_new', 'active', 2, '张三', '微信: zhangsan_wx'),
('自行车 捷安特 ATX660', '去年买的入门山地车，骑了不到200公里，链条有点锈其他完好。毕业便宜出了。', 'other', 350.00, 1098.00, 'fair', 'sold', 5, '刘老师', '电话: 13800000004'),
('《高等数学》上下册 + 习题全解', '同济第七版，上册有部分笔记，下册几乎没用过。送习题全解指南一本。', 'textbook', 35.00, 86.00, 'fair', 'active', 3, '李四', '电话: 13800000002');
-- ============================================
-- 校园综合服务平台 — 报修/投诉服务数据库
-- 数据库: campus_complaint
-- ============================================

CREATE DATABASE IF NOT EXISTS `campus_complaint`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `campus_complaint`;

-- ----------------------------
-- 工单表
-- ----------------------------
DROP TABLE IF EXISTS `t_complaint`;
CREATE TABLE `t_complaint` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type`         VARCHAR(16)  NOT NULL DEFAULT 'repair' COMMENT '类型: repair(报修)/complaint(投诉)/suggest(建议)',
  `title`        VARCHAR(128) NOT NULL COMMENT '工单标题',
  `description`  TEXT         NOT NULL COMMENT '详细描述',
  `images`       JSON         NULL     DEFAULT NULL COMMENT '图片URL数组，JSON格式',
  `location`     VARCHAR(128) NOT NULL DEFAULT '' COMMENT '所在位置（报修地点）',
  `status`       VARCHAR(16)  NOT NULL DEFAULT 'pending' COMMENT '状态: pending(待处理)/processing(处理中)/done(已完成)/rejected(已驳回)',
  `user_id`      BIGINT       NOT NULL COMMENT '提交人ID',
  `user_name`    VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '提交人昵称（冗余）',
  `handler_id`   BIGINT       NULL     DEFAULT NULL COMMENT '处理人ID',
  `handler_name` VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '处理人昵称（冗余）',
  `reply`        TEXT         NULL     DEFAULT NULL COMMENT '处理回复',
  `rating`       TINYINT      NULL     DEFAULT NULL COMMENT '评分: 1-5',
  `feedback`     VARCHAR(512) NOT NULL DEFAULT '' COMMENT '用户评价反馈',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `resolve_time` DATETIME     NULL     DEFAULT NULL COMMENT '解决时间',
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_type`        (`type`),
  KEY `idx_status`      (`status`),
  KEY `idx_user_id`     (`user_id`),
  KEY `idx_handler_id`  (`handler_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_type_status` (`type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报修/投诉工单表';

-- ----------------------------
-- 初始化测试数据
-- ----------------------------
INSERT INTO `t_complaint` (`type`, `title`, `description`, `location`, `status`, `user_id`, `user_name`, `handler_id`, `handler_name`, `reply`, `rating`, `feedback`) VALUES
('repair', '教学楼B座302教室投影仪故障', '投影仪开机后画面出现严重偏色，无法正常显示课件内容，请尽快维修，下周有课要用。', '教学楼B座302教室', 'done', 2, '张三', 1, '系统管理员', '已联系厂商维修，更换了灯泡和色轮，现已恢复正常使用。', 5, '修得很快，好评！'),
('repair', '寝室卫生间水龙头漏水', '9号楼320寝室卫生间热水龙头关不紧，一直在滴水，浪费水还影响休息。', '学生宿舍9号楼320', 'processing', 3, '李四', 1, '系统管理员', '已收到报修，已安排物业师傅明天上午处理。', NULL, ''),
('complaint', '食堂饭菜质量问题', '最近二食堂一楼打菜窗口的菜明显变少了，而且价格涨了2块，希望学校能管一管。', '二食堂一楼', 'pending', 4, '王五', NULL, '', NULL, NULL, ''),
('suggest', '建议图书馆延长开放时间', '期末考试期间图书馆21:00就关门了，建议延长到23:00，方便大家复习。', '图书馆', 'done', 7, '赵六', 5, '刘老师', '感谢建议！经研究决定，期末考试期间（1月8日-1月26日）图书馆闭馆时间延长至23:00。', 4, '终于延长了，希望以后也能这样。'),
('repair', '信息楼5楼男厕灯泡坏了', '信息楼5楼男厕所两个LED灯泡都不亮了，晚上上厕所很不安全。', '信息楼5楼男厕所', 'pending', 8, '孙七', NULL, '', NULL, NULL, ''),
('repair', '校园网WiFi信号差', '图书馆3楼自习区WiFi信号时断时续，严重影响查资料和在线学习。', '图书馆3楼自习区', 'rejected', 2, '张三', 1, '系统管理员', '经现场检测，3楼自习区信号强度正常（-55dBm），建议检查个人设备无线网卡。如有问题可携带设备到信息中心协助排查。', NULL, ''),
('suggest', '建议增加校园共享单车站点', '宿舍区到教学楼距离较远，建议在9号宿舍楼下增加一个共享单车停放点。', '学生宿舍9号楼', 'pending', 3, '李四', NULL, '', NULL, NULL, '');
-- ============================================
-- 校园综合服务平台 — 消息通知服务数据库（可选/加分项）
-- 数据库: campus_message
-- ============================================

CREATE DATABASE IF NOT EXISTS `campus_message`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `campus_message`;

-- ----------------------------
-- 站内信/消息表
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`     BIGINT       NOT NULL COMMENT '接收用户ID',
  `title`       VARCHAR(128) NOT NULL COMMENT '消息标题',
  `content`     VARCHAR(512) NOT NULL DEFAULT '' COMMENT '消息内容',
  `type`        VARCHAR(16)  NOT NULL DEFAULT 'system' COMMENT '类型: system(系统)/comment(评论)/reply(回复)/complaint(工单)',
  `is_read`     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已读: 0未读 1已读',
  `target_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '关联业务ID（帖子/评论/工单等）',
  `target_type` VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '关联业务类型',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id`     (`user_id`),
  KEY `idx_user_read`   (`user_id`, `is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='站内信/消息表';

-- ----------------------------
-- 初始化测试数据
-- ----------------------------
INSERT INTO `t_message` (`user_id`, `title`, `content`, `type`, `target_id`, `target_type`) VALUES
(2, '你的帖子有新的回复', '李四 回复了你的帖子《期末考试复习资料分享，需要的自取》', 'comment', 1, 'post'),
(2, '你的工单已处理完成', '报修工单"教学楼B座302教室投影仪故障"已处理完成，请评价', 'complaint', 1, 'complaint'),
(3, '你的帖子被人点赞了', '张三 点赞了你的帖子《今天食堂的麻辣香锅太好吃了！》', 'system', 3, 'post'),
(4, '你的丢失物品有人找到', '有人发布了一条招领启事与你丢失的"AirPods Pro"相似，点击查看', 'system', 3, 'lost_found'),
(7, '校务通知', '图书馆期末考试期间延长开放至23:00，祝你学习进步！', 'system', 0, '');
