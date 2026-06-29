@echo off
setlocal
echo ====================================================
echo  HW4 Build Script - The Mysterious Ticking Noise
echo ====================================================
echo.

REM --- Check for Maven ---
where mvn >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: mvn not found on PATH.
    echo Please install Maven from https://maven.apache.org/download.cgi
    echo and add it to your PATH, then re-run this script.
    pause
    exit /b 1
)

REM --- Check for Java 17+ ---
for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VER=%%v
)
echo Maven : OK
echo Java  : %JAVA_VER%
echo.

REM --- Build ---
echo [1/2] Compiling, running tests, and packaging...
call mvn clean package -q
if %ERRORLEVEL% neq 0 (
    echo.
    echo BUILD FAILED. Re-running with verbose output:
    call mvn clean package
    pause
    exit /b 1
)
echo       Done.
echo.

REM --- Smoke test ---
echo [2/2] Smoke test (countdown=24):
echo.
java -jar hw4.jar 24
echo.

REM --- Success ---
echo ====================================================
echo  BUILD SUCCESSFUL
echo  hw4.jar is ready in this folder.
echo  Run submit.bat next to create your submission zip.
echo ====================================================
echo.
pause
