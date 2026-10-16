@echo off
if not exist out mkdir out
javac -encoding UTF-8 -d out src\agrivision\*.java
if errorlevel 1 (
    echo.
    echo Compilation failed.
    pause
    exit /b 1
)
java -cp out agrivision.Main
pause
