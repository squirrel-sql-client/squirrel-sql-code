@echo off

IF "%JAVA_HOME%"=="" SET LOCAL_JAVA=java
IF NOT "%JAVA_HOME%"=="" SET LOCAL_JAVA=%JAVA_HOME%\bin\java

set SQUIRREL_SQL_HOME=$INSTALL_PATH

@rem dir /b "%SQUIRREL_SQL_HOME%\squirrel-sql.jar" > temp.tmp
@rem FOR /F %%I IN (temp.tmp) DO CALL "%SQUIRREL_SQL_HOME%\addpath.bat" "%SQUIRREL_SQL_HOME%\%%I"
set TMP_CP="%SQUIRREL_SQL_HOME%\squirrel-sql.jar"

dir /b "%SQUIRREL_SQL_HOME%\lib\*.*" > temp.tmp
FOR /F %%I IN (temp.tmp) DO CALL "%SQUIRREL_SQL_HOME%\addpath.bat" "%SQUIRREL_SQL_HOME%\lib\%%I"

SET TMP_CP=%TMP_CP%;"%CLASSPATH%"
SET TMP_PARMS=-loggingConfigFile="%SQUIRREL_SQL_HOME%\log4j.properties" -squirrelHome="%SQUIRREL_SQL_HOME%"

@rem Run with a command window.
"%LOCAL_JAVA%" -cp %TMP_CP% net.sourceforge.squirrel_sql.client.Main %TMP_PARMS%

@rem Run with no command window. However this may not work with all versions of Windows.
@rem start "SQuirreL SQL Client" /B "%LOCAL_JAVA%w" -cp %TMP_CP% net.sourceforge.squirrel_sql.client.Main %TMP_PARMS%

@rem Run the executable jar file with or without a cmd window. However the
@rem classes from the %CLASSPATH% environment variable will not be available.
@rem "%LOCAL_JAVA%" -jar "%SQUIRREL_SQL_HOME%\squirrel-sql.jar" %TMP_PARMS%
@rem start "SQuirreL SQL Client" /B "%LOCAL_JAVA%w" -jar "%SQUIRREL_SQL_HOME%\squirrel-sql.jar" %TMP_PARMS%

