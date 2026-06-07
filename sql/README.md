# 数据库初始化脚本

## 数据库列表

| 文件 | 数据库 | 核心表 |
|------|--------|--------|
| 01-user-service.sql | campus_user | t_user |
| 02-news-service.sql | campus_news | t_news |
| 03-forum-service.sql | campus_forum | t_post, t_comment, t_notification, t_user_mute |
| 04-lost-found-service.sql | campus_lostfound | t_lost_found |
| 05-second-hand-service.sql | campus_secondhand | t_second_hand, t_message, t_order |
| 06-complaint-service.sql | campus_complaint | t_complaint |

## 一键导入

```bash
mysql -u root -p < 00-all-in-one.sql
```

## 默认密码

所有用户密码 BCrypt 加密，明文均为 `123456`：
- admin / 123456 (管理员)
- 20210001 / 123456 (学生)
- T001 / 123456 (教师)
- 111 / 123456 (学生)
