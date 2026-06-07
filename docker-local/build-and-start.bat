@echo off
echo =================================
echo  Campus Platform Docker Deploy
echo =================================

echo.
echo [1/3] Building backend...
cd /d "E:\ck\campus-platform"
call mvn package spring-boot:repackage -DskipTests -q
if %errorlevel% neq 0 (
    echo Backend build FAILED!
    pause
    exit /b 1
)
echo Backend OK

echo.
echo [2/3] Building frontend...
cd /d "E:\ck\campus-platform\campus-frontend"
call npm run build
echo Frontend OK

echo.
echo [3/3] Starting Docker containers...
cd /d "E:\ck\docker-local"
docker compose up -d --build

echo.
echo =================================
echo  Deploy complete!
echo  Frontend: http://localhost
echo  Stop: docker compose -f E:\ck\docker-local\docker-compose.yml stop
echo =================================
pause
