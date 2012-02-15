package net.sourceforge.squirrel_sql.client.session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.session.properties.EditWhereCols;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetUpdateableTableModelListener;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataModelImplementationDetails;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePartUtil;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.WhereClausePartUtil;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class DataSetUpdateableTableModelImpl implements IDataSetUpdateableTableModel
{

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DataSetUpdateableTableModelImpl.class);       
    
   /** string to be passed to user when table name is not found or is ambiguous */
   // i18n[DataSetUpdateableTableModelImpl.error.tablenotfound=Cannot edit table because table cannot be found\nor table name is not unique in DB.]
   private final String TI_ERROR_MESSAGE = s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.tablenotfound");

   /** Logger for this class. */
   private static final ILogger s_log = LoggerController.createLogger(DataSetUpdateableTableModelImpl.class);


   /**
    * This is the long name of the current table including everything that might be able to distinguish it
    * from another table of the same name in a different DB.
    */
   private String fullTableName = null;
   private ITableInfo ti;
   private ISession _session;

   /**
    * Remember whether or not the user has forced us into editing mode
    * when the SessionProperties says to use read-only mode.
    */
   private boolean editModeForced = false;


   /**
    * We need to save the name of the SessionProperties display class at the time
    * that the table was forced into edit mode so that if the properties get changed
    * while we are in forced edit mode, we will change back to match the new
    * Session Properties.
    */
   String sqlOutputClassNameAtTimeOfForcedEdit = "";

   private Vector<DataSetUpdateableTableModelListener> _dataSetUpdateableTableModelListener = 
       new Vector<DataSetUpdateableTableModelListener>();

   /**
    * Remember which column contains the rowID; if no rowID, this is -1
    * which does not match any legal column index.
    * Note that for this class, since the list of columns to include is given
    * by the user, we never include any pseudo-column automatically in the
    * ResultSet, and thus we never have any legal column index here.
    */
   int _rowIDcol = -1;
   
   /**
    * A util for handling parts of an where clause.
    */
   private IWhereClausePartUtil whereClausePartUtil = new WhereClausePartUtil();

   public void setTableInfo(ITableInfo ti)
   {
      this.ti = ti;
      // re-calculate fullTablename the next time it's requested.
      fullTableName = null;
   }

   public void setSession(ISession session)
   {
      this._session = session;
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
   public String getFullTableName() {
      if (fullTableName == null) {
         try {
            final String name = ti.getQualifiedName();
            fullTableName = getUnambiguousTableName(_session, name);
         }
         catch (Exception e) {
            s_log.error(
                "getFullTableName: Unexpected exception - "+e.getMessage(), e);
         }
      }
      return fullTableName;
   }

   /**
    * If the user forces us into edit mode, remember that they did so for this table.
    */
   public void forceEditMode(boolean mode)
   {
      editModeForced = mode;
      sqlOutputClassNameAtTimeOfForcedEdit =
         _session.getProperties().getTableContentsOutputClassName();

      DataSetUpdateableTableModelListener[] listeners =
         _dataSetUpdateableTableModelListener.toArray(new DataSetUpdateableTableModelListener[0]);

      for (int i = 0; i < listeners.length; i++)
      {
         listeners[i].forceEditMode(mode);
      }


      /**
       * Tell the GUI to rebuild itself.
       * This is not a clean way to do that, since we are telling the
       * SessionProperties listeners that a property has changed when
       * in reality none of them have done so, but this does cause the
       * GUI to be rebuilt.
       */
//		_session.getProperties().forceTableContentsOutputClassNameChange();
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
   public String getDestinationClassName()
   {
      if (editModeForced)
      {
         if (_session.getProperties().getTableContentsOutputClassName().equals(
            sqlOutputClassNameAtTimeOfForcedEdit))
         {
            return _session.getProperties().getEditableTableOutputClassName();
         }
         // forced edit mode ended because user changed the Session Properties
         editModeForced = false;
      }

      // if the user selected Editable Table in the Session Properties,
      // then the display will be an editable table; otherwise the display is read-only
      return _session.getProperties().getTableContentsOutputClassName();
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

      // if we could not identify which table to edit, tell user
      if (ti == null)
         return TI_ERROR_MESSAGE;

      List<IWhereClausePart> whereClauseParts = getWhereClause(values, colDefs, col, oldValue);

      
      // It is possible for a table to contain only columns of types that
      // we cannot process or do selects on, so check for that.
      // Since this check is on the structure of the table rather than the contents,
      // we only need to do it once (ie: it is not needed in getWarningOnProjectedUpdate)
      if (whereClausePartUtil.hasUsableWhereClause(whereClauseParts) == false){
         // i18n[DataSetUpdateableTableModelImpl.confirmupdateallrows=The table has no columns that can be SELECTed on.\nAll rows will be updated.\nDo you wish to proceed?]
         return s_stringMgr.getString("DataSetUpdateableTableModelImpl.confirmupdateallrows");
      }

      final ISession session = _session;
      final ISQLConnection conn = session.getSQLConnection();

      int count = -1;	// start with illegal number of rows matching query

      try
      {
         count = count(whereClauseParts, conn);
      }
      catch (SQLException ex)
      {
          //i18n[DataSetUpdateableTableModelImpl.error.exceptionduringcheck=Exception 
          //seen during check on DB.  Exception was:\n{0}\nUpdate is probably not 
          //safe to do.\nDo you wish to proceed?]
          String msg = 
              s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.exceptionduringcheck", ex.getMessage());
          s_log.error(msg, ex);
          return msg;
      }

      if (count == -1) {
          // i18n[DataSetUpdateableTableModelImpl.error.unknownerror=Unknown error during check on DB.  Update is probably not safe.\nDo you wish to proceed?]
         return s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.unknownerror");
      }
      if (count == 0) {
          // i18n[DataSetUpdateableTableModelImpl.error.staleupdaterow=This row in the Database has been changed since you refreshed the data.\nNo rows will be updated by this operation.\nDo you wish to proceed?]
         return s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.staleupdaterow");
      }
      if (count > 1) {
          // i18n[DataSetUpdateableTableModelImpl.info.updateidenticalrows=This operation will update {0} identical rows.\nDo you wish to proceed?]
          return s_stringMgr.getString("DataSetUpdateableTableModelImpl.info.updateidenticalrows",
                                       Long.valueOf(count));
      }
      // no problems found, so do not return a warning message.
      return null;	// nothing for user to worry about
   }

   /**
    * Counts the number of affected rows, using this where clause.
    * @param whereClauseParts where clause to use
    * @param conn connection to use
    * @return number of rows in the database, which will be selected by the given whereClauseParts
    * @throws SQLException if an SQLExcetpion occurs.
    */
   private int count(List<IWhereClausePart> whereClauseParts,
		   final ISQLConnection conn) throws SQLException {
	   int count;
	   PreparedStatement pstmt = null;
	   ResultSet rs = null;
	   try
	   {
		   String whereClause = whereClausePartUtil.createWhereClause(whereClauseParts);
		   String countSql = "select count(*) from " + ti.getQualifiedName() + whereClause;
		   pstmt = conn.prepareStatement(countSql);
		   whereClausePartUtil.setParameters(pstmt, whereClauseParts, 1);

		   rs = pstmt.executeQuery();
		   rs.next();
		   count = rs.getInt(1);
	   }
	   finally
	   {
		   // We don't care if these throw an SQLException.  Just squelch them
		   // and report to the user what the outcome of the previous statements
		   // were.
		   SQLUtilities.closeResultSet(rs);
		   SQLUtilities.closeStatement(pstmt);
	   }
	   return count;
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
      try
      {
         // if we could not identify which table to edit, tell user
         if (ti == null)
            return TI_ERROR_MESSAGE;

         List<IWhereClausePart> whereClauseParts = getWhereClause(values, colDefs, col, newValue);
         
         final ISession session = _session;
         final ISQLConnection conn = session.getSQLConnection();

         int count = -1;	// start with illegal number of rows matching query

         try
         {
        	count = count(whereClauseParts, conn);
        	
         }
         catch (SQLException ex)
         {
             // i18n[DataSetUpdateableTableModelImpl.error.exceptionduringcheck=Exception seen during check on DB.  Exception was:\n{0}\nUpdate is probably not safe to do.\nDo you wish to proceed?]
             s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.exceptionduringcheck", ex.getMessage());
         }

         if (count == -1) {
             // i18n[DataSetUpdateableTableModelImpl.error.unknownerror=Unknown error during check on DB.  Update is probably not safe.\nDo you wish to proceed?]
            return s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.unknownerror");
         }
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
         
         if (count > 1) {
             // i18n[DataSetUpdateableTableModelImpl.info.identicalrows=This 
             //operation will result in {0} identical rows.\nDo you wish 
             //to proceed?]
             return s_stringMgr.getString("DataSetUpdateableTableModelImpl.info.identicalrows",
                                          Long.valueOf(count));
         }
         
         // no problems found, so do not return a warning message.
         return null;	// nothing for user to worry about
      }
      catch (Exception e)
      {
         throw new  RuntimeException(e);
      }

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

      // if we could not identify which table to edit, tell user
      if (ti == null)
         return TI_ERROR_MESSAGE;

      // get WHERE clause
      // The -1 says to ignore the last arg and use the contents of the values array
      // for the column that we care about.  However, since the data in
      // that column has been limited, when getWhereClause calls that
      // DataType with that value, the DataType will see that the data has
      // been limited and therefore cannnot be used in the WHERE clause.
      // In some cases it may be possible for the DataType to use the
      // partial data, such as "matches <data>*", but that may not be
      // standard accross all Databases and thus may be risky.
      
      
      List<IWhereClausePart> whereClauseParts = getWhereClause(values, colDefs, -1, null);
      String whereClause = whereClausePartUtil.createWhereClause(whereClauseParts); 
      final ISession session = _session;
      final ISQLConnection conn = session.getSQLConnection();

      Object wholeDatum = null;

      try
      {
    	  final String queryString =
              "SELECT " + colDefs[col].getColumnName() +" FROM "+ti.getQualifiedName() +
              whereClause;
    	  
         final PreparedStatement pstmt = conn.prepareStatement(queryString);
         whereClausePartUtil.setParameters(pstmt, whereClauseParts, 1);
         

         try
         {
            ResultSet rs = pstmt.executeQuery(queryString);

            // There should be one row in the data, so try to move to it
            if (rs.next() == false) {
               // no first row, so we cannot retrieve the data
               // i18n[DataSetUpdateableTableModelImpl.error.nomatchingrow=Could not find any row in DB matching current row in table]
               throw new SQLException(s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.nomatchingrow"));
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
               // i18n[DataSetUpdateableTableModelImpl.error.multimatchingrows=Muliple rows in DB match current row in table - cannot re-read data.]
               throw new SQLException(s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.multimatchingrows"));
            }
         }
         finally
         {
            pstmt.close();
         }
      }
      catch (Exception ex)
      {
          // i18n[DataSetUpdateableTableModelImpl.error.rereadingdb=There was a problem reported while re-reading the DB.  The DB message was:\n{0}]
          message.append(
              s_stringMgr.getString(
                          "DataSetUpdateableTableModelImpl.error.rereadingdb", 
                          ex.getMessage()));

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
      // if we could not identify which table to edit, tell user
      if (ti == null)
         return TI_ERROR_MESSAGE;

      // get WHERE clause using original value
       List<IWhereClausePart> whereClauseParts = getWhereClause(values, colDefs, col, oldValue);
       String whereClause = whereClausePartUtil.createWhereClause(whereClauseParts);
      if (s_log.isDebugEnabled()) {
          s_log.debug("updateTableComponent: whereClause = "+whereClause);
      }
      
      final ISession session = _session;
      final ISQLConnection conn = session.getSQLConnection();

      int count = -1;

      final String sql = constructUpdateSql(
            ti.getQualifiedName(), colDefs[col].getColumnName(), whereClause);
      
      if (s_log.isDebugEnabled()) {
          s_log.debug("updateTableComponent: executing SQL - "+sql);
      }
      PreparedStatement pstmt = null;
      try
      {
         pstmt = conn.prepareStatement(sql);

         /* 
          * have the DataType object fill in the appropriate kind of value of the changed data
          * into the first variable position in the prepared stmt
          */
         CellComponentFactory.setPreparedStatementValue(
                colDefs[col], pstmt, newValue, 1);
         
         // Fill the parameters of the where clause - start at position 2 because the data which is updated is at position 1
         whereClausePartUtil.setParameters(pstmt, whereClauseParts, 2);
         count = pstmt.executeUpdate();
      }
      catch (SQLException ex)
      {
          //i18n[DataSetUpdateableTableModelImpl.error.updateproblem=There 
          //was a problem reported during the update.  
          //The DB message was:\n{0}\nThis may or may not be serious depending 
          //on the above message.\nThe data was probably not changed in the 
          //database.\nYou may need to refresh the table to get an accurate 
          //view of the current data.]
          String errMsg = s_stringMgr.getString(
                "DataSetUpdateableTableModelImpl.error.updateproblem",
                ex.getMessage());
          s_log.error("updateTableComponent: unexpected exception - "+
                      ex.getMessage()+" while executing SQL: "+sql);
          
          
         return errMsg;           
      } finally {
          SQLUtilities.closeStatement(pstmt);
      }

      if (count == -1) {
          // i18n[DataSetUpdateableTableModelImpl.error.unknownupdateerror=Unknown problem during update.\nNo count of updated rows was returned.\nDatabase may be corrupted!]
         return s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.unknownupdateerror");
      }
      if (count == 0) {
          // i18n[DataSetUpdateableTableModelImpl.info.norowsupdated=No rows updated.]
         return s_stringMgr.getString("DataSetUpdateableTableModelImpl.info.norowsupdated");
      }
      // everything seems to have worked ok
      return null;
   }

   
   /**
    * Build the update SQL from the specified components.
    *  
    * @param table the fully qualified name of the table
    * @param column the name of the column to update
    * @param whereClause the where clause that restricts the update to one row.
    * 
    * @return the SQL to execute
    */
   private String constructUpdateSql(String table, String column,
           String whereClause) {
       StringBuilder result = new StringBuilder();
       result.append("UPDATE ");
       result.append(table);
       result.append(" SET ");
       result.append(column);
       result.append(" = ? ");
       result.append(whereClause);
       return result.toString();
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
   private List<IWhereClausePart> getWhereClause(
      Object[] values,
      ColumnDisplayDefinition[] colDefs,
      int col,
      Object colValue)
   {
      try
      {

         // For tables that have a lot of columns, the user may have limited the set of columns
         // to use in the where clause, so see if there is a table of col names
         HashMap<String, String> colNames = (EditWhereCols.get(getFullTableName()));

         
         ColumnDisplayDefinition editedCol = null;
			if(-1 != col)
			{
				editedCol = colDefs[col];
			}

			List<IWhereClausePart> clauseParts = new ArrayList<IWhereClausePart>();
			
			for (int i=0; i< colDefs.length; i++) {

            if(i != col &&
					null != editedCol &&
					colDefs[i].getFullTableColumnName().equalsIgnoreCase(editedCol.getFullTableColumnName()))
            {
               // The edited column is in the resultset twice (example: SELECT MyName,* FROM MyTable).
               // We won't add the this col to the where clause.
               continue;
            }

            // if the user has said to not use this column, then skip it
            if (colNames != null) {
               // the user has restricted the set of columns to use.
               // If this name is NOT in the list, then skip it; otherwise we fall through
               // and use the column in the WHERE clause
               if (colNames.get(colDefs[i].getColumnName()) == null)
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
            ISQLDatabaseMetaData md = _session.getMetaData();
            IWhereClausePart clausePart = CellComponentFactory.getWhereClauseValue(colDefs[i], value, md);

            
            if (clausePart.shouldBeUsed())
            	// Now we know that the part should not we ignoredshould
            	clauseParts.add(clausePart);
         }
			
			return clauseParts;

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   /**
    * Delete a set of rows from the DB.
    * If the delete succeeded this returns a null string.
    * The deletes are done within a transaction
    * so they are either all done or all not done.
    */
   public String deleteRows(Object[][] rowData, ColumnDisplayDefinition[] colDefs) {

      // if we could not identify which table to edit, tell user
      if (ti == null)
         return TI_ERROR_MESSAGE;

      // get the SQL session
      final ISession session = _session;
      final ISQLConnection conn = session.getSQLConnection();

      // string used as error indicator and description of problems seen
      // when checking for 0 or mulitple matches in DB
      String rowCountErrorMessage = "";

      // for each row in table, count how many rows match where clause
      // if not exactly one, generate message describing situation
      for (int i = 0; i < rowData.length; i++) {
         // get WHERE clause for the selected row
         // the -1 says to just use the contents of the values without
         // any substitutions
         List<IWhereClausePart> whereClauseParts = getWhereClause(rowData[i], colDefs, -1, null);
         
         // count how many rows this WHERE matches
         try {
        	 
        	 int count = count(whereClauseParts, conn);
               if (count != 1) {
                  if (count == 0) {
                      // i18n[DataSetUpdateableTableModelImpl.error.rownotmatch=\n   Row {0}  did not match any row in DB]
                     rowCountErrorMessage += 
                         s_stringMgr.getString(
                                 "DataSetUpdateableTableModelImpl.error.rownotmatch",
                                 Integer.valueOf(i+1));
                  } else {
                      //i18n[DataSetUpdateableTableModelImpl.error.rowmatched=\n   Row {0} matched {1} rows in DB]
                      rowCountErrorMessage += 
                          s_stringMgr.getString(
                                  "DataSetUpdateableTableModelImpl.error.rowmatched", 
                                  new Object[] { Integer.valueOf(i+1), Integer.valueOf(count) });
                  }
               }
         }
         catch (Exception e) {
            // some kind of problem - tell user
             // i18n[DataSetUpdateableTableModelImpl.error.preparingdelete=While preparing for delete, saw exception:\n{0}]
             return 
                 s_stringMgr.getString(
                         "DataSetUpdateableTableModelImpl.error.preparingdelete",
                         e);
         }
      }

      // if the rows do not match 1-for-1 to DB, ask user if they
      // really want to do delete
      if (rowCountErrorMessage.length() > 0) {
          // i18n[DataSetUpdateableTableModelImpl.error.tabledbmismatch=There may be a mismatch between the table and the DB:\n{0}\nDo you wish to proceed with the deletes anyway?]
          String msg = 
              s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.tabledbmismatch",
                                    rowCountErrorMessage);
         
         int option = 
             JOptionPane.showConfirmDialog(null, msg, "Warning", 
                                           JOptionPane.YES_NO_OPTION, 
                                           JOptionPane.WARNING_MESSAGE);
         
         if ( option != JOptionPane.YES_OPTION) {
             // i18n[DataSetUpdateableTableModelImpl.info.deletecancelled=Delete canceled at user request.]
            return s_stringMgr.getString("DataSetUpdateableTableModelImpl.info.deletecancelled");
         }
      }

      // for each row in table, do delete and add to number of rows deleted from DB
      for (int i = 0; i < rowData.length; i++) {
         // get WHERE clause for the selected row
         // the -1 says to just use the contents of the values without
         // any substitutions
          List<IWhereClausePart> whereClauseParts = getWhereClause(rowData[i], colDefs, -1, null);
          String whereClause = whereClausePartUtil.createWhereClause(whereClauseParts);
         // try to delete
         try {
            // do the delete and add the number of rows deleted to the count
        	 String sql = "DELETE FROM " +
		      ti.getQualifiedName() + whereClause;
        	 final PreparedStatement pstmt = conn.prepareStatement(sql);
        	 whereClausePartUtil.setParameters(pstmt, whereClauseParts, 1);
            try
            {
            	pstmt.executeUpdate();
            }
            finally
            {
               pstmt.close();
            }
         }
         catch (Exception e) {
            // some kind of problem - tell user
             // i18n[DataSetUpdateableTableModelImpl.error.deleteFailed=One of the delete operations failed with exception:\n{0}\nDatabase is in an unknown state and may be corrupted.]
             return s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.deleteFailed", e);
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
      final String[] defaultValues = new String[colDefs.length];

      // if we could not identify which table to edit, just return
      if (ti == null)
      {
         return defaultValues;
      }

      final ISession session = _session;
      final ISQLConnection conn = session.getSQLConnection();

      try
      {
         SQLDatabaseMetaData md = conn.getSQLMetaData();
         TableColumnInfo[] infos = md.getColumnInfo(ti);
         
         // read the DB MetaData info and fill in the value, if any
         // Note that the ResultSet info and the colDefs should be
         // in the same order, but we cannot guarantee that.
         int expectedColDefIndex = 0;
         
         for (int idx = 0; idx < infos.length; idx++) {
             String colName = infos[idx].getColumnName();
             String defValue = infos[idx].getDefaultValue();
             
             // if value was null, we do not need to do
             // anything else with this column.
             // Also assume that a value of "" is equivilent to null
             if (defValue != null &&  defValue.length() > 0) {
                // find the entry in colDefs matching this column
                if (colDefs[expectedColDefIndex].getColumnName().equals(colName)) {
                   // DB cols are in same order as colDefs
                   defaultValues[expectedColDefIndex] = defValue;
                }
                else {
                   // colDefs not in same order as DB, so search for
                   // matching colDef entry
                   // Note: linear search here will NORMALLY be not too bad
                   // because most tables do not have huge numbers of columns.
                   for (int i=0; i<colDefs.length; i++) {
                      if (colDefs[i].getColumnName().equals(colName)) {
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
          // i18n[DataSetUpdateableTableModelImpl.error.retrievingdefaultvalues=Error retrieving default column values]
          s_log.error(s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.retrievingdefaultvalues"), ex);
      }

      return defaultValues;
   }


   /**
    * Insert a row into the DB.
    * If the insert succeeds this returns a null string.
    */
   public String insertRow(Object[] values, ColumnDisplayDefinition[] colDefs) {

      // if we could not identify which table to edit, tell user
      if (ti == null) {
         return TI_ERROR_MESSAGE;
      }
      
      final ISession session = _session;
      final ISQLConnection conn = session.getSQLConnection();
      
      int count = -1;
      
      try
      {
         // start the string for use in the prepared statment
         StringBuilder buf = new StringBuilder("INSERT INTO ");
         buf.append(ti.getQualifiedName());

         // Add the list of column names we will be inserting into - be sure
         // to skip the rowId column and any auto increment columns.
         buf.append(" ( ");
         for (int i=0; i<colDefs.length; i++) {
             if (i == _rowIDcol) {
                 continue;
             }
             if (colDefs[i].isAutoIncrement()) {
                 if (s_log.isInfoEnabled()) {
                     s_log.info("insertRow: skipping auto-increment column "+
                                colDefs[i].getColumnName());
                 }
                 continue;
             } 
             buf.append(colDefs[i].getColumnName());
             buf.append(",");
         }
         buf.setCharAt(buf.length()-1, ')');
         buf.append(" VALUES (");
         
         // add a variable position for each of the columns
         for (int i=0; i<colDefs.length; i++) {
            if (i != _rowIDcol && !colDefs[i].isAutoIncrement() )
                
               buf.append(" ?,");
         }

         // replace the last "," with ")"
         buf.setCharAt(buf.length()-1, ')');

         String pstmtSQL = buf.toString();
         if (s_log.isInfoEnabled()) {
             s_log.info("insertRow: pstmt sql = "+pstmtSQL);
         }
         final PreparedStatement pstmt = conn.prepareStatement(pstmtSQL);

         try
         {
            // We need to keep track of the bind var index separately, since 
            // the number of column defs may not be the number of bind vars
            // (For example: auto-increment columns are excluded)
            int bindVarIdx = 1;
             
            // have the DataType object fill in the appropriate kind of value
            // into the appropriate variable position in the prepared stmt
            for (int i=0; i<colDefs.length; i++) {
               if (i != _rowIDcol && !colDefs[i].isAutoIncrement()) {
                   CellComponentFactory.setPreparedStatementValue(
                           colDefs[i], pstmt, values[i], bindVarIdx);
                   bindVarIdx++;
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
          // i18n[DataSetUpdateableTableModelImpl.error.duringInsert=Exception seen during check on DB.  Exception was:\n{0}\nInsert was probably not completed correctly.  DB may be corrupted!]
          return s_stringMgr.getString(
                  "DataSetUpdateableTableModelImpl.error.duringInsert", 
                  ex.getMessage());
      }

      if (count != 1)
          // i18n[DataSetUpdateableTableModelImpl.error.unknownerrorupdate=Unknown problem during update.\nNo count of inserted rows was returned.\nDatabase may be corrupted!]
         return s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.unknownerrorupdate");

      // insert succeeded
      try {
          IObjectTreeAPI api = _session.getObjectTreeAPIOfActiveSessionWindow();
          api.refreshSelectedTab();
      } catch (Exception e) {
          e.printStackTrace();
      }

      return null;
   }

   public void addListener(DataSetUpdateableTableModelListener l)
   {
      _dataSetUpdateableTableModelListener.add(l);
   }

   public void removeListener(DataSetUpdateableTableModelListener l)
   {
      _dataSetUpdateableTableModelListener.remove(l);
   }


   public void setEditModeForced(boolean b)
   {
      editModeForced = b;
   }

   public void setRowIDCol(int rowIDCol)
   {
      _rowIDcol = rowIDCol;
   }

   public void setWhereClausePartUtil(IWhereClausePartUtil whereClausePartUtil) {
	   this.whereClausePartUtil = whereClausePartUtil;
   }
}
