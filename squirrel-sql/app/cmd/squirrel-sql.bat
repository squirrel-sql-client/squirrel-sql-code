@rem -classic is required with JDK1.3 on Windows 2000. If it isn't used then
@rem plugins look in the wrong directory for files.

@rem Run with a command window.
"%JAVA_HOME%\bin\java" -classic -cp lib\squirrel-sql.jar;lib\fw.jar;lib\nanoxml-2.1.jar;lib\log4j.jar;%CLASSPATH% net.sourceforge.squirrel_sql.client.Main -loggingConfigFile=log4j.properties

@rem Run with no command window. However this may not work with all versions of Windows.
@rem start "%JAVA_HOME%\bin\javaw" -classic -cp lib\squirrel-sql.jar;lib\fw.jar;lib\nanoxml-2.1.jar;%CLASSPATH% net.sourceforge.squirrel_sql.client.Main

