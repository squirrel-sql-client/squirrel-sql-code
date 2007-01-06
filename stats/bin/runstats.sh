
# A simple script for running StatCVS on the SQuirreL source code
# This needs to have access to a CVS sandbox and network access to
# the CVS repository. When this script is finished running, load the
# file 'index.html' in STATS_DIR in your browser. Note: This will 
# bring down the latest version of files you have checked out so 
# if you don't want to merge changes into your local copies, you
# should probably do a fresh checkout and create a new sandbox.
#
# author: Rob Manning 20070106
#

# This should point to the sql12 directory
PROJ_DIR=../../

echo "Updating local CVS sandbox"

cd $PROJ_DIR
cvs -Q update -d -P

echo "Running CVS log command"

cvs -Q log > sql12.log

echo "Running statistics - ignore any warnings about local files out of sync with server"

# The output of this script is a bunch of files. This is where 
# they are placed
STATS_DIR=./stats/build

rm -rf $STATS_DIR/*

java -jar ./stats/lib/statcvs.jar -output-dir $STATS_DIR sql12.log `pwd`

rm sql12.log

cd ../build

firefox index.html

