package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
/**
 * This is the tab showing the contents (data) of the table.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ContentsTab extends BaseTableTab
	implements IDataSetUpdateableTableModel
{

	/**
	 * Name of the table that this tab displayed last time it was loaded.
	 * This is needed to prevent an on-demand edit operation from turning
	 * all data into editable tables.
	 * The initial value of "" allows us to dispense with a check for null on the first pass.
	 */
	String previousTableName = "";
	
	/**
	 * We need to save the name of the SessionProperties display class at the time
	 * that the table was forced into edit mode so that if the properties get changed
	 * while we are in forced edit mode, we will change back to match the new
	 * Session Properties.
	 */
	String sqlOutputClassNameAtTimeOfForcedEdit = "";

	/**
	 * Remember whether or not the user has forced us into editing mode
	 * when the SessionProperties says to use read-only mode.
	 */
	boolean editModeForced = false;

	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String TITLE = "Content";
		String HINT = "Sample Contents";
	}

	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(ContentsTab.class);

	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle()
	{
		return i18n.TITLE;
	}

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint()
	{
		return i18n.HINT;
	}

	/**
	 * Create the <TT>IDataSet</TT> to be displayed in this tab.
	 */
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISession session = getSession();
		final SQLConnection conn = session.getSQLConnection();

		try
		{
			final Statement stmt = conn.createStatement();
			try
			{
				final SessionProperties props = session.getProperties();
				if (props.getContentsLimitRows())
				{
					try
					{
						stmt.setMaxRows(props.getContentsNbrRowsToShow());
					}
					catch (Exception ex)
					{
						s_log.error("Error on Statement.setMaxRows()", ex);
					}
				}
				final ITableInfo ti = getTableInfo();
				
				/**
				 * When the SessionProperties are set to read-only (either table or text)
				 * but the user has selected "Make Editable" on the Popup menu, we want
				 * to limit the edit capability to only that table, and only for as long
				 * as the user is looking at that one table.  When the user switches away
				 * to another table, that new table should not be editable.
				 */
				final String currentTableName = ti.getQualifiedName();
				if ( ! currentTableName.equals(previousTableName)){
					previousTableName = currentTableName;	// needed to prevent an infinite loop
					editModeForced = false;	// edit mode applied only to previous table
					
					/**
					 * Tell the GUI to rebuild itself.
					 * Unfortunately, this has the side effect of calling this same function
					 * another time.  The second call does not seem to be a problem,
					 * but we need to have reset the previousTableName before makeing
					 * this call or we will be in an infinite loop.
					 */
					props.forceSQLOutputClassNameChange();
				}

				final ResultSet rs = stmt.executeQuery("select * from "
													+ ti.getQualifiedName());
				final ResultSetDataSet rsds = new ResultSetDataSet();
				rsds.setResultSet(rs, props.getLargeResultSetObjectInfo());
				return rsds;
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


	/**
	 * If the user forces us into edit mode, remember that they did so for this table.
	 */
	public void forceEditMode()
	{
		editModeForced = true;
		sqlOutputClassNameAtTimeOfForcedEdit = 
			getSession().getProperties().getSQLResultsOutputClassName();
		/**
		 * Tell the GUI to rebuild itself.
		 * This is not a clean way to do that, since we are telling the
		 * SessionProperties listeners that a property has changed when
		 * in reality none of them have done so, but this does cause the
		 * GUI to be rebuilt.
		 */
		getSession().getProperties().forceSQLOutputClassNameChange();
	}

	/**
	 * If the user has forced us into editing mode, use the EDITABLE_TABLE form, but
	 * otherwise use whatever form the user specified in the Session Preferences.
	 */
	protected String getDestinationClassName()
	{
		if (editModeForced)
		{
			if (getSession().getProperties().getSQLResultsOutputClassName().equals(
				sqlOutputClassNameAtTimeOfForcedEdit))
			{
				return getSession().getProperties().getEditableTableOutputClassName();
			}
			// forced edit mode ended because user changed the Session Properties
			editModeForced = false;
		}

		// if the user selected Editable Table in the Session Properties,
		// then the display will be an editable table; otherwise the display is read-only
		return getSession().getProperties().getSQLResultsOutputClassName();
	}
	
	/**
	 * link from fw to this for updating data
	 */
	public boolean updateTableComponent(int row, int col, Object newValue, Object oldValue)
	{

//?????????????????????????????????????????????????????????????????
//
// This is where we do the actual DB update
//
//????????????????????????????????????????????????????????????????????

		return true;
	}
}
