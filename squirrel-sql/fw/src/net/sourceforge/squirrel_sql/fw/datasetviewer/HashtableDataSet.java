package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001 - 2002 Colin Bell
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
import java.util.Hashtable;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.util.EnumerationIterator;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class HashtableDataSet implements IDataSet
{
	private interface HashtableDataSetI18n
	{
		String UNSUPPORTED = "<Unsupported>";
		String NAME_COLUMN = "Key";
		String VALUE_COLUMN = "Value";
	}

	private Hashtable _src;
//	private final int _columnCount = 2;
	private DataSetDefinition _dsDef;
	private final static String[] s_hdgs =
		new String[] {
			HashtableDataSetI18n.NAME_COLUMN,
			HashtableDataSetI18n.VALUE_COLUMN };
	private final static int[] s_hdgLens = new int[] { 30, 100 };
	private String[] _curRow = new String[2];
	private Iterator _rowKeys;

	public HashtableDataSet(Hashtable src) throws DataSetException
	{
		super();
		_src = src;
		_dsDef = new DataSetDefinition(createColumnDefinitions());
		_rowKeys = new EnumerationIterator(_src.keys());
	}

	public final int getColumnCount()
	{
		return s_hdgs.length;
	}

	public DataSetDefinition getDataSetDefinition()
	{
		return _dsDef;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
	{
		_curRow[0] = null;
		if (_rowKeys.hasNext())
		{
			_curRow[0] = (String) _rowKeys.next();
		}
		if (_curRow[0] != null)
		{
			_curRow[1] = _src.get(_curRow[0]).toString();
		}
		return _curRow[0] != null;
	}

	public Object get(int columnIndex)
	{
		return _curRow[columnIndex];
	}

	private ColumnDisplayDefinition[] createColumnDefinitions()
	{
		final int columnCount = getColumnCount();
		ColumnDisplayDefinition[] columnDefs =
			new ColumnDisplayDefinition[columnCount];
		for (int i = 0; i < columnCount; ++i)
		{
			columnDefs[i] =
				new ColumnDisplayDefinition(s_hdgLens[i], s_hdgs[i]);
		}
		return columnDefs;
	}
}
