package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
 * Copyright (C) 2001-2002 Johan Compagner
 * jcompagner@j-com.nl
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
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.LargeResultSetObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ResultSetReader
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ResultSetReader.class);

	/** The <TT>ResultSet</TT> being read. */
	private final ResultSet _rs;

	/**
	 * The indices into the <TT>ResultSet that we want to read, starting from
	 * 1 (not 0). If this contains {1, 5, 6} then only columns 1, 5, and 6 will
	 * be read. If <TT>null or empty then all columns are read.
	 */
	private final int[] _columnIndices;

	/** Describes how to handle "blob" type data. */
	private final LargeResultSetObjectInfo _largeObjInfo;

	/** 
	 * The number of columns to read. This may or may not be the same as the
	 * number of columns in the <TT>ResultSet</TT>. @see _columnIndices.
	 */
	private int _columnCount;

	/** <TT>true</TT> if an error occured reading a column in th previous row. */
	private boolean _errorOccured = false;

	/** Metadata for the <TT>ResultSet</TT>. */
	private ResultSetMetaData _rsmd;

	public ResultSetReader(ResultSet rs) throws SQLException
	{
		this(rs, null, null);
	}

	public ResultSetReader(ResultSet rs, LargeResultSetObjectInfo largeObjInfo)
		throws SQLException
	{
		this(rs, largeObjInfo, null);
	}

	public ResultSetReader(ResultSet rs, int[] columnIndices) throws SQLException
	{
		this(rs, null, columnIndices);
	}

	public ResultSetReader(ResultSet rs, LargeResultSetObjectInfo largeObjInfo,
							int[] columnIndices) throws SQLException
	{
		super();
		if (rs == null)
		{
			throw new IllegalArgumentException("ResultSet == null");
		}

		_rs = rs;

		_largeObjInfo = largeObjInfo != null ? largeObjInfo : new LargeResultSetObjectInfo();
	
		if (columnIndices != null && columnIndices.length == 0)
		{
			columnIndices = null;
		}
		_columnIndices = columnIndices;

		_rsmd = rs.getMetaData();

		_columnCount = columnIndices != null ? columnIndices.length : _rsmd.getColumnCount();
	}

	/**
	 * Read the next row from the <TT>ResultSet</TT>. If no more rows then
	 * <TT>null</TT> will be returned, otherwise an <TT>Object[]</TT> will be
	 * returned where each element of the array is an object representing
	 * the contents of the column. These objects could be of type <TT>String</TT>,
	 * <TT>BigDecimal</TT> etc.
	 * 
	 * <P>If an error occurs calling <TT>next()</TT> on the <TT>ResultSet</TT>
	 * then an <TT>SQLException will be thrown, however if an error occurs
	 * retrieving the data for a column an error msg will be placed in that
	 * element of the array, but no exception will be thrown. To see if an
	 * error occured retrieving column data you can call
	 * <TT>getColumnErrorInPreviousRow</TT> after the call to <TT>readRow()</TT>.
	 * 
	 * @throws	SQLException	Error occured on <TT>ResultSet.next()</TT>.
	 */
	public Object[] readRow() throws SQLException
	{
		_errorOccured = false;
		if (_rs.next())
		{
			return doRead();
		}
		return null;
	}

	/**
	 * Retrieve whether an error occured reading a column in the previous row.
	 * 
	 * @return	<TT>true</TT> if error occured.
	 */
	public boolean getColumnErrorInPreviousRow()
	{
		return _errorOccured;
	}

	private Object[] doRead()
	{
		Object[] row = new Object[_columnCount];
		for (int i = 0; i < _columnCount; ++i)
		{
			int idx = _columnIndices != null ? _columnIndices[i] : i + 1;
			try
			{
				final int columnType = _rsmd.getColumnType(idx);
				//final String columnClassName = _rsmd.getColumnClassName(idx);
				switch (columnType)
				{
					case Types.NULL:
						row[i] = null;
						break;
					
					// TODO: When JDK1.4 is the earliest JDK supported
					// by Squirrel then remove the hardcoding of the
					// boolean data type.
					case Types.BIT:
					case 16:
//					case Types.BOOLEAN:
						row[i] = _rs.getObject(idx);
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
						row[i] = _rs.getTime(idx);
						break;

					case Types.DATE :
						row[i] = _rs.getDate(idx);
						break;

					case Types.TIMESTAMP :
						row[i] = _rs.getTimestamp(idx);
						break;

					case Types.BIGINT :
						row[i] = _rs.getObject(idx);
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
						row[i] = _rs.getObject(idx);
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
						row[i] = _rs.getObject(idx);
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
						row[i] = _rs.getObject(idx);
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

						// TODO: Hard coded -. JDBC/ODBC bridge JDK1.4
						// brings back -9 for nvarchar columns in
						// MS SQL Server tables.
						// -8 is ROWID in Oracle.
					case Types.CHAR:
					case Types.VARCHAR:
					case Types.LONGVARCHAR:
					case -9:
					case -8:
						row[i] = _rs.getString(idx);
						break;

					case Types.BINARY:
						if (_largeObjInfo.getReadBinary())
						{
							row[i] = _rs.getString(idx);
						}
						else
						{
							row[i] = "<Binary>";
						}
						break;

					case Types.VARBINARY:
						if (_largeObjInfo.getReadVarBinary())
						{
							row[i] = _rs.getString(idx);
						}
						else
						{
							row[i] = "<VarBinary>";
						}
						break;

					case Types.LONGVARBINARY:
						if (_largeObjInfo.getReadLongVarBinary())
						{
							row[i] = _rs.getString(idx);
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
							Blob blob = _rs.getBlob(idx);
							if (blob != null)
							{
								int len = (int)blob.length();
								if (len > 0)
								{
									int bytesToRead = len;
									if (!_largeObjInfo.getReadCompleteBlobs())
									{
										bytesToRead = _largeObjInfo.getReadBlobsSize();
									}
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
							Clob clob = _rs.getClob(idx);
							if (clob != null)
							{
								int len = (int)clob.length();
								if (len > 0)
								{
									int charsToRead = len;
									if (!_largeObjInfo.getReadCompleteClobs())
									{
										charsToRead = _largeObjInfo.getReadClobsSize();
									}
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

					case Types.OTHER:
						if (_largeObjInfo.getReadSQLOther())
						{
							row[i] = _rs.getObject(idx);
						}
						else
						{
							row[i] = "<Other>";
						}
						break;

					default:
						if (_largeObjInfo.getReadAllOther())
						{
							row[i] = _rs.getObject(idx);
						}
						else
						{
							row[i] = "<Unknown(" + columnType + ")>";
						}
				}
			}
			catch (Throwable th)
			{
				_errorOccured = true;
				row[i] = "<Error>";
				StringBuffer msg = new StringBuffer("Error reading column data");
				msg.append(", column index = ").append(idx);
				s_log.error(msg.toString(), th);
			}
		}

		return row;
	}
}
