#! /bin/sh
# $Id: squirrel-sql.sh,v 1.2 2001-11-19 18:17:24 placson Exp $
TMP_CP=lib/squirrel-sql.jar:lib/fw.jar:lib/nanoxml-2.1.jar:$CLASSPATH 
if [ `uname -s` = "CYGWIN_NT-5.0" ]; then
    $JAVA_HOME/bin/java -cp `cygpath -w -p $TMP_CP` net.sourceforge.squirrel_sql.client.Main
else
  $JAVA_HOME/bin/java -cp $TMP_CP net.sourceforge.squirrel_sql.client.Main
fi
