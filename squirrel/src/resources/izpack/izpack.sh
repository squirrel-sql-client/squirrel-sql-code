#!/bin/sh

# Installation script for izPack
# Version: $Id: izpack.sh,v 1.2 2001-11-20 09:34:42 placson Exp $
# --*-shellscript-*--

LCL_SQL=d:/cygwin/home/Administrator/src/squirrel
LCL_DIST=$LCL_SQL
COMPILER_LIB=$LCL_SQL/thirdparty/izpack/lib/compiler.jar

rm -fr $LCL_DIST/output
rm -fr $LCL_DIST/output/dist
rm -fr $LCL_DIST/output/dist/lib
rm -fr $LCL_DIST/output/dist/doc
rm -fr $LCL_DIST/output/dist/src

mkdir $LCL_DIST/output
mkdir $LCL_DIST/output/dist
mkdir $LCL_DIST/output/dist/lib
mkdir $LCL_DIST/output/dist/doc
mkdir $LCL_DIST/output/dist/src

$JAVA_HOME/bin/java -jar $COMPILER_LIB $LCL_SQL/src/resources/izpack/izpack-complete-buildmagic.xml -b $LCL_SQL -k standard-kunststoff -o $LCL_DIST/squirrel-sql-dist.jar


