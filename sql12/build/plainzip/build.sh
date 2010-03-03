#! /bin/bash

export VERSION=3.1

export INSTALL_JAR=/home/manningr/squirrel-sql-$VERSION-install.jar



rm -f *.gz
rm -rf tmp
mkdir tmp
rm *.xml

perl -p -e 's/\@squirrel_version\@/$ENV{VERSION}/g' auto_install_base.xml.template > auto_install_base.xml
perl -p -e 's/\@squirrel_version\@/$ENV{VERSION}/g' auto_install_standard.xml.template > auto_install_standard.xml
perl -p -e 's/\@squirrel_version\@/$ENV{VERSION}/g' auto_install_optional.xml.template > auto_install_optional.xml

java -jar $INSTALL_JAR auto_install_base.xml

cp squirrel-sql.sh "tmp/squirrel-sql-$VERSION/"

cd tmp

tar -cvf squirrel-sql-$VERSION-base.tar "squirrel-sql-$VERSION"

gzip squirrel-sql-$VERSION-base.tar

mv "squirrel-sql-$VERSION-base.tar.gz" ..

cd ..

rm -rf tmp
mkdir tmp

java -jar $INSTALL_JAR auto_install_standard.xml

cp squirrel-sql.sh "tmp/squirrel-sql-$VERSION/"

cd tmp

tar -cvf squirrel-sql-$VERSION-standard.tar "squirrel-sql-$VERSION"

gzip squirrel-sql-$VERSION-standard.tar

mv "squirrel-sql-$VERSION-standard.tar.gz" ..

cd ..

rm -rf tmp
mkdir tmp

java -jar $INSTALL_JAR auto_install_optional.xml

cp squirrel-sql.sh "tmp/squirrel-sql-$VERSION/"

cd tmp

tar -cvf squirrel-sql-$VERSION-optional.tar "squirrel-sql-$VERSION"

gzip squirrel-sql-$VERSION-optional.tar

mv "squirrel-sql-$VERSION-optional.tar.gz" ..

cd ..

rm -rf tmp
rm *.xml
