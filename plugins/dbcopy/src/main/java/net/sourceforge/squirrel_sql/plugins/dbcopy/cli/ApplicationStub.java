package net.sourceforge.squirrel_sql.plugins.dbcopy.cli;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.ApplicationListener;
import net.sourceforge.squirrel_sql.client.FontInfoStore;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.gui.db.DataCache;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DesktopStyle;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistory;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfigurationFactory;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;

public class ApplicationStub implements IApplication
{

	private SessionManager sessionManager = null;

	private SquirrelPreferences squirrelPreferences = new SquirrelPreferences();

	private TaskThreadPool threadPool = new TaskThreadPool();

	public ApplicationStub()
	{
		sessionManager = new SessionManager(this);
	}

	@Override
	public void addApplicationListener(ApplicationListener l)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void addToMenu(int menuId, JMenu menu)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void addToMenu(int menuId, Action action)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void addToStatusBar(JComponent comp)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public ActionCollection getActionCollection()
	{

		throw new UnsupportedOperationException();
	}

	@Override
	public DataCache getDataCache()
	{

		throw new UnsupportedOperationException();
	}

	@Override
	public DesktopStyle getDesktopStyle()
	{

		throw new UnsupportedOperationException();
	}

	@Override
	public IPlugin getDummyAppPlugin()
	{

		throw new UnsupportedOperationException();
	}

	@Override
	public FontInfoStore getFontInfoStore()
	{

		throw new UnsupportedOperationException();
	}

	@Override
	public MainFrame getMainFrame()
	{
		return null;
	}

	@Override
	public IMessageHandler getMessageHandler()
	{

		throw new UnsupportedOperationException();
	}

	@Override
	public IPluginManager getPluginManager()
	{

		throw new UnsupportedOperationException();
	}

	@Override
	public SquirrelResources getResources()
	{

		throw new UnsupportedOperationException();
	}

	@Override
	public SQLDriverManager getSQLDriverManager()
	{

		throw new UnsupportedOperationException();
	}

	@Override
	public ISQLEntryPanelFactory getSQLEntryPanelFactory()
	{

		throw new UnsupportedOperationException();
	}

	@Override
	public SQLHistory getSQLHistory()
	{

		throw new UnsupportedOperationException();
	}

	@Override
	public SessionManager getSessionManager()
	{
		return sessionManager;
	}

	@Override
	public SquirrelPreferences getSquirrelPreferences()
	{
		return squirrelPreferences;
	}

	@Override
	public TaskThreadPool getThreadPool()
	{
		return threadPool;
	}

	@Override
	public IWikiTableConfigurationFactory getWikiTableConfigFactory()
	{

		throw new UnsupportedOperationException();
	}

	@Override
	public WindowManager getWindowManager()
	{

		throw new UnsupportedOperationException();
	}

	@Override
	public void openURL(String url)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void removeApplicationListener(ApplicationListener l)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void removeFromStatusBar(JComponent comp)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void saveApplicationState()
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void savePreferences(PreferenceType preferenceType)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void setSQLEntryPanelFactory(ISQLEntryPanelFactory factory)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void showErrorDialog(String msg)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void showErrorDialog(Throwable th)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public void showErrorDialog(String msg, Throwable th)
	{
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean shutdown(boolean updateLaunchScript)
	{

		return false;
	}

	@Override
	public void startup()
	{
		throw new UnsupportedOperationException();

	}

}
