package net.sourceforge.squirrel_sql.fw.datasetviewer;
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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class DatabaseTypesDataSet implements IDataSet
{

	private ResultSet _rs;
	private int[] _columnIndices;
	private int _columnCount;
	private DataSetDefinition _dataSetDefinition;
	private Object[] _row;

	public DatabaseTypesDataSet(ResultSet rs) throws DataSetException
	{
		this(rs, null);
	}

	public DatabaseTypesDataSet(ResultSet rs, int[] columnIndices)
		throws DataSetException
	{
		super();
		if (rs == null)
		{
			throw new IllegalArgumentException("Null ResultSet passed");
		}

		_rs = rs;
		if (columnIndices != null && columnIndices.length == 0)
		{
			columnIndices = null;
		}
		_columnIndices = columnIndices;

		try
		{
			ResultSetMetaData md = _rs.getMetaData();
			_columnCount = columnIndices != null ? columnIndices.length : md.getColumnCount();
			_dataSetDefinition = new DataSetDefinition(createColumnDefinitions(md, columnIndices));
			_row = new Object[_columnCount];
		}
		catch (SQLException ex)
		{
			throw new DataSetException(ex);
		}
	}

	public final int getColumnCount()
	{
		return _columnCount;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dataSetDefinition;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
		throws DataSetException
	{
		boolean rc = false;

//TODO: Replace with ResultSetReader once we have column renderers.
		try
		{
			rc = _rs.next();
			if (rc)
			{
				for (int i = 0; i < _columnCount; ++i)
				{
					int idx = _columnIndices != null ? _columnIndices[i] : i + 1;
					try
					{
						switch (idx)
						{

							case 2 :
								// DATA_TYPE column of result set.
								int data = _rs.getInt(idx);
								StringBuffer buf = new StringBuffer();
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
								_row[i] = _rs.getObject(idx);
								if (_row[i] != null
									&& !(_row[i] instanceof Integer))
								{
									if (_row[i] instanceof Number)
									{
										_row[i] = new Integer(((Number)_row[i]).intValue());
									}
									else
									{
										_row[i] = new Integer(_row[i].toString());
									}
								}
								break;

							case 7:
								// NULLABLE column of result set.
								short nullable = _rs.getShort(idx);
								switch (nullable)
								{
									case DatabaseMetaData.typeNoNulls :
										_row[i] = "false";
										break;
									case DatabaseMetaData.typeNullable :
										_row[i] = "true";
										break;
									case DatabaseMetaData.typeNullableUnknown :
										_row[i] = "unknown";
										break;
									default :
										_row[i] = nullable + "[error]";
										break;
								}
								break;

							case 8:
							case 10:
							case 11:
							case 12:
								// boolean columns
//								_row[i] = _rs.getBoolean(idx) ? "true" : "false";
								_row[i] = _rs.getObject(idx);
								if (_row[i] != null
									&& !(_row[i] instanceof Boolean))
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
								short searchable = _rs.getShort(idx);
								switch (searchable)
								{
									case DatabaseMetaData.typePredNone :
										_row[i] = "no support";
										break;
									case DatabaseMetaData.typePredChar :
										_row[i] = "only supports 'WHERE...like'";
										break;
									case DatabaseMetaData.typePredBasic :
										_row[i] = "supports all except 'WHERE...LIKE'";
										break;
									case DatabaseMetaData.typeSearchable :
										_row[i] = "supports all WHERE";
										break;
									default :
										_row[i] = searchable + "[error]";
										break;
								}
								break;

							case 16:
							case 17:
								// ignore - unused.
								break;

							default :
								_row[i] = _rs.getString(idx);
								break;

						}
					}
					catch (Throwable th)
					{
						if (msgHandler != null)
						{
							_row[i] = "<error>"; //i18n
							msgHandler.showMessage(th);
						}
						else
						{
							throw new DataSetException(th);
						}
					}
				}
			}

			return rc;

		}
		catch (SQLException ex)
		{
			if (msgHandler != null)
			{
				msgHandler.showMessage(ex);
			}
			else
			{
				throw new DataSetException(ex);
			}
		}

		return rc;
	}

	public Object get(int columnIndex)
	{
		return _row[columnIndex];
	}

	private ColumnDisplayDefinition[] createColumnDefinitions(
		ResultSetMetaData md,
		int[] columnIndices)
		throws SQLException
	{

		ColumnDisplayDefinition[] columnDefs =
			new ColumnDisplayDefinition[_columnCount];
		for (int i = 0; i < _columnCount; ++i)
		{
			int idx = columnIndices != null ? columnIndices[i] : i + 1;
			columnDefs[i] =
				new ColumnDisplayDefinition(
					md.getColumnDisplaySize(idx),
					md.getColumnLabel(idx));
		}
		return columnDefs;
	}
}
