package net.sourceforge.squirrel_sql.plugins.mysql.tab;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseObjectTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetScrollingPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.MapDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

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

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(BaseSQLTab.class);
    
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
                    _comp.load(createDataSetFromResultSet(rs), destClassName);				
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
            // 1385270 (Dropping multiple selected tables produces exceptions)
            //
			// This exception is thrown when a group of tables is dropped in a 
            // MySQL session and the MySQL Columns or MySQL Indexes tab was the
            // last tab selected.  For now, just log the error, don't show it
            // in the status until we figure out where the race condition is that
            // causes the tab to get refreshed against a table that was just 
            // dropped.  There may be other valid reasons for an exception here,
            // so we can't just squelch it.
            // TODO: Figure out where the race condition is that causes the tab 
            // to get refreshed against a table that was just dropped. When we
            // have solved that, then put the following line back in and remove
            // the error logging here:
            //
            // throw new DataSetException(ex);
            
            // i18n[mysql.error.refreshcomponent=Unable to refresh MySQL plugin tab
            String msg = s_stringMgr.getString("mysql.error.refreshcomponent");
            s_log.error(msg, ex);
		}
	}

	protected abstract String getSQL() throws SQLException;

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

