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
