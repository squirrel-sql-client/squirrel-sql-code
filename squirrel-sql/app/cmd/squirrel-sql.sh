#! /bin/sh
[ $JAVA_HOME ] && JAVA=$JAVA_HOME/bin/java || JAVA=java

SQUIRREL_SQL_HOME="$INSTALL_PATH"

for a in $SQUIRREL_SQL_HOME/squirrel-sql.jar $SQUIRREL_SQL_HOME/lib/*; do
    TMP_CP="$TMP_CP:$a"
done

cygwin=false;
case "`uname -s`" in
    CYGWIN*) cygwin=true ;;
esac

if $cygwin ; then
    TMP_CP=`cygpath -w -p $TMP_CP`
    TMP_CP=$TMP_CP';'$CLASSPATH
else
    TMP_CP=$TMP_CP:$CLASSPATH
fi

$JAVA -cp $TMP_CP net.sourceforge.squirrel_sql.client.Main -loggingConfigFile=$SQUIRREL_SQL_HOME/log4j.properties
