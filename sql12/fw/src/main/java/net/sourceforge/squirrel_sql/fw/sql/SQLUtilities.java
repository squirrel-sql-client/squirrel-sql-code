/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.sql;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * @author manningr : except where noted
 */
public class SQLUtilities
{

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(SQLUtilities.class);

	/**
	 * Contributed by Thomas Mueller to handle doubling quote characters found in an identifier. In H2 and
	 * other dbs, the following statement creates a table with an embedded quote character: CREATE TABLE
	 * "foo""bar" (someid int); However, what is returned by the driver for table name is: foo"bar The reason
	 * is simple. Just like embedded quotes in SQL strings, such as: select 'I don''t know' from test
	 * Similarly, embedded quote characters can also appear in identifiers such as table names, by doubling (or
	 * quoting, if you will) the quote.
	 * 
	 * @param s
	 *           the string to have embedded quotes expanded.
	 * @return a new string with any embedded quotes doubled, or null if null is passed.
	 * @author Thomas Mueller
	 */
	public static String quoteIdentifier(String s)
	{
		if (s == null)
		{
			return null;
		}
		StringBuilder buff = null;
		buff = new StringBuilder();
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (c == '"' && i != 0 && i != s.length() - 1)
			{
				buff.append(c);
			}
			buff.append(c);
		}
		String result = buff.toString();
		return result;
	}

	/**
	 * Reverses the insertion order list. Just a convenience method.
	 * 
	 * @param md
	 * @param tables
	 * @return
	 * @throws SQLException
	 */
	public static List<ITableInfo> getDeletionOrder(List<ITableInfo> tables, SQLDatabaseMetaData md,
		ProgressCallBack callback) throws SQLException
	{
		List<ITableInfo> insertionOrder = getInsertionOrder(tables, md, callback);
		Collections.reverse(insertionOrder);
		return insertionOrder;
	}

	/**
	 * Returns the specified list of tables in an order such that insertions into all tables will satisfy any
	 * foreign key constraints. This will not correctly handle recursive constraints. This algorthim was
	 * adapted from SchemaSpy class/method: net.sourceforge.schemaspy.SchemaSpy.sortTablesByRI() unattached -
	 * tables that have no dependencies on other tables parents - tables that only have children children -
	 * tables that only have parents sandwiches - tables that have both parents and children - as in the
	 * "sandwich" generation. The first SQLException encountered while attempting to get FK information on any
	 * table will cause this to bail it's effort to re-order the list and the list will be returned as it came
	 * in - there's no point in spewing exceptions to end up with a flawed result; just give up.
	 * 
	 * @param md
	 * @param tables
	 * @param listener
	 * @return
	 * @throws SQLException
	 */
	public static List<ITableInfo> getInsertionOrder(List<ITableInfo> tables, SQLDatabaseMetaData md,
		ProgressCallBack callback) throws SQLException
	{
		List<ITableInfo> result = new ArrayList<ITableInfo>();
		// tables that are netiher children nor parents - utility tables
		List<ITableInfo> unattached = new ArrayList<ITableInfo>();
		// tables that have at least one parent table
		List<ITableInfo> children = new ArrayList<ITableInfo>();
		// tables that have at least one child table
		List<ITableInfo> parents = new ArrayList<ITableInfo>();
		// tables that have at least one child table and have a least one parent table
		List<ITableInfo> sandwiches = new ArrayList<ITableInfo>();
		ITableInfo lastTable = null;
		try
		{
			for (ITableInfo table : tables)
			{
				lastTable = table;
				callback.currentlyLoading(table.getSimpleName());
				ForeignKeyInfo[] importedKeys = getImportedKeys(table, md);
				ForeignKeyInfo[] exportedKeys = getExportedKeys(table, md);

				if (importedKeys != null && importedKeys.length == 0 && exportedKeys.length == 0)
				{
					unattached.add(table);
					continue;
				}
				if (exportedKeys != null && exportedKeys.length > 0)
				{
					if (importedKeys != null && importedKeys.length > 0)
					{
						sandwiches.add(table);
					} else
					{
						parents.add(table);
					}
					continue;
				}
				if (importedKeys != null && importedKeys.length > 0)
				{
					children.add(table);
				}
			}
			reorderTables(sandwiches);

			for (ITableInfo info : unattached)
			{
				result.add(info);
			}
			for (ITableInfo info : parents)
			{
				result.add(info);
			}
			for (ITableInfo info : sandwiches)
			{
				result.add(info);
			}
			for (ITableInfo info : children)
			{
				result.add(info);
			}
			if (result.size() != tables.size())
			{
				s_log.error("getInsertionOrder(): failed to obtain a result table list " + "(" + result.size()
					+ ") that is the same size as the input table " + "list (" + tables.size()
					+ ") - returning the original unordered " + "list");
				result = tables;
			}
		} catch (Exception e)
		{
			if (lastTable != null)
			{
				String tablename = lastTable.getSimpleName();
				s_log.error("Unexpected exception while getting foreign key info for " + "table " + tablename, e);
			} else
			{
				s_log.error("Unexpected exception while getting foreign key info ", e);
			}
			result = tables;
		}
		return result;
	}

	public static ForeignKeyInfo[] getImportedKeys(ITableInfo ti, SQLDatabaseMetaData md) throws SQLException
	{
		ForeignKeyInfo[] result = ti.getImportedKeys();
		if (result == null)
		{
			result = md.getImportedKeysInfo(ti);
			// Avoid the hit next time
			ti.setImportedKeys(result);
		}
		return result;
	}

	/**
	 * @param ti
	 * @param md
	 * @return
	 * @throws SQLException
	 */
	public static ForeignKeyInfo[] getExportedKeys(ITableInfo ti, SQLDatabaseMetaData md) throws SQLException
	{
		ForeignKeyInfo[] result = ti.getExportedKeys();
		if (result == null)
		{
			result = md.getExportedKeysInfo(ti);
			// Avoid the hit next time
			ti.setExportedKeys(result);
		}
		return result;
	}

	/**
	 * @param sandwiches
	 */
	private static void reorderTables(List<ITableInfo> sandwiches)
	{
		Collections.sort(sandwiches, new TableComparator());
	}

	/**
	 * Returns a list of table names that have Primary Keys that are referenced by foreign key constraints on
	 * columns in the specified list of tables, that are not also contained in the specified list
	 * 
	 * @param md
	 * @param tables
	 * @return
	 * @throws SQLException
	 */
	public static List<String> getExtFKParents(SQLDatabaseMetaData md, List<ITableInfo> tables)
		throws SQLException
	{
		List<String> result = new ArrayList<String>();
		HashSet<String> tableNames = new HashSet<String>();

		for (ITableInfo table : tables)
		{
			tableNames.add(table.getSimpleName());
		}

		for (ITableInfo table : tables)
		{
			ForeignKeyInfo[] importedKeys = md.getImportedKeysInfo(table);
			for (int i = 0; i < importedKeys.length; i++)
			{
				ForeignKeyInfo info = importedKeys[i];
				String pkTable = info.getPrimaryKeyTableName();
				if (!tableNames.contains(pkTable))
				{
					result.add(pkTable);
				}
			}
		}
		return result;
	}

	/**
	 * Returns a list of table names that have Foreign keys that reference Primary Keys in the specified List
	 * of tables, but that are not also contained in the list of tables.
	 * 
	 * @param md
	 * @param tables
	 * @return
	 * @throws SQLException
	 */
	public static List<String> getExtFKChildren(SQLDatabaseMetaData md, List<ITableInfo> tables)
		throws SQLException
	{
		List<String> result = new ArrayList<String>();
		HashSet<String> tableNames = new HashSet<String>();

		for (ITableInfo table : tables)
		{
			tableNames.add(table.getSimpleName());
		}

		for (ITableInfo table : tables)
		{
			ForeignKeyInfo[] exportedKeys = md.getExportedKeysInfo(table);
			for (int i = 0; i < exportedKeys.length; i++)
			{
				ForeignKeyInfo info = exportedKeys[i];
				String fkTable = info.getForeignKeyTableName();
				if (!tableNames.contains(fkTable))
				{
					result.add(fkTable);
				}
			}
		}
		return result;
	}

	/**
	 * Closes the specified ResultSet safely (with no exceptions) and logs a debug message if SQLException is
	 * encountered. This will not close the Statement that created the ResultSet.
	 * 
	 * @param rs
	 *           the ResultSet to close - it can be null.
	 */
	public static void closeResultSet(ResultSet rs)
	{
		closeResultSet(rs, false);
	}

	/**
	 * Closes the specified ResultSet safely (with no exceptions) and logs a debug message if SQLException is
	 * encountered. This will also close the Statement that created the ResultSet if closeStatement boolean is
	 * true.
	 * 
	 * @param rs
	 *           the ResultSet to close - it can be null.
	 * @param closeStatement
	 *           if true, will close the Statement that created this ResultSet; false - will not close the
	 *           Statement.
	 */
	public static void closeResultSet(ResultSet rs, boolean closeStatement)
	{
		if (rs == null)
		{
			return;
		}
		// Close the ResultSet
		try
		{
			rs.close();
		} catch (SQLException e)
		{
			if (s_log.isDebugEnabled())
			{
				s_log.debug("Unexpected exception while closing ResultSet: " + e.getMessage(), e);
			}
		}
		if (closeStatement)
		{
			// Close the ResultSet's Statement if it is non-null. This frees open
			// cursors.

			try
			{
				Statement stmt = rs.getStatement();
				if (stmt != null)
				{
					stmt.close();
				}
			} catch (SQLException e)
			{
				if (s_log.isDebugEnabled())
				{
					s_log.debug("Unexpected exception while closing " + "Statement: " + e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Closes the specified Statement safely (with no exceptions) and logs a debug message if SQLException is
	 * encountered.
	 * 
	 * @param stmt
	 *           the Statement to close - it can be null.
	 */
	public static void closeStatement(Statement stmt)
	{
		if (stmt == null)
		{
			return;
		}
		try
		{
			stmt.close();
		} catch (SQLException e)
		{
			if (s_log.isDebugEnabled())
			{
				s_log.error("Unexpected exception while closing Statement: " + e.getMessage(), e);
			}
		}
	}

   public static String getQualifiedTableName(String catalog, String schema, String tableName)
   {
      String ret = "";

      if(null != catalog)
      {
         ret += catalog + ".";
      }

      if(null != schema)
      {
         ret += schema + ".";
      }

      ret += tableName;

      return ret;
   }

   /**
	 * @author manningr
	 */
	private static class TableComparator implements Comparator<ITableInfo>, Serializable
	{

		private static final long serialVersionUID = 1L;

		public int compare(ITableInfo t1, ITableInfo t2)
		{
			ForeignKeyInfo[] t1ImportedKeys = t1.getImportedKeys();
			for (int i = 0; i < t1ImportedKeys.length; i++)
			{
				ForeignKeyInfo info = t1ImportedKeys[i];
				if (info.getPrimaryKeyTableName().equals(t2.getSimpleName()))
				{
					// t1 depends on t2
					return 1;
				}
			}
			ForeignKeyInfo[] t2ImportedKeys = t2.getImportedKeys();
			for (int i = 0; i < t2ImportedKeys.length; i++)
			{
				ForeignKeyInfo info = t2ImportedKeys[i];
				if (info.getPrimaryKeyTableName().equals(t1.getSimpleName()))
				{
					// t2 depends on t1
					return -1;
				}
			}
			if (t1.getImportedKeys().length > t2ImportedKeys.length)
			{
				return 1;
			}
			if (t1.getImportedKeys().length < t2ImportedKeys.length)
			{
				return -1;
			}
			return 0;
		}

	}
}
