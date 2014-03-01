package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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

import java.sql.SQLException;

import javax.swing.Action;
import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.session.MainPanel;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.event.SimpleSessionListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

/**
 * The current session.
 */
public interface ISession extends IHasIdentifier
{

   public interface IMainPanelTabIndexes extends MainPanel.ITabIndexes
   {
      // Empty body.
   }

   /**
    * Retrieve whether this session has been closed.
    *
    * @return	<TT>true</TT> if session closed else <TT>false</TT>.
    */
   boolean isClosed();

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
   ISQLConnection getSQLConnection();

   /**
    * Return the current SQL connection database metadata.
    *
    * @return the current SQL connection database metadata.
    */
   ISQLDatabaseMetaData getMetaData();
   
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
   ISQLAliasExt getAlias();

   /**
    * Return the properties for this session.
    *
    * @return the properties for this session.
    */
   SessionProperties getProperties();

    /**
    * Commit the current SQL session.
    */
   void commit();

   /**
    * Rollback the current SQL session.
    */
   void rollback();

   /**
    * Close this session.
    *
    * @throws	SQLException
    * 			Thrown if an error closing the SQL connection. The session
    * 			will still be closed even though the connection may not have
    *			been.
    */
   void close() throws SQLException;

   /**
    * Close the current connection to the database.
    *
    * @throws	SQLException	if an SQL error occurs.
    */
   void closeSQLConnection() throws SQLException;

   void setSessionInternalFrame(SessionInternalFrame sif);

   /**
    * Reconnect to the database.
    */
   void reconnect();

   Object getPluginObject(IPlugin plugin, String key);
   Object putPluginObject(IPlugin plugin, String key, Object obj);
   void removePluginObject(IPlugin plugin, String key);

   void setMessageHandler(IMessageHandler handler);

   SessionPanel getSessionSheet();

   SessionInternalFrame getSessionInternalFrame();

// JASON:
//	SQLFilterClauses getSQLFilterClauses();

   /**
    * Retrieve the schema information object for this session.
    */
   net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo getSchemaInfo();

   /**
    * Select a tab in the main tabbed pane.
    *
    * @param	tabIndex	The tab to select. @see #IMainTabIndexes
    *
    * @throws	IllegalArgumentException
    *			Thrown if an invalid <TT>tabIndex</TT> passed.
    */
   void selectMainTab(int tabIndex) throws IllegalArgumentException;

   public int getSelectedMainTabIndex();


   /**
    * Add a tab to the main tabbed panel.
    *
    * @param	tab	 The tab to be added.
    * 
    * @return the index of the new tab that was added.
    *
    * @throws	IllegalArgumentException
    *			Thrown if a <TT>null</TT> <TT>IMainPanelTab</TT> passed.
    */
   int addMainTab(IMainPanelTab tab);

   /**
    * Add component to the session sheets status bar.
    *
    * @param	comp	Component to add.
    */
   void addToStatusBar(JComponent comp);

   /**
    * Remove component to the session sheets status bar.
    *
    * @param	comp	Component to remove.
    */
   void removeFromStatusBar(JComponent comp);

   /**
    * Add a listener to this session
    *
    * @param	lis		The listener to add.
    *
    * @throws	IllegalArgumentException
    * 			Thrown if a <TT>null</TT> listener passed.
    */
// JASON: Removed as part of patch
//	void addSessionListener(ISessionListener lis);

   /**
    * Remove a listener from this session
    *
    * @param	lis		The listener to remove.
    *
    * @throws	IllegalArgumentException
    * 			Thrown if a <TT>null</TT> listener passed.
    */
//	 JASON: Removed as part of patch
//	void removeSessionListener(ISessionListener lis);

   /**
    * Retrieve the descriptive title of this session.
    *
    * @return		The descriptive title of this session.
    */
   String getTitle();


   /**
    * Changes the title of this session to the new one.
    * @param 	New title of this session.
    */
   public void setTitle(String newTitle);
   
   /**
    * Add the passed action to the toolbar of the sessions main window.
    *
    * @param	action	Action to be added.
    */
   void addToToolbar(Action action);

   public void addSeparatorToToolbar();

   /**
    * The code in any SQLEditor is parsed in the background. You may attach a listener to the ParserEventsProcessor
    * to get to know about the results of parsing. The events are passed synchron with the event queue
    * (via SwingUtils.invokeLater()). At the moment events are produced for errors in the SQLScript
    * which are highlighted in the syntax plugin and for aliases of table names which are used in the
    * code completion plugin.
    * <p>
    * If you want the ParserEventsProcessor to produce further events feel free to contact gerdwagner@users.sourceforge.net.
    */
   IParserEventsProcessor getParserEventsProcessor(IIdentifier sqlEntryPanelIdentifier);

   void setActiveSessionWindow(ISessionWidget activeActiveSessionWindow);

