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
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class ObjectArrayDataSet implements IDataSet
{
	private Object[][] _src;
	private DataSetDefinition _dsDef;

	private Object[] _curRow;
	private int _curIndex = -1;
	private int _columnCount;

	public ObjectArrayDataSet(Object[] src) throws DataSetException
	{
		this(convert(src));
	}

	public ObjectArrayDataSet(Object[][] src) throws DataSetException
	{
		super();
		if (src == null)
		{
			throw new IllegalArgumentException("Null Object[][] passed");
		}
		_src = src;
		for (int i = 0; i < src.length; ++i)
		{
			if (src[i] != null && src[i].length > _columnCount)
			{
				_columnCount = src[i].length;
			}
		}
		_dsDef = new DataSetDefinition(createColumnDefinitions());
	}

	public final int getColumnCount()
	{
		return _columnCount;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dsDef;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
	{
		_curRow = null;
		if (_src.length > (_curIndex + 1))
		{
			_curRow = _src[++_curIndex];
			return true;
		}
		return false;
	}

	public synchronized Object get(int columnIndex)
	{
		if (_curRow != null && columnIndex < _curRow.length)
		{
			return _curRow[columnIndex];
		}
		return null;
	}

	private ColumnDisplayDefinition[] createColumnDefinitions()
	{
		final int columnCount = getColumnCount();
		ColumnDisplayDefinition[] columnDefs =
			new ColumnDisplayDefinition[columnCount];
		for (int i = 0; i < columnCount; ++i)
		{
			columnDefs[i] = new ColumnDisplayDefinition(100, " ");
		}
		return columnDefs;
	}

	private static Object[][] convert(Object[] src)
	{
		Object[][] dsInfo = new Object[src.length][1];
		for (int i = 0; i < src.length; ++i)
		{
			dsInfo[i][0] = src[i];
		}
		return dsInfo;
	}
}
