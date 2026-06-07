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
