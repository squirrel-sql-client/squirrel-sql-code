#!/bin/sh

# Installation script for izPack
# Version: $Id: izpack.sh,v 1.3 2001-11-20 09:39:37 placson Exp $
# --*-shellscript-*--


LCL_SQL=../../..
LCL_DIST=$LCL_SQL
IZPACK_BIN=$LCL_SQL/thirdparty/izpack/bin
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

cd $IZPACK_BIN

$JAVA_HOME/bin/java -jar $COMPILER_LIB $LCL_SQL/src/resources/izpack/izpack-complete-buildmagic.xml -b $LCL_SQL -k standard-kunststoff -o $LCL_DIST/squirrel-sql-dist.jar


