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
