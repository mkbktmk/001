CREATE DATABASE IF NOT EXISTS campus_complaint DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE campus_complaint;

CREATE TABLE IF NOT EXISTS t_complaint (
  id BIGINT NOT NULL AUTO_INCREMENT,
  type VARCHAR(16) NOT NULL DEFAULT 'repair',
  title VARCHAR(128) NOT NULL,
  description TEXT NOT NULL,
  images JSON NULL,
  location VARCHAR(128) NOT NULL DEFAULT '',
  status VARCHAR(16) NOT NULL DEFAULT 'pending',
  user_id BIGINT NOT NULL,
  user_name VARCHAR(32) NOT NULL DEFAULT '',
  handler_id BIGINT NULL,
  handler_name VARCHAR(32) NOT NULL DEFAULT '',
  reply TEXT NULL,
  rating TINYINT NULL,
  feedback VARCHAR(512) NOT NULL DEFAULT '',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  resolve_time DATETIME NULL,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id), KEY idx_type (type), KEY idx_status (status),
  KEY idx_user_id (user_id), KEY idx_handler_id (handler_id),
  KEY idx_create_time (create_time), KEY idx_type_status (type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
