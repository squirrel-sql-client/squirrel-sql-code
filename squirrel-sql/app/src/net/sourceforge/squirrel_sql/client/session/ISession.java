package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

/**
 * The current session.
 */
public interface ISession extends IHasIdentifier {
    /**
     * IDs of tabs in the main tabbed pane.
     */
    public interface IMainTabIndexes extends SessionSheet.IMainTabIndexes {
    }

    /**
     * Close the current connection to the database.
     * 
     * @throws	SQLException	if an SQL error occurs.
     */
    void closeSQLConnection() throws SQLException;

    /**
     * Return the Application API object.
     *
     * @return the Application API object.
     */
    IApplication getApplication();

    /**
     * Return the current SQL connection object.
     *
     * @return the current SQL connection object.
     */
    SQLConnection getSQLConnection();

    /**
     * Return the driver used to connect to the database.
     *
     * @return the driver used to connect to the database.
     */
    ISQLDriver getDriver();

    /**
     * Return the alias used to connect to the database.
     *
     * @return the alias used to connect to the database.
     */
    ISQLAlias getAlias();

    /**
     * Return the properties for this session.
     *
     * @return the properties for this session.
     */
    SessionProperties getProperties();

    Object getPluginObject(IPlugin plugin, String key);
    Object putPluginObject(IPlugin plugin, String key, Object obj);

    void setMessageHandler(IMessageHandler handler);
    IMessageHandler getMessageHandler();

    String getSQLScript();
    void setSQLScript(String sqlScript);

    /**
     * Return an array of <TT>IDatabaseObjectInfo</TT> objects representing all
     * the objects selected in the objects tree.
     * 
     * @return	array of <TT>IDatabaseObjectInfo</TT> objects.
     */
    IDatabaseObjectInfo[] getSelectedDatabaseObjects();

    void setSessionSheet(SessionSheet child);
    SessionSheet getSessionSheet();

    /**
     * Add a listener listening for SQL Execution.
     *
     * @param   lis     Listener
     *
     * @throws  IllegalArgumentException
     *              If a null <TT>ISQLExecutionListener</TT> passed.
     */
    public void addSQLExecutionListener(ISQLExecutionListener lis)
        throws IllegalArgumentException;

    /**
     * Remove an SQL execution listener.
     *
     * @param   lis     Listener
     *
     * @throws  IllegalArgumentException
     *              If a null <TT>ISQLExecutionListener</TT> passed.
     */
    public void removeSQLExecutionListener(ISQLExecutionListener lis)
        throws IllegalArgumentException;

    /**
     * Select a tab in the main tabbed pane.
     * 
     * @param	tabIndex	The tab to select. @see #IMainTabIndexes
     * 
     * @throws	IllegalArgumentException
     * 			Thrown if an invalid <TT>tabIndex</TT> passed.
     */
    void selectMainTab(int tabIndex) throws IllegalArgumentException;

    /**
     * Execute the current SQL.
     */
    void executeCurrentSQL();

    /**
     * Commit the current SQL transaction.
     */
    void commit();

    /**
     * Rollback the current SQL transaction.
     */
    void rollback();
}
