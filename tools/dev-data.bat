@echo off
rem Regenerate recipes, loot tables and models into src/generated/resources.
rem Run this after anything under src/main/java/com/beyondtime/datagen changes.
rem Keep this file ASCII-only with CRLF line endings: cmd.exe mis-parses both
rem LF-only line endings and non-ASCII text under a non-UTF-8 code page.
setlocal
pushd "%~dp0.."
rem Extra arguments are forwarded to Gradle, e.g. "dev-data.bat --dry-run".
call gradlew.bat runData %*
if errorlevel 1 pause
popd
endlocal
