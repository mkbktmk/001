-- ========================================
-- 校园综合服务平台 - 论坛服务数据库
-- ========================================

CREATE DATABASE IF NOT EXISTS campus_forum
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE campus_forum;

-- 帖子表
CREATE TABLE IF NOT EXISTS t_post (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(128) NOT NULL,
  content TEXT NOT NULL,
  images JSON NULL,
  board VARCHAR(32) NOT NULL DEFAULT 'life',
  author_id BIGINT NOT NULL,
  author_name VARCHAR(32) NOT NULL DEFAULT '',
  view_count INT NOT NULL DEFAULT 0,
  like_count INT NOT NULL DEFAULT 0,
  comment_count INT NOT NULL DEFAULT 0,
  is_pinned TINYINT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted INT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_board (board),
  KEY idx_author_id (author_id),
  KEY idx_create_time (create_time),
  KEY idx_status_board (status, board),
  FULLTEXT KEY ft_title_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 回复表
CREATE TABLE IF NOT EXISTS t_comment (
  id BIGINT NOT NULL AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  parent_id BIGINT NOT NULL DEFAULT 0,
  reply_to_id BIGINT NOT NULL DEFAULT 0,
  reply_to_uid BIGINT NOT NULL DEFAULT 0,
  content TEXT NOT NULL,
  author_id BIGINT NOT NULL,
  author_name VARCHAR(32) NOT NULL DEFAULT '',
  like_count INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_post_id (post_id),
  KEY idx_author_id (author_id),
  KEY idx_parent_id (parent_id),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 点赞记录表
CREATE TABLE IF NOT EXISTS t_post_like (
  id BIGINT NOT NULL AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_post_user (post_id, user_id),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 收藏记录表
CREATE TABLE IF NOT EXISTS t_post_favorite (
  id BIGINT NOT NULL AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_post_user (post_id, user_id),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 站内通知表
CREATE TABLE IF NOT EXISTS t_notification (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  type VARCHAR(32) NOT NULL,
  title VARCHAR(128) NOT NULL,
  content VARCHAR(512) NULL,
  related_id BIGINT NULL,
  is_read TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_user_read (user_id, is_read),
  KEY idx_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 禁言表
CREATE TABLE IF NOT EXISTS t_user_mute (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  muted_until DATETIME NOT NULL,
  reason VARCHAR(256) NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY user_id (user_id),
  KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始数据
INSERT INTO t_post (title, content, board, author_id, author_name, view_count, like_count, comment_count) VALUES
('Final Exam Review Materials Sharing', 'I have compiled final exam review notes for Data Structures, welcome to download', 'study', 2, 'Zhang San', 120, 15, 8),
('Anyone Preparing for Spring Job Interviews Together?', 'Spring 2024 recruitment is coming soon, looking for classmates to practice interviews together', 'job', 2, 'Zhang San', 89, 10, 5),
('Today Cafeteria Spicy Pot is Amazing!', 'Third cafeteria spicy pot is super delicious, highly recommended', 'life', 3, 'lisi', 200, 25, 12),
('Sharing Some Useful GitHub Student Pack Benefits', 'GitHub Student Developer Pack includes many free tools', 'tech', 4, 'Wang Wu', 156, 30, 15),
('Junior Year: Grad School or Job Hunting?', 'Really struggling with this decision, anyone with experience can share?', 'job', 7, 'Zhao Liu', 300, 45, 20);
