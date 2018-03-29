#! /bin/sh

# This function sets a global variable named "CP" to a command-path separated list of jars located beneath the
# specified folder.  If the specified folder contains a lib directory, then jars beneath the lib folder are
# @ added as well as the squirrel-sql.jar file located in the directory specified as the first argument to
# this function.
buildCPFromDir()
{
	CP=""
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
IZPACK_JAVA_HOME=%JAVA_HOME

# We detect the java executable to use according to the following algorithm:
#
# 1. If it is located in JAVA_HOME, then we use that; or
# 2. If the one used by the IzPack installer is available then use that, otherwise
# 3. Use the java that is in the command path.
#


if [ -d "$IZPACK_JAVA_HOME" -a -x "$IZPACK_JAVA_HOME/bin/java" ]; then
	JAVA_HOME="$IZPACK_JAVA_HOME"
fi

# Are we running within Cygwin on some version of Windows or on Mac OS X?
cygwin=false;
case "`uname -s`" in
	CYGWIN*)
		cygwin=true
		;;
esac

# SQuirreL home. This is the plain zip version of squirrel-sql.sh, so the installer isn't run.  In this case
# the script cannot be modified by the installer to hard-code the install path.  We prefer to specify squirrel
# home as an absolute path, so that the command will work when exec'd from any location. So we attempt to
# detect the absolute path using dirname "$0", which should work in most cases.
SQUIRREL_CLI_HOME=$(dirname "$0")

# SQuirreL home in Unix format.
if $cygwin ; then
        UNIX_STYLE_HOME=`cygpath "$SQUIRREL_CLI_HOME"`
else
        UNIX_STYLE_HOME="$SQUIRREL_CLI_HOME"
fi

cd "$UNIX_STYLE_HOME"



# Check to see if the JVM meets the minimum required to run SQuirreL and inform the user if not and skip
# launch.  versioncheck.jar is a special jar file which has been compiled with javac version 1.2.2, which
# should be able to be run by that version or higher. The arguments to JavaVersionChecker below specify the
# minimum acceptable version (first arg) and any other acceptable subsequent versions.  <MAJOR>.<MINOR> should
# be all that is necessary for the version form.
$JAVA_HOME/bin/java -cp "$UNIX_STYLE_HOME/../lib/versioncheck.jar" JavaVersionChecker 1.8 9 10
if [ "$?" != "0" ]; then
  exit
fi

# Build a command-path separated list of installed jars from the lib folder and squirrel-sql.jar
buildCPFromDir "$UNIX_STYLE_HOME/.."
TMP_CP=$CP

# Now add the system classpath to the classpath. If running
# Cygwin we also need to change the classpath to Windows format.
if $cygwin ; then
        TMP_CP=`cygpath -w -p $TMP_CP`
        TMP_CP=$TMP_CP';'$CLASSPATH
else
        TMP_CP=$TMP_CP:$CLASSPATH
fi


# Now, pickup all jars once again from the installation and lib directories. The variable "CP" is assigned this value.
buildCPFromDir "$UNIX_STYLE_HOME"


# Launch SQuirreL CLI
if [ $# == 0 ]; then
   echo "Entering Java 9 JShell based mode. JAVA 9 is required."
   export _JAVA_OPTIONS="-Dsquirrel.home='$SQUIRREL_CLI_HOME'/.."
   $JAVA_HOME/bin/jshell --class-path "$TMP_CP"  "$UNIX_STYLE_HOME"/startsquirrelcli.jsh
elif [ $# == 2 ] && [ $1 == "-userdir" ]; then
   echo "Entering Java 9 JShell based mode. JAVA 9 is required."
   export _JAVA_OPTIONS="-Dsquirrel.home='$SQUIRREL_CLI_HOME'/.. -Dsquirrel.userdir='$2'"
   $JAVA_HOME/bin/jshell --class-path "$TMP_CP"  "$UNIX_STYLE_HOME"/startsquirrelcli.jsh
else
   $JAVA_HOME/bin/java -cp "$TMP_CP" -Dsquirrel.home="$SQUIRREL_CLI_HOME"/.. net.sourceforge.squirrel_sql.client.cli.SquirrelBatch "$1" "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9" "${10}" "${11}" "${12}" "${13}" "${14}" "${15}" "${16}" "${17}"
fi


