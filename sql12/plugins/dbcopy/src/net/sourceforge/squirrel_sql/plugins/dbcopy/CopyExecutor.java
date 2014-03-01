/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectUtils;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.AnalysisEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.ErrorEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.RecordEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.StatementEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.DBCopyPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;
import net.sourceforge.squirrel_sql.plugins.dbcopy.util.DBUtil;

import org.hibernate.MappingException;

/**
 * This is the class that performs the table copy using database connections 
 * to two different database schemas.  
 */
public class CopyExecutor extends I18NBaseObject {

    /** the class that provides out session information */
    SessionInfoProvider prov = null;
    
    /** the source session.  This comes from prov */
    ISession sourceSession = null;
    
    /** the destination session.  This comes from prov */
    ISession destSession = null;
    
    /** the thread we do the work in */
    private Thread execThread = null;
    
    /** what value did autocommit have in dest connection when we received it */ 
    private boolean originalAutoCommitValue = true;
    
    /** what value does autocommit have in dest connection now */
    private boolean currentAutoCommitValue = true;    
    
    /** the user's preferences */
    private static DBCopyPreferenceBean prefs = 
                                            PreferencesManager.getPreferences();    
    
    /** Logger for this class. */
    private final static ILogger log = 
                         LoggerController.createLogger(CopyExecutor.class);
        
    /** the list of ITableInfos that represent the user's last selection. */
    private ArrayList<ITableInfo> selectedTableInfos = null;    
    
    /** the CopyTableListeners that have registered with this class */
    private ArrayList<CopyTableListener> listeners = 
        new ArrayList<CopyTableListener>();
    
    /** whether or not the user cancelled the copy operation */
    private volatile boolean cancelled = false;    
    
    /** impl that gives us feedback from the user */
    private UICallbacks pref = null;
    
    /** the start time in millis that the copy operation began */
    private long start = 0;
    
    /** the finish time in millis that the copy operation began */
    private long end = 0;
    
    /**
     * Constructor.
     * 
     * @param p the provider of information regarding what to copy where.
     */
    public CopyExecutor(SessionInfoProvider p) {
        prov = p;
        sourceSession = prov.getSourceSession();
        destSession = prov.getDestSession();
    }
    
    /**
     * Starts the thread that executes the copy operation.
     */
    public void execute() {   	    	 
        Runnable runnable = new Runnable() {
            public void run() {
                _execute();
            }
        };
        execThread = new Thread(runnable);
        execThread.setName("DBCopy Executor Thread");
        execThread.start();
    }

    /** 
     * Cancels the copy operation.
     */
    public void cancel() {
        cancelled = true;
        execThread.interrupt();        
    }
    
