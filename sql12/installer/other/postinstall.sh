#! /bin/bash

if [ "$RPM_INSTALL_PREFIX" != "" ]; then
    sed -i  "s:/opt:$RPM_INSTALL_PREFIX:g" $RPM_INSTALL_PREFIX/SQuirreLSQLClient/squirrel-sql.sh 
    chmod 777 $RPM_INSTALL_PREFIX/SQuirreLSQLClient
else 
    chmod 777 /opt/SQuirreLSQLClient
fi
