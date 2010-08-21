package net.sourceforge.squirrel_sql.fw.sql;
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
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Date;

public interface ISQLConnection {

    public interface IPropertyNames
    {
        String AUTO_COMMIT = "autocommit";
        String CATALOG = "catalog";
    }    
    
    void close() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;

    /**
     * Retrieve the properties specified when connection was opened. This can
     * be <TT>null</TT>.
     * 
     * @return	Connection properties.
     */
    SQLDriverPropertyCollection getConnectionProperties();

    boolean getAutoCommit() throws SQLException;

    void setAutoCommit(boolean value) throws SQLException;

    boolean getCommitOnClose();

    int getTransactionIsolation() throws SQLException;

    void setTransactionIsolation(int value) throws SQLException;

    void setCommitOnClose(boolean value);

    Statement createStatement() throws SQLException;

    PreparedStatement prepareStatement(String sql) throws SQLException;

    /**
     * Retrieve the time that this connection was opened. Note that this time
     * is the time that this <TT>SQLConnection</TT> was created, not the time
     * that the <TT>java.sql.Connection</TT> object that it is wrapped around
     * was opened.
     * 
     * @return	Time connection opened.
     */
    Date getTimeOpened();

    /**
     * Retrieve the time that this connection was closed. If this connection
     * is still opened then <TT>null</TT> will be returned..
     * 
     * @return	Time connection closed.
     */
    Date getTimeClosed();

    /**
     * Retrieve the metadata for this connection.
     * 
     * @return	The <TT>SQLMetaData</TT> object.
     */
    SQLDatabaseMetaData getSQLMetaData();

    Connection getConnection();

    String getCatalog() throws SQLException;

    void setCatalog(String catalogName) throws SQLException;

    SQLWarning getWarnings() throws SQLException;

    /**
     * Add a listener for property change events.
     *
     * @param	lis		The new listener.
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove a property change listener.
     *
     * @param	lis		The listener to be removed.
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    ISQLDriver getSQLDriver();

}