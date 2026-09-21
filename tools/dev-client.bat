@echo off
rem 启动带本模组的 Minecraft 26.2（开发环境）。双击即可。
cd /d "%~dp0.."
call gradlew.bat runClient
if errorlevel 1 pause
