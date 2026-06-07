-- ========================================
-- 校园综合服务平台 - 用户服务数据库
-- ========================================

CREATE DATABASE IF NOT EXISTS campus_user
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE campus_user;

CREATE TABLE IF NOT EXISTS t_user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(32) NOT NULL,
  password VARCHAR(128) NOT NULL,
  nickname VARCHAR(64) NOT NULL DEFAULT '',
  phone VARCHAR(16) NOT NULL DEFAULT '',
  email VARCHAR(64) NOT NULL DEFAULT '',
  avatar_url VARCHAR(255) NOT NULL DEFAULT '',
  role VARCHAR(16) NOT NULL DEFAULT 'student',
  college VARCHAR(64) NOT NULL DEFAULT '',
  major VARCHAR(64) NOT NULL DEFAULT '',
  grade VARCHAR(8) NOT NULL DEFAULT '',
  status TINYINT NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username),
  KEY idx_role (role),
  KEY idx_college (college),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO t_user (username, password, nickname, phone, email, role, college, major, grade) VALUES
('admin', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', 'System Admin', '13800000000', 'admin@campus.edu', 'admin', 'Information Center', '', ''),
('20210001', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', 'Zhang San', '13800000001', '', 'student', 'Computer Science', 'Software Engineering', '2021'),
('20210002', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', 'lisi', '13800000002', '', 'student', 'Information Management', '', '2021'),
('20210003', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', 'Wang Wu', '13800000003', '', 'student', 'Electronics', '', '2021'),
('T001', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', 'Liu Teacher', '', '', 'teacher', 'Computer Science', '', ''),
('T002', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', 'Chen Teacher', '', '', 'teacher', 'Foreign Languages', '', ''),
('20210004', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', 'Zhao Liu', '13800000004', '', 'student', '', '', '2021'),
('20210005', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', 'Sun Qi', '13800000005', '', 'student', '', '', '2021'),
('111', '$2a$10$PvnPFPt0HF5cNXK/crETPOa5vfV2ZoS3juOEM9jTD.OFzi7eMxc1u', '111', '', '', 'student', '', '', '');
