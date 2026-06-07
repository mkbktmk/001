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
