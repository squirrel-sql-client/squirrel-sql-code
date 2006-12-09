set PLAIN_ZIP_DIR=c:\home\projects\squirrel-sql\sql12\build\plainzip

set INSTALL_JAR=c:\tmp\squirrel-sql-2.3.2-install.jar

set VERSION=2.3.2


rm -f *.zip
rm -rf tmp
mkdir tmp

java -jar %INSTALL_JAR% auto_install_base.xml

cd tmp

jar -cvf squirrel-sql-%VERSION%-base.zip "SQuirreL SQL Client"

mv squirrel-sql-%VERSION%-base.zip ..

cd ..

rm -rf tmp
mkdir tmp


java -jar %INSTALL_JAR% auto_install_standard.xml

cd tmp

jar -cvf squirrel-sql-%VERSION%-standard.zip "SQuirreL SQL Client"

mv squirrel-sql-%VERSION%-standard.zip ..

cd ..

rm -rf tmp
mkdir tmp

java -jar %INSTALL_JAR% auto_install_optional.xml

cd tmp

jar -cvf squirrel-sql-%VERSION%-optional.zip "SQuirreL SQL Client"

mv squirrel-sql-%VERSION%-optional.zip ..

cd ..

rm -rf tmp
