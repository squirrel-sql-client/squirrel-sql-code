
export VERSION=3.1.0

export INSTALL_JAR=/home/manningr/squirrel_builds/squirrel_$VERSION_build/squirrel-sql-dist/squirrel-sql-$VERSION-install.jar



rm -f *.gz
rm -rf tmp
mkdir tmp

perl -pi -e 's/\@squirrel-version\@/$VERSION/' auto_install_base.xml
perl -pi -e 's/\@squirrel-version\@/$VERSION/' auto_install_standard.xml
perl -pi -e 's/\@squirrel-version\@/$VERSION/' auto_install_optional.xml

java -jar $INSTALL_JAR auto_install_base.xml

cp squirrel-sql.sh "tmp/SQuirreL SQL Client/"

cd tmp

tar -cvf squirrel-sql-$VERSION-base.tar "SQuirreL SQL Client"

gzip squirrel-sql-$VERSION-base.tar

mv "squirrel-sql-$VERSION-base.tar.gz" ..

cd ..

rm -rf tmp
mkdir tmp

java -jar $INSTALL_JAR auto_install_standard.xml

cp squirrel-sql.sh "tmp/SQuirreL SQL Client/"

cd tmp

tar -cvf squirrel-sql-$VERSION-standard.tar "SQuirreL SQL Client"

gzip squirrel-sql-$VERSION-standard.tar

mv "squirrel-sql-$VERSION-standard.tar.gz" ..

cd ..

rm -rf tmp
mkdir tmp

java -jar $INSTALL_JAR auto_install_optional.xml

cp squirrel-sql.sh "tmp/SQuirreL SQL Client/"

cd tmp

tar -cvf squirrel-sql-$VERSION-optional.tar "SQuirreL SQL Client"

gzip squirrel-sql-$VERSION-optional.tar

mv "squirrel-sql-$VERSION-optional.tar.gz" ..

cd ..

rm -rf tmp
