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
