@echo off

@rem IZPACK_JAVA is filtered in by the IzPack installer when this script is installed
set IZPACK_JAVA="%JAVA_HOME%"

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
"%LOCAL_JAVA%" -cp "%SQUIRREL_SQL_HOME%\..\lib\versioncheck.jar" JavaVersionChecker 1.8 9
if ErrorLevel 1 goto ExitForWrongJavaVersion

:launchsquirrel
@rem build SQuirreL's classpath
set TMP_CP="%SQUIRREL_SQL_HOME%\..\squirrel-sql.jar"
dir /b "%SQUIRREL_SQL_HOME%\..\lib\*.*" >"%TEMP%\squirrel-lib.tmp"
FOR /F "usebackq" %%I IN ("%TEMP%\squirrel-lib.tmp") DO CALL "%SQUIRREL_SQL_HOME%\..\addpath.bat" "%SQUIRREL_SQL_HOME%\..\lib\%%I"
SET SQUIRREL_CP=%TMP_CP%

@rem Launch SQuirreL CLI
set EXECDONE=0

set _JAVA_OPTIONS=

IF "%1" == "" (
   set EXECDONE=1
   echo "Entering Java 9 JShell based mode. JAVA 9 is required."
   set _JAVA_OPTIONS="-Dsquirrel.home=%SQUIRREL_SQL_HOME%"
   %JAVA_HOME%\bin\jshell.exe --class-path %TMP_CP%  %SQUIRREL_SQL_HOME%/startsquirrelcli.jsh
)

IF "%EXECDONE%" == "0" IF "%3" == "" IF NOT "%2" == "" IF "%1" == "-userdir" (
   set EXECDONE=1
   echo "Entering Java 9 JShell based mode. JAVA 9 is required."
   set _JAVA_OPTIONS=-Dsquirrel.home="%SQUIRREL_SQL_HOME%" -Dsquirrel.userdir="%2"
   %JAVA_HOME%\bin\jshell.exe --class-path %TMP_CP%  %SQUIRREL_SQL_HOME%/startsquirrelcli.jsh
)

IF "%EXECDONE%" == "0" (
   %JAVA_HOME%\bin\java --class-path %TMP_CP%  net.sourceforge.squirrel_sql.client.cli.SquirrelBatch "%SQUIRREL_SQL_HOME%" %1 %2 %3 %4 %5 %6 %7 %8 %9
)

GOTO END

:ExitForWrongJavaVersion

:DONEOKTOHERE
echo REACHED DONE OK

:END