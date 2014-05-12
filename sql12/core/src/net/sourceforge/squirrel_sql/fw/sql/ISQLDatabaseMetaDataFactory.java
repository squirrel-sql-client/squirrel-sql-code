package net.sourceforge.squirrel_sql.fw.sql;

public interface ISQLDatabaseMetaDataFactory {

	SQLDatabaseMetaData fetchMeta(ISQLConnection conn);

}
