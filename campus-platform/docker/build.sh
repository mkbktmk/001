#!/bin/bash
# ============================================
# 校园综合服务平台 — 一键构建 + 部署脚本
# ============================================
set -e

echo "=========================================="
echo " 校园综合服务平台 — Docker 部署"
echo "=========================================="

# 1. Maven 构建
echo ""
echo "[1/3] Maven 编译打包..."
cd "$(dirname "$0")/.."
mvn clean package -DskipTests -q
echo "  ✓ JAR 构建完成"

# 2. Docker Compose 启动
echo ""
echo "[2/3] Docker Compose 启动..."
docker compose up -d --build
echo "  ✓ 容器启动中..."

# 3. 等待并检查状态
echo ""
echo "[3/3] 等待服务就绪..."
sleep 30

echo ""
echo "=========================================="
echo " 容器状态:"
echo "=========================================="
docker compose ps

echo ""
echo "=========================================="
echo " 访问地址:"
echo "=========================================="
echo "  Gateway 入口:     http://localhost:8080"
echo "  Nacos 控制台:     http://localhost:8848/nacos"
echo "  Sentinel 控制台:  http://localhost:8081"
echo "  RabbitMQ 管理:    http://localhost:15672 (admin/admin)"
echo ""
echo " 各服务健康检查:"
echo "  user-service:     http://localhost:8082/actuator/health"
echo "  news-service:     http://localhost:8083/actuator/health"
echo "  forum-service:    http://localhost:8084/actuator/health"
echo "  lost-found:       http://localhost:8085/actuator/health"
echo "  second-hand:      http://localhost:8086/actuator/health"
echo "  complaint:        http://localhost:8087/actuator/health"
echo "=========================================="
