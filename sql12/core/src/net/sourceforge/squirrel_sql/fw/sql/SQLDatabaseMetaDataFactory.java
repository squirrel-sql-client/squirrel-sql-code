package net.sourceforge.squirrel_sql.fw.sql;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.databasemetadata.SQLDatabaseMetaData;

import java.util.HashMap;
import java.util.Map;

public class SQLDatabaseMetaDataFactory {
	
	private static Map<DialectType, ISQLDatabaseMetaDataFactory> registeredOverrides = new HashMap<>();

	public static SQLDatabaseMetaData fetchMeta(DialectType type, ISQLConnection conn)
	{
		if(registeredOverrides.containsKey(type))
		{
			return registeredOverrides.get(type).fetchMeta(conn);
		}
		return new SQLDatabaseMetaData(conn);
	}
	
	public static void registerOverride(DialectType type, ISQLDatabaseMetaDataFactory factory) {
		registeredOverrides.put(type, factory);
	}

}
