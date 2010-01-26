#!/bin/bash

files=$@

for file in $files
do
	ext="${file/*./}"
	if [ "$ext" == "bte" ] || [ "$ext" == "html" ] 
	then
		mode="sgml"
	else
		mode="url"
	fi
	if [ "$ext" != "java" ] || [ ! -e "${file/java/lex}" ]
	then
		cp "$file" temp
		aspell check --mode=$mode -x -p ./syntax.dict temp
		if [ "`diff "temp" "$file"`" ] 
		then
			mv temp "$file"
		fi
	fi
done
head -1 "syntax.dict" > temp
tail +2 "syntax.dict" | sort | uniq >> temp
if [ "`diff "temp" "syntax.dict"`" ] 
then
	mv temp "syntax.dict"
fi
rm -f temp temp.bak
