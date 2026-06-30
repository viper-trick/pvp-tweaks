@echo off
setlocal enabledelayedexpansion

REM Build all version groups
call gradlew :1_21_4_5:build
if %errorlevel% neq 0 exit /b %errorlevel%
call gradlew :1_21_6_8:build
if %errorlevel% neq 0 exit /b %errorlevel%
call gradlew :1_21_9_10:build
if %errorlevel% neq 0 exit /b %errorlevel%
call gradlew :1_21_11:build
if %errorlevel% neq 0 exit /b %errorlevel%
call gradlew :26_1_x:build
if %errorlevel% neq 0 exit /b %errorlevel%
call gradlew :26_2:build
if %errorlevel% neq 0 exit /b %errorlevel%

REM Move built JARs to final-jars/
if not exist final-jars mkdir final-jars

for /d %%v in (src\build-*) do (
    if exist "%%v\build\libs" (
        for %%j in ("%%v\build\libs\pvp-tweaks-*.jar") do (
            echo %%j | findstr /C:"-sources.jar" >nul
            if !errorlevel! neq 0 (
                move "%%j" "final-jars\" >nul
            )
        )
    )
)

echo All JARs moved to final-jars/
