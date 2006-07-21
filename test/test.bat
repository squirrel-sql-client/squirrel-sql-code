
@echo off

call cp.bat

set MAINCLASS=net.sourceforge.squirrel_sql.plugins.dbcopy.CopyExecutorTestRunner
set SOURCE_DB=%1
set DEST_DB=%2
set LOGS_DIR=logs\%SOURCE_DB%


echo "Running %SOURCE_DB%->%DEST_DB%"
java -cp %CLASSPATH% %MAINCLASS% %SOURCE_DB%_to_%DEST_DB% 
