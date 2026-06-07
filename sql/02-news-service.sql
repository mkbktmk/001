CREATE DATABASE IF NOT EXISTS campus_news DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE campus_news;

CREATE TABLE IF NOT EXISTS t_news (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(128) NOT NULL,
  content LONGTEXT NOT NULL,
  summary VARCHAR(512) NOT NULL DEFAULT '',
  category VARCHAR(32) NOT NULL DEFAULT 'notice',
  cover_image VARCHAR(255) NOT NULL DEFAULT '',
  author_id BIGINT NOT NULL,
  author_name VARCHAR(32) NOT NULL DEFAULT '',
  view_count INT NOT NULL DEFAULT 0,
  status VARCHAR(16) NOT NULL DEFAULT 'draft',
  is_top TINYINT NOT NULL DEFAULT 0,
  publish_time DATETIME NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_category (category), KEY idx_status (status), KEY idx_author_id (author_id),
  KEY idx_publish_time (publish_time), KEY idx_is_top_status (is_top, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO t_news (title, content, summary, category, author_id, author_name, status, publish_time) VALUES
('Notice on Holding 2024 Spring Campus Job Fair', '<p>Our school will hold a spring campus job fair on <strong>March 15, 2024</strong> in the gymnasium. Over 100 companies will attend. Please prepare your resumes in advance.</p>', 'Over 100 companies will attend the spring job fair on March 15.', 'job', 5, 'Liu Teacher', 'published', '2024-01-10 10:00:00'),
('Academic Lecture: Frontiers and Applications of Large Language Models', '<p>Speaker: Prof. Wang (Tsinghua University)</p><p>Time: January 20, 2024 14:00</p><p>Venue: Information Building Lecture Hall</p>', 'Prof. Wang from Tsinghua University gives a lecture on LLM frontiers.', 'lecture', 5, 'Liu Teacher', 'published', '2024-01-08 14:00:00'),
('Notice on Final Exam Schedule', '<p>The final exams for this semester will be held from <strong>January 22 to January 26, 2024</strong>. Please login to the academic system to check your specific exam arrangements.</p>', 'Final exams from January 22 to 26.', 'notice', 6, 'Chen Teacher', 'published', '2024-01-05 09:00:00');
