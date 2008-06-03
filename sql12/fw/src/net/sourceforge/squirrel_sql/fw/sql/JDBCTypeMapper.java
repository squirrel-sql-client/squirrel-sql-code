package net.sourceforge.squirrel_sql.fw.sql;

/*
 * Copyright (C) 2002 David MacLean
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.ArrayList;

public class JDBCTypeMapper
{

	public static String[] getJdbcTypeList()
	{
		ArrayList<String> result = new ArrayList<String>();
		result.add(getJdbcTypeName(Types.ARRAY));
		result.add(getJdbcTypeName(Types.BOOLEAN));
		result.add(getJdbcTypeName(Types.BIGINT));
		result.add(getJdbcTypeName(Types.BINARY));
		result.add(getJdbcTypeName(Types.BIT));
		result.add(getJdbcTypeName(Types.BLOB));
		result.add(getJdbcTypeName(Types.CHAR));
		result.add(getJdbcTypeName(Types.CLOB));
		result.add(getJdbcTypeName(Types.DATALINK));
		result.add(getJdbcTypeName(Types.DATE));
		result.add(getJdbcTypeName(Types.DECIMAL));
		result.add(getJdbcTypeName(Types.DISTINCT));
		result.add(getJdbcTypeName(Types.DOUBLE));
		result.add(getJdbcTypeName(Types.FLOAT));
		result.add(getJdbcTypeName(Types.INTEGER));
		result.add(getJdbcTypeName(Types.JAVA_OBJECT));
		result.add(getJdbcTypeName(Types.LONGVARBINARY));
		result.add(getJdbcTypeName(Types.LONGVARCHAR));
		result.add(getJdbcTypeName(Types.NUMERIC));
		result.add(getJdbcTypeName(Types.NULL));
		result.add(getJdbcTypeName(Types.OTHER));
		result.add(getJdbcTypeName(Types.REAL));
		result.add(getJdbcTypeName(Types.REF));
		result.add(getJdbcTypeName(Types.SMALLINT));
		result.add(getJdbcTypeName(Types.STRUCT));
		result.add(getJdbcTypeName(Types.TIME));
		result.add(getJdbcTypeName(Types.TIMESTAMP));
		result.add(getJdbcTypeName(Types.TINYINT));
		result.add(getJdbcTypeName(Types.VARBINARY));
		result.add(getJdbcTypeName(Types.VARCHAR));
		return result.toArray(new String[result.size()]);
	}

	public static String getJdbcTypeName(int jdbcType)
	{
		String typeName = "";
		switch (jdbcType)
		{
		case Types.ARRAY:
			typeName = "ARRAY";
			break;
		case Types.BOOLEAN:
			typeName = "BOOLEAN";
			break;
		case Types.BIGINT:
			typeName = "BIGINT";
			break;
		case Types.BINARY:
			typeName = "BINARY";
			break;
		case Types.BIT:
			typeName = "BIT";
			break;
		case Types.BLOB:
			typeName = "BLOB";
			break;
		case Types.CHAR:
			typeName = "CHAR";
			break;
		case Types.CLOB:
			typeName = "CLOB";
			break;
		case Types.DATALINK:
			typeName = "DATALINK";
			break;
		case Types.DATE:
			typeName = "DATE";
			break;
		case Types.DECIMAL:
			typeName = "DECIMAL";
			break;
		case Types.DISTINCT:
			typeName = "DISTINCT";
			break;
		case Types.DOUBLE:
			typeName = "DOUBLE";
			break;
		case Types.FLOAT:
			typeName = "FLOAT";
			break;
		case Types.INTEGER:
			typeName = "INTEGER";
			break;
		case Types.JAVA_OBJECT:
			typeName = "JAVA_OBJECT";
			break;
		case Types.LONGVARBINARY:
			typeName = "LONGVARBINARY";
			break;
		case Types.LONGVARCHAR:
			typeName = "LONGVARCHAR";
			break;
		case Types.NULL:
			typeName = "NULL";
			break;
		case Types.NUMERIC:
			typeName = "NUMERIC";
			break;
		case Types.OTHER:
			typeName = "OTHER";
			break;
		case Types.REAL:
			typeName = "REAL";
			break;
		case Types.REF:
			typeName = "REF";
			break;
		case Types.SMALLINT:
			typeName = "SMALLINT";
			break;
		case Types.STRUCT:
			typeName = "STRUCT";
			break;
		case Types.TIME:
			typeName = "TIME";
			break;
		case Types.TIMESTAMP:
			typeName = "TIMESTAMP";
			break;
		case Types.TINYINT:
			typeName = "TINYINT";
			break;
		case Types.VARBINARY:
			typeName = "VARBINARY";
			break;
		case Types.VARCHAR:
			typeName = "VARCHAR";
			break;
		}
		return typeName;
	}
	
	/**
	 * Returns the java.sql.Types.java type of the specified type name.  If  the type is not found then 
	 * defaultVal is returned.
	 * 
	 * @param jdbcTypeName the name to lookup.
	 * 
	 * @return the type code
	 */
	public static int getJdbcType(String jdbcTypeName, int defaultVal) {
		
		if (jdbcTypeName == null) 
		{ 
			return Types.NULL; 
		} 
		else if ("ARRAY".equals(jdbcTypeName))
		{
			return Types.ARRAY;
		} 
		else if ("BIGINT".equals(jdbcTypeName))
		{
			return Types.BIGINT;
		} 
		else if ("BINARY".equals(jdbcTypeName))
		{
			return Types.BINARY;
		}
		else if ("BIT".equals(jdbcTypeName))
		{
			return Types.BIT;
		}
		else if ("BLOB".equals(jdbcTypeName))
		{
			return Types.BLOB;
		}
		else if ("BOOLEAN".equals(jdbcTypeName))
		{
			return Types.BOOLEAN;
		}
		else if ("CHAR".equals(jdbcTypeName))
		{
			return Types.CHAR;
		}
		else if ("CLOB".equals(jdbcTypeName))
		{
			return Types.CLOB;
		}
		else if ("DATE".equals(jdbcTypeName))
		{
			return Types.DATE;
		}
		else if ("DATALINK".equals(jdbcTypeName))
		{
			return Types.DATALINK;
		}
		else if ("DECIMAL".equals(jdbcTypeName))
		{
			return Types.DECIMAL;
		}
		else if ("DISTINCT".equals(jdbcTypeName))
		{
			return Types.DISTINCT;
		}
		else if ("DOUBLE".equals(jdbcTypeName))
		{
			return Types.DOUBLE;
		}
		else if ("FLOAT".equals(jdbcTypeName))
		{
			return Types.FLOAT;
		}
		else if ("INTEGER".equals(jdbcTypeName))
		{
			return Types.INTEGER;
		}
		else if ("JAVA_OBJECT".equals(jdbcTypeName))
		{
			return Types.JAVA_OBJECT;
		}
		else if ("LONGVARBINARY".equals(jdbcTypeName))
		{
			return Types.LONGVARBINARY;
		}
		else if ("LONGVARCHAR".equals(jdbcTypeName))
		{
			return Types.LONGVARCHAR;
		}
		else if ("NULL".equals(jdbcTypeName))
		{
			return Types.NULL;
		}
		else if ("NUMERIC".equals(jdbcTypeName))
		{
			return Types.NUMERIC;
		}
		else if ("OTHER".equals(jdbcTypeName))
		{
			return Types.OTHER;
		}
		else if ("REAL".equals(jdbcTypeName))
		{
			return Types.REAL;
		}
		else if ("REF".equals(jdbcTypeName))
		{
			return Types.REF;
		}
		else if ("SMALLINT".equals(jdbcTypeName))
		{
			return Types.SMALLINT;
		}
		else if ("STRUCT".equals(jdbcTypeName))
		{
			return Types.STRUCT;
		}
		else if ("TIME".equals(jdbcTypeName))
		{
			return Types.TIME;
		}
		else if ("TIMESTAMP".equals(jdbcTypeName))
		{
			return Types.TIMESTAMP;
		}
		else if ("TINYINT".equals(jdbcTypeName))
		{
			return Types.TINYINT;
		}
		else if ("VARBINARY".equals(jdbcTypeName))
		{
			return Types.VARBINARY;
		}
		else if ("VARCHAR".equals(jdbcTypeName))
		{
			return Types.VARCHAR;
		}
		else if (jdbcTypeName.startsWith("NVARCHAR"))
		{
			return Types.VARCHAR;
		}
		return defaultVal;
	}
	
	
	/**
	 * Returns a type code for the specified type name.  If not found then this will return Types.NULL.
	 * 
	 * @param jdbcTypeName the name to lookup.
	 * 
	 * @return the type code
	 */
	public static int getJdbcType(String jdbcTypeName)
	{
		return getJdbcType(jdbcTypeName, Types.NULL);
	}

	public static boolean isNumberType(int jdbcType)
	{
		boolean result = false;
		switch (jdbcType)
		{
		case Types.BIGINT:
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.INTEGER:
		case Types.NUMERIC:
			result = true;
			break;
		default:
			result = false;
		}
		return result;
	}

	public static boolean isDateType(int jdbcType)
	{
		boolean result = false;
		switch (jdbcType)
		{
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			result = true;
			break;
		default:
			result = false;
		}
		return result;
	}

	public static boolean isLongType(int jdbcType)
	{
		boolean result = false;
		switch (jdbcType)
		{
		case Types.LONGVARBINARY:
		case Types.LONGVARCHAR:
		case Types.BLOB:
		case Types.CLOB:
			result = true;
			break;
		default:
			result = false;
		}
		return result;
	}

	public static IndexInfo.IndexType getIndexType(short indexType)
	{
		IndexInfo.IndexType result = null;
		switch (indexType)
		{
		case DatabaseMetaData.tableIndexStatistic:
			result = IndexInfo.IndexType.STATISTIC;
			break;
		case DatabaseMetaData.tableIndexClustered:
			result = IndexInfo.IndexType.CLUSTERED;
			break;
		case DatabaseMetaData.tableIndexHashed:
			result = IndexInfo.IndexType.HASHED;
			break;
		case DatabaseMetaData.tableIndexOther:
			result = IndexInfo.IndexType.OTHER;
			break;
		default:
			throw new IllegalArgumentException("Unknown index type: " + indexType);
		}
		return result;
	}

	public static IndexInfo.SortOrder getIndexSortOrder(String sortOrder)
	{
		if (sortOrder == null) { return IndexInfo.SortOrder.NONE; }
		if (sortOrder.equalsIgnoreCase("A")) { return IndexInfo.SortOrder.ASC; }
		if (sortOrder.equalsIgnoreCase("D")) { return IndexInfo.SortOrder.DESC; }

		throw new IllegalArgumentException("Unknown index sort order: " + sortOrder);
	}
}
