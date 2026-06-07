CREATE DATABASE IF NOT EXISTS campus_lostfound DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE campus_lostfound;

CREATE TABLE IF NOT EXISTS t_lost_found (
  id BIGINT NOT NULL AUTO_INCREMENT,
  type VARCHAR(8) NOT NULL DEFAULT 'lost',
  item_name VARCHAR(64) NOT NULL,
  category VARCHAR(32) NOT NULL DEFAULT 'other',
  description TEXT NOT NULL,
  images JSON NULL,
  location VARCHAR(128) NOT NULL DEFAULT '',
  contact VARCHAR(64) NOT NULL DEFAULT '',
  status VARCHAR(16) NOT NULL DEFAULT 'active',
  user_id BIGINT NOT NULL,
  user_name VARCHAR(32) NOT NULL DEFAULT '',
  view_count INT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id), KEY idx_type (type), KEY idx_category (category), KEY idx_status (status),
  KEY idx_user_id (user_id), KEY idx_create_time (create_time), KEY idx_type_status (type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
