@rem Run with a command window.
"%JAVA_HOME%\bin\java" -cp squirrel-sql.jar;lib\fw.jar;lib\nanoxml-2.1.jar;lib\log4j.jar;"%CLASSPATH%" net.sourceforge.squirrel_sql.client.Main -loggingConfigFile=log4j.properties

@rem Run with no command window. However this may not work will all versions of Windows.
@rem start "%JAVA_HOME%\bin\javaw" -cp squirrel-sql.jar;lib\fw.jar;lib\nanoxml-2.1.jar;"%CLASSPATH%" net.sourceforge.squirrel_sql.client.Main

@rem Run the executable jar file. However the classes from the %CLASSPATH%
@rem environment variable will not be available.
@rem squirrel-sql.jar
