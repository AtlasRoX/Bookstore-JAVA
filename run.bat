@echo off
rem Compiles the program, then opens it in its own window that is exactly its size: 80 x 24 characters.
cd /d "%~dp0"
javac *.java || (pause & exit /b 1)
where wt >nul 2>nul
if errorlevel 1 (
    start "Bookstore" cmd /c "mode con cols=80 lines=24 & java Main || pause"
) else (
    start "" wt -w new --size 80,24 --pos 0,0 --title Bookstore -d . cmd /c "java Main || pause"
)
