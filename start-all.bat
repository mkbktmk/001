@echo off
chcp 65001 >nul
title 校园综合服务平台 - 启动中...

echo ========================================
echo   校园综合服务平台 一键启动
echo ========================================
echo.

REM === 检查基础设施 ===
echo [1/5] 检查基础设施...

netstat -ano | findstr ":3306" >nul
if %errorlevel% neq 0 (
    echo   MySQL 未运行，请先启动 MySQL！
    pause
    exit /b 1
)
echo   MySQL (3306) ✓

netstat -ano | findstr ":6379" >nul
if %errorlevel% neq 0 (
    echo   启动 Redis...
    start /B "" "E:\Redis-8.6.2-Windows-x64-cygwin-with-Service\redis-server.exe" --port 6379
    timeout /t 2 >nul
)
echo   Redis (6379) ✓

netstat -ano | findstr ":8848" >nul
if %errorlevel% neq 0 (
    echo   启动 Nacos...
    start /B "" "C:\Program Files\Java\jdk-11\bin\java.exe" -Xms512m -Xmx512m -Xmn256m -Dnacos.standalone=true -jar E:\nacos\target\nacos-server.jar --spring.config.additional-location=optional:file:./conf/
    timeout /t 8 >nul
)
echo   Nacos (8848) ✓

REM === 安装 campus-common ===
echo.
echo [2/5] 编译 campus-common...
cd /d E:\ck\campus-platform
call mvn install -pl campus-common -DskipTests -q
if %errorlevel% neq 0 (
    echo   campus-common 编译失败！
    pause
    exit /b 1
)
echo   campus-common ✓

REM === 启动微服务 ===
echo.
echo [3/5] 启动微服务（6个）...

start "user-service" cmd /c "cd /d E:\ck\campus-platform && mvn spring-boot:run -pl user-service -DskipTests"
start "news-service" cmd /c "cd /d E:\ck\campus-platform && mvn spring-boot:run -pl campus-news-service -DskipTests"
start "forum-service" cmd /c "cd /d E:\ck\campus-platform && mvn spring-boot:run -pl forum-service -DskipTests"
start "lostfound-service" cmd /c "cd /d E:\ck\campus-platform && mvn spring-boot:run -pl lost-found-service -DskipTests"
start "secondhand-service" cmd /c "cd /d E:\ck\campus-platform && mvn spring-boot:run -pl second-hand-service -DskipTests -Dspring-boot.run.arguments=--server.port=8086"
start "complaint-service" cmd /c "cd /d E:\ck\campus-platform && mvn spring-boot:run -pl complaint-service -DskipTests"

echo   等待微服务启动（约30秒）...
timeout /t 30 >nul

REM === 启动网关 ===
echo.
echo [4/5] 启动网关...
start "gateway" cmd /c "cd /d E:\ck\campus-platform && mvn spring-boot:run -pl campus-gateway -DskipTests"
timeout /t 10 >nul

REM === 启动前端 ===
echo.
echo [5/5] 启动前端...
start "frontend" cmd /c "cd /d E:\ck\campus-platform\campus-frontend && npx vite --port 3000"
timeout /t 6 >nul

REM === 验证 ===
echo.
echo ========================================
echo   启动完成！
echo   前端: http://localhost:3000
echo   网关: http://localhost:8080
echo   Nacos: http://localhost:8848/nacos
echo ========================================
echo.
echo 按任意键关闭此窗口（不影响服务运行）
pause >nul
