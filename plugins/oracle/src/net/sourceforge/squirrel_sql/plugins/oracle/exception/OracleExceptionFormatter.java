/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.oracle.exception;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISessionListener;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * An ExceptionFormatter for Oracle which adds the position of the syntax error
 * and moves the caret position to that error position.  It does nothing else
 * to format the exception, beyond what the default formatter does.
 * 
 * @author manningr
 */
public class OracleExceptionFormatter extends SessionAdapter 
    implements ISessionListener, ExceptionFormatter {

    /**
     * Logger for this class.
     */
    private final static ILogger s_log = 
        LoggerController.createLogger(OracleExceptionFormatter.class);  

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(OracleExceptionFormatter.class);
    
    private static interface i18n {
        //i18n[OracleExceptionFormatter.positionLabel=Position: ]
        String POSITION_LABEL = 
            s_stringMgr.getString("OracleExceptionFormatter.positionLabel");
    }
    
    /** The session that this formatter is associated with */
    private ISession _session = null;
    
    private DefaultExceptionFormatter formatter = new DefaultExceptionFormatter();
    
    private boolean offsetFunctionAvailable = false;
    
    /** Interface to allow us to set the caret position in the SQL editor */
    private ISQLEntryPanel sqlEntryPanel = null;
    
    
    public OracleExceptionFormatter() {
    }
    
    /**
     * Sets the Oracle session that this formatter is associated with.
     * 
     * @param session the session
     */
    public void setSession(ISession session) {
        _session = session;        
        this.sqlEntryPanel = 
            session.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel();
        
        try {
            if (!isOffsetFunctionAvailable()) {
                if (initOffsetFunction()) {
                   offsetFunctionAvailable = true;
                }
            }
        } catch (SQLException e) {
            s_log.error("setSession: Unexpected exception - "+
                e.getMessage(), e);            
        }
    }
    
    /** 
     * The name to give to our function.  Oracle doesn't support anonymous 
     * function blocks.
     */
    public static final String OFFSET_FUNCTION_NAME = 
        "SQUIRREL_GET_ERROR_OFFSET";
    
    /** 
     * The syntax error offset function to use to determine where the error 
     * occurred in a SQL statement.
     */
    private static final String OFFSET_FUNCTION = 
        "create or replace function "+OFFSET_FUNCTION_NAME+
        " (query IN varchar2) " +
        "return number authid current_user " +
        "is " +
        "     l_theCursor     integer default dbms_sql.open_cursor; " +
        "     l_status        integer; " +
        "begin " +
        "         begin " +
        "         dbms_sql.parse(  l_theCursor, query, dbms_sql.native ); " +
        "         exception " +
        "                 when others then l_status := dbms_sql.last_error_position; " +
        "         end; " +
        "         dbms_sql.close_cursor( l_theCursor ); " +
        "         return l_status; " +
        "end; ";
    
    /**
     * Pass through to default formatter, detecting the error position and 
     * setting the caret position appropriately.
     * 
     * @see net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter#format(java.lang.Throwable)
     */
    public String format(Throwable t) throws Exception {
        StringBuilder result = new StringBuilder(formatter.format(t));
        String sql = getCurrentSql();
        if (sql != null) {
            int position = getErrorPosition(sql);
            if (position != -1) {
                result.append("\n");
                result.append(i18n.POSITION_LABEL);
                result.append(position);
                
                int[] bounds = sqlEntryPanel.getBoundsOfSQLToBeExecuted();
                int start = bounds[0];
                int newPosition = start + position;
                sqlEntryPanel.setCaretPosition(newPosition);
                
            }
        }
        return result.toString();
    }

    /**
     * @see net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter#formatsException(java.lang.Throwable)
     */
    public boolean formatsException(Throwable t) {
        return true;
    }
    
    private String getCurrentSql() {
        String result = null;
        ISQLPanelAPI api = _session.getSQLPanelAPIOfActiveSessionWindow();
        result = api.getSQLEntryPanel().getSQLToBeExecuted();
        return result;
    }
    
    private int getErrorPosition(String sql) throws SQLException {
        int result = -1;
        ISQLConnection sqlcon = _session.getSQLConnection();
        Connection con = sqlcon.getConnection();
        CallableStatement cstmt = null;

        try {
            String callSql = "{?=call "+OFFSET_FUNCTION_NAME+"(?)}";
            if (s_log.isDebugEnabled()) {
                s_log.debug("getErrorPosition: Executing sql: "+callSql);
                s_log.debug("getErrorPosition: errant SQL was: "+sql);
            }
            cstmt = con.prepareCall(callSql);
            cstmt.registerOutParameter(1, java.sql.Types.INTEGER);
            cstmt.setString(2, sql);
            cstmt.execute();
            result = cstmt.getInt(1);
        } catch (SQLException e ) {
            s_log.error("getErrorPosition: Unexpected exception - "+
                e.getMessage(), e);
        } finally {
            SQLUtilities.closeStatement(cstmt);
        }
        return result;
    }
    
    private boolean initOffsetFunction() throws SQLException {
        ISQLConnection sqlcon = _session.getSQLConnection();
        Connection con = sqlcon.getConnection();
        CallableStatement cstmt = null;
        Statement stmt = null;
        boolean result = true;
        try {
            stmt = con.createStatement();
            if (s_log.isDebugEnabled()) {
                s_log.debug("initOffsetFunction: Executing sql: "+
                            OFFSET_FUNCTION);    
            }
            
            stmt.executeUpdate(OFFSET_FUNCTION);
        } catch (SQLException e ) {
           result = false; 
           s_log.error("initOffsetFunction: Unexpected exception - "+
                e.getMessage(), e);
            
            
        } finally {
            SQLUtilities.closeStatement(cstmt);
            SQLUtilities.closeStatement(stmt);
        }
        return result;
    }
    
    private boolean isOffsetFunctionAvailable() throws SQLException {
        // Don't try to find it if we have already created it.
        if (offsetFunctionAvailable) {
            return true;
        }
        boolean result = false;
        String[] functionNames = _session.getMetaData().getStringFunctions();
        for (String functionName : functionNames) {
            if (OFFSET_FUNCTION_NAME.equals(functionName)) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("isOffsetFunctionAvailable: Found offset " +
                    		"function: "+OFFSET_FUNCTION_NAME);
                }
                result = true;
                break;
            }
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("isOffsetFunctionAvailable: Couldn't locate offset " +
                            "function: "+OFFSET_FUNCTION_NAME);
        }
        return result;
    }
    
    // ISessionListener interface methods
    
    /*     
     * Since we depend upon the Connection class associated with the ISession, 
     * we need to keep a reference to the ISession we are associated with.  
     * However, this session could be closed, at which time we want to give up 
     * our reference so that it can be garbage collected.
     */
    
    /**
     * @see net.sourceforge.squirrel_sql.client.session.event.ISessionListener#allSessionsClosed()
     */
    public void allSessionsClosed() {
        _session.getApplication().getSessionManager().removeSessionListener(this);
        _session = null;
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.event.ISessionListener#sessionClosed(net.sourceforge.squirrel_sql.client.session.event.SessionEvent)
     */
    public void sessionClosed(SessionEvent evt) {
        if (evt.getSession() == _session) {
            _session.getApplication().getSessionManager().removeSessionListener(this);
            _session = null;
        }
    }

    /**
     * @see net.sourceforge.squirrel_sql.client.session.event.ISessionListener#sessionClosing(net.sourceforge.squirrel_sql.client.session.event.SessionEvent)
     */
    public void sessionClosing(SessionEvent evt) {
        if (evt.getSession() == _session) {
            _session.getApplication().getSessionManager().removeSessionListener(this);
            _session = null;
        }        
    }
    

}
