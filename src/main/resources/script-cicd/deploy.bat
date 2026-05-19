@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "APP_NAME=paul-api"
set "CONTAINER_NAME=beb31"
set "HOST_PORT=8080"
set "CONTAINER_PORT=8080"
set "DOCKER_SERVICE=com.docker.service"

cd /d "%~dp0"

echo ========================================
echo Deploy %APP_NAME%
echo Folder: %CD%
echo ========================================
echo.

where docker >nul 2>&1
if errorlevel 1 (
    call :Fail "Docker CLI tidak ditemukan. Pastikan Docker sudah terinstall dan PATH sudah benar."
    exit /b 1
)

where mvn >nul 2>&1
if errorlevel 1 (
    call :Fail "Maven CLI tidak ditemukan. Pastikan Maven sudah terinstall dan PATH sudah benar."
    exit /b 1
)

echo [1/9] Mengecek service Docker...
call :EnsureDockerService
if errorlevel 1 exit /b 1

call :WaitDockerReady
if errorlevel 1 exit /b 1

echo.
echo [2/9] Mematikan container %CONTAINER_NAME% jika sedang berjalan...
set "CONTAINER_RUNNING="
for /f %%S in ('docker inspect -f "{{.State.Running}}" %CONTAINER_NAME% 2^>nul') do set "CONTAINER_RUNNING=%%S"
if /I "!CONTAINER_RUNNING!"=="true" (
    docker stop %CONTAINER_NAME%
    if errorlevel 1 (
        call :Fail "Gagal mematikan container %CONTAINER_NAME%."
        exit /b 1
    )
) else (
    echo [INFO] Container %CONTAINER_NAME% tidak sedang berjalan atau belum ada.
)

echo.
echo [3/9] Menghapus container %CONTAINER_NAME% jika ada...
docker container inspect %CONTAINER_NAME% >nul 2>&1
if not errorlevel 1 (
    docker rm %CONTAINER_NAME%
    if errorlevel 1 (
        call :Fail "Gagal menghapus container %CONTAINER_NAME%."
        exit /b 1
    )
) else (
    echo [INFO] Container %CONTAINER_NAME% tidak ditemukan.
)

echo.
echo [4/9] Menghapus image %APP_NAME% jika ada...
docker image inspect %APP_NAME% >nul 2>&1
if not errorlevel 1 (
    docker rmi %APP_NAME%
    if errorlevel 1 (
        call :Fail "Gagal menghapus image %APP_NAME%."
        exit /b 1
    )
) else (
    echo [INFO] Image %APP_NAME% tidak ditemukan.
)

echo.
echo [5/9] Menghapus folder target jika ada...
if not exist "target\" (
    echo [INFO] Folder target tidak ditemukan. Lanjut ke proses 6.
) else (
    rmdir /s /q "target"
    if exist "target\" (
        call :Fail "Gagal menghapus folder target. Pastikan tidak ada file yang sedang dipakai."
        exit /b 1
    )
    echo [OK] Folder target berhasil dihapus.
)

echo.
echo [6/9] Build Maven: mvn clean package -DskipTests
call mvn clean package -DskipTests
if errorlevel 1 (
    call :Fail "Build Maven gagal."
    exit /b 1
)

echo.
echo [7/9] Build Docker image: docker build -t %APP_NAME% .
docker build -t %APP_NAME% .
if errorlevel 1 (
    call :Fail "Build Docker image gagal."
    exit /b 1
)

echo.
echo [8/9] Membuat container %CONTAINER_NAME% di port %HOST_PORT%:%CONTAINER_PORT%...
docker create --name %CONTAINER_NAME% ^
  -p %HOST_PORT%:%CONTAINER_PORT% ^
  -e DB_URL="e84b3efac0261a03fff0c5182c725421821c9ad8b5394f3f50cb52a5da2d5a6c803e90abf2a9a233a8a0ec9585263d093ec9c6d32473258e41931ed207245cf04accb672202b6f0530be373599d87ea5a2a693af73b4b70e94930365e551ee82ff484f28977b9ef1dc0a15ec18cf2ebd" ^
  -e DB_USERNAME="6d4bbf96fec4cbd55fc9be6954a06d17" ^
  -e DB_PASSWORD="034456e6b2b6e4a86a38ffc10fbe7445" ^
  %APP_NAME%
if errorlevel 1 (
    call :Fail "Gagal membuat container %CONTAINER_NAME%."
    exit /b 1
)

echo.
echo [9/9] Menjalankan container %CONTAINER_NAME%...
docker start %CONTAINER_NAME%
if errorlevel 1 (
    call :Fail "Gagal menjalankan container %CONTAINER_NAME%."
    exit /b 1
)

echo.
echo ========================================
echo Deploy selesai.
echo Container: %CONTAINER_NAME%
echo Image    : %APP_NAME%
echo URL      : http://localhost:%HOST_PORT%
echo ========================================
exit /b 0

:EnsureDockerService
sc query "%DOCKER_SERVICE%" >nul 2>&1
if errorlevel 1 (
    set "DOCKER_SERVICE=docker"
    sc query "!DOCKER_SERVICE!" >nul 2>&1
)

if errorlevel 1 (
    echo [WARN] Service Docker tidak ditemukan melalui sc query.
    echo [INFO] Melanjutkan pengecekan langsung ke Docker daemon.
    exit /b 0
)

set "SERVICE_STATE="
for /f "tokens=3 delims=: " %%S in ('sc query "!DOCKER_SERVICE!" ^| findstr /I "STATE"') do set "SERVICE_STATE=%%S"

if /I "!SERVICE_STATE!"=="RUNNING" (
    echo [OK] Service !DOCKER_SERVICE! sudah berjalan.
    exit /b 0
)

echo [INFO] Service !DOCKER_SERVICE! belum berjalan. Mencoba menyalakan...
net start "!DOCKER_SERVICE!"
if errorlevel 1 (
    call :Fail "Gagal menyalakan service Docker. Jalankan file ini sebagai Administrator atau nyalakan Docker Desktop manual."
    exit /b 1
)

echo [OK] Service !DOCKER_SERVICE! berhasil dinyalakan.
exit /b 0

:WaitDockerReady
echo [INFO] Menunggu Docker daemon siap...
for /L %%I in (1,1,30) do (
    docker info >nul 2>&1
    if not errorlevel 1 (
        echo [OK] Docker daemon siap.
        exit /b 0
    )
    timeout /t 2 /nobreak >nul
)

call :Fail "Docker daemon belum siap setelah 60 detik. Pastikan Docker Desktop sudah berjalan."
exit /b 1

:Fail
echo.
echo [ERROR] %~1
echo Proses dihentikan.
exit /b 1
