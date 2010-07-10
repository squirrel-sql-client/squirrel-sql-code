package net.sourceforge.squirrel_sql.fw.datasetviewer;
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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ResultSetDataSet implements IDataSet
{
	private ILogger s_log =
		LoggerController.createLogger(ResultSetDataSet.class);

	// TODO: These 2 should be handled with an Iterator.
	private int _iCurrent = -1;
	private Object[] _currentRow;

	private int _columnCount;
	private DataSetDefinition _dataSetDefinition;
	private List _alData;

//	private LargeResultSetObjectInfo _largeObjInfo = new LargeResultSetObjectInfo();

	public ResultSetDataSet() throws DataSetException
	{
//		this(null, null);
		super();
	}

//	public ResultSetDataSet(ResultSet rs) throws DataSetException
//	{
//		this(rs, null);
//	}

//	public ResultSetDataSet(ResultSet rs, int[] columnIndices)
//		throws IllegalArgumentException, DataSetException
//	{
//		super();
//		setResultSet(rs, columnIndices);
//	}

	public void setResultSet(ResultSet rs) throws DataSetException
	{
		setResultSet(rs, null, null);
	}

	public void setResultSet(ResultSet rs, LargeResultSetObjectInfo largeObjInfo)
		throws DataSetException
	{
		setResultSet(rs, largeObjInfo, null);
	}

	public void setResultSet(ResultSet rs, int[] columnIndices)
		throws DataSetException
	{
		setResultSet(rs, null, columnIndices);
	}

	public void setResultSet(ResultSet rs, LargeResultSetObjectInfo largeObjInfo,
								int[] columnIndices)
		throws DataSetException
	{
		if (largeObjInfo == null)
		{
			largeObjInfo = new LargeResultSetObjectInfo();
		}

		if (columnIndices != null && columnIndices.length == 0)
		{
			columnIndices = null;
		}
		_iCurrent = -1;
		_alData = new ArrayList();

		if (rs != null)
		{
			try
			{
				ResultSetMetaData md = rs.getMetaData();
				_columnCount = columnIndices != null
								? columnIndices.length
								: md.getColumnCount();
				ColumnDisplayDefinition[] colDefs = createColumnDefinitions(md, columnIndices);
				_dataSetDefinition = new DataSetDefinition(colDefs);

				ResultSetReader rdr = new ResultSetReader(rs, largeObjInfo, columnIndices);
				Object[] row = null;
				while ((row = rdr.readRow()) != null)
				{
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
		// TODO: This should be handled with an Iterator
		if (++_iCurrent < _alData.size())
		{
			_currentRow = (Object[])_alData.get(_iCurrent);
			return true;
		}
		return false;
	}

	public Object get(int columnIndex)
	{
		return _currentRow[columnIndex];
	}

//	public void setLargeResultSetObjectInfo(LargeResultSetObjectInfo value)
//	{
//		_largeObjInfo = value != null ? value : new LargeResultSetObjectInfo();
//	}

	private ColumnDisplayDefinition[] createColumnDefinitions(ResultSetMetaData md,
							int[] columnIndices) throws SQLException
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
