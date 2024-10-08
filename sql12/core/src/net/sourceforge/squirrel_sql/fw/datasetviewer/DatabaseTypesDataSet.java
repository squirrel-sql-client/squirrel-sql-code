package net.sourceforge.squirrel_sql.fw.datasetviewer;

/*
 * Copyright (C) 2002-2004 David MacLean
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

import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTypesDataSet implements IDataSet
{
	private int[] _columnIndices;

	private int _columnCount;

	private DataSetDefinition _dataSetDefinition;

	private List<Object[]> _allData = new ArrayList<>();

	private int _currentRowIdx = -1;

	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DatabaseTypesDataSet.class);

	public DatabaseTypesDataSet(ResultSet rs) throws DataSetException
	{
		this(rs, null);
	}

	public DatabaseTypesDataSet(ResultSet rs, int[] columnIndices) throws DataSetException
	{
		if (columnIndices != null && columnIndices.length == 0)
		{
			columnIndices = null;
		}
		_columnIndices = columnIndices;

		if (rs != null)
		{
			try
			{
				ResultSetMetaData md = rs.getMetaData();
				_columnCount = columnIndices != null ? columnIndices.length : md.getColumnCount();
				_dataSetDefinition = new DataSetDefinition(createColumnDefinitions(md, columnIndices));
			}
			catch (SQLException ex)
			{
				throw new DataSetException(ex);
			}
		}
		setResultSet(rs);
	}

	public final int getColumnCount()
	{
		return _columnCount;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dataSetDefinition;
	}

	public boolean next(IMessageHandler msgHandler) throws DataSetException
	{
		if (_currentRowIdx < _allData.size() - 1)
		{
			_currentRowIdx++;
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Helper method to read a row from the specified ResultSet.
	 * 
	 * @return an array of column values in the next row.
	 */
	private Object[] getNextRow(ResultSet rs) throws SQLException
	{
		Object[] _row = new Object[_columnCount];
		for (int i = 0; i < _columnCount; ++i)
		{
			int idx = _columnIndices != null ? _columnIndices[i] : i + 1;
			switch (idx)
			{
			case 2:
				// DATA_TYPE column of result set.
				// int data = _rs.getInt(idx);
				int data = rs.getInt(idx);
				StringBuilder buf = new StringBuilder();
				buf.append(String.valueOf(data))
					.append(" [")
					.append(JDBCTypeMapper.getJdbcTypeName(data))
					.append("]");
				_row[i] = buf.toString();
				break;

			case 3:
			case 14:
			case 15:
			case 18:
				_row[i] = rs.getObject(idx);
				if (_row[i] != null && !(_row[i] instanceof Integer))
				{
					if (_row[i] instanceof Number)
					{
						_row[i] = ((Number) _row[i]).intValue();
					}
					else
					{
						_row[i] = Integer.valueOf(_row[i].toString());
					}
				}
				break;

			case 7:
				// NULLABLE column of result set.
				short nullable = rs.getShort(idx);
				switch (nullable)
				{
				case DatabaseMetaData.typeNoNulls:
					_row[i] = s_stringMgr.getString("DatabaseMetaData.nullableTypeNoNulls");
					break;
				case DatabaseMetaData.typeNullable:
					_row[i] = s_stringMgr.getString("DatabaseMetaData.nullableTypeNullable");
					break;
				case DatabaseMetaData.typeNullableUnknown:
					_row[i] = s_stringMgr.getString("DatabaseMetaData.nullableTypeNullableUnknown");
					break;
				default:
					_row[i] = nullable + "[error]";
					break;
				}
				break;

			case 8:
			case 10:
			case 11:
			case 12:
				// boolean columns
				// _row[i] = _rs.getBoolean(idx) ? "true" : "false";
				_row[i] = rs.getObject(idx);
				if (_row[i] != null && !(_row[i] instanceof Boolean))
				{
					if (_row[i] instanceof Number)
					{
						if (((Number) _row[i]).intValue() == 0)
						{
							_row[i] = Boolean.FALSE;
						}
						else
						{
							_row[i] = Boolean.TRUE;
						}
					}
					else
					{
						_row[i] = Boolean.valueOf(_row[i].toString());
					}
				}
				break;

			case 9:
				// SEARCHABLE column of result set.
				short searchable = rs.getShort(idx);
				switch (searchable)
				{
				case DatabaseMetaData.typePredNone:
					_row[i] = s_stringMgr.getString("DatabaseMetaData.searchableTypePredNone");
					break;
				case DatabaseMetaData.typePredChar:
					_row[i] = s_stringMgr.getString("DatabaseMetaData.searchableTypePredChar");
					break;
				case DatabaseMetaData.typePredBasic:
					_row[i] = s_stringMgr.getString("DatabaseMetaData.searchableTypePredBasic");
					break;
				case DatabaseMetaData.typeSearchable:
					_row[i] = s_stringMgr.getString("DatabaseMetaData.searchableTypeSearchable");
					break;
				default:
					_row[i] = searchable + "[error]";
					break;
				}
				break;

			case 16:
			case 17:
				// ignore - unused.
				break;

			default:
				_row[i] = rs.getString(idx);
				break;

			}
		}
		return _row;
	}

	/**
     * 
     */
	public Object get(int columnIndex)
	{
		Object[] currentRow = _allData.get(_currentRowIdx);
		return currentRow[columnIndex];
	}

	private ColumnDisplayDefinition[] createColumnDefinitions(ResultSetMetaData md, int[] columnIndices)
		throws SQLException
	{

		ColumnDisplayDefinition[] columnDefs = new ColumnDisplayDefinition[_columnCount];
		for (int i = 0; i < _columnCount; ++i)
		{
			int idx = columnIndices != null ? columnIndices[i] : i + 1;

			String columnLabel = md.getColumnLabel(idx);
			int columnLabelLength = 1;
			if(null !=columnLabel)
			{
				columnLabelLength = columnLabel.length();
			}

			columnDefs[i] = new ColumnDisplayDefinition(Math.max(columnLabelLength, Math.min(md.getColumnDisplaySize(idx), columnLabelLength + 10)), columnLabel);
		}
		return columnDefs;
	}

	/**
	 * Reads the specified ResultSet of all of it's rows and closes it.
	 * 
	 * @param rs
	 *           the ResultSet to read from.
	 * @throws DataSetException
	 */
	private void setResultSet(ResultSet rs) throws DataSetException
	{
		if (rs == null) { return; }
		try
		{
			while (rs.next())
			{
				Object[] row = getNextRow(rs);
				_allData.add(row);
			}
		}
		catch (SQLException e)
		{
			throw new DataSetException(e);
		}
		finally
		{
			SQLUtilities.closeResultSet(rs);
		}
	}
}
