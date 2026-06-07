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
