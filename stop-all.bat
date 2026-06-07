@echo off
chcp 65001 >nul
title 校园综合服务平台 - 关闭中...

echo ========================================
echo   校园综合服务平台 一键关闭
echo ========================================
echo.

set PORTS=8080 8082 8083 8084 8085 8086 8087 3000

echo 停止微服务 + 网关 + 前端...
for %%p in (%PORTS%) do (
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%%p " ^| findstr "LISTENING"') do (
        taskkill /F /PID %%a >nul 2>&1
        if !errorlevel! equ 0 echo   端口 %%p (PID %%a) 已停止
    )
)

echo.
echo 如需停止基础设施 (MySQL/Redis/Nacos)，请手动操作。
echo.
echo ========================================
echo   项目已关闭
echo ========================================

pause >nul
