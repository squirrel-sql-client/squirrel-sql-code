#! /bin/sh
[ ${JAVA_HOME} ] && JAVA=${JAVA_HOME}/bin/java || JAVA=java

# Are we running within Cygwin on some version of Windows?
cygwin=false;
case "`uname -s`" in
	CYGWIN*) cygwin=true ;;
esac

# Squirrel home.
SQUIRREL_SQL_HOME="$INSTALL_PATH"

# SQuirreL home in Unix format.
if $cygwin ; then
	UNIX_STYLE_HOME=`cygpath "$SQUIRREL_SQL_HOME"`
else
	UNIX_STYLE_HOME=$SQUIRREL_SQL_HOME
fi

# First entry in classpath is the Squirrel application.
TMP_CP=$UNIX_STYLE_HOME/squirrel-sql.jar

# Then add all library jars to the classpath.
IFS=""
for a in $UNIX_STYLE_HOME/lib/*; do
	TMP_CP="$TMP_CP":"$a";
done

# Now add the system classpath to our classpath. If running
# Cygwin we also need to change our classpath to Windows format.
if $cygwin ; then
	TMP_CP=`cygpath -w -p $TMP_CP`
	TMP_CP=$TMP_CP';'$CLASSPATH
else
	TMP_CP=$TMP_CP:$CLASSPATH
fi

$JAVA -cp $TMP_CP net.sourceforge.squirrel_sql.client.Main -loggingConfigFile=$SQUIRREL_SQL_HOME/log4j.properties -squirrelHome=$SQUIRREL_SQL_HOME

