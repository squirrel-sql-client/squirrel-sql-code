#! /bin/sh

[ ${JAVA_HOME} ] && JAVA=${JAVA_HOME}/bin/java || [ %JAVA_HOME ]  && JAVA=%JAVA_HOME/bin/java  || JAVA=java

# Are we running within Cygwin on some version of Windows?
cygwin=false;
macosx=false;

case "`uname -s`" in
	CYGWIN*) cygwin=true ;;
esac
case "`uname -s`" in
        Darwin) macosx=true;;
esac

# SQuirreL home.
if $macosx ; then
        SQUIRREL_SQL_HOME='%INSTALL_PATH/Contents/Resources/Java'
else 
        SQUIRREL_SQL_HOME='%INSTALL_PATH'
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
# should be able to be run by that version of higher. The arguments to JavaVersionChecker below specify the 
# minimum acceptable version (first arg) and any other acceptable subsequent versions.  <MAJOR>.<MINOR> should 
# be all that is necessary for the version form. 
$JAVA -cp "$UNIX_STYLE_HOME/lib/versioncheck.jar" JavaVersionChecker 1.6 1.7
if [ "$?" = "1" ]; then
  exit
fi

# First entry in classpath is the Squirrel application.
TMP_CP="$UNIX_STYLE_HOME/squirrel-sql.jar"

# Then add all library jars to the classpath.
for a in "$UNIX_STYLE_HOME"/lib/*; do
	TMP_CP="$TMP_CP":"$a";
done

# Set the update app's classpath to use jars in download area first, then the installed jars
UPDATE_CP=$TMP_CP
for a in "$UNIX_STYLE_HOME"/update/downloads/core/*; do
    UPDATE_CP="$a":"$UPDATE_CP"
done


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
        $JAVA -cp "$UPDATE_CP" $MACOSX_UPDATER_PROPS -Dlog4j.defaultInitOverride=true -Dprompt=true net.sourceforge.squirrel_sql.client.update.gui.installer.PreLaunchUpdateApplication -l "$UNIX_STYLE_HOME/update-log4j.properties"
fi

if $macosx ; then
        # macosx provides unknown args to the script, causing SQuirreL to bail..
        SCRIPT_ARGS=""
else
        SCRIPT_ARGS="$1 $2 $3 $4 $5 $6 $7 $8 $9"
fi

# Launch SQuirreL application
$JAVA -Xmx256m -cp "$TMP_CP" $MACOSX_SQUIRREL_PROPS net.sourceforge.squirrel_sql.client.Main --log-config-file "$UNIX_STYLE_HOME"/log4j.properties --squirrel-home "$UNIX_STYLE_HOME" $NATIVE_LAF_PROP $SCRIPT_ARGS