    /**
     * Performs the table copy operation.
     */
    private void _execute() {
        start = System.currentTimeMillis();
        boolean encounteredException = false;
        ISQLConnection destConn = destSession.getSQLConnection();
        if (!analyzeTables()) {
            return;
        }
        setupAutoCommit(destConn);
        List<IDatabaseObjectInfo> sourceObjs = prov.getSourceDatabaseObjects();
        int[] counts = getTableCounts();
        sendCopyStarted(counts);

        //String destSchema = prov.getDestDatabaseObject().getSimpleName();  used to break, when a table was selected

       String destSchema = DBUtil.getSchemaNameFromDbObject(prov.getDestDatabaseObject());

       String destCatalog = prov.getDestDatabaseObject().getCatalogName();

       TableInfo pasteToTableInfo = prov.getPasteToTableInfo(destConn, destSchema, destCatalog);

       int sourceObjectCount = 0;
        for (IDatabaseObjectInfo info : sourceObjs) {
            if (! (info instanceof ITableInfo)) {
                continue;
            }
            ITableInfo sourceTI = (ITableInfo)info;
            sendTableCopyStarted(chooseDestTableInfo(sourceTI, pasteToTableInfo), sourceObjectCount+1);
            try {
                int destTableCount = DBUtil.getTableCount(destSession,
                                                          destCatalog,
                                                          destSchema,
                                                          chooseDestTableInfo(sourceTI, pasteToTableInfo).getSimpleName(),
                                                          DialectFactory.DEST_TYPE);
                if (destTableCount == -1) {
                    createTable(sourceTI, chooseDestTableInfo(sourceTI, pasteToTableInfo).getSimpleName(), destSchema, destCatalog);
                } 
                if (destTableCount > 0) {
                    try {
                        if (pref.appendRecordsToExisting()) {
                            /* Do nothing */
                        } else if (pref.deleteTableData(chooseDestTableInfo(sourceTI, pasteToTableInfo).getSimpleName())) {
                            // Yes || Yes to all
                            DBUtil.deleteDataInExistingTable(destSession,
                                                             destCatalog,
                                                             destSchema,
                                                             chooseDestTableInfo(sourceTI, pasteToTableInfo).getSimpleName());
                        } else {
                            continue; // skip this table, try the next.
                        }
                        
                    } catch (UserCancelledOperationException e) {
                        cancelled = true;
                        break;
                    }
                } 
                
                copyTable(sourceTI, pasteToTableInfo, counts[sourceObjectCount]);
                
                if (sourceObjectCount == sourceObjs.size() - 1 && !cancelled) {
                    // We just copied the last table.  Now it is safe to copy the
                    // constraints.(Well, that is, if all FK dependencies are met
                    // in the group of tables being copied. 
                    // TODO: new feature could be to examine table list for FK's 
                    // in tables not in the list then prompt the user to add 
                    // those missing tables to the list.
                    copyConstraints(sourceObjs);
                }
                if (!cancelled) {
                    sendTableCopyFinished(chooseDestTableInfo(sourceTI, pasteToTableInfo), sourceObjectCount+1);
                    sleep(prefs.getTableDelayMillis());
                }
            } catch (SQLException e) {
                encounteredException = true;
                sendErrorEvent(ErrorEvent.SQL_EXCEPTION_TYPE, e);
                break;
            } catch (MappingException e) {
                encounteredException = true;
                sendErrorEvent(ErrorEvent.MAPPING_EXCEPTION_TYPE, e);
                break;
            } catch (UserCancelledOperationException e) {
                cancelled = true;
                break;
            } catch (Exception e) {
                encounteredException = true;
                sendErrorEvent(ErrorEvent.GENERIC_EXCEPTION, e);
                break;
            }
            sourceObjectCount++;
        }        
        restoreAutoCommit(destConn);
        if (cancelled) {
            sendErrorEvent(ErrorEvent.USER_CANCELLED_EXCEPTION_TYPE);
            return;
        }
        if (encounteredException) {
            return;
        }         
        end = System.currentTimeMillis();
        
        ISession session = prov.getDestSession();
        if (session.getSessionSheet() != null) {
      	  session.getSchemaInfo().reload(DBUtil.getSchemaFromDbObject(prov.getDestDatabaseObject(), session.getSchemaInfo()));
      	  session.getSchemaInfo().fireSchemaInfoUpdate();
        }

        notifyCopyFinished();
    }

   private ITableInfo chooseDestTableInfo(ITableInfo sourceTI, TableInfo pasteToTableInfo)
   {
      if (null == pasteToTableInfo)
      {
         return sourceTI;
      }
      else
      {
         return pasteToTableInfo;
      }
   }

   /**
     * Registers the specified listener to receive copy events from this class.
     * 
     * @param listener
     */
    public void addListener(CopyTableListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        listeners.add(listener);
    }
    
