#! /bin/bash


cp ${project.build.directory}/test-classes/squirrel-sql.sh ${project.build.directory}/squirrel-sql-${squirrelsql.version}-base/squirrel-sql.sh
cp ${project.build.directory}/test-classes/squirrel-sql.sh ${project.build.directory}/squirrel-sql-${squirrelsql.version}-standard/squirrel-sql.sh
cp ${project.build.directory}/test-classes/squirrel-sql.sh ${project.build.directory}/squirrel-sql-${squirrelsql.version}-optional/squirrel-sql.sh


cd ${project.build.directory}

tar -cvf squirrel-sql-${squirrelsql.version}-base.tar squirrel-sql-${squirrelsql.version}-base
gzip squirrel-sql-${squirrelsql.version}-base.tar

tar -cvf squirrel-sql-${squirrelsql.version}-standard.tar squirrel-sql-${squirrelsql.version}-standard
gzip squirrel-sql-${squirrelsql.version}-standard.tar

tar -cvf squirrel-sql-${squirrelsql.version}-optional.tar squirrel-sql-${squirrelsql.version}-optional
gzip squirrel-sql-${squirrelsql.version}-optional.tar
