@echo off

@rem IZPACK_JAVA is filtered in by the IzPack installer (via %JAVA_HOME) when this script is installed
set IZPACK_JAVA=%JAVA_HOME

@rem We detect the java executable to use according to the following algorithm:
@rem
@rem 1. If the one used by the IzPack installer is available then use that; otherwise
@rem 2. Use the java that is in the command path.
@rem 

if exist "%IZPACK_JAVA%\bin\javaw.exe" (
  set LOCAL_JAVA=%IZPACK_JAVA%\bin\javaw.exe
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

@rem Check to see if we are running in a 1.6/1.7 JVM and inform the user if not and skip launch. versioncheck.jar 
@rem is a special jar file which has been compiled with javac version 1.2.2, which should be able to be run by 
@rem that version of higher.  The arguments to JavaVersionChecker below specify the minimum acceptable version 
@rem (first arg) and any other acceptable subsequent versions.  <MAJOR>.<MINOR> should be all that is 
@rem necessary for the version form. 
"%LOCAL_JAVA%" -cp "%SQUIRREL_SQL_HOME%\lib\versioncheck.jar" JavaVersionChecker 1.6 1.7
if ErrorLevel 1 goto ExitForWrongJavaVersion

@rem If the changelist.xml file isn't present or the downloaded update jars don't exist, skip launching the updater - these files are created by the 
@rem software update feature inside of SQuirreL. So their absence, simply means the software update feature hasn't been accessed.
if not exist "%SQUIRREL_SQL_HOME%\update\changeList.xml" goto launchsquirrel
SET TMP_CP="%SQUIRREL_SQL_HOME%\update\downloads\core\squirrel-sql.jar"
if not exist %TMP_CP% goto launchsquirrel
dir /b "%SQUIRREL_SQL_HOME%\update\downloads\core\*.*" > %TEMP%\update-lib.tmp
FOR /F %%I IN (%TEMP%\update-lib.tmp) DO CALL "%SQUIRREL_SQL_HOME%\addpath.bat" "%SQUIRREL_SQL_HOME%\update\downloads\core\%%I"
SET UPDATE_CP=%TMP_CP%
SET UPDATE_PARMS=--log-config-file "%SQUIRREL_SQL_HOME%\update-log4j.properties" --squirrel-home "%SQUIRREL_SQL_HOME%" %1 %2 %3 %4 %5 %6 %7 %8 %9
"%LOCAL_JAVA%" -cp %UPDATE_CP% -Dlog4j.defaultInitOverride=true -Dprompt=true net.sourceforge.squirrel_sql.client.update.gui.installer.PreLaunchUpdateApplication %UPDATE_PARAMS%

:launchsquirrel
@rem build SQuirreL's classpath
set TMP_CP="%SQUIRREL_SQL_HOME%\squirrel-sql.jar"
dir /b "%SQUIRREL_SQL_HOME%\lib\*.*" > %TEMP%\squirrel-lib.tmp
FOR /F %%I IN (%TEMP%\squirrel-lib.tmp) DO CALL "%SQUIRREL_SQL_HOME%\addpath.bat" "%SQUIRREL_SQL_HOME%\lib\%%I"
SET SQUIRREL_CP=%TMP_CP%
echo "SQUIRREL_CP=%SQUIRREL_CP%"

SET TMP_PARMS=--log-config-file "%SQUIRREL_SQL_HOME%\log4j.properties" --squirrel-home "%SQUIRREL_SQL_HOME%" %1 %2 %3 %4 %5 %6 %7 %8 %9

@rem -Dsun.java2d.noddraw=true prevents performance problems on Win32 systems. 

@rem Run with no command window. This may not work with versions of Windows prior to XP. 
@rem Remove 'start "SQuirreL SQL Client" /B' for compatibility only if necessary 
start "SQuirreL SQL Client" /B "%LOCAL_JAVA%" -Xmx256m -Dsun.java2d.noddraw=true -cp %SQUIRREL_CP% net.sourceforge.squirrel_sql.client.Main %TMP_PARMS%

:ExitForWrongJavaVersion

