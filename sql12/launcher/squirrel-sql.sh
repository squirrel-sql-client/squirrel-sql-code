#! /bin/sh


# IzPack replaces ($ or %)JAVA_HOME with the JDK/JRE IzPack installer was started with.
# I.e. is a built in variable of IzPack,
# see https://izpack.atlassian.net/wiki/spaces/IZPACK/pages/491572/Variables
# Note the shell script untypical %JAVA_HOME which is the one replaced by IzPack here.
export IZPACK_JAVA_HOME=%JAVA_HOME

# We detect the java executable to use according to the following algorithm:
#
# 1. If it is located in JAVA_HOME, then we use that; or
# 2. If the one used by the IzPack installer is available then use that, otherwise
# 3. Use the java that is in the command path.
# 
if [ -d "$JAVA_HOME" -a -x "$JAVA_HOME/bin/java" ]; then
	JAVACMD="$JAVA_HOME/bin/java"
elif [ -d "$IZPACK_JAVA_HOME" -a -x "$IZPACK_JAVA_HOME/bin/java" ]; then
	JAVACMD="$IZPACK_JAVA_HOME/bin/java"
else
	JAVACMD=java
fi

# Are we running within Cygwin on some version of Windows or on Mac OS X?
cygwin=false;
case "`uname -s`" in
	CYGWIN*) 
		cygwin=true 
		;;
esac

# SQuirreL home.
SQUIRREL_SQL_HOME='%INSTALL_PATH'

# SQuirreL home in Unix format.
if $cygwin ; then
	UNIX_STYLE_HOME=`cygpath "$SQUIRREL_SQL_HOME"`
else
	UNIX_STYLE_HOME="$SQUIRREL_SQL_HOME"
fi

cd "$UNIX_STYLE_HOME"

# Check to see if the JVM meets the minimum required to run SQuirreL and inform the user if not and skip 
# launch.  versioncheck.jar is a special jar file which has been compiled with javac version 1.2.2, which 
# should be able to be run by that version or higher. The arguments to JavaVersionChecker below specify the 
# minimum acceptable version (first arg) and any other acceptable subsequent versions.  <MAJOR>.<MINOR> should 
# be all that is necessary for the version form. 
$JAVACMD -cp "$UNIX_STYLE_HOME/lib/versioncheck.jar" JavaVersionChecker 11 12 13 14 15 16 17 18 19 20 21
if [ "$?" != "0" ]; then
	exit
fi



SCRIPT_ARGS="$1 $2 $3 $4 $5 $6 $7 $8 $9"

if $cygwin ; then
    CP="$UNIX_STYLE_HOME"/squirrel-sql.jar;"$UNIX_STYLE_HOME"/lib/*
else
    CP="$UNIX_STYLE_HOME"/squirrel-sql.jar:"$UNIX_STYLE_HOME"/lib/*
fi

# Launch SQuirreL application
exec "$JAVACMD" -cp "$CP" $SQUIRREL_SQL_OPTS -splash:"$SQUIRREL_SQL_HOME/icons/splash.jpg" net.sourceforge.squirrel_sql.client.Main --squirrel-home "$UNIX_STYLE_HOME" $NATIVE_LAF_PROP $SCRIPT_ARGS
