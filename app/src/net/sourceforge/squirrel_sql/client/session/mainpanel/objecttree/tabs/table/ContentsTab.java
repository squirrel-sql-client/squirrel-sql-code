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
import java.sql.DatabaseMetaData;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
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

				/**
				 * If the table has a pseudo-column that is the best unique
				 * identifier for the rows (like Oracle's rowid), then we
				 * want to include that field in the query so that it will
				 * be available if the user wants to edit the data later.
				 */
				String pseudoColumn = "";

				//??????????????????????????????????
				//??  The following has not been tested because I cannot find a free db on Linux
				//??  that has a getBestRowIdentifier that provides this info.  GWG
				//??????????????????????????????????
				ResultSet rowIdentifierRS = conn.getSQLMetaData().getBestRowIdentifier(ti);
				while (rowIdentifierRS.next()) {
					// according to spec, col 8 is indicator of pseudo/not-pseudo
					// and col 2 is name of rowid column
					short pseudo = rowIdentifierRS.getShort(8);
					if (pseudo == DatabaseMetaData.bestRowPseudo) {
						pseudoColumn = " ," + rowIdentifierRS.getString(2);
						break;
					}
				}

				//
				// KLUDGE!!!!!!
				//
				// For some DBs (e.g. PostgreSQL) there is actually a pseudo-column
				// providing the rowId, but the getBestRowIdentifier function is not
				// implemented.  This kludge hardcodes the knowledge that specific
				// DBs use a specific pseudo-column.
				//
				if (pseudoColumn.length() == 0) {
					String dbName = conn.getSQLMetaData().getDatabaseProductName().toUpperCase();
					if (dbName.equals("POSTGRESQL")) {
						pseudoColumn = ", oid";
					}
				}

				final ResultSet rs = stmt.executeQuery("select *" + pseudoColumn+ " from "
													+ ti.getQualifiedName());
				final ResultSetDataSet rsds = new ResultSetDataSet();
				rsds.setResultSet(rs, props.getLargeResultSetObjectInfo());
				
				//?? remember which column is the rowID (if any) so we can
				//?? prevent editing on it
				if (pseudoColumn.length() > 0)
					_rowIDcol = rsds.getColumnCount() - 1;

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
	 * Link from fw to check on whether there are any unusual coniditions
	 * in the current data that the user needs to be aware of before updating.
	 */
	public String getWarningOnCurrentData(Object[] values, ColumnDisplayDefinition[] colDefs, int col, Object oldValue)
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
	public String getWarningOnProjectedUpdate(Object[] values, ColumnDisplayDefinition[] colDefs, int col, Object newValue)
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

		// Since a BLOB or CLOB cannot be used in a WHERE clause, an update to one of those
		// fields will look like we are replacing one row with an identical row (because
		// we can only "see" the fields that we know how to do WHEREs on).  Therefore,
		// when we are updating a BLOB/CLOB, there should be exactly one row that matches
		// all of our other fields, and when we are not updating a BLOB/CLOB, there should be
		// no rows that exactly match our criteria (we hope).
//??
//?? Open Issue:
//??	Are there other data types that should be included in this list?
//??

