#! /bin/sh
# $Id: squirrel-sql.sh,v 1.5 2002-04-06 12:35:34 colbell Exp $
TMP_CP=squirrel-sql.jar:lib/fw.jar:lib/nanoxml-2.1.jar:lib/log4j.jar:$CLASSPATH 
if [ `uname -s` = "CYGWIN_NT-5.0" ]; then
    $JAVA_HOME/bin/java -cp `cygpath -w -p $TMP_CP` net.sourceforge.squirrel_sql.client.Main -loggingConfigFile=log4j.properties
else
  $JAVA_HOME/bin/java -cp $TMP_CP net.sourceforge.squirrel_sql.client.Main -loggingConfigFile=log4j.properties
fi
