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

package net.sourceforge.squirrel_sql.plugins.dbdiff;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

public class TableDiffExecutor
{

	private final ISQLDatabaseMetaData _md1;

	private final ISQLDatabaseMetaData _md2;

	private ITableInfo _t1;

	private ITableInfo _t2;

	private List<ColumnDifference> colDifferences = null;

	public TableDiffExecutor(ISQLDatabaseMetaData md1, ISQLDatabaseMetaData md2)
	{
		_md1 = md1;
		_md2 = md2;
	}

	public void setTableInfos(ITableInfo t1, ITableInfo t2)
	{
		_t1 = t1;
		_t2 = t2;
		if (colDifferences != null)
		{
			colDifferences.clear();
		}
	}

	public void execute() throws SQLException
	{
		colDifferences = new ArrayList<ColumnDifference>();
		final TableColumnInfo[] t1cols = _md1.getColumnInfo(_t1);
		final TableColumnInfo[] t2cols = _md2.getColumnInfo(_t2);
		final Set<String> columnNames = getAllColumnNames(t1cols, t2cols);
		final Set<String> t1ColumnNames = getAllColumnNames(t1cols);
		final Map<String, TableColumnInfo> t1ColMap = getColumnMap(t1cols);
		final Set<String> t2ColumnNames = getAllColumnNames(t2cols);
		final Map<String, TableColumnInfo> t2ColMap = getColumnMap(t2cols);

		for (final String columnName : columnNames)
		{
			final ColumnDifference diff = new ColumnDifference();
			if (t1ColumnNames.contains(columnName))
			{
				final TableColumnInfo c1 = t1ColMap.get(columnName);

				if (t2ColumnNames.contains(columnName))
				{ // Column is in both table 1 and 2
					final TableColumnInfo c2 = t2ColMap.get(columnName);
					diff.setColumns(c1, c2);
				}
				else
				{
					// Column is in table 1, but not table 2
					diff.setCol2Exists(false);
					diff.setColumn1(c1);
				}
			}
			else
			{
				// Column is in table 2, but not table 1 - how else would we get
				// here??
				diff.setCol1Exists(false);
				diff.setColumn2(t2ColMap.get(columnName));
			}
			if (diff.execute())
			{
				colDifferences.add(diff);
			}
		}
	}

	public List<ColumnDifference> getColumnDifferences()
	{
		return colDifferences;
	}

	private Map<String, TableColumnInfo> getColumnMap(TableColumnInfo[] tci)
	{
		final HashMap<String, TableColumnInfo> result = new HashMap<String, TableColumnInfo>();
		for (final TableColumnInfo info : tci)
		{
			result.put(info.getColumnName(), info);
		}
		return result;
	}

	/**
	 * Build a list of all table column names.
	 * 
	 * @param tci1
	 * @param tci2
	 * @return
	 */
	private Set<String> getAllColumnNames(TableColumnInfo[] tci1, TableColumnInfo[] tci2)
	{
		final HashSet<String> result = new HashSet<String>();
		result.addAll(getAllColumnNames(tci1));
		result.addAll(getAllColumnNames(tci2));
		return result;
	}

	private Set<String> getAllColumnNames(TableColumnInfo[] tci)
	{
		final HashSet<String> result = new HashSet<String>();
		for (final TableColumnInfo info : tci)
		{
			result.add(info.getColumnName());
		}
		return result;
	}

}