//?? Also, this code has not been tested properly (I don't have BLOBs or CLOBs set up yet
		if (colDefs[col].getSqlType() == Types.BLOB ||
			colDefs[col].getSqlType() == Types.CLOB ) {
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
	 * link from fw to this for updating data
	 */
	public String updateTableComponent(Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object oldValue,
		Object newValue)
	{
		// get WHERE clause using original value
		String whereClause = getWhereClause(values, colDefs, col, oldValue);
		
		// The format of the SET part of the UPDATE statement varies depending
		// on the data type, and whether it is NULL or not.
		String setClause = getSetClause(colDefs, col, newValue);
		
		// we do not know how to set some fields, so...
		if (setClause == null)
			return "The '" + colDefs[col].getLabel() + "' column is a type that Squirrel does not know how to set";

		final ISession session = getSession();
		final SQLConnection conn = session.getSQLConnection();

		int count = -1;
		
		try
		{
			final Statement stmt = conn.createStatement();
			try
			{
				final ITableInfo ti = getTableInfo();
				String updateCommand = "UPDATE " + ti.getQualifiedName() + setClause + whereClause;
				count = stmt.executeUpdate(updateCommand);					
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
				"\nUpdate was probably not completed correctly.  DB may be corrupted!";
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
	 */
	private String getWhereClause(
		Object[] values,
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object colValue)
	{
		
		StringBuffer whereClause = new StringBuffer("");
		
		for (int i=0; i< colDefs.length; i++) {
			// do different things depending on data type
			
			String clause = null;

			// for the column that is being changed, use the value
			// passed in by the caller (which may be either the
			// current value or the new replacement value)
			Object value = values[i];
			if (i == col)
				value = colValue;
			
			String columnLabel = colDefs[i].getLabel();

			switch (colDefs[i].getSqlType())
			{
				case Types.NULL:	// should never happen
					//??
					break;

					
				// TODO: When JDK1.4 is the earliest JDK supported
				// by Squirrel then remove the hardcoding of the
				// boolean data type.
				case Types.BIT:
				case 16:
//				case Types.BOOLEAN:
					//??
					break;

				case Types.TIME :
					//??
					break;

				case Types.DATE :
					//??
					break;

				case Types.TIMESTAMP :
					//??
					break;

				case Types.BIGINT :
					//??
					break;

				case Types.DOUBLE:
				case Types.FLOAT:
				case Types.REAL:
					//??
					break;

				case Types.DECIMAL:
				case Types.NUMERIC:
					//??
					break;

				case Types.INTEGER:
				case Types.SMALLINT:
				case Types.TINYINT:
					if (value == null || value.toString() == null || value.toString().length() == 0)
						clause = columnLabel + " IS NULL";
					clause = columnLabel + "=" + value.toString();
					break;


				// TODO: Hard coded -. JDBC/ODBC bridge JDK1.4
				// brings back -9 for nvarchar columns in
				// MS SQL Server tables.
				// -8 is ROWID in Oracle.
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
				case -9:
				case -8:
					if (value == null || value.toString() == null )
						clause = columnLabel + " IS NULL";
					else
						clause = columnLabel + "='" + value.toString() + "'";
					break;

				case Types.BINARY:
					//??
					break;

				case Types.VARBINARY:
					//??
					break;

				case Types.LONGVARBINARY:
					//??
					break;

				case Types.BLOB:
					//??
					break;

				case Types.CLOB:
					//??
					break;

				case Types.OTHER:
					//??
					break;

				default:	// should never happen
					//??
					break;
			}

			if (clause != null)
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
	 * helper function to create a SET clause to update the cell in the DB.
	 */
	private String getSetClause(
		ColumnDisplayDefinition[] colDefs,
		int col,
		Object colValue)
	{

		String clause = null;
		
		String columnLabel = colDefs[col].getLabel();

		switch (colDefs[col].getSqlType())
		{
			case Types.NULL:	// should never happen
				//??
				break;

					
			// TODO: When JDK1.4 is the earliest JDK supported
			// by Squirrel then remove the hardcoding of the
			// boolean data type.
			case Types.BIT:
			case 16:
//			case Types.BOOLEAN:
				//??
				break;

			case Types.TIME :
				//??
				break;

			case Types.DATE :
				//??
				break;

			case Types.TIMESTAMP :
				//??
				break;

			case Types.BIGINT :
				//??
				break;

			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.REAL:
				//??
				break;

			case Types.DECIMAL:
			case Types.NUMERIC:
				//??
				break;

			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
//????? check somehow whether column is nullable? or is this handled by cell editor?
				if (colValue == null || colValue.toString() == null || colValue.toString().length() == 0)
					clause = columnLabel + " TO NULL ";
				else clause = columnLabel + "=" + colValue.toString();
				break;


			// TODO: Hard coded -. JDBC/ODBC bridge JDK1.4
			// brings back -9 for nvarchar columns in
			// MS SQL Server tables.
			// -8 is ROWID in Oracle.
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case -9:
			case -8:
				if (colValue == null || colValue.toString() == null )
					clause = columnLabel + " TO NULL ";
				else
					clause = columnLabel + "='" + colValue.toString() + "'";
				break;

			case Types.BINARY:
				//??
				break;

			case Types.VARBINARY:
				//??
				break;

			case Types.LONGVARBINARY:
				//??
				break;

			case Types.BLOB:
				//??
				break;

			case Types.CLOB:
				//??
				break;

			case Types.OTHER:
				//??
				break;

			default:	// should never happen
				//??
				break;
		}

		
		// insert the "WHERE" at the front if there is anything in the clause
		if (clause.length() == 0)
			return null;

		return " SET " + clause;
	}
}
