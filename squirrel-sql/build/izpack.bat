pushd \apps\izpack\bin
set LCL_SQL=\src\squirrel-sql\squirrel-sql
set LCL_DIST=\src\squirrel-sql\squirrel-sql-dist
@rem d:\apps\jdk13\bin\java -jar "../lib/compiler.jar" %LCL_SQL%\build\izpack-complete.xml -b %LCL_SQL% -k standard-kunststoff -o %LCL_DIST%\squirrel-sql-1.1beta2\squirrel-sql-1.1beta2-install-complete.jar
d:\apps\jdk13\bin\java -jar "../lib/compiler.jar" %LCL_SQL%\build\izpack-basic.xml -b %LCL_SQL% -k standard-kunststoff -o %LCL_DIST%\squirrel-sql-1.1beta2\squirrel-sql-1.1beta2-install.jar
popd