   /**
    * Hint for plugins:
    * When ISessionPlugin.sessionStarted is called the active session window is
    * always the SessionInternalFrame which provides an SQLPanelAPI. This might help to simplyfy
    * the code in the sessionStarted() method of a plugin.
    */
   ISessionWidget getActiveSessionWindow();

   /**
    * Hint for plugins:
    * When ISessionPlugin.sessionStarted is called the active session window is
    * always the SessionInternalFrame which provides an SQLPanelAPI. This might help to simplyfy
    * the code in the sessionStarted() method of a plugin.
    *
    * @throws IllegalStateException if ActiveSessionWindow doesn't provide an SQLPanelAPI
    * for example if it is an ObjectTreeInternalFrame
    */
   ISQLPanelAPI getSQLPanelAPIOfActiveSessionWindow();

   /**
    *
    * Hint for plugins:
    * When ISessionPlugin.sessionStarted is called the active session window is
    * always the SessionInternalFrame which provides an SQLPanelAPI. This might help to simplyfy
    * the code in the sessionStarted() method of a plugin.
    *
    * @throws IllegalStateException if ActiveSessionWindow doesn't provide an IObjectTreeAPI
    * for example if it is an SQLInternalFrame
    */
   IObjectTreeAPI getObjectTreeAPIOfActiveSessionWindow();

   /**
    * @return Returns the _finishedLoading.
    */
   public boolean isfinishedLoading();

   /**
    * @param _finishedLoading The _finishedLoading to set.
    */
   public void setPluginsfinishedLoading(boolean _finishedLoading);

   /**
    * Determine from the session whether or not it is ok to close it.  It might
    * be the case that the session's SQLPanel has unsaved edits that require
    * the user's approval to discard.
    * 
    * @return true if it's ok to close the session; false otherwise.
    */
   public boolean confirmClose();

   
   /**
    * Sets the IQueryTokenizer implementation to use for this session.
    * 
    * @param tokenizer
    */
   public void setQueryTokenizer(IQueryTokenizer tokenizer);
   
   /**
    * Returns the IQueryTokenizer implementation to use for tokenizing scripts
    * statements that should be sent to the server.
    * 
    * @return an implementation of IQueryTokenizer
    */
   IQueryTokenizer getQueryTokenizer();
       
   /**
    * Sets the exception formatter to use when handling messages.
    * 
    * @param formatter
    *            the ExceptionFormatter
    */
   void setExceptionFormatter(ExceptionFormatter formatter);
   
   /**
    * Returns the exception formatter to use when handling messages.
    * 
    * @return the ExceptionFormatter
    */
   ExceptionFormatter getExceptionFormatter();

   /**
    * Facade method for ExceptionFormatter to allow callers to safely get a 
    * formatted exception without forcing them to wrap it in a try/catch block
    *  
    * @param t the exception to format
    * 
    * @return the context of the exception, in default or custom format, 
    *         depending on whether or not a custom ExceptionFormatter was 
    *         installed.
    */
   String formatException(Throwable t);
   
   
   // Facade methods for IMessageHandler.  We don't want to allow code to 
   // directly access an internal IMessageHandler, now that the IMessageHandler
   // interface requires an ExceptionFormatter to be injected for methods that
   // accept Exceptions.
   
   /**
    * Show a message describing the passed exception. This will apply
    * any custom formatting if an ExceptionFormatter has been set.
    * 
    * @param th the exception to be shown
    */
   void showMessage(Throwable th);

   /**
    * Show a message.
    * 
    * @param msg
    *            The message.
    */
   void showMessage(String msg);

   /**
    * Show an error message describing the passed exception. This will apply
    * any custom formatting if an ExceptionFormatter has been set.
    * 
    * @param th the exception to be shown
    */
   void showErrorMessage(Throwable th);

   /**
    * Show an error message. The implementation of <TT>IMessageHandler</TT>
    * may or may not treat this differently to <TT>showMessage(String)</TT>.
    * @param session the session that generated the exception.
    * @param th
    *            Exception.
    */
   void showErrorMessage(String msg);

   void showWarningMessage(String msg);    

   /**
    * Create a new connection, which is not associated with the current session.
    * The new {@link SQLConnection} will is based on the same alias, user name and password of the current session. But it will be not associated with the session.
    * Please note, that a commit and roll back on the current session will not affect the new created connection.
    * The caller have to manage the connection by himself. Especially commit, roll back, close and so on. 
    * @return A new connection for the alias of the current session.
    */
   public SQLConnection createUnmanagedConnection();

   boolean isSessionWidgetActive();

   IMainPanelTab getSelectedMainTab();

   JdbcConnectionData getJdbcData();


   /**
    * Simple because it is directly attached to the Session itself
    * in contrast to SessionListener that is attached to SessionManager.
    */
   void addSimpleSessionListener(SimpleSessionListener simpleSessionListener);

   void removeSimpleSessionListener(SimpleSessionListener simpleSessionListener);


}
