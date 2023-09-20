@echo off

@rem IzPack replaces ($ or %)JAVA_HOME with the JDK/JRE IzPack installer was started with.
@rem I.e. is a built in variable of IzPack,
@rem see https://izpack.atlassian.net/wiki/spaces/IZPACK/pages/491572/Variables
@rem Note the batch script untypical $JAVA_HOME which is the one replaced by IzPack here.
set "IZPACK_JAVA=$JAVA_HOME"

@rem We detect the java executable to use according to the following algorithm:
@rem
@rem 1. If the system variable JAVA_HOME is set then use that; otherwise
@rem 2. If the one used by the IzPack installer is available then use that; otherwise
@rem 3. Use the java that is in the command path.
@rem 

if exist "%JAVA_HOME%\bin\javaw.exe" (
  set "LOCAL_JAVA=%JAVA_HOME%\bin\javaw.exe"
) else if exist "%IZPACK_JAVA%\bin\javaw.exe" (
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

"%LOCAL_JAVA%" -cp "%SQUIRREL_SQL_HOME%\lib\versioncheck.jar" JavaVersionChecker 11 12 13 14 15 16 17 18 19 20 21
if ErrorLevel 1 goto ExitForWrongJavaVersion

:launchsquirrel
SET CP="%SQUIRREL_SQL_HOME%\squirrel-sql.jar;%SQUIRREL_SQL_HOME%\lib\*"
echo "CP=%CP%"

SET TMP_PARMS= --squirrel-home "%SQUIRREL_SQL_HOME%" %1 %2 %3 %4 %5 %6 %7 %8 %9

@rem -Dsun.java2d.noddraw=true prevents performance problems on Win32 systems. 

@rem Run with no command window. This may not work with versions of Windows prior to XP. 
@rem Remove 'start "SQuirreL SQL Client" /B' for compatibility only if necessary
@rem VM Parameter --add-exports=java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED was added for JGoodies Windows L&F, see SourceForge bug #1507.
start "SQuirreL SQL Client" /B "%LOCAL_JAVA%" -cp %CP% --add-exports=java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED -Dsun.awt.nopixfmt=true -Dsun.java2d.noddraw=true %SQUIRREL_SQL_OPTS% -splash:"%SQUIRREL_SQL_HOME%/icons/splash.jpg" net.sourceforge.squirrel_sql.client.Main %TMP_PARMS%

:ExitForWrongJavaVersion

