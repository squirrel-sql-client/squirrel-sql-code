#! /bin/sh
[ $JAVA_HOME ] && JAVA=$JAVA_HOME/bin/java || JAVA=java

for a in squirrel-sql.jar lib/*; do
    TMP_CP="$TMP_CP:$a"
done

if [ -n "$(echo `uname -s` | grep '^CYGWIN')" ]; then
    TMP_CP=`cygpath -w -p $TMP_CP`
	TMP_CP=$TMP_CP';'$CLASSPATH
else
    TMP_CP=$TMP_CP:$CLASSPATH
fi

$JAVA -cp $TMP_CP net.sourceforge.squirrel_sql.client.Main -loggingConfigFile=log4j.properties

