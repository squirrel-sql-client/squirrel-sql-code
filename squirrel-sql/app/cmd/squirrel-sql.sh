#! /bin/sh
# $Id: squirrel-sql.sh,v 1.3 2001-12-23 21:45:56 colbell Exp $
TMP_CP=lib/squirrel-sql.jar:lib/fw.jar:lib/nanoxml-2.1.jar:lib/log4j.jar:$CLASSPATH 
if [ `uname -s` = "CYGWIN_NT-5.0" ]; then
    $JAVA_HOME/bin/java -cp `cygpath -w -p $TMP_CP` net.sourceforge.squirrel_sql.client.Main
else
  $JAVA_HOME/bin/java -cp $TMP_CP net.sourceforge.squirrel_sql.client.Main
fi
