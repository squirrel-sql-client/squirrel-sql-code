#!/bin/sh

# Installation script for izPack
# Version: $Id: izpack.sh,v 1.1 2001-11-20 08:35:28 placson Exp $
# --*-shellscript-*--

cd e:/downloads/izpack/installed/bin

LCL_SQL=d:/cygwin/home/Administrator/src/squirrel
LCL_DIST=d:/cygwin/home/Administrator/src/squirrel

$JAVA_HOME/bin/java -jar "../lib/compiler.jar" $LCL_SQL/src/resources/izpack/izpack-complete.xml -b $LCL_SQL -k standard-kunststoff -o $LCL_DIST/squirrel-sql-dist.jar

#d:\apps\jdk13\bin\java -jar "../lib/compiler.jar" %LCL_SQL%\build\izpack-basic.xml -b %LCL_SQL% -k standard-kunststoff -o %LCL_DIST%\squirrel-sql-1.1alpha1\squirrel-sql-1.1alpha1-install-basic.jar

