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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;

/**
 * Represents the list of functions and SQL keywords as a one-column data set.
 */
public class MetaDataListDataSet implements IDataSet
{
	private interface i18n
	{
		String UNSUPPORTED = "<Unsupported>";
		String NAME_COLUMN = "Name";
		//		String NULL = "<null>";
		String VALUE_COLUMN = "Value";
	}

	private final static String[] s_hdgs = new String[] { i18n.NAME_COLUMN };
	private DataSetDefinition _dsDef;
	private Iterator _rowIter;
	private List _row = new ArrayList();
	private String _rowElem;
	private IMessageHandler _msgHandler;

	public MetaDataListDataSet(String functionList) throws DataSetException
	{
		this(functionList, null);
	}

	public MetaDataListDataSet(String functionList, IMessageHandler msgHandler)
		throws DataSetException
	{
		super();
		_msgHandler = msgHandler != null ? msgHandler : NullMessageHandler.getInstance();
		_dsDef = new DataSetDefinition(createColumnDefinitions());
		load(functionList);
	}

	public int getColumnCount()
	{
		return s_hdgs.length;
	}

	public DataSetDefinition getDataSetDefinition() throws DataSetException
	{
		return _dsDef;
	}

	public synchronized boolean next(IMessageHandler msgHandler)
		throws DataSetException
	{
		if (_rowIter.hasNext())
		{
			_rowElem = (String) _rowIter.next();
			return true;
		}
		return false;
	}

	public synchronized Object get(int columnIndex) throws DataSetException
	{
		if (columnIndex == 0)
		{
			return _rowElem;
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
			columnDefs[i] = new ColumnDisplayDefinition(200, s_hdgs[i]);
		}
		return columnDefs;
	}

	private void load(String functionList)
	{
		if (functionList != null)
		{
			StringTokenizer st = new StringTokenizer(functionList, ",");
			while (st.hasMoreTokens())
			{
				_row.add(st.nextToken());
			}
			Collections.sort(_row);
		}
		_rowIter = _row.iterator();
	}
}
