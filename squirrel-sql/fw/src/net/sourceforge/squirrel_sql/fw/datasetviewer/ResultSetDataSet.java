package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
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
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ResultSetDataSet implements IDataSet
{
	private ILogger s_log =
		LoggerController.createLogger(ResultSetDataSet.class);

	// These 2 should be handled with an Iterator!!!
	private int _iCurrent = -1;
	private Object[] _currentRow;

	private int[] _columnIndices;
	private int _columnCount;
	private DataSetDefinition _dataSetDefinition;
	private ArrayList _alData;

	private LargeResultSetObjectInfo _largeObjInfo = new LargeResultSetObjectInfo();

	public ResultSetDataSet() throws DataSetException
	{
		this(null, null);
	}

	public ResultSetDataSet(ResultSet rs) throws DataSetException
	{
		this(rs, null);
	}

	public ResultSetDataSet(ResultSet rs, int[] columnIndices)
		throws IllegalArgumentException, DataSetException
	{
		super();
		setResultSet(rs, columnIndices);
	}

	public void setLargeResultSetObjectInfo(LargeResultSetObjectInfo value)
	{
		_largeObjInfo = value != null ? value : new LargeResultSetObjectInfo();
	}

	public void setResultSet(ResultSet rs) throws DataSetException
	{
		setResultSet(rs, null);
	}

	public void setResultSet(ResultSet rs, int[] columnIndices)
		throws DataSetException
	{
		if (columnIndices != null && columnIndices.length == 0)
		{
			columnIndices = null;
		}
		_columnIndices = columnIndices;
		_iCurrent = -1;
		_alData = new ArrayList();

		if (rs != null)
		{
			try
			{
				final char[] work = new char[100];
				ResultSetMetaData md = rs.getMetaData();
				_columnCount = columnIndices != null
								? columnIndices.length
								: md.getColumnCount();
				ColumnDisplayDefinition[] colDefs = createColumnDefinitions(md, columnIndices);
				_dataSetDefinition = new DataSetDefinition(colDefs);
				while (rs.next())
				{
					Object[] row = new Object[_columnCount];
					for (int i = 0; i < _columnCount; ++i)
					{
						int idx = _columnIndices != null ? _columnIndices[i] : i + 1;
						try
						{
							switch (md.getColumnType(idx))
							{
								case Types.NULL:
									row[i] = null;
									break;
								
								// TODO: When JDK1.4 is the earliest JDK supported
								// by Squirrel then remove the hardcoding of the
								// boolean data type.
								case Types.BIT:
								case 16:
//								case Types.BOOLEAN:
									row[i] = rs.getObject(idx);
									if (row[i] != null
										&& !(row[i] instanceof Boolean))
									{
										if (row[i] instanceof Number)
										{
											if (((Number) row[i]).intValue() == 0)
											{
												row[i] = Boolean.FALSE;
											}
											else
											{
												row[i] = Boolean.TRUE;
											}
										}
										else
										{
											row[i] = Boolean.valueOf(row[i].toString());
										}
									}
									break;

								case Types.TIME :
									row[i] = rs.getTime(idx);
									break;

								case Types.DATE :
									row[i] = rs.getDate(idx);
									break;

								case Types.TIMESTAMP :
									row[i] = rs.getTimestamp(idx);
									break;

								case Types.BIGINT :
									row[i] = rs.getObject(idx);
									if (row[i] != null
										&& !(row[i] instanceof Long))
									{
										if (row[i] instanceof Number)
										{
											row[i] = new Long(((Number)row[i]).longValue());
										}
										else
										{
											row[i] = new Long(row[i].toString());
										}
									}
									break;

								case Types.DOUBLE:
								case Types.FLOAT:
								case Types.REAL:
									row[i] = rs.getObject(idx);
									if (row[i] != null
										&& !(row[i] instanceof Double))
									{
										if (row[i] instanceof Number)
										{
											Number nbr = (Number)row[i];
											row[i] = new Double(nbr.doubleValue());
										}
										else
										{
											row[i] = new Double(row[i].toString());
										}
									}
									break;

								case Types.DECIMAL:
								case Types.NUMERIC:
									row[i] = rs.getObject(idx);
									if (row[i] != null
										&& !(row[i] instanceof BigDecimal))
									{
										if (row[i] instanceof Number)
										{
											Number nbr = (Number)row[i];
											row[i] = new BigDecimal(nbr.doubleValue());
										}
										else
										{
											row[i] = new BigDecimal(row[i].toString());
										}
									}
									break;

								case Types.INTEGER:
								case Types.SMALLINT:
								case Types.TINYINT:
									row[i] = rs.getObject(idx);
									if (row[i] != null
										&& !(row[i] instanceof Integer))
									{
										if (row[i] instanceof Number)
										{
											row[i] = new Integer(((Number)row[i]).intValue());
										}
										else
										{
											row[i] = new Integer(row[i].toString());
										}
									}
									break;

									// ?? Hard coded -. JDBC/ODBC bridge JDK1.4
									// brings back -9 for nvarchar columns in
									// MS SQL Server tables.
								case Types.CHAR :
								case Types.VARCHAR :
								case Types.LONGVARCHAR :
								case -9 :
									row[i] = rs.getString(idx);
									break;

								case Types.BINARY:
									if (_largeObjInfo.getReadBinary())
									{
										row[i] = rs.getString(idx);
									}
									else
									{
										row[i] = "<Binary>";
									}
									break;

								case Types.VARBINARY:
									if (_largeObjInfo.getReadVarBinary())
									{
										row[i] = rs.getString(idx);
									}
									else
									{
										row[i] = "<VarBinary>";
									}
									break;

								case Types.LONGVARBINARY:
									if (_largeObjInfo.getReadLongVarBinary())
									{
										row[i] = rs.getString(idx);
									}
									else
									{
										row[i] = "<LongVarBinary>";
									}
									break;

								case Types.BLOB:
									if (_largeObjInfo.getReadBlobs())
									{
										row[i] = null;
										Blob blob = rs.getBlob(idx);
										if (blob != null)
										{
											int len = (int)blob.length();
											if (len > 0)
											{
												int bytesToRead = _largeObjInfo.getReadBlobsSize();
												if (bytesToRead > len)
												{
													bytesToRead = len;
												}
												row[i] = new String(blob.getBytes(1, bytesToRead));
											}
										}
									}
									else
									{
										row[i] = "<Blob>";
									}
									break;

								case Types.CLOB:
									if (_largeObjInfo.getReadClobs())
									{
										row[i] = null;
										Clob clob = rs.getClob(idx);
										if (clob != null)
										{
											int len = (int)clob.length();
											if (len > 0)
											{
												int charsToRead = _largeObjInfo.getReadClobsSize();
												if (charsToRead > len)
												{
													charsToRead = len;
												}
												row[i] = clob.getSubString(1, charsToRead);
											}
										}
									}
									else
									{
										row[i] = "<Clob>";
									}
									break;

								default :
									row[i] = "<Unknown>";
							}
						}
						catch (Throwable th)
						{
							row[i] = "<Error>";
							s_log.error("Error reading column data", th);
						}

					}
					_alData.add(row);
				}
			}
			catch (SQLException ex)
			{
				s_log.error("Error reading ResultSet", ex);
				throw new DataSetException(ex);
			}
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
		// This should be handled with an Iterator!!!
		if (++_iCurrent < _alData.size())
		{
			_currentRow = (Object[]) _alData.get(_iCurrent);
			return true;
		}
		return false;
	}

	public Object get(int columnIndex)
	{
		return _currentRow[columnIndex];
	}

	private ColumnDisplayDefinition[] createColumnDefinitions(
		ResultSetMetaData md,
		int[] columnIndices)
		throws SQLException
	{
		// TODO?? ColumnDisplayDefinition should also have the Type (String, Date, Double,Integer,Boolean)
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
