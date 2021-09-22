package net.sourceforge.squirrel_sql.fw.timeoutproxy;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@FunctionalInterface
public interface DatabaseMetaDataProvider
{
   DatabaseMetaData getDataBaseMetaData() throws SQLException;;
}
