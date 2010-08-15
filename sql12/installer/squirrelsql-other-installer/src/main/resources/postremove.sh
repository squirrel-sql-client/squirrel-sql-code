#! /bin/bash

if [ "$RPM_INSTALL_PREFIX" != "" ]; then
    rm -rf $RPM_INSTALL_PREFIX/SQuirreLSQLClient
else
    rm -rf /opt/SQuirreLSQLClient
fi