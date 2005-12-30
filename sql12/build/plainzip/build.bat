set PLAIN_ZIP_DIR=c:\home\projects\squirrel-sql\sql12\build\plainzip

set INSTALL_JAR=c:\tmp\squirrel-sql-2.1final-install.jar

set VERSION=2.1final


rm -f *.zip
rm -rf tmp
mkdir tmp

java -jar %INSTALL_JAR% auto_install_base.xml

copy squirrel-sql.bat "tmp\SQuirreL SQL Client\"

cd tmp

jar -cvf squirrel-sql-%VERSION%-base.zip "SQuirreL SQL Client"

mv squirrel-sql-%VERSION%-base.zip ..

cd ..

rm -rf tmp
mkdir tmp


java -jar %INSTALL_JAR% auto_install_standard.xml

copy squirrel-sql.bat "tmp\SQuirreL SQL Client\"

cd tmp

jar -cvf squirrel-sql-%VERSION%-standard.zip "SQuirreL SQL Client"

mv squirrel-sql-%VERSION%-standard.zip ..

cd ..

rm -rf tmp
