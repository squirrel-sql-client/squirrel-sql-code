package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.session.CurrentSchemaModel;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.JdbcConnectionData;
import net.sourceforge.squirrel_sql.client.session.ModificationAwareSessionTitle;
import net.sourceforge.squirrel_sql.client.session.SQLPanelApiInfo;
import net.sourceforge.squirrel_sql.client.session.action.reconnect.ReconnectInfo;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionJsonBean;
import net.sourceforge.squirrel_sql.client.session.action.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.client.session.connectionpool.SessionConnectionPool;
import net.sourceforge.squirrel_sql.client.session.event.SimpleSessionListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import javax.swing.Action;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class CliSessionAdapter implements ISession
{
   private HashMap<Object, Object> _sessionLocales = new HashMap<>();


   @Override
   public boolean isClosed()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public IApplication getApplication()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public ISQLConnection getSQLConnection()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public ISQLConnection checkOutUserQuerySQLConnection()
   {
      return getSQLConnection();
   }

   @Override
   public void returnUserQuerySQLConnection(ISQLConnection conn)
   {

   }

   @Override
   public ISQLDatabaseMetaData getMetaData()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public ISQLDriver getDriver()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public SQLAlias getAlias()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public SessionProperties getProperties()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public SyntaxPreferences getSyntaxPreferences()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void commit()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void rollback()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void close() throws SQLException
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void setSessionInternalFrame(SessionInternalFrame sif)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void reconnect(ReconnectInfo reconnectInfo)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public Object getPluginObject(IPlugin plugin, String key)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public Object putPluginObject(IPlugin plugin, String key, Object obj)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void setSqlEntryPanel(ISQLEntryPanel sqlEntryPanel)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public ISQLEntryPanel getSqlEntryPanel()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void removePluginObject(IPlugin plugin, String key)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void setMessageHandler(IMessageHandler handler)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public SessionPanel getSessionPanel()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public SessionInternalFrame getSessionInternalFrame()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public SchemaInfo getSchemaInfo()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void selectMainTab(int tabIndex) throws IllegalArgumentException
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public int getMainPanelTabIndex(IMainPanelTab mainPanelTab)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public int getSelectedMainTabIndex()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public int addMainTab(IMainPanelTab tab)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public String getTitle()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public ModificationAwareSessionTitle getTitleModificationAware()
   {
      return null;
   }

   @Override
   public void setUserChangedTitle(String newTitle)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void addToToolbar(Action action)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void addSeparatorToToolbar()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public IParserEventsProcessor getParserEventsProcessor(IIdentifier sqlEntryPanelIdentifier)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void setActiveSessionWindow(ISessionWidget activeActiveSessionWindow)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public ISessionWidget getActiveSessionWindow()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public ISQLPanelAPI getSQLPanelAPIOfActiveSessionWindow()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public IObjectTreeAPI getObjectTreeAPIOfActiveSessionWindow()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public boolean isfinishedLoading()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void setPluginsfinishedLoading(boolean _finishedLoading)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public boolean confirmClose()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void setQueryTokenizer(IQueryTokenizer tokenizer)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public IQueryTokenizer getQueryTokenizer()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public IQueryTokenizer getNewQueryTokenizer()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void setExceptionFormatter(ExceptionFormatter formatter)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public ExceptionFormatter getExceptionFormatter()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public String formatException(Throwable t)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void showMessage(Throwable th)
   {
      CliMessageUtil.showMessage(CliMessageType.MESSAGE, th);
   }

   @Override
   public void showMessage(String msg)
   {
      CliMessageUtil.showMessage(CliMessageType.MESSAGE, msg);
   }

   @Override
   public void showErrorMessage(Throwable th)
   {
      CliMessageUtil.showMessage(CliMessageType.ERROR, th);
   }

   @Override
   public void showErrorMessage(String msg)
   {
      CliMessageUtil.showMessage(CliMessageType.ERROR, msg);
   }

   @Override
   public void showWarningMessage(String msg)
   {
      CliMessageUtil.showMessage(CliMessageType.WARNING, msg);
   }

   @Override
   public void showWarningMessage(Throwable th)
   {
      CliMessageUtil.showMessage(CliMessageType.WARNING, th);
   }

   @Override
   public SQLConnection createUnmanagedConnection()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public boolean isSessionWidgetActive()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public IMainPanelTab getSelectedMainTab()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public JdbcConnectionData getJdbcData()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void addSimpleSessionListener(SimpleSessionListener simpleSessionListener)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void removeSimpleSessionListener(SimpleSessionListener simpleSessionListener)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public IIdentifier getIdentifier()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public Object getSessionLocal(Object key)
   {
      return _sessionLocales.get(key);
   }

   @Override
   public void putSessionLocal(Object key, Object value)
   {
      _sessionLocales.put(key, value);
   }

   @Override
   public SessionConnectionPool getConnectionPool()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public SavedSessionJsonBean getSavedSession()
   {
      return null;
   }

   @Override
   public void setSavedSession(SavedSessionJsonBean savedSession)
   {
   }

   @Override
   public CurrentSchemaModel getCurrentSchemaModel()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public ISQLPanelAPI getSQLPanelAPIOfActiveSessionWindow(boolean allowReturnNull)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public List<SQLPanelApiInfo> getAllSQLPanelApiInfos()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }
}
