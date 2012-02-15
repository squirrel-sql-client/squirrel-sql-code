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

import net.sourceforge.squirrel_sql.client.session.DefaultDataModelImplementationDetails;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetScrollingPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.MapDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

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

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(BasePreparedStatementTab.class);

	public BasePreparedStatementTab(String title, String hint) {
		this(title, hint, false);
	}

	public BasePreparedStatementTab(String title, String hint, boolean firstRowOnly) {
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
	 * @return The title for the tab.
	 */
	public String getTitle()
	{
		return _title;
	}

	/**
	 * Return the hint for the tab.
	 * 
	 * @return The hint for the tab.
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
			ISession session = getSession();
			SessionProperties props = session.getProperties();
			String destClassName = props.getMetaDataOutputClassName();
			try
			{
				_comp = new DataSetScrollingPanel(destClassName, null, new DefaultDataModelImplementationDetails(session));
			} catch (Exception e)
			{
				s_log.error("Unexpected exception from call to getComponent: " + e.getMessage(), e);
			}

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
		PreparedStatement pstmt = null;
		ResultSet rs = null;

      try
      {
         pstmt = createStatement();
         rs = pstmt.executeQuery();
         final IDataSet ds = createDataSetFromResultSet(rs);
         _comp.load(ds, new DefaultDataModelImplementationDetails(session));
      }
      catch (SQLException ex)
      {
         throw new DataSetException(ex);
      }
      finally
      {
         SQLUtilities.closeResultSet(rs, true);
      }
   }

	/**
	 * Subclasses must implement this to provide a PreparedStatement that has it's parameter values bound and
	 * is ready to be executed. It will be used in refreshComponent to load a DataSet into this tab.
	 * 
	 * @return the PreparedStatement to execute.
	 * 
	 * @throws SQLException
	 */
	protected abstract PreparedStatement createStatement() throws SQLException;

	protected IDataSet createDataSetFromResultSet(ResultSet rs) throws DataSetException
	{
		final ResultSetDataSet rsds = new ResultSetDataSet();
		rsds.setResultSet(rs, getDialectType());
		if (!_firstRowOnly)
		{
			return rsds;
		}

		final int columnCount = rsds.getColumnCount();
		final ColumnDisplayDefinition[] colDefs = rsds.getDataSetDefinition().getColumnDefinitions();
		final Map<String, Object> data = new HashMap<String, Object>();
		if (rsds.next(null))
		{
			for (int i = 0; i < columnCount; ++i)
			{
				data.put(colDefs[i].getColumnName(), rsds.get(i));
			}
		}
		return new MapDataSet(data);

	}
}
