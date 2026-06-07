-- ============================================
-- 校园综合服务平台 — 全部数据库一键创建
-- 执行方式: mysql -u root -p < 00-all-in-one.sql
-- ============================================

-- 注意: 此文件汇总了所有服务的数据库创建脚本
-- 如果你的 MySQL 不支持多数据库同时创建，请按 01-07 顺序依次执行

SOURCE 01-user-service.sql;
SOURCE 02-news-service.sql;
SOURCE 03-forum-service.sql;
SOURCE 04-lost-found-service.sql;
SOURCE 05-second-hand-service.sql;
SOURCE 06-complaint-service.sql;
SOURCE 07-message-service.sql;
