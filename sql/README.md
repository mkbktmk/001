# 校园综合服务平台 — 数据库脚本说明

## 数据库列表

| 编号 | 文件 | 数据库 | 核心表 | 初始数据行数 |
|------|------|--------|--------|-------------|
| 01 | `01-user-service.sql` | `campus_user` | `t_user` | 8 |
| 02 | `02-news-service.sql` | `campus_news` | `t_news` | 6 |
| 03 | `03-forum-service.sql` | `campus_forum` | `t_post`, `t_comment`, `t_post_like`, `t_post_favorite` | 6+11 |
| 04 | `04-lost-found-service.sql` | `campus_lostfound` | `t_lost_found` | 8 |
| 05 | `05-second-hand-service.sql` | `campus_secondhand` | `t_second_hand`, `t_goods_favorite` | 6 |
| 06 | `06-complaint-service.sql` | `campus_complaint` | `t_complaint` | 7 |
| 07 | `07-message-service.sql` | `campus_message` | `t_message` | 5 |

## 执行方式

### 方式一：逐个执行（推荐，便于排错）

```bash
mysql -u root -p < 01-user-service.sql
mysql -u root -p < 02-news-service.sql
mysql -u root -p < 03-forum-service.sql
mysql -u root -p < 04-lost-found-service.sql
mysql -u root -p < 05-second-hand-service.sql
mysql -u root -p < 06-complaint-service.sql
mysql -u root -p < 07-message-service.sql
```

### 方式二：一键执行

```bash
# 进入 sql 目录
cd E:\ck\sql

# 依次执行（Windows CMD）
for %f in (0?-*.sql) do @mysql -u root -p < "%f"
```

### 方式三：Docker 自动初始化

将 SQL 文件挂载到 MySQL 容器的 `/docker-entrypoint-initdb.d/`：

```yaml
mysql:
  image: mysql:8.0
  volumes:
    - ./sql:/docker-entrypoint-initdb.d
```

## 测试账号

所有测试用户密码为: **123456**（BCrypt 加密存储）

| 用户名 | 密码 | 角色 |
|--------|------|------|
| `admin` | 123456 | 管理员 |
| `20210001` | 123456 | 学生（张三） |
| `20210002` | 123456 | 学生（李四） |
| `20210003` | 123456 | 学生（王五） |
| `20210004` | 123456 | 学生（赵六） |
| `20210005` | 123456 | 学生（孙七） |
| `T001` | 123456 | 教师（刘老师） |
| `T002` | 123456 | 教师（陈老师） |
