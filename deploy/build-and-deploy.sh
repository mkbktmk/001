#!/bin/bash
set -e

echo "========================================="
echo " 校园综合服务平台 - Docker 部署"
echo "========================================="

# 1. 编译后端
echo "[1/4] 编译后端..."
cd ../campus-platform
mvn clean package -DskipTests -q
echo "  编译完成"

# 2. 编译前端
echo "[2/4] 编译前端..."
cd campus-frontend
npm install --silent
npm run build
echo "  前端编译完成"

# 3. 构建 Docker 镜像
echo "[3/4] 构建 Docker 镜像..."
cd ../../deploy
docker-compose build
echo "  Docker 镜像构建完成"

# 4. 启动
echo "[4/4] 启动服务..."
docker-compose up -d
echo ""
echo "========================================="
echo " 部署完成！"
echo " 访问地址: http://服务器IP"
echo ""
echo " 查看状态: docker-compose ps"
echo " 查看日志: docker-compose logs -f"
echo "========================================="
