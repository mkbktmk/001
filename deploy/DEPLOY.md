# 校园综合服务平台 - 阿里云部署指南

## 前置准备

### 1. 购买阿里云 ECS
- 配置：4核 8GB，Ubuntu 22.04，5Mbps 带宽
- 安全组开放端口：80、8080、8848

### 2. 登录服务器，安装 Docker
```bash
ssh root@你的服务器IP

# 安装 Docker
curl -fsSL https://get.docker.com | bash
systemctl enable docker && systemctl start docker

# 安装 Docker Compose
apt install docker-compose-plugin -y
```

### 3. 上传项目
```bash
# 本地打包（排除 node_modules 和 target）
cd E:/ck
tar --exclude='node_modules' --exclude='target' --exclude='.git' \
    -czf campus.tar.gz campus-platform/ deploy/

# 上传到服务器
scp campus.tar.gz root@你的服务器IP:/opt/

# 服务器上解压
ssh root@你的服务器IP
cd /opt && tar -xzf campus.tar.gz
```

## 部署

```bash
cd /opt/deploy
chmod +x build-and-deploy.sh
./build-and-deploy.sh
```

首次需要编译，约 5-10 分钟。之后只需：
```bash
docker compose up -d
```

## 常用命令

```bash
# 查看状态
docker compose ps

# 查看日志
docker compose logs -f gateway

# 重启单个服务
docker compose restart secondhand-service

# 全部停止
docker compose stop

# 全部删除
docker compose down -v
```

## 架构

```
用户浏览器 (80)
    ↓
Nginx (80)
    ├── /           → Vue 前端静态文件
    ├── /api/*      → Gateway (8080)
    └── /uploads/*  → Gateway (8080)
                          ↓
                    ┌─────┴──────┐
                    │   Gateway  │
                    └─────┬──────┘
         ┌─────────┬──────┼──────┬─────────┐
         ↓         ↓      ↓      ↓         ↓
       user     news   forum  lostfound  complaint
       (8082)  (8083) (8084)  (8085)    (8087)
                                     secondhand(8086)
                                          ↓
                              MySQL(3306)  Redis(6379)
```

## 配置域名 + HTTPS（可选）

```bash
# 安装 Certbot
apt install certbot python3-certbot-nginx -y

# 获取证书
certbot --nginx -d your-domain.com

# 自动续期
certbot renew --dry-run
```
