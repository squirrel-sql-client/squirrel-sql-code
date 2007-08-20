package net.sourceforge.squirrel_sql.plugins.syntax;
/*
 * Copyright (C) 2003 Colin Bell
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.FindAction;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.NetbeansSQLEditorPane;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.NetbeansSQLEntryPanel;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.ReplaceAction;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.SQLKit;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.SQLSettingsInitializer;
import net.sourceforge.squirrel_sql.plugins.syntax.oster.OsterSQLEntryPanel;

import org.netbeans.editor.BaseKit;

/**
 * The Ostermiller plugin class. This plugin adds syntax highlighting to the
 * SQL entry area.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SyntaxPugin extends DefaultSessionPlugin
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SyntaxPugin.class);


    private static interface i18n {
        //i18n[SyntaxPlugin.touppercase=touppercase]
        String TO_UPPER_CASE = 
            s_stringMgr.getString("SyntaxPlugin.touppercase");    
        //i18n[SyntaxPlugin.tolowercase=tolowercase]
        String TO_LOWER_CASE = 
            s_stringMgr.getString("SyntaxPlugin.tolowercase");        
        //i18n[SyntaxPlugin.find=find]
        String FIND = s_stringMgr.getString("SyntaxPlugin.find");
        //i18n[SyntaxPlugin.replace=replace]
        String REPLACE = s_stringMgr.getString("SyntaxPlugin.replace");
        //i18n[SyntaxPlugin.autocorr=autocorr]
        String AUTO_CORR = s_stringMgr.getString("SyntaxPlugin.autocorr");
        //i18n[SyntaxPlugin.duplicateline=duplicateline]
        String DUP_LINE = s_stringMgr.getString("SyntaxPlugin.duplicateline");
        //i18n[SyntaxPlugin.comment=comment]
        String COMMENT = s_stringMgr.getString("SyntaxPlugin.comment");
        //i18n[SyntaxPlugin.uncomment=uncomment]
        String UNCOMMENT = s_stringMgr.getString("SyntaxPlugin.uncomment");
        
    }
    
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(SyntaxPugin.class);

	/** SyntaxPreferences for new sessions. */
	private SyntaxPreferences _newSessionPrefs;

	/** Folder to store user settings in. */
	private File _userSettingsFolder;

	/** Factory that creates text controls. */
	private SQLEntryPanelFactoryProxy _sqlEntryFactoryProxy;

	/** Listeners to the preferences object in each open session. */
	private Map<IIdentifier, SessionPreferencesListener> _prefListeners = 
	    new HashMap<IIdentifier, SessionPreferencesListener>();

	/** Resources for this plugin. */
	private SyntaxPluginResources _resources;
	private AutoCorrectProviderImpl _autoCorrectProvider;

	private interface IMenuResourceKeys
	{
		String MENU = "syntax";
	}


	/**
	 * Return the internal name of this plugin.
	 *
	 * @return	the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "syntax";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return	the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "Syntax Highlighting Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return	the current version of this plugin.
	 */
	public String getVersion()
	{
		return "1.0";
	}

	/**
	 * Returns the authors name.
	 *
	 * @return	the authors name.
	 */
	public String getAuthor()
	{
		return "Gerd Wagner, Colin Bell";
	}


	/**
	 * Returns the name of the change log for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the changelog file name or <TT>null</TT> if plugin doesn't have
	 *			a change log.
	 */
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * Returns the name of the Help file for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the Help file name or <TT>null</TT> if plugin doesn't have
	 *			a help file.
	 */
	public String getHelpFileName()
	{
		return "readme.html";
	}

	/**
	 * Returns the name of the Licence file for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the Licence file name or <TT>null</TT> if plugin doesn't have
	 *			a licence file.
	 */
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException
	{
		super.initialize();

		_resources = new SyntaxPluginResources(this);

		// Folder to store user settings.
		try
		{
			_userSettingsFolder = getPluginUserSettingsFolder();
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}

		// Load plugin preferences.
		loadPrefs();

		// Install the factory for creating SQL entry text controls.
		final IApplication app = getApplication();
		final ISQLEntryPanelFactory originalFactory = app.getSQLEntryPanelFactory();
		//_sqlEntryFactoryProxy = new OsterSQLEntryAreaFactory(this, originalFactory);

		_sqlEntryFactoryProxy = new SQLEntryPanelFactoryProxy(this, originalFactory);

		app.setSQLEntryPanelFactory(_sqlEntryFactoryProxy);

		_autoCorrectProvider = new AutoCorrectProviderImpl(_userSettingsFolder);

		createMenu();
	}

	private void createMenu()
	{
		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		JMenu menu = _resources.createMenu(IMenuResourceKeys.MENU);
		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);

		Action act = new ConfigureAutoCorrectAction(app, _resources, this);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new FindAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new ReplaceAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new DuplicateLineAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new CommentAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new UncommentAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

	}


	/**
	 * Application is shutting down so save preferences.
	 */
	public void unload()
	{
		savePrefs();
		super.unload();
	}

	/**
	 * Called when a session created but the UI hasn't been built for the
	 * session.
	 *
	 * @param	session	The session that is starting.
	 */
	public void sessionCreated(ISession session)
	{
		SyntaxPreferences prefs = null;

		try
		{
			prefs = (SyntaxPreferences)_newSessionPrefs.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError("CloneNotSupportedException for SyntaxPreferences");
		}

		session.putPluginObject(this, IConstants.ISessionKeys.PREFS, prefs);

		SessionPreferencesListener lis = 
		    new SessionPreferencesListener(this, session);
		prefs.addPropertyChangeListener(lis);
		_prefListeners.put(session.getIdentifier(), lis);
	}


	public PluginSessionCallback sessionStarted(final ISession session)
	{
		PluginSessionCallback ret = new PluginSessionCallback()
		{
			public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
			{
				initSqlInternalFrame(sqlInternalFrame);
			}

			public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
			{
			}
		};

      initSessionSheet(session);

		return ret;
	}

   private void initSessionSheet(ISession session)
   {
      ActionCollection coll = getApplication().getActionCollection();
      session.addSeparatorToToolbar();
      session.addToToolbar(coll.get(FindAction.class));
      session.addToToolbar(coll.get(ReplaceAction.class));
      session.addToToolbar(coll.get(ConfigureAutoCorrectAction.class));

      SessionInternalFrame sif = session.getSessionInternalFrame();
      sif.addToToolsPopUp(i18n.FIND, coll.get(FindAction.class));
      sif.addToToolsPopUp(i18n.REPLACE, coll.get(ReplaceAction.class));
      sif.addToToolsPopUp(i18n.AUTO_CORR, coll.get(ConfigureAutoCorrectAction.class));
      sif.addToToolsPopUp(i18n.DUP_LINE, coll.get(DuplicateLineAction.class));
      sif.addToToolsPopUp(i18n.COMMENT, coll.get(CommentAction.class));
      sif.addToToolsPopUp(i18n.UNCOMMENT, coll.get(UncommentAction.class));

      ISQLPanelAPI sqlPanelAPI = sif.getSQLPanelAPI();
      ISQLEntryPanel sep = sqlPanelAPI.getSQLEntryPanel();
      JComponent septc = sep.getTextComponent();

      new AutoCorrector((JTextComponent) septc, this);


      if (sep.getTextComponent() instanceof NetbeansSQLEditorPane)
      {
         NetbeansSQLEditorPane nbEdit = (NetbeansSQLEditorPane) septc;
         SQLKit kit = (SQLKit) nbEdit.getEditorKit();

         Action toUpperAction = kit.getActionByName(BaseKit.toUpperCaseAction);
         toUpperAction.putValue(Resources.ACCELERATOR_STRING,
            SQLSettingsInitializer.ACCELERATOR_STRING_TO_UPPER_CASE);
         sif.addToToolsPopUp(i18n.TO_UPPER_CASE, toUpperAction);

         Action toLowerAction = kit.getActionByName(BaseKit.toLowerCaseAction);
         toLowerAction.putValue(Resources.ACCELERATOR_STRING,
            SQLSettingsInitializer.ACCELERATOR_STRING_TO_LOWER_CASE);
         sif.addToToolsPopUp(i18n.TO_LOWER_CASE, toLowerAction);
      }

      JMenuItem mnuComment = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(CommentAction.class));
      JMenuItem mnuUncomment = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(UncommentAction.class));
      _resources.configureMenuItem(coll.get(CommentAction.class), mnuComment);
      _resources.configureMenuItem(coll.get(UncommentAction.class), mnuUncomment);
   }

   private void initSqlInternalFrame(SQLInternalFrame sqlInternalFrame)
	{
		ActionCollection coll = getApplication().getActionCollection();
		FindAction findAction = ((FindAction) coll.get(FindAction.class));
		ReplaceAction replaceAction = (ReplaceAction) coll.get(ReplaceAction.class);

		sqlInternalFrame.addSeparatorToToolbar();
		sqlInternalFrame.addToToolbar(findAction);
		sqlInternalFrame.addToToolbar(replaceAction);
		sqlInternalFrame.addToToolbar(coll.get(ConfigureAutoCorrectAction.class));

		sqlInternalFrame.addToToolsPopUp(i18n.FIND , coll.get(FindAction.class));
		sqlInternalFrame.addToToolsPopUp(i18n.REPLACE , coll.get(ReplaceAction.class));
		sqlInternalFrame.addToToolsPopUp(i18n.AUTO_CORR , coll.get(ConfigureAutoCorrectAction.class));
		sqlInternalFrame.addToToolsPopUp(i18n.DUP_LINE , coll.get(DuplicateLineAction.class));
		sqlInternalFrame.addToToolsPopUp(i18n.COMMENT , coll.get(CommentAction.class));
		sqlInternalFrame.addToToolsPopUp(i18n.UNCOMMENT , coll.get(UncommentAction.class));

		ISQLPanelAPI sqlPanelAPI = sqlInternalFrame.getSQLPanelAPI();

		new AutoCorrector(sqlPanelAPI.getSQLEntryPanel().getTextComponent(), this);

		if (sqlPanelAPI.getSQLEntryPanel().getTextComponent() instanceof NetbeansSQLEditorPane)
		{
			NetbeansSQLEditorPane nbEdit = (NetbeansSQLEditorPane) sqlPanelAPI.getSQLEntryPanel().getTextComponent();
			Action toUpperAction = ((SQLKit) nbEdit.getEditorKit()).getActionByName(BaseKit.toUpperCaseAction);
			toUpperAction.putValue(Resources.ACCELERATOR_STRING, SQLSettingsInitializer.ACCELERATOR_STRING_TO_UPPER_CASE);
			sqlInternalFrame.addToToolsPopUp(i18n.TO_UPPER_CASE, toUpperAction);

			Action toLowerAction = ((SQLKit) nbEdit.getEditorKit()).getActionByName(BaseKit.toLowerCaseAction);
			toLowerAction.putValue(Resources.ACCELERATOR_STRING, SQLSettingsInitializer.ACCELERATOR_STRING_TO_LOWER_CASE);
			sqlInternalFrame.addToToolsPopUp(i18n.TO_LOWER_CASE, toLowerAction);
		}


		JMenuItem mnuComment = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(CommentAction.class));
		JMenuItem mnuUncomment = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(UncommentAction.class));
		_resources.configureMenuItem(coll.get(CommentAction.class), mnuComment);
		_resources.configureMenuItem(coll.get(UncommentAction.class), mnuUncomment);

	}




	/**
	 * Called when a session shutdown.
	 *
	 * @param	session	The session that is ending.
	 */
	public void sessionEnding(ISession session)
	{
		super.sessionEnding(session);

		session.removePluginObject(this, IConstants.ISessionKeys.PREFS);
		_prefListeners.remove(session.getIdentifier());
		_sqlEntryFactoryProxy.sessionEnding(session);
	}

	/**
	 * Create preferences panel for the New Session Properties dialog.
	 *
	 * @return	preferences panel.
	 */
	public INewSessionPropertiesPanel[] getNewSessionPropertiesPanels()
	{
		return new INewSessionPropertiesPanel[]
		{
			new SyntaxPreferencesPanel(_newSessionPrefs, _resources)
		};
	}

	/**
	 * Create panels for the Session Properties dialog.
	 *
	 * @return		Array of panels for the properties dialog.
	 */
	public ISessionPropertiesPanel[] getSessionPropertiesPanels(ISession session)
	{
		SyntaxPreferences sessionPrefs = (SyntaxPreferences)session.getPluginObject(this,
											IConstants.ISessionKeys.PREFS);

		return new ISessionPropertiesPanel[]
		{
			new SyntaxPreferencesPanel(sessionPrefs, _resources)
		};
	}

	PluginResources getResources()
	{
		return _resources;
	}

	ISQLEntryPanelFactory getSQLEntryAreaFactory()
	{
		return _sqlEntryFactoryProxy;
	}

	/**
	 * Load from preferences file.
	 */
	private void loadPrefs()
	{
		try
		{
			final XMLBeanReader doc = new XMLBeanReader();
			final File file = new File(_userSettingsFolder,
					IConstants.USER_PREFS_FILE_NAME);
			doc.load(file, getClass().getClassLoader());

			Iterator<?> it = doc.iterator();

			if (it.hasNext())
			{
				_newSessionPrefs = (SyntaxPreferences)it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			// property file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			final String msg = "Error occured reading from preferences file: " +
				IConstants.USER_PREFS_FILE_NAME;
			s_log.error(msg, ex);
		}

		if (_newSessionPrefs == null)
		{
			_newSessionPrefs = new SyntaxPreferences();
		}
	}

	/**
	 * Save preferences to disk.
	 */
	private void savePrefs()
	{
		try
		{
			final XMLBeanWriter wtr = new XMLBeanWriter(_newSessionPrefs);
			wtr.save(new File(_userSettingsFolder, IConstants.USER_PREFS_FILE_NAME));
		}
		catch (Exception ex)
		{
			final String msg = "Error occured writing to preferences file: " +
								IConstants.USER_PREFS_FILE_NAME;
			s_log.error(msg, ex);
		}
	}


	public Object getExternalService()
	{
		return getAutoCorrectProviderImpl();
	}


	public AutoCorrectProviderImpl getAutoCorrectProviderImpl()
	{
		return _autoCorrectProvider;
	}

	private static final class SessionPreferencesListener
		implements PropertyChangeListener
	{
		private SyntaxPugin _plugin;
		private ISession _session;

		SessionPreferencesListener(SyntaxPugin plugin, ISession session)
		{
			super();
			_plugin = plugin;
			_session = session;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			String propName = evt.getPropertyName();

			if(   false == SyntaxPreferences.IPropertyNames.USE_NETBEANS_CONTROL.equals(propName)
				&& false == SyntaxPreferences.IPropertyNames.USE_OSTER_CONTROL.equals(propName) )
			{

				// Not the Textcontrol itself changed but some other of the Syntax Preferences, for example a color.
				// So we tell the current control to update the preferences.
				Object pluginObject = _session.getPluginObject(_plugin, IConstants.ISessionKeys.SQL_ENTRY_CONTROL);

				if(pluginObject instanceof NetbeansSQLEntryPanel)
				{
					((NetbeansSQLEntryPanel)pluginObject).updateFromPreferences();
				}

				if(pluginObject instanceof OsterSQLEntryPanel)
				{
					((OsterSQLEntryPanel)pluginObject).updateFromPreferences();
				}
			}
			else
			{
				/*
								We don't support switching the entry control during a session
								because serveral things, that are attached to the entry control
								from outside this plugin would need to reinitialze too.
								For example code completion and edit extras.

								synchronized (_session)
								{
									ISQLEntryPanelFactory factory = _plugin.getSQLEntryAreaFactory();
									ISQLEntryPanel pnl = factory.createSQLEntryPanel(_session);
									_session.getSQLPanelAPI(_plugin).installSQLEntryPanel(pnl);
								}
								*/

				String msg =
					// i18n[syntax.switchingNotSupported=Switching the editor of a runninig session is not supported.\nYou may switch the entry area in the New Session Properties dialog]
					s_stringMgr.getString("syntax.switchingNotSupported");

				JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);

				throw new SyntaxPrefChangeNotSupportedException();

			}

		}
	}
}
