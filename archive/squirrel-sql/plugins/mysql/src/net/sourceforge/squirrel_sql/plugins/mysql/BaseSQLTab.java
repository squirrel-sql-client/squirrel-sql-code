package net.sourceforge.squirrel_sql.plugins.mysql;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetScrollingPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.LargeResultSetObjectInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.MapDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseObjectTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

abstract class BaseSQLTab extends BaseObjectTab
{
	/** Title to display for tab. */
	private final String _title;

	/** Hint to display for tab. */
	private final String _hint;

	private boolean _firstRowOnly;

	/** Component to display in tab. */
	private DataSetScrollingPanel _comp;

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(BaseSQLTab.class);

	public BaseSQLTab(String title, String hint)
	{
		this(title, hint, false);
	}

	public BaseSQLTab(String title, String hint, boolean firstRowOnly)
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
		final ISession session = getSession();
		if (session == null)
		{
			throw new IllegalStateException("Null ISession");
		}

		try
		{
			Statement stmt = session.getSQLConnection().createStatement();
			try
			{
				ResultSet rs = stmt.executeQuery(getSQL());
				try
				{
					final SessionProperties props = session.getProperties();
					final String destClassName = props.getMetaDataOutputClassName();
					final LargeResultSetObjectInfo rsoi = props.getLargeResultSetObjectInfo();
					_comp.load(createDataSetFromResultSet(rs, rsoi), destClassName);
				}
					finally
				{
					rs.close();
				}
			}
			finally
			{
				stmt.close();
			}
		}
		catch (SQLException ex)
		{
			throw new DataSetException(ex);
		}
	}

	protected abstract String getSQL() throws SQLException;

	protected IDataSet createDataSetFromResultSet(ResultSet rs,
				LargeResultSetObjectInfo rsoi) throws DataSetException
	{
		final ResultSetDataSet rsds = new ResultSetDataSet();
		rsds.setResultSet(rs, rsoi);
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
