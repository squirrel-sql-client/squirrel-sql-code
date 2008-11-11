
As of November 11, 2008, the build.xml file (sql12/build/build.xml) references a findbugs custom task, for the
purpose of running findbugs on the code base and incorporating the report into the Hudson continuous 
integration build server (http://www.squirrel-sql.org:8080/).  In order for this to work properly in your 
local build environment, you should download the findbugs distribution 
(http://findbugs.sourceforge.net/downloads.html), extract it and copy the findbugs-ant.jar file into your 
$ANT_HOME/lib directory. You will also need to update build.properties to point to the location of the 
extracted findbugs software.