@echo off

@rem IZPACK_JAVA is filtered in by the IzPack installer when this script is installed
set "IZPACK_JAVA=%JAVA_HOME%"

@rem We detect the java executable to use according to the following algorithm:
@rem
@rem 1. If the one used by the IzPack installer is available then use that; otherwise
@rem 2. Use the java that is in the command path.
@rem

if exist "%IZPACK_JAVA%\bin\javaw.exe" (
  set "LOCAL_JAVA=%IZPACK_JAVA%\bin\javaw.exe"
) else (
  set LOCAL_JAVA=javaw.exe
)

echo Using java: %LOCAL_JAVA%

set basedir=%~f0
:strip
set removed=%basedir:~-1%
set basedir=%basedir:~0,-1%
if NOT "%removed%"=="\" goto strip
set SQUIRREL_SQL_HOME=%basedir%

"%LOCAL_JAVA%" -cp "%SQUIRREL_SQL_HOME%\lib\versioncheck.jar" JavaVersionChecker 1.8 9 10 11 12
if ErrorLevel 1 goto ExitForWrongJavaVersion

:launchsquirrel
SET CP="%SQUIRREL_SQL_HOME%\squirrel-sql.jar;%SQUIRREL_SQL_HOME%\lib\*"
echo "CP=%CP%"

SET TMP_PARMS=--log-config-file "%SQUIRREL_SQL_HOME%\log4j.properties" --squirrel-home "%SQUIRREL_SQL_HOME%" %1 %2 %3 %4 %5 %6 %7 %8 %9

@rem -Dsun.java2d.noddraw=true prevents performance problems on Win32 systems.

@rem Run with no command window. This may not work with versions of Windows prior to XP.
@rem Remove 'start "SQuirreL SQL Client" /B' for compatibility only if necessary
start "SQuirreL SQL Client" /B "%LOCAL_JAVA%" -Dsun.awt.nopixfmt=true -Dsun.java2d.noddraw=true -cp %CP% -splash:"%SQUIRREL_SQL_HOME%/icons/splash.jpg" net.sourceforge.squirrel_sql.client.Main %TMP_PARMS%

:ExitForWrongJavaVersion
