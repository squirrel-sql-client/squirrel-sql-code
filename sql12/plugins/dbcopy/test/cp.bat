
@echo off


set DBCOPY_HOME=C:\home\projects\squirrel-sql\sql12\plugins\dbcopy
set DBCOPY_TEST=C:\home\projects\squirrel-sql\sql12\plugins\dbcopy\test
set DBCOPY_TMP=C:\home\projects\squirrel-sql\sql12\plugins\dbcopy\test\tmp


set DBCOPY_JAR=C:\tools\squirrel\plugins\dbcopy.jar
set ECLIPSE_CLASSES=C:\home\projects\squirrel-sql\sql12\eclipse-bin
set SQUIRREL_JAR=C:\tools\squirrel\squirrel-sql.jar;C:\tools\squirrel\lib\fw.jar
set COMMONS_CLI=C:\tools\squirrel\lib\commons-cli.jar
set LOG4J=C:\tools\squirrel\lib\log4j.jar
set NANO=C:\tools\squirrel\lib\nanoxml-2.2.3.jar
set HIBERNATE=C:\home\projects\squirrel-sql\sql12\plugins\dbcopy\lib\hibernate3.jar
set ANTLR=C:\home\projects\squirrel-sql\sql12\plugins\dbcopy\lib\antlr-2.7.5H3.jar
set COMMONS_LOGGING=C:\home\CVS\collabraspace\software\commons-logging-1.0.4\lib\commons-logging.jar
set COMMONS_LOGGING_API=C:\home\CVS\collabraspace\software\commons-logging-1.0.4\lib\commons-logging-api.jar
set COMMONS_COLLECTIONS=C:\home\projects\axion\lib\commons-collections.jar
set COMMONS_PRIMITIVES=C:\home\projects\axion\lib\commons-primitives.jar

rem JDBC Drivers
set AXION_JAR=C:\home\projects\axion\bin\axion-1.0-M3-dev.jar
set DAFFODIL_JAR=C:\tools\OneDollarDB4_0\lib\DaffodilDB_Client.jar
set DERBY_JAR=C:\tools\db-derby-snapshot-10.1.1.1-292855\lib\derbyclient.jar
set DB2_JAR=C:\tools\DB2_PE\java\db2jcc.jar;C:\tools\DB2_PE\java\db2jcc_license_cu.jar

set FIREBIRD_JAR=C:\tools\firebird-jdbc-2.0.1\jaybird-full-2.0.1.jar;C:\tools\firebird-jdbc-2.0.1\mini-j2ee.jar
rem set FRONTBASE_JAR=C:\tools\FrontBaseJManager0_505\frontbasejdbc.jar
set FRONTBASE_JAR=C:\tools\FrontBaseJDBC-2.5.2\frontbasejdbc.jar
set H2_JAR=C:\tools\H2\bin\h2.jar
set HSQLDB_JAR=C:\tools\hsqldb-1_8_0_2\lib\hsqldb.jar
set INGRES_JAR=C:\tools\Ingres\ingres\lib\iijdbc.jar
set MCKOI_JAR=C:\tools\mckoi1.0.3\mkjdbc.jar
set MSSQL_JAR=C:\tools\Microsoft_SQL_Server_2005_JDBC_Driver\sqljdbc_1.0\enu\sqljdbc.jar
set MYSQL_JAR=C:\tools\mysql-connector-java-3.1.8\mysql-connector-java-3.1.8-bin.jar
set ORACLE_JAR=C:\tools\Oracle_10g_JDBC_Driver\ojdbc14.jar
set POINTBASE_JAR=C:\bea\weblogic81\common\eval\pointbase\lib\pbclient44.jar
set POSTGRES_JAR=C:\tools\PostgreSQL\8.0\jdbc\postgresql-8.0-311.jdbc3.jar
set SQLSERVER_JAR=C:\tools\Microsoft_SQL_Server_2005_JDBC_Driver\sqljdbc_1.0\enu\sqljdbc.jar
set SYBASE_JAR=C:\tools\sybase\jConnect-6_0\classes\jconn3.jar;C:\tools\sybase\jConnect-6_0\classes\jTDS3.jar

set CLASSPATH=%DBCOPY_JAR%;%SQUIRREL_JAR%;%DBCOPY_TEST%;%COMMONS_COLLECTIONS%;%COMMONS_PRIMITIVES%;%DBCOPY_HOME%;%DBCOPY_TMP%;%ECLIPSE_CLASSES%;%ORACLE_JAR%;%MSSQL_JAR%;%COMMONS_CLI%;%LOG4J%
set CLASSPATH=%CLASSPATH%;%NANO%;%HIBERNATE%;%ANTLR%;%DB2_JAR%;%COMMONS_LOGGING%;%COMMONS_LOGGING_API%;%POSTGRES_JAR%;%AXION_JAR%
set CLASSPATH=%CLASSPATH%;%DERBY_JAR%;%FIREBIRD_JAR%;%FRONTBASE_JAR%;%H2_JAR%;%HSQLDB_JAR%;%INGRES_JAR%;%MCKOI_JAR%
set CLASSPATH=%CLASSPATH%;%MYSQL_JAR%;%POINTBASE_JAR%;%SQLSERVER%;%SYBASE_JAR%;%DAFFODIL_JAR%

echo "Using CLASSPATH=%CLASSPATH%"



