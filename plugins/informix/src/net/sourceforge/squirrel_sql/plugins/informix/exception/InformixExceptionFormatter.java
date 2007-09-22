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
package net.sourceforge.squirrel_sql.plugins.informix.exception;

import java.lang.reflect.Method;
import java.sql.Connection;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISessionListener;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A custom exception formatter for Informix database which provides the 
 * offset of the syntax error in the current SQL that had problems.  This will 
 * also set the cursor position to the location of the offset.  This uses the 
 * Informix-specific IfmxConnection class which is available in the Informix 
 * driver. 
 *   
 * @author manningr
 */
public class InformixExceptionFormatter extends SessionAdapter 
    implements ISessionListener, ExceptionFormatter {
    
    /** Interface to allow us to set the caret position in the SQL editor */
    private ISQLEntryPanel sqlEntryPanel = null;
    
    /** The session that we are interested in */
    private ISession _session = null;
    
    /** Default SQLException report format */
    private static final DefaultExceptionFormatter defaultFormatter = 
        new DefaultExceptionFormatter();
    
    /** Logger for this class. */
    private static final ILogger s_log =
       LoggerController.createLogger(InformixExceptionFormatter.class); 
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(InformixExceptionFormatter.class);
    
    static interface i18n {
        //i18n[InformixExceptionFormatter.notAvailableMsg=Not Available]
        String NOT_AVAILABLE_MSG = 
            s_stringMgr.getString("InformixExceptionFormatter.notAvailableMsg");
        
        //i18n[InformixExceptionFormatter.positionLabel=Position: ] 
        String positionLabel = 
            s_stringMgr.getString("InformixExceptionFormatter.positionLabel");
    }
    
    /**
     * Constructs a new instance of this ExceptionFormatter to work with the 
     * specified session.
     * 
     * @param session the ISesssion implementation to work with.
     */
    public InformixExceptionFormatter(ISession session) {
        this.sqlEntryPanel = 
            session.getSQLPanelAPIOfActiveSessionWindow().getSQLEntryPanel();
        this._session = session;
        session.getApplication().getSessionManager().addSessionListener(this);
    }
    
    /**
     * @see net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter#format(java.lang.Throwable)
     */
    public String format(Throwable t) throws Exception {
        StringBuilder msg = new StringBuilder();
        msg.append(defaultFormatter.format(t));
        ISQLConnection sqlcon = _session.getSQLConnection();
        if (sqlcon != null && sqlcon.getConnection() != null) {
            String offset = getSqlErrorOffset();
            msg.append("\n");
            msg.append(i18n.positionLabel);
            msg.append(offset);
            if (!i18n.NOT_AVAILABLE_MSG.equals(offset)) {
                int offsetNum = getNumber(offset, -1);
                if (offsetNum != -1) {
                    int[] bounds = sqlEntryPanel.getBoundsOfSQLToBeExecuted();
                    int start = bounds[0];
                    int newPosition = start + offsetNum - 1;
                    sqlEntryPanel.setCaretPosition(newPosition);
                }
            }   
        } else {
            msg.append(i18n.NOT_AVAILABLE_MSG);
        }
        return msg.toString();
    }
    
    /**
     * @see net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter#formatsException(java.lang.Throwable)
     */
    public boolean formatsException(Throwable t) {
        return true;
    }

    /**
     * @return a string describing the offset of the error in the current SQL.
     */
    private String getSqlErrorOffset() {
        String result = i18n.NOT_AVAILABLE_MSG;
        try {
            ISQLConnection sqlcon = _session.getSQLConnection();
            Class<?> conClass = sqlcon.getConnection().getClass();
            Connection ifmxcon = sqlcon.getConnection();

            Method getSQLStatementOffsetMethod = 
                conClass.getMethod("getSQLStatementOffset", (Class[])null);
            Object offset = 
                getSQLStatementOffsetMethod.invoke(ifmxcon, (Object[])null);
            result = offset.toString();
        } catch (Exception e) {
            s_log.error("getSqlErrorOffset: Unexpected exception - "
                    + e.getMessage(), e);
        }
        return result;
    }
    
    /**
     * Returns a number that is embodied by the specified string.
     * 
     * @param numberStr
     *        the String containing the number to return
     * @param defaultNum
     *        the number to return if the specified number string doesn't
     *        actually represent a number.
     * @return numberStr as a number or defaultNum if that isn't possible.
     */
    private int getNumber(String numberStr, int defaultNum) {
        int result = defaultNum;
        try {
            result = Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            s_log.error("getNumber: Unexpected exception - "
                + e.getMessage(), e);
        }
        return result;
    }

    // ISessionListener interface methods
    
    /*     
     * Since we depend upon the Informix-specific IfmxConnection class, we need 
     * to keep a reference to the ISession we are associated with.  However,
     * this session could be closed, at which time we want to give up our 
     * reference so that it can be garbage collected.
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
