@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo ============================================
echo SysOne Clean - Android Studio'siz APK build
echo ============================================
echo.

where java >nul 2>nul
if errorlevel 1 (
  echo [XATO] Java topilmadi. JDK 17 o'rnating.
  pause
  exit /b 1
)

where gradle >nul 2>nul
if errorlevel 1 (
  echo [XATO] Gradle topilmadi. Gradle o'rnating yoki GitHub Actions usulidan foydalaning.
  pause
  exit /b 1
)

if "%ANDROID_HOME%"=="" (
  echo [XATO] ANDROID_HOME sozlanmagan. Android SDK Command-line Tools kerak.
  pause
  exit /b 1
)

echo Build boshlanmoqda...
gradle assembleDebug --stacktrace
if errorlevel 1 (
  echo.
  echo [XATO] Build muvaffaqiyatsiz tugadi.
  pause
  exit /b 1
)

echo.
echo [OK] APK tayyor:
echo %CD%\app\build\outputs\apk\debug\app-debug.apk
pause
