@echo off
rem 生成资源与数据 JSON 到 src\generated\resources。双击即可。
cd /d "%~dp0.."
call gradlew.bat runData
if errorlevel 1 pause
