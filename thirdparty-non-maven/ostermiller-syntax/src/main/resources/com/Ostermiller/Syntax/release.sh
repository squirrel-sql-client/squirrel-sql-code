#!/bin/bash

size=`ls -lah syntax.jar | grep -oE '[0-9]+[A-Za-z]'`
if [ -z "`grep -i $size download.html`" ]
then
    echo "syntax.jar size is $size but download.html does not show that."
    exit 1
fi

FILES=$@
FILES=${FILES/package.html/} 
FILES=${FILES/web/} 
FILES=${FILES/javadoc/} 
FILES=${FILES/compile/} 
if [ "$FILES" ]
then
	echo Make: Uploading to web site: $FILES
	cp -r $FILES ~/ostermiller.org/www/syntax
fi
