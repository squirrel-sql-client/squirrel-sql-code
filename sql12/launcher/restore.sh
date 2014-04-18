#! /bin/sh

[ ${JAVA_HOME} ] && JAVA=${JAVA_HOME}/bin/java || [ %JAVA_HOME ]  && JAVA=%JAVA_HOME/bin/java  || JAVA=java

# Are we running within Cygwin on some version of Windows?
cygwin=false;
case "`uname -s`" in
        CYGWIN*) cygwin=true ;;
esac

# Squirrel home.
SQUIRREL_SQL_HOME='%INSTALL_PATH'

# SQuirreL home in Unix format.
if $cygwin ; then
        UNIX_STYLE_HOME=`cygpath "$SQUIRREL_SQL_HOME"`
else
        UNIX_STYLE_HOME=$SQUIRREL_SQL_HOME
fi


set basedir=%~f0
:strip
set removed=%basedir:~-1%
set basedir=%basedir:~0,-1%
if NOT "%removed%"=="\" goto strip
set SQUIRREL_SQL_HOME=%basedir%

@rem launch updater in "restore" mode
SET UPDATE_PARMS=--log-config-file "%SQUIRREL_SQL_HOME%\update-log4j.properties" --squirrel-home "%SQUIRREL_SQL_HOME%"
"%LOCAL_JAVA%w" -cp %UPDATE_CP% -Dlog4j.defaultInitOverride=true -Drestore=true net.sourceforge.squirrel_sql.client.update.gui.installer.PreLaunchUpdateApplication %UPDATE_PARAMS%


