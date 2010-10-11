#! /bin/sh

# This function sets a global variable named "CP" to a command-path separated list of jars located beneath the 
# specified folder.  If the specified folder contains a lib directory, then jars beneath the lib folder are 
# @ added as well as the squirrel-sql.jar file located in the directory specified as the first argument to 
# this function. 
buildCPFromDir()
{
	if [ -d "$1"/lib ]; then
		# First entry in classpath is the Squirrel application.
		CP="$1/squirrel-sql.jar"

		# Then add all library jars to the classpath.
		for a in "$1"/lib/*; do
			CP="$CP":"$a"
		done
	else 
		for a in "$1"/*; do
			CP="$CP":"$a"
		done
	fi
} 

# IZPACK_JAVA_HOME is filtered in by the IzPack installer when this script is installed
IZPACK_JAVA_HOME=/opt/jdk1.6.0_14_x64/jre

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
macosx=false;
case "`uname -s`" in
	CYGWIN*) 
		cygwin=true 
		;;
	Darwin*) 
		macosx=true
		;;
esac

# SQuirreL home.
if $macosx ; then
	SQUIRREL_SQL_HOME='/tmp/squirrel-sql-Snapshot-20100918_1720/Contents/Resources/Java'
else 
	SQUIRREL_SQL_HOME='/tmp/squirrel-sql-Snapshot-20100918_1720'
fi

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
$JAVACMD -cp "$UNIX_STYLE_HOME/lib/versioncheck.jar" JavaVersionChecker 1.6 1.7
if [ "$?" = "1" ]; then
	exit
fi

# Build a command-path separated list of installed jars from the lib folder and squirrel-sql.jar
buildCPFromDir "$UNIX_STYLE_HOME"
TMP_CP=$CP

# Set the update app's classpath to use jars in download area first, then the installed jars
buildCPFromDir "$UNIX_STYLE_HOME/update/downloads/core"
UPDATE_CP=$CP:$TMP_CP

# Now add the system classpath to the classpath. If running
# Cygwin we also need to change the classpath to Windows format.
if $cygwin ; then
	TMP_CP=`cygpath -w -p $TMP_CP`
	UPDATE_CP=`cygpath -w -p $UPDATE_CP`
	TMP_CP=$TMP_CP';'$CLASSPATH
	UPDATE_CP=$UPDATE_CP';'$CLASSPATH
else
	TMP_CP=$TMP_CP:$CLASSPATH
	UPDATE_CP=$UPDATE_CP:$CLASSPATH
fi

if $macosx ; then
	# Define mac-specific system properties if running on Mac OS X
	MACOSX_UPDATER_PROPS="-Dapple.laf.useScreenMenuBar=true -Dcom.apple.mrj.application.apple.menu.about.name=SQuirreLSQLUpdater"
	MACOSX_SQUIRREL_PROPS="-Dapple.laf.useScreenMenuBar=true -Dcom.apple.mrj.application.apple.menu.about.name=SQuirreLSQL"
	NATIVE_LAF_PROP="--native-laf"
fi

# Check for updates and prompt to apply if any are available
if [ -f "$UNIX_STYLE_HOME/update/downloads/core/squirrel-sql.jar" -a -f "$UNIX_STYLE_HOME/update/changeList.xml" ]; then
	$JAVACMD -cp "$UPDATE_CP" $MACOSX_UPDATER_PROPS -Dlog4j.defaultInitOverride=true -Dprompt=true net.sourceforge.squirrel_sql.client.update.gui.installer.PreLaunchUpdateApplication -l "$UNIX_STYLE_HOME/update-log4j.properties"
fi

if $macosx ; then
	# macosx provides unknown args to the script, causing SQuirreL to bail..
	SCRIPT_ARGS=""
else
	SCRIPT_ARGS="$1 $2 $3 $4 $5 $6 $7 $8 $9"
fi

# Now, pickup all jars once again from the installation and lib directories in case any new jars were
# downloaded during the update process.  The variable "CP" is assigned this value.
buildCPFromDir "$UNIX_STYLE_HOME"

# Launch SQuirreL application
$JAVACMD -Xmx256m -cp "$CP" $MACOSX_SQUIRREL_PROPS net.sourceforge.squirrel_sql.client.Main --log-config-file "$UNIX_STYLE_HOME"/log4j.properties --squirrel-home "$UNIX_STYLE_HOME" $NATIVE_LAF_PROP $SCRIPT_ARGS
