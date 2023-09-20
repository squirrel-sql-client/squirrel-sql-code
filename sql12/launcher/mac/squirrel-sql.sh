#! /bin/sh -x

#############################################################################
# NOTE: If this script DOES NOT WORK search for the CHANGE_HERE comment below
#############################################################################



ABSPATH=$(cd "$(dirname "$0")"; pwd)


# Note: Windows and Linux/Unix scripts use the IzPack built in variable ($ or %)JAVA_HOME here.
# The variable ($ or %)JAVA_HOME contains the JDK/JRE the IzPack installer was started with.
IZPACK_JAVA_HOME="/Library/Internet Plug-Ins/JavaAppletPlugin.plugin/Contents/Home"

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
macosx=false;
case "`uname -s`" in
	Darwin*)
		macosx=true
		;;
esac

# SQuirreL home.
if $macosx ; then


##################################################################################################
# CHANGE_HERE
# Mac users are not able to agree upon which of the three lines below works correctly, see bugs 1287, 1321, 1310.
# Thus if you have problems running SQuirreL on IOS please try out the alternative lines.
##################################################################################################

#Alternative 1
SQUIRREL_SQL_HOME=`dirname "$0"`/Contents/Resources/Java

#Alternative 2
#SQUIRREL_SQL_HOME=$(echo $ABSPATH | grep -o '^/.*/Contents/')Resources/Java

#Alternative 3
#SQUIRREL_SQL_HOME=`dirname "$0"`/../Resources/Java

#Alternative 4 (thanks to Frank Kemmer)
#APP_MacOS_DIR=$(dirname "$0")
#APP_CONTENTS_DIR=$(dirname "${APP_MacOS_DIR}")
#SQUIRREL_SQL_HOME="${APP_CONTENTS_DIR}/Resources/Java"
#echo "### SQUIRREL_SQL_HOME: [${SQUIRREL_SQL_HOME}]"




if [ ! -d "$SQUIRREL_SQL_HOME" ]; then
    # We assume that this is the ZIP file extracted on MacOS,
    # so, fall-back to the defult path
    SQUIRREL_SQL_HOME=`dirname "$0"`
fi
else 
    SQUIRREL_SQL_HOME='/Applications/SQuirreLSQL.app'
fi

# SQuirreL home in Unix format.
UNIX_STYLE_HOME="$SQUIRREL_SQL_HOME"

cd "$UNIX_STYLE_HOME"

# Check to see if the JVM meets the minimum required to run SQuirreL and inform the user if not and skip 
# launch.  versioncheck.jar is a special jar file which has been compiled with javac version 1.2.2, which 
# should be able to be run by that version or higher. The arguments to JavaVersionChecker below specify the 
# minimum acceptable version (first arg) and any other acceptable subsequent versions.  <MAJOR>.<MINOR> should 
# be all that is necessary for the version form. 
"$JAVACMD" -cp "$UNIX_STYLE_HOME/../Resources/Java/lib/versioncheck.jar" JavaVersionChecker 11 12 13 14 15 16 17 18 19 20 21
if [ "$?" != "0" ]; then
	exit
fi

#CP="$UNIX_STYLE_HOME"/squirrel-sql.jar:"$UNIX_STYLE_HOME"/lib/*
CP="$UNIX_STYLE_HOME"/../Resources/Java/squirrel-sql.jar:"$UNIX_STYLE_HOME"/../Resources/Java/lib/*

# Define mac-specific system properties if running on Mac OS X
MACOSX_SQUIRREL_PROPS="-Dapple.laf.useScreenMenuBar=true -Dcom.apple.mrj.application.apple.menu.about.name=SQuirreLSQL -Dapple.awt.application.name=SQuirreLSQL"
NATIVE_LAF_PROP="--native-laf"


if $macosx ; then
	# macosx provides unknown args to the script, causing SQuirreL to bail..
	SCRIPT_ARGS=""
else
	SCRIPT_ARGS="$1 $2 $3 $4 $5 $6 $7 $8 $9"
fi

# Launch SQuirreL application
"$JAVACMD" -cp "$CP" $SQUIRREL_SQL_OPTS $MACOSX_SQUIRREL_PROPS -splash:"$SQUIRREL_SQL_HOME/icons/splash.jpg" net.sourceforge.squirrel_sql.client.Main --squirrel-home "$UNIX_STYLE_HOME" $NATIVE_LAF_PROP $SCRIPT_ARGS