-- ========================================
-- 校园综合服务平台 - 二手交易服务数据库
-- ========================================

CREATE DATABASE IF NOT EXISTS campus_secondhand
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE campus_secondhand;

CREATE TABLE IF NOT EXISTS t_second_hand (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(128) NOT NULL,
  description TEXT NOT NULL,
  category VARCHAR(32) NOT NULL DEFAULT 'other',
  images JSON NULL,
  price DECIMAL(10,2) NOT NULL,
  original_price DECIMAL(10,2) NULL,
  goods_condition VARCHAR(16) NOT NULL DEFAULT 'good',
  status VARCHAR(16) NOT NULL DEFAULT 'active',
  seller_id BIGINT NOT NULL,
  seller_name VARCHAR(32) NOT NULL DEFAULT '',
  contact VARCHAR(64) NOT NULL DEFAULT '',
  view_count INT NOT NULL DEFAULT 0,
  fav_count INT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_category (category),
  KEY idx_status (status),
  KEY idx_price (price),
  KEY idx_seller_id (seller_id),
  KEY idx_create_time (create_time),
  KEY idx_status_cat (status, category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS t_goods_favorite (
  id BIGINT NOT NULL AUTO_INCREMENT,
  goods_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_goods_user (goods_id, user_id),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS t_message (
  id BIGINT NOT NULL AUTO_INCREMENT,
  goods_id BIGINT NOT NULL,
  sender_id BIGINT NOT NULL,
  sender_name VARCHAR(64) NOT NULL,
  receiver_id BIGINT NOT NULL,
  content VARCHAR(1024) NOT NULL,
  is_read TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_goods_users (goods_id, sender_id, receiver_id),
  KEY idx_receiver (receiver_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_order (
  id BIGINT NOT NULL AUTO_INCREMENT,
  buyer_id BIGINT NOT NULL,
  goods_id BIGINT NOT NULL,
  goods_title VARCHAR(128) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  seller_name VARCHAR(64) NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'paid',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_buyer (buyer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
