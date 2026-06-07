@echo off
REM ============================================
REM 校园综合服务平台 — 一键构建 + 部署脚本 (Windows)
REM ============================================

echo ==========================================
echo  校园综合服务平台 — Docker 部署
echo ==========================================

REM 1. Maven 构建
echo.
echo [1/3] Maven 编译打包...
cd /d %~dp0..
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo Maven 构建失败！
    pause
    exit /b 1
)
echo   √ JAR 构建完成

REM 2. Docker Compose 启动
echo.
echo [2/3] Docker Compose 启动...
docker-compose up -d --build
if %errorlevel% neq 0 (
    echo Docker Compose 启动失败！
    pause
    exit /b 1
)
echo   √ 容器启动中...

REM 3. 等待并检查状态
echo.
echo [3/3] 等待服务就绪（约30秒）...
timeout /t 30 /nobreak >nul

echo.
echo ==========================================
echo  容器状态:
echo ==========================================
docker-compose ps

echo.
echo ==========================================
echo  访问地址:
echo ==========================================
echo   Gateway 入口:     http://localhost:8080
echo   Nacos 控制台:     http://localhost:8848/nacos
echo   Sentinel 控制台:  http://localhost:8081
echo   RabbitMQ 管理:    http://localhost:15672 (admin/admin)
echo.
echo  各服务健康检查:
echo   user-service:     http://localhost:8082/actuator/health
echo   news-service:     http://localhost:8083/actuator/health
echo   forum-service:    http://localhost:8084/actuator/health
echo   lost-found:       http://localhost:8085/actuator/health
echo   second-hand:      http://localhost:8086/actuator/health
echo   complaint:        http://localhost:8087/actuator/health
echo ==========================================

pause
