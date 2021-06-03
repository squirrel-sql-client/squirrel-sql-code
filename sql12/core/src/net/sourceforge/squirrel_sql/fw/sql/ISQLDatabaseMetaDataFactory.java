package net.sourceforge.squirrel_sql.fw.sql;

import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;

public interface ISQLDatabaseMetaDataFactory {

	SQLDatabaseMetaData fetchMeta(ISQLConnection conn);

}