    /**
     * Causes the current thread to sleep for the amount of time specified if 
     * sleepTime > 0.  No effect for sleepTime <= 0.
     * 
     * @param sleepTime time in milliseconds to make the current thread sleep.
     */
    private void sleep(long sleepTime) {
        boolean shouldSleep = prefs.isDelayBetweenObjects();
        if (!shouldSleep || sleepTime <= 0) {
            return;
        }
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            // Do Nothing
        }
    }
    
    /**
     * For all selected tables, loop through their columns and see if the column
     * name can be used as a column name in the destination database.  This 
     * method will send an error event if a table has any column names that 
     * cannot be used in the destination database.  This method just returns
     * true if the user preference is not to test column names.  
     * 
     * @return true if the tables can be created in the destination database; 
     *         false is returned otherwise.
     */
    private boolean analyzeTables() {
        boolean result = true;
        if (!prefs.isTestColumnNames()) {
            return true;
        }
        if (DBUtil.sameDatabaseType(prov.getSourceSession(), 
                                    prov.getDestSession()))
        {
            // No need to check column name validity when source and dest are
            // of the same type of database.
            return true;
        }
        sendAnalysisStarted();
        try {
            List<IDatabaseObjectInfo> dbObjs = prov.getSourceDatabaseObjects();
            int sourceObjectCount = 0;
            for (IDatabaseObjectInfo info : dbObjs) {
                ITableInfo ti = (ITableInfo) info;
                sendAnalyzingTable(ti, sourceObjectCount+1);
                DBUtil.validateColumnNames(ti, prov);
                sourceObjectCount++;
            }
        } catch (MappingException e) {
            sendErrorEvent(ErrorEvent.MAPPING_EXCEPTION_TYPE, e);
            result = false;
        } catch (UserCancelledOperationException e) {
            sendErrorEvent(ErrorEvent.USER_CANCELLED_EXCEPTION_TYPE, e);
            result = false;
        }
        return result;
    }        

    
    /**
     * Setup the auto-commit setting on the specified connection to 
     * the user's preference.
     * 
     * @param con
     */
    private void setupAutoCommit(ISQLConnection con) {
        boolean autoCommitPref = prefs.isAutoCommitEnabled();
        try {
            originalAutoCommitValue = con.getAutoCommit();
            currentAutoCommitValue = originalAutoCommitValue;
            if (autoCommitPref != originalAutoCommitValue) {
                con.setAutoCommit(autoCommitPref);
                currentAutoCommitValue = autoCommitPref;
            }
        } catch (SQLException e) {
            // Don't fool around with manual commit later.
            currentAutoCommitValue = true;
            sendErrorEvent(ErrorEvent.SETUP_AUTO_COMMIT_TYPE, e);
        }
    
    }
    
    /**
     * Restore the auto-commit setting on the specified connection to the 
     * whatever it was previous to our manipulation
     * 
     * @param con
     */
    private void restoreAutoCommit(ISQLConnection con) {
        if (originalAutoCommitValue == currentAutoCommitValue) {
            return;
        }
        try {
            con.setAutoCommit(originalAutoCommitValue);
        } catch (SQLException e) {
            sendErrorEvent(ErrorEvent.RESTORE_AUTO_COMMIT_TYPE, e);                      
        }
    }    
    
    private int[] getTableCounts() {
        int[] result = null;
        
        ISession sourceSession = prov.getSourceSession();
        List<IDatabaseObjectInfo> dbObjs = prov.getSourceDatabaseObjects();
        if (dbObjs != null) {
            result = new int[dbObjs.size()];
            selectedTableInfos = new ArrayList<ITableInfo>();
            int sourceObjectCount = 0;
            for (IDatabaseObjectInfo info : dbObjs) {
                if (! (info instanceof ITableInfo)) {
                    continue;
                }          
                try {
                    ITableInfo ti = (ITableInfo) info;
                    selectedTableInfos.add(ti);
                    // This doesn't appear to work for PROGRESS RDBMS
                    //result[i] = DBUtil.getTableCount(con, ti.getSimpleName());
                    result[sourceObjectCount] = 
                        DBUtil.getTableCount(sourceSession,
                                             ti.getCatalogName(),
                                             ti.getSchemaName(),
                                             ti.getSimpleName(),
                                             DialectFactory.SOURCE_TYPE);
                } catch (Exception e) {
                    log.error("",e);
                    result[sourceObjectCount] = 0;
                }
                sourceObjectCount++;
            }           
        }
        return result;
    }
    
    private void sendAnalysisStarted() {
        AnalysisEvent event = new AnalysisEvent(prov);
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.tableAnalysisStarted(event);
        }
    }
    
    private void sendAnalyzingTable(ITableInfo ti, int number) {
        TableEvent event = new TableEvent(prov);
        event.setTableCount(prov.getSourceDatabaseObjects().size());
        event.setTableNumber(number);
        Iterator<CopyTableListener> i = listeners.iterator();
        event.setTableName(ti.getSimpleName());
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.analyzingTable(event);
        }                
    }
    
    private void sendCopyStarted(int[] tableCounts) {
        CopyEvent event = new CopyEvent(prov);
        event.setTableCounts(tableCounts);
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.copyStarted(event);
        }        
    }
    
    private void sendTableCopyStarted(ITableInfo ti, int number) {
        TableEvent event = new TableEvent(prov);
        event.setTableNumber(number);
        event.setTableCount(prov.getSourceDatabaseObjects().size());
        event.setTableName(ti.getSimpleName());
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.tableCopyStarted(event);
        }
    }

    private void sendTableCopyFinished(ITableInfo ti, int number) {
        TableEvent event = new TableEvent(prov);
        event.setTableNumber(number);
        event.setTableCount(prov.getSourceDatabaseObjects().size());
        event.setTableName(ti.getSimpleName());
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.tableCopyFinished(event);
        }
    }    
    
    /**
     * Send an error event message to all CopyTableListeners
     * @param type the type of the ErrorEvent.
     */
    private void sendErrorEvent(int type) {
        sendErrorEvent(type, null);
    }

    /**
     * Send an error event message to all CopyTableListeners
     * @param type the type of the ErrorEvent.
     * @param e the exception that was encountered.
     */    
    private void sendErrorEvent(int type, Exception e) {
        ErrorEvent event = new ErrorEvent(prov, type);
        event.setException(e);
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.handleError(event);
        }        
    }
    
    private void sendRecordEvent(int number, int count) {
        RecordEvent event = new RecordEvent(prov, number, count);
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.recordCopied(event);
        }
    }
    
    private void sendStatementEvent(String sql, String[] vals) {
        StatementEvent event = 
            new StatementEvent(sql, StatementEvent.INSERT_RECORD_TYPE);
        event.setBindValues(vals);
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.statementExecuted(event);
        }        
    }
    
    private void notifyCopyFinished() {
        int seconds = (int)getElapsedSeconds();
        Iterator<CopyTableListener> i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = i.next();
            listener.copyFinished(seconds);
        }
    }
    
    /**
     * 
     * @return
     */
    private long getElapsedSeconds() {
        long result = 1;
        double elapsed = end - start;
        if (elapsed > 1000) {
            result = Math.round(elapsed / 1000);
        }
        return result;
    }
    
    /**
     * 
     *
     * @param sourceTableInfo
     * @param pasteToTableInfo
     *@param sourceTableCount  @throws MappingException
     * @throws SQLException
     */
    private void copyTable(ITableInfo sourceTableInfo, TableInfo pasteToTableInfo, int sourceTableCount)
        throws MappingException, SQLException, UserCancelledOperationException
    {
        PreparedStatement insertStmt = null;
        ResultSet rs = null;
        if (cancelled) {
            return;
        }
        if (!PreferencesManager.getPreferences().isCopyData()) {
            return;
        }
        ISQLConnection sourceConn = prov.getSourceSession().getSQLConnection();
        ISQLConnection destConn = prov.getDestSession().getSQLConnection();
        SQLDatabaseMetaData sourceMetaData = sourceConn.getSQLMetaData();
        SQLDatabaseMetaData destMetaData = destConn.getSQLMetaData();
        try {
            String destSchema = DBUtil.getSchemaNameFromDbObject(prov.getDestDatabaseObject());

            ITableInfo destTableInfo =
                DBUtil.getTableInfo(prov.getDestSession(),
                                    destSchema,
                                    chooseDestTableInfo(sourceTableInfo, pasteToTableInfo).getSimpleName());
            
            TableColumnInfo[] sourceInfos = sourceMetaData.getColumnInfo(sourceTableInfo);
            TableColumnInfo[] destInfos = destMetaData.getColumnInfo(destTableInfo);
            
            destInfos = sort(sourceInfos, 
                             destInfos, 
                             sourceTableInfo.getQualifiedName(),
                             destTableInfo.getQualifiedName());
            
            String sourceColList = DBUtil.getColumnList(sourceInfos);
            String destColList = DBUtil.getColumnList(destInfos);
            
            String selectSQL = DBUtil.getSelectQuery(prov,
                                                     sourceColList, 
                                                     sourceTableInfo);
            String insertSQL = DBUtil.getInsertSQL(prov, destColList,
                                                   destTableInfo,
                                                   destInfos.length);
            insertStmt = destConn.prepareStatement(insertSQL);
            
            int count = 1;
            int commitCount = prefs.getCommitCount(); 
            int columnCount = destInfos.length;
            String[] bindVarVals = new String[columnCount];
                        
            boolean foundLOBType = false;
            // Loop through source records...
            DBUtil.setLastStatement(selectSQL);
            rs = DBUtil.executeQuery(prov.getSourceSession(), selectSQL);
            DBUtil.setLastStatement(insertSQL);
            boolean isMysql = DialectFactory.isMySQL(destSession.getMetaData());
            boolean isSourceOracle = 
                DialectFactory.isOracle(sourceSession.getMetaData());
            boolean isDestOracle = DialectFactory.isOracle(destSession.getMetaData());
            while (rs.next() && !cancelled) {
                // MySQL driver gets unhappy when we use the same 
                // PreparedStatement to bind null and non-null LOB variables
                // without clearing the parameters first.
                if (isMysql && foundLOBType) 
                {
                    insertStmt.clearParameters();
                }
                StringBuilder lastStmtValuesBuffer = new StringBuilder();
                lastStmtValuesBuffer.append("\n(Bind variable values: ");
                for (int i = 0; i < columnCount; i++) {

                    int sourceColType = sourceInfos[i].getDataType();
                    // If source column is type 1111 (OTHER), try to use the 
                    // column type name to find a type that isn't 1111.
                    sourceColType = DBUtil.replaceOtherDataType(sourceInfos[i], prov.getSourceSession());
                    sourceColType = getDateReplacement(sourceColType, 
                                                       isSourceOracle);
                    
                    int destColType   = destInfos[i].getDataType();
                    // If source column is type 1111 (OTHER), try to use the 
                    // column type name to find a type that isn't 1111.
                    destColType = DBUtil.replaceOtherDataType(destInfos[i], prov.getDestSession());
                    destColType = getDateReplacement(destColType, isDestOracle);
                    
                    
                    String bindVal = DBUtil.bindVariable(insertStmt,
                                                         sourceColType,
                                                         destColType,
                                                         i+1,
                                                         rs);
                    bindVarVals[i] = bindVal;
                    lastStmtValuesBuffer.append(bindVal);
                    if (i + 1 < columnCount) {
                        lastStmtValuesBuffer.append(", ");
                    }
                    if (isLOBType(destColType)) {
                    	foundLOBType = true;
                    }
                }                
                lastStmtValuesBuffer.append(")");
                DBUtil.setLastStatementValues(lastStmtValuesBuffer.toString());
                sendStatementEvent(insertSQL, bindVarVals);
                insertStmt.executeUpdate();
                sendRecordEvent(count, sourceTableCount);
                count++;
                if (!currentAutoCommitValue) {
                    if ((count % commitCount) == 0) {
                        commitConnection(destConn);
                    }
                }
                sleep(prefs.getRecordDelayMillis());
            }
        } finally {
            SQLUtilities.closeResultSet(rs);
            SQLUtilities.closeStatement(insertStmt);
            if (!currentAutoCommitValue) {
                commitConnection(destConn);
            }
        }
    }
    
    /**
     * This will return a TIMESTAMP type when the specified type is a DATE and 
     * isOracle is true.  This is done so that Oracle dates that have a time 
     * component, will have the time component copied correctly.
     *  
     * @param session
     * @param type
     * @param isOracle
     * @return
     */
    private int getDateReplacement(int type, boolean isOracle) 
    {
        int result = type;
        if (isOracle && type == java.sql.Types.DATE) {
            result = java.sql.Types.TIMESTAMP;
        }
        return result;
    }
    
    /**
     * Returns a boolean value indicating whether or not the specific column
     * type is a binary or LOB column.
     * @param columnType the JDBC type.
     * 
     * @return true if the specified type is LOB; false otherwise.
     */
    private boolean isLOBType(int columnType) {
        if (columnType == Types.BLOB 
        		|| columnType == Types.CLOB
                || columnType == Types.LONGVARBINARY
                || columnType == Types.BINARY) 
        {
            return true;
        }
        return false;
    }
    
    /**
     * Sorts the specified destInfos array based on the order of the sourceInfos
     * array.  Not a very efficient algorthim, but it gets the job done.
     * TODO: rewrite this using Collections sorting capability.
     * 
     * @param sourceInfos
     * @param destInfos
     * @param sourceTableName
     * @param destTableName
     * @return a re-ordered version of the specified destInfos array
     * @throws MappingException if the arrays differ in length or column names.
     */
    private TableColumnInfo[] sort(TableColumnInfo[] sourceInfos, 
                                   TableColumnInfo[] destInfos,
                                   String sourceTableName,
                                   String destTableName)
        throws MappingException 
    {
        if (sourceInfos.length != destInfos.length) {
            //i18n[CopyExecutor.tablecolmismatch=Column count for table {0} in 
            //source database is {1}, but column count for table {2} in 
            //destination database is {3}
            String msg = 
                getMessage("CopyExecutor.tablecolmismatch",
                                      new Object[] {
                                              sourceTableName,
                                              Integer.valueOf(sourceInfos.length),
                                              destTableName,
                                              Integer.valueOf(destInfos.length)});
            throw new MappingException(msg);
        }
        ArrayList<TableColumnInfo> result = new ArrayList<TableColumnInfo>();
        
        for (int sourceIdx = 0; sourceIdx < sourceInfos.length; sourceIdx++) {
            TableColumnInfo sourceInfo = sourceInfos[sourceIdx];
            // trim the column name in case of HADB
            String sourceColumnName = sourceInfo.getColumnName().trim();
            boolean found = false;
            int destIdx = 0;
            while (!found && destIdx < destInfos.length) {
                TableColumnInfo destInfo = destInfos[destIdx];
                // trim the column name in case of HADB
                String destColumnName = destInfo.getColumnName().trim();
                if (destColumnName.equalsIgnoreCase(sourceColumnName)) {
                    result.add(destInfo);
                    found = true;
                }
                destIdx++;
            }
            if (!found) {
                throw new MappingException("Destination table "+destTableName+
                                    " doesn't appear to have a column named "+
                                    sourceInfo.getColumnName());
            }
        }
        return result.toArray(new TableColumnInfo[destInfos.length]);
    }
    
    /**
     * Commit the specified Connection and log any SQLExceptions that might 
     * occur.
     * 
     * @param connection
     */
    private void commitConnection(ISQLConnection connection) {
        try {
            connection.commit();
        } catch (SQLException e) {
            log.error("Failed to commit connection - "+connection, e);
        }
    }
    
    /**
     * Copies the foreign key constraints.  Primary keys are created in the table
     * create statement, since some databases don't support adding primary keys
     * after table creation. This will have no effect when using Axion as the 
     * source database.
     *  
     * @param sourceConn
     * @param destConn
     * @param ti
     * @throws SQLException
     */
    private void copyConstraints(List<IDatabaseObjectInfo> dbObjs) 
        throws SQLException, UserCancelledOperationException 
    {
        if (!prefs.isCopyForeignKeys() 
        		|| DialectFactory.isAxion(prov.getSourceSession().getMetaData())) {
            return;
        }
        ISQLConnection destConn = prov.getDestSession().getSQLConnection();
        for (IDatabaseObjectInfo info : dbObjs) {
            ITableInfo ti = (ITableInfo) info;
            Set<String> fkStmts = 
                DBUtil.getForeignKeySQL(prov, ti, selectedTableInfos);
            Iterator<String> it = fkStmts.iterator();
            while (it.hasNext()) {
                String fkSQL = it.next();
                DBUtil.setLastStatementValues("");
                try {
                    DBUtil.executeUpdate(destConn, fkSQL, true);
                } catch (SQLException e) {
                    log.error("Unexpected exception while attempting to " +
                              "create FK constraint using sql = "+fkSQL, e);
                }
            }           
        }
    }    
    
    private void createTable(ITableInfo ti, String destTableName, String destSchema, String destCatalog)
        throws SQLException, UserCancelledOperationException, MappingException
    {
        if (cancelled) {
            return;
        }
        ISQLConnection destCon = prov.getDestSession().getSQLConnection();
        String createTableSql = DBUtil.getCreateTableSql(prov, ti, destTableName, destSchema, destCatalog);

        if (log.isDebugEnabled()) {
      	  log.debug("Creating table in dest db with SQL: "+createTableSql);
        }
        
        DBUtil.executeUpdate(destCon, createTableSql, true);
        
        if (prefs.isCommitAfterTableDefs() && !currentAutoCommitValue) {
            commitConnection(destCon);
        }
        
        if (prefs.isCopyIndexDefs() && (null == prov.getPasteToTableName() || false == prov.isCopiedFormDestinationSession() ) ) {
            Collection<String> indices = null;
            ISQLDatabaseMetaData sqlmd = sourceSession.getMetaData();
            CreateScriptPreferences prefs = new CreateScriptPreferences();
            prefs.setQualifyTableNames(null != destSchema);

           if (this.prefs.isCopyPrimaryKeys()) {
                PrimaryKeyInfo[] pkList = sqlmd.getPrimaryKey(ti);
                List<PrimaryKeyInfo> pkList2 = Arrays.asList(pkList);
                indices = DialectUtils.createIndexes(ti, destTableName, destSchema, sqlmd, pkList2, prefs);
            } else {
                indices = DialectUtils.createIndexes(ti, destTableName, destSchema, sqlmd, null, prefs);
            }
            Iterator<String> i = indices.iterator();
            while (i.hasNext()) {
                String createIndicesSql = i.next();
                DBUtil.executeUpdate(destCon, createIndicesSql, true);
            }
        }
    }

    /**
     * @param pref The pref to set.
     */
    public void setPref(UICallbacks pref) {
        this.pref = pref;
    }

    /**
     * @return Returns the pref.
     */
    public UICallbacks getPref() {
        return pref;
    }    
    
}
