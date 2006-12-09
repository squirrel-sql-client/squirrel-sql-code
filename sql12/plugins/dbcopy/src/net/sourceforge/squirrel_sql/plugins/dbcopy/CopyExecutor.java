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
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.AnalysisEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.ErrorEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.RecordEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.StatementEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.DBCopyPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;
import net.sourceforge.squirrel_sql.plugins.dbcopy.util.Compat;
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
    private ArrayList selectedTableInfos = null;    
    
    /** the CopyTableListeners that have registered with this class */
    private ArrayList listeners = new ArrayList();
    
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
        sourceSession = prov.getCopySourceSession();
        destSession = prov.getCopyDestSession();
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
        SQLConnection destConn = destSession.getSQLConnection();
        if (!analyzeTables()) {
            return;
        }
        setupAutoCommit(destConn);
        IDatabaseObjectInfo[] dbObjs = prov.getSourceSelectedDatabaseObjects();
        int[] counts = getTableCounts();
        sendCopyStarted(counts);
        String destSchema = prov.getDestSelectedDatabaseObject().getSimpleName();
        for (int i = 0; i < dbObjs.length; i++) {
            if (false == dbObjs[i] instanceof ITableInfo) {
                continue;
            }
            ITableInfo ti = (ITableInfo)dbObjs[i];
            sendTableCopyStarted(ti, i+1);
            try {
                int destTableCount = DBUtil.getTableCount(destSession, 
                                                          destSchema, 
                                                          ti.getSimpleName(),
                                                          DialectFactory.DEST_TYPE);
                if (destTableCount == -1) {
                    createTable(ti);
                } 
                if (destTableCount > 0) {
                    try {
                        String t = ti.getSimpleName();
                        if (pref.appendRecordsToExisting(t)) {
                            /* Do nothing */
                        } else if (pref.deleteTableData(ti.getSimpleName())) {
                            // Yes || Yes to all
                            DBUtil.deleteDataInExistingTable(destSession,
                                                             destSchema,
                                                             ti.getSimpleName());
                        } else {
                            continue; // skip this table, try the next.
                        }
                        
                    } catch (UserCancelledOperationException e) {
                        cancelled = true;
                        break;
                    }
                } 
                
                copyTable(ti, counts[i]);
                
                if (i == dbObjs.length - 1 && !cancelled) {
                    // We just copied the last table.  Now it is safe to copy the
                    // constraints.(Well, that is, if all FK dependencies are met
                    // in the group of tables being copied. 
                    // TODO: new feature could be to examine table list for FK's 
                    // in tables not in the list then prompt the user to add 
                    // those missing tables to the list.
                    copyConstraints(dbObjs);
                }
                if (!cancelled) {
                    sendTableCopyFinished(ti, i+1);
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
        Compat.reloadSchema(prov.getCopyDestSession(), 
        					prov.getDestSelectedDatabaseObject());	
        notifyCopyFinished();
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
        if (DBUtil.sameDatabaseType(prov.getCopySourceSession(), 
                                    prov.getCopyDestSession()))
        {
            // No need to check column name validity when source and dest are
            // of the same type of database.
            return true;
        }
        sendAnalysisStarted();
        try {
            IDatabaseObjectInfo[] dbObjs = prov.getSourceSelectedDatabaseObjects();        
            for (int tableIdx = 0; tableIdx < dbObjs.length; tableIdx++) {
                ITableInfo ti = (ITableInfo)dbObjs[tableIdx];
                sendAnalyzingTable(ti, tableIdx);
                DBUtil.validateColumnNames(ti, prov);
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
    private void setupAutoCommit(SQLConnection con) {
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
    private void restoreAutoCommit(SQLConnection con) {
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
        
        ISession sourceSession = prov.getCopySourceSession();
        IDatabaseObjectInfo[] dbObjs = prov.getSourceSelectedDatabaseObjects();
        if (dbObjs != null) {
            result = new int[dbObjs.length];
            selectedTableInfos = new ArrayList();
            for (int i = 0; i < dbObjs.length; i++) {
                if (false == dbObjs[i] instanceof ITableInfo) {
                    continue;
                }          
                try {
                    ITableInfo ti = (ITableInfo) dbObjs[i];
                    selectedTableInfos.add(ti);
                    // This doesn't appear to work for PROGRESS RDBMS
                    //result[i] = DBUtil.getTableCount(con, ti.getSimpleName());
                    result[i] = 
                        DBUtil.getTableCount(sourceSession,
                                             ti.getSchemaName(),
                                             ti.getSimpleName(),
                                             DialectFactory.SOURCE_TYPE);
                } catch (Exception e) {
                    log.error("",e);
                    result[i] = 0;
                }
            }           
        }
        return result;
    }
    
    private void sendAnalysisStarted() {
        AnalysisEvent event = new AnalysisEvent(prov);
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = (CopyTableListener)i.next();
            listener.tableAnalysisStarted(event);
        }
    }
    
    private void sendAnalyzingTable(ITableInfo ti, int number) {
        TableEvent event = new TableEvent(prov);
        event.setTableCount(prov.getSourceSelectedDatabaseObjects().length);
        event.setTableNumber(number);
        Iterator i = listeners.iterator();
        event.setTableName(ti.getSimpleName());
        while (i.hasNext()) {
            CopyTableListener listener = (CopyTableListener)i.next();
            listener.analyzingTable(event);
        }                
    }
    
    private void sendCopyStarted(int[] tableCounts) {
        CopyEvent event = new CopyEvent(prov);
        event.setTableCounts(tableCounts);
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = (CopyTableListener)i.next();
            listener.copyStarted(event);
        }        
    }
    
    private void sendTableCopyStarted(ITableInfo ti, int number) {
        TableEvent event = new TableEvent(prov);
        event.setTableNumber(number);
        event.setTableCount(prov.getSourceSelectedDatabaseObjects().length);
        event.setTableName(ti.getSimpleName());
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = (CopyTableListener)i.next();
            listener.tableCopyStarted(event);
        }
    }

    private void sendTableCopyFinished(ITableInfo ti, int number) {
        TableEvent event = new TableEvent(prov);
        event.setTableNumber(number);
        event.setTableCount(prov.getSourceSelectedDatabaseObjects().length);
        event.setTableName(ti.getSimpleName());
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = (CopyTableListener)i.next();
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
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = (CopyTableListener)i.next();
            listener.handleError(event);
        }        
    }
    
    private void sendRecordEvent(int number, int count) {
        RecordEvent event = new RecordEvent(prov, number, count);
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = (CopyTableListener)i.next();
            listener.recordCopied(event);
        }
    }
    
    private void sendStatementEvent(String sql, String[] vals) {
        StatementEvent event = 
            new StatementEvent(sql, StatementEvent.INSERT_RECORD_TYPE);
        event.setBindValues(vals);
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = (CopyTableListener)i.next();
            listener.statementExecuted(event);
        }        
    }
    
    private void notifyCopyFinished() {
        int seconds = (int)getElapsedSeconds();
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            CopyTableListener listener = (CopyTableListener)i.next();
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
     * @param sourceTableInfo
     * @param sourceTableCount
     * @throws MappingException
     * @throws SQLException
     */
    private void copyTable(ITableInfo sourceTableInfo, int sourceTableCount) 
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
        SQLConnection sourceConn = prov.getCopySourceSession().getSQLConnection();
        SQLConnection destConn = prov.getCopyDestSession().getSQLConnection();
        SQLDatabaseMetaData sourceMetaData = sourceConn.getSQLMetaData();
        SQLDatabaseMetaData destMetaData = destConn.getSQLMetaData();
        try {
            String destSchema = 
                prov.getDestSelectedDatabaseObject().getSimpleName();            
            ITableInfo destTableInfo = 
                DBUtil.getTableInfo(prov.getCopyDestSession(),
                                    destSchema,
                                    sourceTableInfo.getSimpleName());
            
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
                                                   sourceTableInfo, 
                                                   destInfos.length);
            insertStmt = destConn.prepareStatement(insertSQL);
            
            int count = 1;
            int commitCount = prefs.getCommitCount(); 
            int columnCount = destInfos.length;
            String[] bindVarVals = new String[columnCount];
                        
            boolean foundLOBType = false;
            // Loop through source records...
            rs = DBUtil.executeQuery(prov.getCopySourceSession(), selectSQL);
            while (rs.next() && !cancelled) {
                // MySQL driver gets unhappy when we use the same 
                // PreparedStatement to bind null and non-null LOB variables
                // without clearing the parameters first.
                if (DialectFactory.isMySQLSession(destSession) 
                        && foundLOBType) 
                {
                    insertStmt.clearParameters();
                }
                for (int i = 0; i < columnCount; i++) {

                    int sourceColType = sourceInfos[i].getDataType();
                    // If source column is type 1111 (OTHER), try to use the 
                    // column type name to find a type that isn't 1111.
                    sourceColType = DBUtil.replaceOtherDataType(sourceInfos[i]);

                    int destColType   = destInfos[i].getDataType();
                    // If source column is type 1111 (OTHER), try to use the 
                    // column type name to find a type that isn't 1111.
                    destColType = DBUtil.replaceOtherDataType(destInfos[i]);

                    
                    String bindVal = DBUtil.bindVariable(insertStmt,
                                                         sourceColType,
                                                         destColType,
                                                         i+1,
                                                         rs);
                    bindVarVals[i] = bindVal;
                    if (isLOBType(destColType)) {
                    	foundLOBType = true;
                    }
                }                
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
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(insertStmt);
            if (!currentAutoCommitValue) {
                commitConnection(destConn);
            }
        }
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
            String msg = 
                "Table "+sourceTableName+" in source " +
                "database has "+sourceInfos.length+" columns, but table "+
                destTableName+" in destination database "+
                "has "+destInfos.length+" columns";
                
            throw new MappingException(msg);
        }
        ArrayList result = new ArrayList();
        
        for (int sourceIdx = 0; sourceIdx < sourceInfos.length; sourceIdx++) {
            TableColumnInfo sourceInfo = sourceInfos[sourceIdx];
            boolean found = false;
            int destIdx = 0;
            while (!found && destIdx < destInfos.length) {
                TableColumnInfo destInfo = destInfos[destIdx];
                String destColumnName = destInfo.getColumnName();
                String sourceColumnName = sourceInfo.getColumnName();
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
        return (TableColumnInfo[])result.toArray(new TableColumnInfo[destInfos.length]);
    }
    
    /**
     * Commit the specified Connection and log any SQLExceptions that might 
     * occur.
     * 
     * @param connection
     */
    private void commitConnection(SQLConnection connection) {
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
    private void copyConstraints(IDatabaseObjectInfo[] dbObjs) 
        throws SQLException, UserCancelledOperationException 
    {
        if (!prefs.isCopyForeignKeys() 
        		|| DialectFactory.isAxionSession(prov.getCopySourceSession())) {
            return;
        }
        SQLConnection destConn = prov.getCopyDestSession().getSQLConnection();
        for (int i = 0; i < dbObjs.length; i++) {
            ITableInfo ti = (ITableInfo) dbObjs[i];
            Set fkStmts = 
                DBUtil.getForeignKeySQL(prov, ti, selectedTableInfos);
            Iterator it = fkStmts.iterator();
            while (it.hasNext()) {
                String fkSQL = (String)it.next();
                DBUtil.executeUpdate(destConn, fkSQL, true);
            }           
        }
    }    
    
    private void createTable(ITableInfo ti) 
        throws SQLException, UserCancelledOperationException, MappingException
    {
        if (cancelled) {
            return;
        }
        SQLConnection destCon = prov.getCopyDestSession().getSQLConnection();
        String createTableSql = DBUtil.getCreateTableSql(prov, ti);
        DBUtil.executeUpdate(destCon, createTableSql, true);
        
        if (prefs.isCommitAfterTableDefs() && !currentAutoCommitValue) {
            commitConnection(destCon);
        }
        
        if (prefs.isCopyIndexDefs()) {
            Collection indices = 
                DBUtil.getCreateIndicesSQL(prov, ti);
            
            Iterator i = indices.iterator();
            while (i.hasNext()) {
                String createIndicesSql = (String)i.next();
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
