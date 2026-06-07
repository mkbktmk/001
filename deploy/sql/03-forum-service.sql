-- ============================================
-- 校园综合服务平台 — 论坛服务数据库
-- 数据库: campus_forum
-- ============================================

CREATE DATABASE IF NOT EXISTS `campus_forum`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `campus_forum`;

-- ----------------------------
-- 帖子表
-- ----------------------------
DROP TABLE IF EXISTS `t_post`;
CREATE TABLE `t_post` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title`         VARCHAR(128) NOT NULL COMMENT '帖子标题',
  `content`       TEXT         NOT NULL COMMENT '帖子正文（Markdown）',
  `board`         VARCHAR(32)  NOT NULL DEFAULT 'life' COMMENT '板块: study/job/life/tech/other',
  `author_id`     BIGINT       NOT NULL COMMENT '发帖人ID',
  `author_name`   VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '发帖人昵称（冗余）',
  `view_count`    INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count`    INT          NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT          NOT NULL DEFAULT 0 COMMENT '回复数',
  `is_pinned`     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否置顶: 0否 1是',
  `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0删除 1正常 2隐藏',
  `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_board`        (`board`),
  KEY `idx_author_id`    (`author_id`),
  KEY `idx_create_time`  (`create_time`),
  KEY `idx_status_board` (`status`, `board`),
  FULLTEXT KEY `ft_title_content` (`title`, `content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='帖子表';

-- ----------------------------
-- 回复/评论表
-- ----------------------------
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id`     BIGINT   NOT NULL COMMENT '帖子ID',
  `parent_id`   BIGINT   NOT NULL DEFAULT 0 COMMENT '父评论ID（0=一级回复）',
  `reply_to_id` BIGINT   NOT NULL DEFAULT 0 COMMENT '回复的目标评论ID',
  `reply_to_uid`BIGINT   NOT NULL DEFAULT 0 COMMENT '回复的目标用户ID',
  `content`     TEXT     NOT NULL COMMENT '回复内容',
  `author_id`   BIGINT   NOT NULL COMMENT '回复人ID',
  `author_name` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '回复人昵称（冗余）',
  `like_count`  INT      NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status`      TINYINT  NOT NULL DEFAULT 1 COMMENT '状态: 0删除 1正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id`    (`post_id`),
  KEY `idx_author_id`  (`author_id`),
  KEY `idx_parent_id`  (`parent_id`),
  KEY `idx_create_time`(`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='回复/评论表';

-- ----------------------------
-- 帖子点赞记录表
-- ----------------------------
DROP TABLE IF EXISTS `t_post_like`;
CREATE TABLE `t_post_like` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id`     BIGINT   NOT NULL COMMENT '帖子ID',
  `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='帖子点赞记录表';

-- ----------------------------
-- 帖子收藏记录表
-- ----------------------------
DROP TABLE IF EXISTS `t_post_favorite`;
CREATE TABLE `t_post_favorite` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id`     BIGINT   NOT NULL COMMENT '帖子ID',
  `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='帖子收藏记录表';

-- ----------------------------
-- 初始化测试数据
-- ----------------------------
INSERT INTO `t_post` (`title`, `content`, `board`, `author_id`, `author_name`, `view_count`, `like_count`, `comment_count`) VALUES
('期末考试复习资料分享，需要的自取', '整理了这学期《操作系统》《数据结构》《计算机网络》的复习笔记和历年真题，PDF版本，需要的同学评论区留言邮箱。\n\n> 赠人玫瑰，手有余香 🌹', 'study', 2, '张三', 356, 28, 5),
('有没有一起准备春招面试的？', '2024春招马上开始了，想找几个同学一起刷 LeetCode、模拟面试，有没有感兴趣的小伙伴？\n\n计划：\n- 每天 3 道 LeetCode\n- 每周一次模拟面试\n- 互相 review 简历', 'study', 2, '张三', 189, 15, 3),
('今天食堂的麻辣香锅太好吃了！', '二食堂二楼新开的麻辣香锅窗口，价格实惠量又足，人均15块吃到撑。强烈推荐！\n\n![香锅照片]', 'life', 3, '李四', 423, 42, 8),
('推荐一个非常好用的笔记工具', '最近发现了一个叫 **Obsidian** 的笔记软件，本地存储 Markdown，支持双向链接和知识图谱，比 Notion 更轻量。\n\n非常适合用来做课程笔记和知识管理，安利给大家！', 'tech', 4, '王五', 267, 19, 4),
('大三了，考研还是就业，好纠结', '本人软件工程大三，成绩中等偏上，家里建议考研，但自己更想早点工作积累经验。有没有过来人给点建议？', 'life', 7, '赵六', 512, 8, 12),
('分享几个实用的 GitHub 学生包福利', 'GitHub Student Developer Pack 里的免费资源：\n1. GitHub Copilot（AI编程助手）\n2. DigitalOcean $200额度\n3. JetBrains 全家桶\n4. Canva Pro\n5. Namecheap 域名\n\n用edu邮箱就能申请！', 'tech', 8, '孙七', 634, 38, 6);

INSERT INTO `t_comment` (`post_id`, `parent_id`, `content`, `author_id`, `author_name`) VALUES
(1, 0, '感谢分享！求发一份操作系统资料，邮箱 test1@qq.com', 3, '李四'),
(1, 0, '好人一生平安！三科都要，邮箱 test2@qq.com', 4, '王五'),
(1, 3, '已发，注意查收', 2, '张三'),
(2, 0, '带我一个！我建了个刷题群，加微信 coding123', 7, '赵六'),
(2, 0, '+1，同大三找队友', 8, '孙七'),
(3, 0, '真的假的，改天去试试', 2, '张三'),
(3, 0, '上次去吃了，确实不错，就是排队有点长...', 8, '孙七'),
(4, 0, 'Obsidian 确实好用，配合 git 同步完美', 2, '张三'),
(5, 0, '建议考研，现在就业形势不太好，有研究生学历更稳', 4, '王五'),
(5, 0, '看你自己喜欢什么，喜欢写代码就直接就业，喜欢搞科研就考研', 3, '李四'),
(6, 0, '补充一个：MongoDB Atlas 也有免费额度', 3, '李四');
