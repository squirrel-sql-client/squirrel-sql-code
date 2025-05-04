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


set basedir=%~f0
:strip
set removed=%basedir:~-1%
set basedir=%basedir:~0,-1%
if NOT "%removed%"=="\" goto strip
set SQUIRREL_CLI_HOME=%basedir%

@rem Check to see if we are running in a 1.6/1.7 JVM and inform the user if not and skip launch. versioncheck.jar
@rem is a special jar file which has been compiled with javac version 1.2.2, which should be able to be run by
@rem that version of higher.  The arguments to JavaVersionChecker below specify the minimum acceptable version
@rem (first arg) and any other acceptable subsequent versions.  <MAJOR>.<MINOR> should be all that is
@rem necessary for the version form.
"%LOCAL_JAVA%" -cp "%SQUIRREL_CLI_HOME%\..\lib\versioncheck.jar" JavaVersionChecker 17 18 19 20 21 22 23 24
if ErrorLevel 1 goto ExitForWrongJavaVersion

:launchsquirrel
@rem build SQuirreL's classpath
SET CP="%SQUIRREL_CLI_HOME%\..\squirrel-sql.jar;%SQUIRREL_CLI_HOME%\..\lib\*"
echo "CP=%CP%"

@rem Launch SQuirreL CLI
set EXECDONE=0

set _JAVA_OPTIONS=

IF "%1" == "" (
   set EXECDONE=1
   set _JAVA_OPTIONS=-Dsquirrel.home="%SQUIRREL_CLI_HOME%\.."
   %JAVA_HOME%\bin\jshell.exe --class-path %CP%  "%SQUIRREL_CLI_HOME%/startsquirrelcli.jsh"
)

IF "%EXECDONE%" == "0" IF "%3" == "" IF NOT "%2" == "" IF "%1" == "-userdir" (
   set EXECDONE=1
   set _JAVA_OPTIONS=-Dsquirrel.home="%SQUIRREL_CLI_HOME%\.." -Dsquirrel.userdir="%2"
   %JAVA_HOME%\bin\jshell.exe --class-path %CP%  "%SQUIRREL_CLI_HOME%/startsquirrelcli.jsh"
)

IF "%EXECDONE%" == "0" (
   @rem TODO: find a way to pass more than 9 parameters to SquirrelBatch without using _JAVA_OPTIONS
   @rem because using _JAVA_OPTIONS always leads to a "Picked up _JAVA_OPTIONS..." output.
   set _JAVA_OPTIONS=-Dsquirrel.home="%SQUIRREL_CLI_HOME%\.."
   %JAVA_HOME%\bin\java -cp %CP% net.sourceforge.squirrel_sql.client.cli.SquirrelBatch %*
)

:ExitForWrongJavaVersion
