export INSTALL_JAR=/home/manningr/squirrel_2_5_1_build/squirrel-sql-dist/squirrel-sql-2.5.1-install.jar

export VERSION=2.5.1

rm -f *.gz
rm -rf tmp
mkdir tmp

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
