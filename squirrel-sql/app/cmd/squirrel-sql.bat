echo off

IF "%JAVA_HOME%"=="" SET LOCAL_JAVA=javaw
IF NOT "%JAVA_HOME%"=="" SET LOCAL_JAVA=%JAVA_HOME%\bin\javaw

dir /b squirrel-sql.jar > temp.tmp
FOR /F %%I IN (temp.tmp) DO CALL addpath.bat %%I

dir /b lib\*.* > temp.tmp
FOR /F %%I IN (temp.tmp) DO CALL addpath.bat lib\%%I

SET TMP_CP=%TMP_CP%;%CLASSPATH%

@rem Run with a command window.
"%LOCAL_JAVA%" -cp "%TMP_CP%" net.sourceforge.squirrel_sql.client.Main -loggingConfigFile=log4j.properties

@rem Run with no command window. However this may not work with all versions of Windows.
@rem start "SQuirreL SQL Client" /B "%LOCAL_JAVA%" -cp "%TMP_CP%" net.sourceforge.squirrel_sql.client.Main

@rem Run the executable jar file with and without a cmd window. However the
@rem classes from the %CLASSPATH% environment variable will not be available.
@rem start "SQuirreL SQL Client" /B "%LOCAL_JAVA%" -jar squirrel-sql.jar
@rem "%LOCAL_JAVA%" -jar squirrel-sql.jar
