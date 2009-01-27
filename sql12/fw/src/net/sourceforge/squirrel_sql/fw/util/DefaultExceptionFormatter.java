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
package net.sourceforge.squirrel_sql.fw.util;

import java.sql.DataTruncation;
import java.sql.SQLException;
import java.sql.SQLWarning;

import net.sourceforge.squirrel_sql.fw.sql.SQLExecutionException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * The default implementation of ExceptionFormatter which is used by Session's
 * IMessageHandler (MessagePanel) if another one isn't specified by a plugin.
 * Also, if a custom ExceptionFormatter is stored
 * 
 * @author manningr
 */
public class DefaultExceptionFormatter implements ExceptionFormatter {
    
    /** Logger for this class. */
    private static final ILogger s_log =
        LoggerController.createLogger(DefaultExceptionFormatter.class);
                
    /** 
     * When a plugin registers it's own ExceptionFormatter for session which 
     * contains this class, this will be non-null
     */
    private ExceptionFormatter customFormatter = null;
    
    /**
     * @see net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter#format(java.lang.Throwable)
     */
    public String format(Throwable th) {
        if (th == null) {
            throw new IllegalArgumentException("format: th cannot be null");
        }
        StringBuilder result = new StringBuilder();
        
        String postError = "";
        Throwable cause = th;
        if (th instanceof SQLExecutionException) {
            postError = ((SQLExecutionException)th).getPostError();
            cause = th.getCause();
        }
        
        String customMessage = null;
        
        if (customFormatter != null && customFormatter.formatsException(cause)) {
            try {
                customMessage = customFormatter.format(cause);
            } catch (Exception e) {
                s_log.error( 
                    "Exception occurred while formatting:  "+e.getMessage(), e);
            }
        } 
        
        if (customMessage != null) {
            result.append(customMessage);
        } else {
            result.append(defaultFormatSQLException(cause));
        }
        if (postError != null && !"".equals(postError)) {
            result.append("\n");
            result.append(postError);
        }

        return result.toString();
    }

    public String defaultFormatSQLException(Throwable cause) {
        StringBuilder result = new StringBuilder();
        if (cause instanceof DataTruncation) {
            result.append(getDataTruncationMessage((DataTruncation)cause));
        } else if (cause instanceof SQLWarning){
            result.append(getSQLWarningMessage((SQLWarning)cause));
        } else if (cause instanceof SQLException) {
            result.append(getSQLExceptionMessage((SQLException)cause));
        } else {
            result.append(cause.toString());
        }
        return result.toString();
    }
    
    
    /**
     * @see net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter#formatsException(java.lang.Throwable)
     */
    public boolean formatsException(Throwable t) {
        return true;
    }
    
    /**
     * Sets the custom ExceptionFormatter to delegate calls to format to if the 
     * custom formatter formats the exception.
     * 
     * @param customFormatter the ExceptionFormatter to delegate calls to.  If 
     *                        one has already been set, this will have no effect
     *                        except logging an error message.
     */
    public void setCustomExceptionFormatter(ExceptionFormatter customFormatter) {
        if (customFormatter == null) {
            throw new IllegalArgumentException("customFormatter cannot be null");
        }
        
        if (this.customFormatter == null) {
            this.customFormatter = customFormatter;
        } else {
            // Uh-oh!  We shouldn't have more than one plugin registering a 
            // custom exception handler for a given session.  The first one 
            // wins and all others get an error message.
            s_log.error(
                "setCustomExceptionFormatter: An existing customFormatter ( "+
                this.customFormatter.getClass().getName()+" )has already " +
                "been set - ignoring "+customFormatter.getClass().getName());
        }
    }
    
    private String getDataTruncationMessage(DataTruncation ex) {
        StringBuilder buf = new StringBuilder();
        buf.append("Data Truncation error occured on")
           .append(ex.getRead() ? " a read " : " a write ")
           .append(" of column ")
           .append(ex.getIndex())
           .append("Data was ")
           .append(ex.getDataSize())
           .append(" bytes long and ")
           .append(ex.getTransferSize())
           .append(" bytes were transferred.");
        return buf.toString();
    }
    
    private String getSQLWarningMessage(SQLWarning ex) {
        StringBuilder buf = new StringBuilder();
        while (ex != null)
        {
           buf.append(buildMessage("Warning:   ", ex));
           ex = ex.getNextWarning();
        }

        return buf.toString();
    }
    
    private String getSQLExceptionMessage(SQLException ex) {
        StringBuilder buf = new StringBuilder();
        while (ex != null)
        {
            buf.append(buildMessage("Error: ", ex));
            if (s_log.isDebugEnabled()) {
                s_log.debug("Error", ex);
            }
            ex = ex.getNextException();
            if (ex != null) {
                buf.append("\n");
            }
        }                
        return buf.toString();
    }

    private String buildMessage(String prefix, SQLException ex) {
        StringBuilder result = new StringBuilder();
        result.append(prefix);
        result.append(ex.getMessage());
        result.append("\nSQLState:  ");
        result.append(ex.getSQLState());
        result.append("\nErrorCode: ");
        result.append(ex.getErrorCode());
        return result.toString();
    }

}
