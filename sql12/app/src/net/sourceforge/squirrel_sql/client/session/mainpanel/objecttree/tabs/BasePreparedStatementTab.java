package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetScrollingPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.MapDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseObjectTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public abstract class BasePreparedStatementTab extends BaseObjectTab
{
	/** Title to display for tab. */
	private final String _title;

	/** Hint to display for tab. */
	private final String _hint;

	private boolean _firstRowOnly;

	/** Component to display in tab. */
	private DataSetScrollingPanel _comp;

	public BasePreparedStatementTab(String title, String hint)
	{
		this(title, hint, false);
	}

	public BasePreparedStatementTab(String title, String hint, boolean firstRowOnly)
	{
		super();
		if (title == null)
		{
			throw new IllegalArgumentException("Title == null");
		}
		_title = title;
		_hint = hint != null ? hint : title;
		_firstRowOnly = firstRowOnly;
	}

	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle()
	{
		return _title;
	}

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint()
	{
		return _hint;
	}

	public void clear()
	{
	}

	public Component getComponent()
	{
		if (_comp == null)
		{
			_comp = new DataSetScrollingPanel();
		}
		return _comp;
	}

	protected void refreshComponent() throws DataSetException
	{
		ISession session = getSession();
		if (session == null)
		{
			throw new IllegalStateException("Null ISession");
		}
		try
		{
			PreparedStatement pstmt = createStatement();
			try
			{
				ResultSet rs = pstmt.executeQuery();
				try
				{
					final SessionProperties props = session.getProperties();
					final String destClassName = props.getMetaDataOutputClassName();
					final IDataSet ds = createDataSetFromResultSet(rs);
					_comp.load(ds, destClassName);
				}
				finally
				{
					rs.close();
				}
			}
			finally
			{
				pstmt.close();
			}
		}
		catch (SQLException ex)
		{
			throw new DataSetException(ex);
		}
	}

	protected abstract PreparedStatement createStatement() throws SQLException;

	protected IDataSet createDataSetFromResultSet(ResultSet rs)
		throws DataSetException
	{
		final ResultSetDataSet rsds = new ResultSetDataSet();
		rsds.setResultSet(rs);
		if (!_firstRowOnly)
		{
			return rsds;
		}

		final int columnCount = rsds.getColumnCount();
		final ColumnDisplayDefinition[] colDefs = rsds.getDataSetDefinition().getColumnDefinitions();
		final Map data = new HashMap();
		if (rsds.next(null))
		{
			for (int i = 0; i < columnCount; ++i)
			{
				data.put(colDefs[i].getLabel(), rsds.get(i));
			}
		}
		return new MapDataSet(data);

	}
}
