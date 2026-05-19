@echo off
setlocal

cd /d "%~dp0"

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0watch-deploy.ps1"
if errorlevel 1 (
    echo.
    echo [ERROR] watch-deploy.ps1 gagal dijalankan.
    exit /b 1
)

exit /b 0
