package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Component;


import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.gui.TablePopupMenu;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetScrollingPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;


import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.OrderByClausePanel;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.SQLFilterClauses;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.WhereClausePanel;
import net.sourceforge.squirrel_sql.client.session.properties.EditWhereCols;

/**
 * This is the tab showing the contents (data) of the table.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ContentsTab extends BaseTableTab
	implements IDataSetUpdateableTableModel
{
	public static String TITLE = i18n.TITLE;

	/**
	 * Name of the table that this tab displayed last time it was loaded.
	 * This is needed to prevent an on-demand edit operation from turning
	 * all data into editable tables.
	 * The initial value of "" allows us to dispense with a check for null
	 * on the first pass.
	 */
	String previousTableName = "";
	
	/**
	 * This is the long name of the current table including everything that might be able to distinguish it
	 * from another table of the same name in a different DB.
	 */
	String fullTableName = null;

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
	 * Remember which column contains the rowID; if no rowID, this is -1
	 * which does not match any legal column index.
	 */
	int _rowIDcol = -1;

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
	private static final ILogger s_log =
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
	 * This inner class defines a pop-up menu with only one item, "insert", which
	 * allows the user to add a new row to an empty table.
	 */
	class ContentsTabPopupMenu extends TablePopupMenu
	{
		public ContentsTabPopupMenu(DataSetViewerTablePanel viewer) {
			super(viewer.isTableEditable(), ContentsTab.this, viewer);
			removeAll();	// the normal constructor creates a bunch of entries we do not want
			add(_insertRow);
		}
	}
	
	/**
	 * Override the parent's getComponent method so that we can
	 * attach a menu to the ContentsTab pane that allows the user
	 * to insert a new row when the table is empty.
	 */
	public Component getComponent(){
		final Component c = super.getComponent();
		
		if (c != null) {
			// remove any previously set listeners
			MouseListener[] oldListeners = c.getMouseListeners();
			for (int i=0; i< oldListeners.length; i++)
				c.removeMouseListener(oldListeners[i]);
			// If the viewer is a table AND table iseditable, add a listener using the current viewer
			if (((DataSetScrollingPanel)c).getViewer() instanceof DataSetViewerTablePanel) {
				final DataSetViewerTablePanel viewer =
							(DataSetViewerTablePanel)((DataSetScrollingPanel)c).getViewer();
				if (viewer.isTableEditable()) {
					c.addMouseListener(new MouseAdapter()
					{
						public void mousePressed(MouseEvent evt)
						{
							// in theory, we should only need to check the PopupTrigger, but
							// at least one user had a problem where they clicked on the right
							// mouse button and the event was not classified as the PopupTrigger.
							// It is unknown why that occured, but we needed to add the second
							// test (button3&&clickcount=1) to resolve that issue.
							if (evt.isPopupTrigger() || (evt.getButton()==3 && evt.getClickCount() == 1))
								new ContentsTabPopupMenu(viewer).show(evt);
						}
					});
				}
			}
		}

		return c;
	}	
	
	/**
	 * return the name of the table that is unambiguous across DB accesses,
	 * including the same DB on different machines.
	 * This function is static because it is used elsewhere to generate the same
	 * name as is used within instances of this class.
	 * 
	 * @return the name of the table that is unique for this DB access
	 */
	public static String getUnambiguousTableName(ISession session, String name) {
		return session.getAlias().getUrl()+":"+name;
	}
	
	/**
	 * Get the full name of this table, creating that name the first time we are called
	 */
	private String getFullTableName() {
		if (fullTableName == null) {
			try {
				final ISession session = getSession();
				final String name = getTableInfo().getQualifiedName();
				fullTableName = getUnambiguousTableName(session, name);
			}
			catch (Exception e) {
					// not sure what to do with this exception???
			}
		}
		return fullTableName;
	}
	

	/**
	 * Create the <TT>IDataSet</TT> to be displayed in this tab.
	 */
	protected IDataSet createDataSet() throws DataSetException
	{
		final ISession session = getSession();
		final SQLConnection conn = session.getSQLConnection();
		final SQLFilterClauses sqlFilterClauses = session.getSQLFilterClauses();

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
				if (!currentTableName.equals(previousTableName))
				{
					previousTableName = currentTableName;	// needed to prevent an infinite loop
					editModeForced = false;	// edit mode applied only to previous table

					/**
					 * Tell the GUI to rebuild itself.
					 * Unfortunately, this has the side effect of calling this same function
					 * another time.  The second call does not seem to be a problem,
					 * but we need to have reset the previousTableName before makeing
					 * this call or we will be in an infinite loop.
					 */
					props.forceTableContentsOutputClassNameChange();
				}

				/**
				 * If the table has a pseudo-column that is the best unique
				 * identifier for the rows (like Oracle's rowid), then we
				 * want to include that field in the query so that it will
				 * be available if the user wants to edit the data later.
				 */
				String pseudoColumn = "";

				try
				{
					ResultSet rowIdentifierRS = conn.getSQLMetaData().getBestRowIdentifier(ti);
					try
					{
						while (rowIdentifierRS.next())
						{
							// according to spec, col 8 is indicator of pseudo/not-pseudo
							// and col 2 is name of rowid column
							short pseudo = rowIdentifierRS.getShort(8);
							if (pseudo == DatabaseMetaData.bestRowPseudo)
							{
								pseudoColumn = " ," + rowIdentifierRS.getString(2);
								break;
							}
						}
					}
					finally
					{
						rowIdentifierRS.close();
					}
				}

				// Some DBMS's (EG Think SQL) throw an exception on a call to
				// getBestRowIdentifier.
				catch (Throwable th)
				{
					s_log.debug("getBestRowIdentifier not supported", th);
				}

				// TODO: - Col - Add method to Databasemetadata that returns array
				// of objects for getBestRowIdentifier. For PostgreSQL put this kludge in
				// the new function. THis way all the kludges are kept in one place.
				//
				// KLUDGE!!!!!!
				//
				// For some DBs (e.g. PostgreSQL) there is actually a pseudo-column
				// providing the rowId, but the getBestRowIdentifier function is not
				// implemented.  This kludge hardcodes the knowledge that specific
				// DBs use a specific pseudo-column.
				//
				if (pseudoColumn.length() == 0)
				{
					String dbName = conn.getSQLMetaData().getDatabaseProductName().toUpperCase();
					if (dbName.equals("POSTGRESQL"))
					{
						pseudoColumn = ", oid";
					}
				}

				ResultSet rs = null;
				try
				{
					// Note. Some DBMSs such as Oracle do not allow:
					// "select *, rowid from table"
					// You cannot have any column name in the columns clause
					// if you have * in there. Aliasing the table name seems to
					// be the best way to get around the problem.
					final StringBuffer buf = new StringBuffer();
					buf.append("select tbl.*")
						.append(pseudoColumn)
						.append(" from ")
						.append(ti.getQualifiedName())
						.append(" tbl");

					String clause = sqlFilterClauses.get(WhereClausePanel.getClauseIdentifier(), ti.getQualifiedName());
					if ((clause != null) && (clause.length() > 0))
					{
					  buf.append(" where ").append(clause);
					}
					clause = sqlFilterClauses.get(OrderByClausePanel.getClauseIdentifier(), ti.getQualifiedName());
					if ((clause != null) && (clause.length() > 0))
					{
					  buf.append(" order by ").append(clause);
					}

					rs = stmt.executeQuery(buf.toString());
				}
				catch (SQLException ex)
				{
					if (pseudoColumn.length() == 0)
					{
						throw ex;
					}

					// Some tables have pseudo column primary keys and others
					// do not.  JDBC on some DBMSs does not handle pseudo
					// columns 'correctly'.  Also, getTables returns 'views' as
					// well as tables, so the thing we are looking at might not
					// be a table. (JDBC does not give a simple way to
					// determine what we are looking at since the type of
					// object is described in a DBMS-specific encoding.)  For
					// these reasons, rather than testing for all these
					// conditions, we just try using the pseudo column info to
					// get the table data, and if that fails, we try to get the
					// table data without using the pseudo column.
					// TODO: Should we change the mode from editable to
					// non-editable?
					s_log.debug("Error querying using pseudo column", ex);
					final StringBuffer buf = new StringBuffer();
					buf.append("select *")
						.append(" from ")
						.append(ti.getQualifiedName())
						.append(" tbl");

					String clause = sqlFilterClauses.get(WhereClausePanel.getClauseIdentifier(), ti.getQualifiedName());
					if ((clause != null) && (clause.length() > 0))
					{
					  buf.append(" where ").append(clause);
					}
					clause = sqlFilterClauses.get(OrderByClausePanel.getClauseIdentifier(), ti.getQualifiedName());
					if ((clause != null) && (clause.length() > 0))
					{
					  buf.append(" order by ").append(clause);
					}

					rs = stmt.executeQuery(buf.toString());
				}

				final ResultSetDataSet rsds = new ResultSetDataSet();

				// to allow the fw to save and reload user options related to
				// specific columns, we construct a unique name for the table
				// so the column can be associcated with only that table.
				// Some drivers do not provide the catalog or schema info, so
				// those parts of the name will end up as null.  That's ok since
				// this string is never viewed by the user and is just used to
				// distinguish this table from other tables in the DB.
				// We also include the URL used to connect to the DB so that
				// the same table/DB on different machines is treated differently.
				rsds.setContentsTabResultSet(rs, getFullTableName());

				// KLUDGE:
				// We want some info about the columns to be available for validating the
				// user input during cell editing operations.  Ideally we would get that
				// info inside the ResultSetDataSet class during the creation of the
				// columnDefinition objects by using various functions in ResultSetMetaData
				// such as isNullable(idx).  Unfortunately, in at least some DBMSs (e.g.
				// Postgres, HSDB) the results of those calls are not the same (and are less accurate
				// than) the SQLMetaData.getColumns() call used in ColumnsTab to get the column info.
				// Even more unfortunate is the fact that the set of attributes reported on by the two
				// calls is not the same, with the ResultSetMetadata listing things not provided by
				// getColumns.  Most of the data provided by the ResultSetMetaData calls is correct.
				// However, the nullable/not-nullable property is not set correctly in at least two
				// DBMSs, while it is correct for those DBMSs in the getColumns() info.  Therefore,
				// we collect the collumn nullability information from getColumns() and pass that
				// info to the ResultSet to override what it got from the ResultSetMetaData.
				final ResultSet columnRS = conn.getSQLMetaData().getColumns(getTableInfo());
				try
				{
					final ColumnDisplayDefinition[] colDefs = rsds.getDataSetDefinition().getColumnDefinitions();
	
					// get the nullability information and pass it into the ResultSet
					// Unfortunately, not all DBMSs provide the column number in object 17 as stated in the
					// SQL documentation, so we have to guess that the result set is in column order
					int columnNumber = 0;
					while (columnRS.next()) {
						boolean isNullable = true;
						if (columnRS.getInt(11) == DatabaseMetaData.columnNoNulls)
							isNullable = false;
						colDefs[columnNumber++].setIsNullable(isNullable);
					}
				}
				finally
				{
					columnRS.close();
				}

				//?? remember which column is the rowID (if any) so we can
				//?? prevent editing on it
				if (pseudoColumn.length() > 0)
				{
					_rowIDcol = rsds.getColumnCount() - 1;
				}

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
	public void forceEditMode(boolean mode)
	{
		editModeForced = mode;
		sqlOutputClassNameAtTimeOfForcedEdit = 
			getSession().getProperties().getTableContentsOutputClassName();

		/**
		 * Tell the GUI to rebuild itself.
		 * This is not a clean way to do that, since we are telling the
		 * SessionProperties listeners that a property has changed when
		 * in reality none of them have done so, but this does cause the
		 * GUI to be rebuilt.
		 */
		getSession().getProperties().forceTableContentsOutputClassNameChange();
	}
	
	/**
	 * The fw needs to know whether we are in forced edit mode or not
	 * so it can decide whether or not to let the user undo that mode.
	 */
	public boolean editModeIsForced()
	{
		return editModeForced;
	}

	/**
	 * If the user has forced us into editing mode, use the EDITABLE_TABLE form, but
	 * otherwise use whatever form the user specified in the Session Preferences.
	 */
	protected String getDestinationClassName()
	{
		if (editModeForced)
		{
			if (getSession().getProperties().getTableContentsOutputClassName().equals(
				sqlOutputClassNameAtTimeOfForcedEdit))
			{
				return getSession().getProperties().getEditableTableOutputClassName();
			}
			// forced edit mode ended because user changed the Session Properties
			editModeForced = false;
		}

		// if the user selected Editable Table in the Session Properties,
		// then the display will be an editable table; otherwise the display is read-only
		return getSession().getProperties().getTableContentsOutputClassName();
	}

	/**
	 * Link from fw to check on whether there are any unusual conditions
	 * in the current data that the user needs to be aware of before updating.
	 */
	public String getWarningOnCurrentData(
		Object[] values, 
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object oldValue)
	{
		String whereClause = getWhereClause(values, colDefs, col, oldValue);

		// It is possible for a table to contain only columns of types that
		// we cannot process or do selects on, so check for that.
		// Since this check is on the structure of the table rather than the contents,
		// we only need to do it once (ie: it is not needed in getWarningOnProjectedUpdate)
		if (whereClause.length() == 0)
			return "The table has no columns that can be SELECTed on.\nAll rows will be updated.\nDo you wish to proceed?";

		final ISession session = getSession();
		final SQLConnection conn = session.getSQLConnection();

		int count = -1;	// start with illegal number of rows matching query

		try
		{
			final Statement stmt = conn.createStatement();
			try
			{
				final ITableInfo ti = getTableInfo();
				final ResultSet rs = stmt.executeQuery("select count(*) from "
									+ ti.getQualifiedName() + whereClause);
				rs.next();
				count = rs.getInt(1);
			}
			finally
			{
				stmt.close();
			}
		}
		catch (SQLException ex)
		{
			return "Exception seen during check on DB.  Exception was:\n"+
				ex.getMessage() +
				"\nUpdate is probably not safe to do.\nDo you wish to proceed?";
		}

		if (count == -1)
			return "Unknown error during check on DB.  Update is probably not safe.\nDo you wish to proceed?";

		if (count == 0)
			return "This row in the Database has been changed since you refreshed the data.\nNo rows will be updated by this operation.\nDo you wish to proceed?";

		if (count > 1)
			return "This operation will update " + count + " identical rows.\nDo you wish to proceed?";

		// no problems found, so do not return a warning message.
		return null;	// nothing for user to worry about
	}

	/**
	 * Link from fw to check on whether there are any unusual conditions
	 * that will occur after the update has been done.
	 */
	public String getWarningOnProjectedUpdate(
		Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object newValue)
	{

		String whereClause = getWhereClause(values, colDefs, col, newValue);

		final ISession session = getSession();
		final SQLConnection conn = session.getSQLConnection();

		int count = -1;	// start with illegal number of rows matching query

		try
		{
			final Statement stmt = conn.createStatement();
			try
			{
				final ITableInfo ti = getTableInfo();
				final ResultSet rs = stmt.executeQuery("select count(*) from "
									+ ti.getQualifiedName() + whereClause);
				rs.next();
				count = rs.getInt(1);
			}
			finally
			{
				stmt.close();
			}
		}
		catch (SQLException ex)
		{
			return "Exception seen during check on DB.  Exception was:\n"+
				ex.getMessage() +
				"\nUpdate is probably not safe to do.\nDo you wish to proceed?";
		}

		if (count == -1)
			return "Unknown error during check on DB.  Update is probably not safe.\nDo you wish to proceed?";

		// There are some fields that cannot be used in a WHERE clause, either
		// because there cannot be an exact match (e.g. REAL, FLOAT), or
		// because we may not have the actual data in hand (BLOB/CLOB), or
		// because the data cannot be expressed in a string form (e.g. BINARY).
		// An update to one of those fields
		// will look like we are replacing one row with an identical row (because
		// we can only "see" the fields that we know how to do WHEREs on).  Therefore,
		// when we are updating them, there should be exactly one row that matches
		// all of our other fields, and when we are not updating one of these
		// special types of fields, there should be
		// no rows that exactly match our criteria (we hope).
		//
		// We determine whether this field is one that cannot be used in the WHERE
		// clause by checking the value returned for that field to use in the
		// WHERE clause.  Any field that can be used there will return something
		// of the form "<fieldName> = <value>", and a field that cannot be
		// used will return a null or zero-length string.
		if (CellComponentFactory.getWhereClauseValue(colDefs[col], values[col]) == null ||
		 	CellComponentFactory.getWhereClauseValue(colDefs[col], values[col]).length() == 0) {
				if (count > 1)
					return "This operation will result in " + count +" identical rows.\nDo you wish to proceed?";
		}
		else {
			// the field being updated is one whose contents
			//should be visible in the WHERE clause
			if (count > 0)
				return "This operation will result in " + count + " identical rows.\nDo you wish to proceed?";
		}

		// no problems found, so do not return a warning message.
		return null;	// nothing for user to worry about

	}

	/**
	 * Re-read the value for a single cell in the table, if possible.
	 * If there is a problem, the message has a non-zero length when this returns.
	 */
	public Object reReadDatum(
		Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		StringBuffer message) {
			
		// get WHERE clause
		// The -1 says to ignore the last arg and use the contents of the values array
		// for the column that we care about.  However, since the data in
		// that column has been limited, when getWhereClause calls that
		// DataType with that value, the DataType will see that the data has
		// been limited and therefore cannnot be used in the WHERE clause.
		// In some cases it may be possible for the DataType to use the
		// partial data, such as "matches <data>*", but that may not be
		// standard accross all Databases and thus may be risky.
		String whereClause = getWhereClause(values, colDefs, -1, null);

		final ISession session = getSession();
		final SQLConnection conn = session.getSQLConnection();
		
		Object wholeDatum = null;

		try
		{
			final Statement stmt = conn.createStatement();
			final ITableInfo ti = getTableInfo();
			final String queryString =
				"SELECT " + colDefs[col].getLabel() +" FROM "+ti.getQualifiedName() +
				whereClause;

			try
			{
				ResultSet rs = stmt.executeQuery(queryString);
				
				// There should be one row in the data, so try to move to it
				if (rs.next() == false) {
					// no first row, so we cannot retrieve the data
					throw new SQLException(
						"Could not find any row in DB matching current row in table");
				}
				
				// we have at least one row, so try to retrieve the object
				// Do Not limit the read of this data
				wholeDatum = CellComponentFactory.readResultSet(colDefs[col], rs, 1, false);

				//  There should not be more than one row in the DB that matches
				// the table, and if there is we cannot determine which one to read,
				// so check that there are no more
				if (rs.next() == true) {
					// multiple rows - not good
					wholeDatum = null;
					throw new SQLException(
						"Muliple rows in DB match current row in table - cannot re-read data.");
				}				
			}
			finally
			{
				stmt.close();
			}
		}
		catch (Exception ex)
		{
			message.append(
				"There was a problem reported while re-reading the DB.  The DB message was:\n"+
				ex.getMessage());
			
			// It would be nice to tell the user what happened, but if we try to
			// put up a dialog box at this point, we run into trouble in some
			// cases where the field continually tries to re-read after the dialog
			// closes (because it is being re-painted).
		}


		// return the whole contents of this column in the DB
		return wholeDatum; 
	};

	/**
	 * link from fw to this for updating data
	 */
	public String updateTableComponent(
		Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object oldValue,
		Object newValue)
	{
		// get WHERE clause using original value
		String whereClause = getWhereClause(values, colDefs, col, oldValue);

		final ISession session = getSession();
		final SQLConnection conn = session.getSQLConnection();

		int count = -1;

		try
		{
			final ITableInfo ti = getTableInfo();
			final String sql = "UPDATE " + ti.getQualifiedName() +
				" SET " + colDefs[col].getLabel() + " = ? " +
				whereClause;
			s_log.debug(sql);
			final PreparedStatement pstmt = conn.prepareStatement(sql);
			try
			{
				// have the DataType object fill in the appropriate kind of value
				// into the first (and only) variable position in the prepared stmt
				CellComponentFactory.setPreparedStatementValue(
					colDefs[col], pstmt, newValue, 1);
				count = pstmt.executeUpdate();
			}
			finally
			{
				pstmt.close();
			}
		}
		catch (SQLException ex)
		{
			return "There was a problem reported during the update.  The DB message was:\n"+
				ex.getMessage() +
				"\nThis may or may not be serious depending on the above message."+
				"\nThe data was probably not changed in the database."+
				"\nYou may need to refresh the table to get an accurate view of the current data.";
		}

		if (count == -1)
			return "Unknown problem during update.\nNo count of updated rows was returned.\nDatabase may be corrupted!";

		if (count == 0)
			return "No rows updated.";

		// everything seems to have worked ok
		return null;
	}


	/**
	 * Let fw get the rowIDcol
	 */
	public int getRowidCol()
	{
		return _rowIDcol;
	}


	/**
	 * helper function to create a WHERE clause to search the DB for matching rows.
	 * If the col number is < 0, then the colValue is ignored
	 * and the WHERE clause is constructed using only the values[].
	 */
	private String getWhereClause(
		Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object colValue)
	{

		StringBuffer whereClause = new StringBuffer("");
		
		// For tables that have a lot of columns, the user may have limited the set of columns
		// to use in the where clause, so see if there is a table of col names
		HashMap colNames = (EditWhereCols.get(getFullTableName()));

		for (int i=0; i< colDefs.length; i++) {
			
			// if the user has said to not use this column, then skip it
			if (colNames != null) {
				// the user has restricted the set of columns to use.
				// If this name is NOT in the list, then skip it; otherwise we fall through
				// and use the column in the WHERE clause
				if (colNames.get(colDefs[i].getLabel()) == null)
					continue;	// go on to the next item
			}

			// for the column that is being changed, use the value
			// passed in by the caller (which may be either the
			// current value or the new replacement value)
			Object value = values[i];
			if (i == col)
				value = colValue;

			// convert user representation of null into an actual null
			if (value != null && value.toString().equals("<null>"))
				value = null;

			// do different things depending on data type
			String clause = CellComponentFactory.getWhereClauseValue(colDefs[i], value);	

			if (clause != null && clause.length() > 0)
				if (whereClause.length() == 0)
				{
					whereClause.append(clause);
				}
				else
				{
					whereClause.append(" AND ");
					whereClause.append(clause);
				}
		}

		// insert the "WHERE" at the front if there is anything in the clause
		if (whereClause.length() == 0)
			return "";

		whereClause.insert(0, " WHERE ");
		return whereClause.toString();
	}


	/**
	 * Delete a set of rows from the DB.
	 * If the delete succeeded this returns a null string.
	 * The deletes are done within a transaction
	 * so they are either all done or all not done.
	 */
	public String deleteRows(Object[][] rowData, ColumnDisplayDefinition[] colDefs) {

		// get the SQL session
		final ISession session = getSession();
		final SQLConnection conn = session.getSQLConnection();

		// string used as error indicator and description of problems seen
		// when checking for 0 or mulitple matches in DB
		String rowCountErrorMessage = "";

		// for each row in table, count how many rows match where clause
		// if not exactly one, generate message describing situation
		for (int i = 0; i < rowData.length; i++) {
			// get WHERE clause for the selected row
			// the -1 says to just use the contents of the values without
			// any substitutions
			String whereClause = getWhereClause(rowData[i], colDefs, -1, null);

			// count how many rows this WHERE matches
			try {
				// do the delete and add the number of rows deleted to the count
				Statement stmt = conn.createStatement();

				ResultSet rs = stmt.executeQuery("SELECT count(*) FROM " +
					getTableInfo().getSimpleName()+whereClause);

				rs.next();
				if (rs.getInt(1) != 1) {
					if (rs.getInt(1) == 0)
						rowCountErrorMessage += "\n   Row "+ (i+1) +" did not match any row in DB";
					else
						rowCountErrorMessage += "\n   Row "+ (i+1) +" matched "+rs.getInt(1)+" rows in DB";
				}
			}
			catch (Exception e) {
				// some kind of problem - tell user
				return "While preparing for delete, saw exception:\n" + e;
			}
		}

		// if the rows do not match 1-for-1 to DB, ask user if they
		// really want to do delete
		if (rowCountErrorMessage.length() > 0) {
			int option = JOptionPane.showConfirmDialog(null,
				"There may be a mismatch between the table and the DB:\n"+ 
				rowCountErrorMessage +
				"\nDo you wish to proceed with the deletes anyway?",
				"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if ( option != JOptionPane.YES_OPTION) {
				return "Delete canceled at user request.";
			} 
		}

		// for each row in table, do delete and add to number of rows deleted from DB
		for (int i = 0; i < rowData.length; i++) {
			// get WHERE clause for the selected row
			// the -1 says to just use the contents of the values without
			// any substitutions
			String whereClause = getWhereClause(rowData[i], colDefs, -1, null);

			// try to delete
			try {
				// do the delete and add the number of rows deleted to the count
				Statement stmt = conn.createStatement();

				stmt.executeUpdate("DELETE FROM " +
					getTableInfo().getSimpleName()+whereClause);
			}
			catch (Exception e) {
				// some kind of problem - tell user
				return "One of the delete operations failed with exception:\n" + e +
						"\nDatabase is in an unknown state and may be corrupted.";
			}	
		}

		return null;	// hear no evil, see no evil
	}
	
	
	/**
	 * Let fw get the list of default values for the columns
	 * to be used when creating a new row
	 */
	public String[] getDefaultValues(ColumnDisplayDefinition[] colDefs) {
		// we return something valid even if there is a DB error
		String[] defaultValues = new String[colDefs.length];		
		
		final ITableInfo ti = getTableInfo();
		final ISession session = getSession();
		final SQLConnection conn = session.getSQLConnection();
		DatabaseMetaData dmd = null;
		try
		{
			dmd = conn.getSQLMetaData().getJDBCMetaData();
			ResultSet rs =
				dmd.getColumns(ti.getCatalogName(), ti.getSchemaName(),
					ti.getSimpleName(), "");
			
			// read the DB MetaData info and fill in the value, if any
			// Note that the ResultSet info and the colDefs should be
			// in the same order, but we cannot guarantee that.
			int expectedColDefIndex = 0;
			while (rs.next()) {
				// get the column name
				String colName = rs.getString(4);
				
				// get the default value
				String defValue = rs.getString(13);
				
				// if value was null, we do not need to do
				// anything else with this column.
				// Also assume that a value of "" is equivilent to null
				if (defValue != null &&  defValue.length() > 0) {
					// find the entry in colDefs matching this column
					if (colDefs[expectedColDefIndex].getLabel().equals(colName)) {
						// DB cols are in same order as colDefs
						defaultValues[expectedColDefIndex] = defValue;
					}
					else {
						// colDefs not in same order as DB, so search for
						// matching colDef entry
						// Note: linear search here will NORMALLY be not too bad
						// because most tables do not have huge numbers of columns.
						for (int i=0; i<colDefs.length; i++) {
							if (colDefs[i].getLabel().equals(colName)) {
								defaultValues[i] = defValue;
								break;
							}
						}
					}
				}
				
				// assuming that the columns in table match colDefs,
				// bump the index to point to the next colDef entry
				expectedColDefIndex++;
			}
		}
		catch (Exception ex)
		{
			s_log.error("Error retrieving default column values", ex);
		}
		
		return defaultValues;
	}
	
	
	/**
	 * Insert a row into the DB.
	 * If the insert succeeds this returns a null string.
	 */
	public String insertRow(Object[] values, ColumnDisplayDefinition[] colDefs) {
		
		final ISession session = getSession();
		final SQLConnection conn = session.getSQLConnection();

		int count = -1;

		try
		{
			final ITableInfo ti = getTableInfo();
			
			// start the string for use in the prepared statment
			StringBuffer buf = new StringBuffer(
				"INSERT INTO " + ti.getQualifiedName() + " VALUES (");
			
			// add a variable position for each of the columns
			for (int i=0; i<colDefs.length; i++) {
				if (i != _rowIDcol)
					buf.append(" ?,");
			}
			
			// replace the last "," with ")"
			buf.setCharAt(buf.length()-1, ')');
			
			final PreparedStatement pstmt = conn.prepareStatement(buf.toString());

			try
			{
				// have the DataType object fill in the appropriate kind of value
				// into the appropriate variable position in the prepared stmt
				for (int i=0; i<colDefs.length; i++) {
					if (i != _rowIDcol) {
						CellComponentFactory.setPreparedStatementValue(
							colDefs[i], pstmt, values[i], i+1);
					}
				}
				count = pstmt.executeUpdate();
			}
			finally
			{
				pstmt.close();
			}
		}
		catch (SQLException ex)
		{
			return "Exception seen during check on DB.  Exception was:\n"+
				ex.getMessage() +
				"\nInsert was probably not completed correctly.  DB may be corrupted!";
		}

		if (count != 1)
			return "Unknown problem during update.\nNo count of inserted rows was returned.\nDatabase may be corrupted!";
			
		// insert succeeded
		return null;
	}

}
