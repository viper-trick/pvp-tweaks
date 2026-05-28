@echo off
setlocal enabledelayedexpansion

REM Build all versions — exit on any failure
call gradlew build
if %errorlevel% neq 0 exit /b %errorlevel%

REM Move built JARs to final-jars/
if not exist final-jars mkdir final-jars

for /d %%v in (src\*) do (
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
