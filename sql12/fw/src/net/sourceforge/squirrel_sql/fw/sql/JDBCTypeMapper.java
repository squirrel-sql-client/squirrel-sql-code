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
import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A utility class that maps java SQL type codes to java sql type names and back again.
 */
public class JDBCTypeMapper
{

	/** logger for this class */
	private static ILogger s_log = LoggerController.createLogger(JDBCTypeMapper.class);
	
	/**
	 * Returns a list of Type names that are found in the java.sql.Types class using reflection.
	 * 
	 * @return
	 */
	public static String[] getJdbcTypeList()
	{
		ArrayList<String> result = new ArrayList<String>();		
		Field[] fields = java.sql.Types.class.getDeclaredFields();
      for (int i = 0; i < fields.length; i++) {
          Field field = fields[i];
          result.add(field.getName());
      }
		return result.toArray(new String[result.size()]);
	}

	public static String getJdbcTypeName(int jdbcType)
	{
		String result = "UNKNOWN";
		try
		{
			Field[] fields = java.sql.Types.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[i];
				if (field.getInt(null) == jdbcType)
				{
					result = field.getName();
					break;
				}
			}
		}
		catch (SecurityException e)
		{
			s_log.error("getJdbcTypeName: unexpected exception: "+e.getMessage(), e);
		}
		catch (IllegalArgumentException e)
		{
			s_log.error("getJdbcTypeName: unexpected exception: "+e.getMessage(), e);
		}
		catch (IllegalAccessException e)
		{
			s_log.error("getJdbcTypeName: unexpected exception: "+e.getMessage(), e);
		}
		return result;
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
		int result = defaultVal;
		
		try
		{
			Field[] fields = java.sql.Types.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[i];
				if (field.getName().equals(jdbcTypeName)) {
					result = field.getInt(null);
				}
			}
		}
		catch (IllegalArgumentException e)
		{
			s_log.error("getJdbcTypeName: unexpected exception: "+e.getMessage(), e);
		}
		catch (IllegalAccessException e)
		{
			s_log.error("getJdbcTypeName: unexpected exception: "+e.getMessage(), e);
		}
		return result;
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
