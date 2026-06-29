@echo off
setlocal
echo ====================================================
echo  HW4 Submission Packager
echo ====================================================
echo.

REM --- Prompt for student IDs ---
set /p ID1=Enter your student ID (first partner):
set /p ID2=Enter your student ID (second partner):
set ZIPNAME=%ID1%_%ID2%.zip

echo.

REM --- Check hw4.jar exists ---
if not exist hw4.jar (
    echo ERROR: hw4.jar not found. Run build.bat first.
    pause
    exit /b 1
)

REM --- Check hw4.pdf exists ---
if not exist hw4.pdf (
    echo ERROR: hw4.pdf not found.
    pause
    exit /b 1
)

REM --- Delete old zip if present ---
if exist %ZIPNAME% del %ZIPNAME%

REM --- Create zip using PowerShell (available on all modern Windows) ---
echo Creating %ZIPNAME% ...
powershell -NoProfile -Command ^
  "Compress-Archive -Path 'hw4.jar','hw4.pdf' -DestinationPath '%ZIPNAME%' -Force"

if %ERRORLEVEL% neq 0 (
    echo ERROR: Could not create zip. Try zipping hw4.jar and hw4.pdf manually.
    pause
    exit /b 1
)

echo.
echo ====================================================
echo  SUCCESS: %ZIPNAME% is ready to submit.
echo  Contents: hw4.jar + hw4.pdf
echo ====================================================
echo.
pause
