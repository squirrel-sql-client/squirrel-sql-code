#! /bin/sh
# $Id: squirrel-sql.sh,v 1.4 2001-12-31 06:44:59 colbell Exp $
TMP_CP=lib/squirrel-sql.jar:lib/fw.jar:lib/nanoxml-2.1.jar:lib/log4j.jar:$CLASSPATH 
if [ `uname -s` = "CYGWIN_NT-5.0" ]; then
    $JAVA_HOME/bin/java -cp `cygpath -w -p $TMP_CP` net.sourceforge.squirrel_sql.client.Main -loggingConfigFile=log4j.properties
else
  $JAVA_HOME/bin/java -cp $TMP_CP net.sourceforge.squirrel_sql.client.Main -loggingConfigFile=log4j.properties
fi
