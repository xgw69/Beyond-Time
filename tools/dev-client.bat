@echo off
rem Launch Minecraft 26.2 with Beyond-Time loaded (development environment).
rem Double-click this file, or run it from a command prompt.
rem Keep this file ASCII-only with CRLF line endings: cmd.exe mis-parses both
rem LF-only line endings and non-ASCII text under a non-UTF-8 code page.
setlocal
pushd "%~dp0.."
rem Extra arguments are forwarded to Gradle, e.g. "dev-client.bat --dry-run".
call gradlew.bat runClient %*
if errorlevel 1 pause
popd
endlocal
