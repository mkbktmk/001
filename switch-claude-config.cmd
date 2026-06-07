@echo off
echo ============================================
echo  Claude Code 配置目录迁移脚本
echo ============================================
echo.
echo 将 C:\Users\玛卡巴卡\.claude 替换为指向 E:\claude-config 的链接
echo.
echo 警告: 请确保 Claude Code 已经完全关闭！
echo.
pause

echo.
echo [1/3] 验证新目录存在...
if not exist "E:\claude-config\settings.json" (
    echo 错误: E:\claude-config 不存在或不完整，请检查！
    pause
    exit /b 1
)
echo OK - 新目录就绪

echo [2/3] 重命名旧目录为备份...
if exist "C:\Users\玛卡巴卡\.claude.bak" (
    echo 备份目录已存在，删除旧备份...
    rmdir /s /q "C:\Users\玛卡巴卡\.claude.bak"
)
move "C:\Users\玛卡巴卡\.claude" "C:\Users\玛卡巴卡\.claude.bak"
if %errorlevel% neq 0 (
    echo 重命名失败！可能有程序正在占用文件。请关闭所有程序后重试。
    pause
    exit /b 1
)
echo OK - 旧目录已重命名为 .claude.bak

echo [3/3] 创建 Junction...
mklink /J "C:\Users\玛卡巴卡\.claude" "E:\claude-config"
if %errorlevel% neq 0 (
    echo Junction 创建失败！正在恢复旧目录...
    move "C:\Users\玛卡巴卡\.claude.bak" "C:\Users\玛卡巴卡\.claude"
    pause
    exit /b 1
)
echo OK - Junction 创建成功

echo.
echo ============================================
echo  迁移完成！
echo  C:\Users\玛卡巴卡\.claude   =^>  E:\claude-config
echo  旧文件备份在: C:\Users\玛卡巴卡\.claude.bak
echo ============================================
echo.
echo 确认一切正常后，可以手动删除备份目录:
echo   rmdir /s /q "C:\Users\玛卡巴卡\.claude.bak"
echo.
pause
