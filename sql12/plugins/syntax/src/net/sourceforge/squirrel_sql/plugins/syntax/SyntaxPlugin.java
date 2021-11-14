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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.shortcut.ShortcutUtil;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.syntax.externalservice.SyntaxExternalService;
import net.sourceforge.squirrel_sql.plugins.syntax.externalservice.SyntaxExternalServiceImpl;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirreLRSyntaxTextAreaUI;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.SquirrelRSyntaxTextArea;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.action.SquirrelCopyAsRtfAction;
import org.fife.ui.rtextarea.RTextAreaEditorKit;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This plugin adds syntax highlighting to the SQL entry area.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SyntaxPlugin extends DefaultSessionPlugin
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SyntaxPlugin.class);

	private static final ILogger s_log = LoggerController.createLogger(SyntaxPlugin.class);

	/** SyntaxPreferences for new sessions. */
	private SyntaxPreferences _syntaxPreferences;

	/** Folder to store user settings in. */
	private FileWrapper _userSettingsFolder;

	/** Factory that creates text controls. */
	private SQLEntryPanelFactoryProxy _sqlEntryFactoryProxy;

	/** Listeners to the preferences object in each open session. */
	private Map<IIdentifier, SessionPreferencesListener> _prefListeners = new HashMap<>();

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
	 * @return the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "syntax";
	}

	/**
	 * Return the descriptive name of this plugin.
	 * 
	 * @return the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "Syntax Highlighting Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 * 
	 * @return the current version of this plugin.
	 */
	public String getVersion()
	{
		return "1.0";
	}

	/**
	 * Returns the authors name.
	 * 
	 * @return the authors name.
	 */
	public String getAuthor()
	{
		return "Gerd Wagner, Colin Bell";
	}

	/**
	 * Returns the name of the change log for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the changelog file name or <TT>null</TT> if plugin doesn't have a change log.
	 */
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * Returns the name of the Help file for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the Help file name or <TT>null</TT> if plugin doesn't have a help file.
	 */
	public String getHelpFileName()
	{
		return "doc/readme.html";
	}

	/**
	 * Returns the name of the Licence file for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the Licence file name or <TT>null</TT> if plugin doesn't have a licence file.
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
		// _sqlEntryFactoryProxy = new OsterSQLEntryAreaFactory(this, originalFactory);

		_sqlEntryFactoryProxy = new SQLEntryPanelFactoryProxy(this, originalFactory);

		app.setSQLEntryPanelFactory(_sqlEntryFactoryProxy);

		_autoCorrectProvider = new AutoCorrectProviderImpl(_userSettingsFolder);

		createMenu();
		
		createAdditionalActions();
		preRegisterShortcuts();

	}

	private void preRegisterShortcuts()
	{
		SquirreLRSyntaxTextAreaUI.getToUpperCaseKeyStroke();
		SquirreLRSyntaxTextAreaUI.getToLowerCaseKeyStroke();

		SquirreLRSyntaxTextAreaUI.getLineUpKeyStroke();
		SquirreLRSyntaxTextAreaUI.getLineDownKeyStroke();
	}

	/**
	 * Create some additional actions and add them to the application.
	 * These actions are not part of the menu, but needs to be initialized with the resources of the syntax plugin.
	 * Some of these actions may be depend on a concrete editor. 
	 */
	private void createAdditionalActions()
	{
		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		coll.add(new SquirrelCopyAsRtfAction(_resources));

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

		act = new FindSelectedAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new RepeatLastFindAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new MarkSelectedAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new ReplaceAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new UnmarkAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new GoToLineAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new DuplicateLineAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);


		///////////////////////////////////////////////////////////////////
		//
		act = new CommentAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new CommentActionAltAccelerator(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);
		//
		////////////////////////////////////////////////////////////////////

		act = new UncommentAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new UncommentActionAltAccelerator(getApplication(), _resources);
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
	 * Called when a session created but the UI hasn't been built for the session.
	 * 
	 * @param session The session that is starting.
	 */
	public void sessionCreated(ISession session)
	{
		SyntaxPreferences prefs = Utilities.cloneObject(_syntaxPreferences, SyntaxPreferences.class.getClassLoader());

		session.putPluginObject(this, IConstants.ISessionKeys.PREFS, prefs);

		SessionPreferencesListener lis = new SessionPreferencesListener(this, session);
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

			@Override
			public void objectTreeInSQLTabOpened(ObjectTreePanel objectTreePanel)
			{
			}

			@Override
			public void additionalSQLTabOpened(AdditionalSQLTab additionalSQLTab)
			{
				initSqlPanel(additionalSQLTab.getSQLPanelAPI());
			}
		};

		initSessionSheet(session);

		return ret;
	}

	public SyntaxPreferences getSyntaxPreferences()
	{
		return _syntaxPreferences;
	}

	private void initSessionSheet(ISession session)
	{
		ActionCollection coll = getApplication().getActionCollection();
		session.addSeparatorToToolbar();
		session.addToToolbar(coll.get(FindAction.class));
		session.addToToolbar(coll.get(ReplaceAction.class));
		session.addToToolbar(coll.get(ConfigureAutoCorrectAction.class));

		initSqlPanel(session.getSessionInternalFrame().getMainSQLPanelAPI());
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

		initSqlPanel(sqlInternalFrame.getMainSQLPanelAPI());
	}

	private void initSqlPanel(ISQLPanelAPI sqlPanelAPI)
	{
		ActionCollection coll = Main.getApplication().getActionCollection();
		new ToolsPopupHandler(this).initToolsPopup(coll, sqlPanelAPI);
		completeSqlPanelEntryAreaMenu(coll, sqlPanelAPI);
	}

	private void completeSqlPanelEntryAreaMenu(ActionCollection coll, ISQLPanelAPI sqlPanelAPI)
   {
      JMenuItem mnuUnmark = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(UnmarkAction.class));
      _resources.configureMenuItem(coll.get(UnmarkAction.class), mnuUnmark);


      //////////////////////////////////////////////////////////////////////
		//
      JMenuItem mnuComment = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(CommentAction.class));
      _resources.configureMenuItem(coll.get(CommentAction.class), mnuComment);

      JMenuItem mnuCommentAltAccelerator = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(CommentActionAltAccelerator.class));
      _resources.configureMenuItem(coll.get(CommentActionAltAccelerator.class), mnuCommentAltAccelerator);
		mnuCommentAltAccelerator.setVisible(false);
		//
		//////////////////////////////////////////////////////////////////////


		/////////////////////////////////////////////////////////////////////
		//
      JMenuItem mnuUncomment = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(UncommentAction.class));
      _resources.configureMenuItem(coll.get(UncommentAction.class), mnuUncomment);

      JMenuItem mnuUncommentAltAccelerator = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(UncommentActionAltAccelerator.class));
      _resources.configureMenuItem(coll.get(UncommentActionAltAccelerator.class), mnuUncommentAltAccelerator);
		mnuUncommentAltAccelerator.setVisible(false);
		//
		/////////////////////////////////////////////////////////////////////


      if (sqlPanelAPI.getSQLEntryPanel().getTextComponent() instanceof SquirrelRSyntaxTextArea)
      {

         JMenuItem mnuCopyToRtf = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(SquirrelCopyAsRtfAction.class));
         _resources.configureMenuItem(coll.get(SquirrelCopyAsRtfAction.class), mnuCopyToRtf);

         SquirrelRSyntaxTextArea rsEdit = (SquirrelRSyntaxTextArea) sqlPanelAPI.getSQLEntryPanel().getTextComponent();

         configureRichTextAction(sqlPanelAPI, rsEdit, RTextAreaEditorKit.rtaUpperSelectionCaseAction, SquirreLRSyntaxTextAreaUI.getToUpperCaseKeyStroke(), s_stringMgr.getString("SyntaxPlugin.ToUpperShortDescription"));
         configureRichTextAction(sqlPanelAPI, rsEdit, RTextAreaEditorKit.rtaLowerSelectionCaseAction, SquirreLRSyntaxTextAreaUI.getToLowerCaseKeyStroke(), s_stringMgr.getString("SyntaxPlugin.ToLowerShortDescription"));

         configureRichTextAction(sqlPanelAPI, rsEdit, RTextAreaEditorKit.rtaLineUpAction, SquirreLRSyntaxTextAreaUI.getLineUpKeyStroke(), s_stringMgr.getString("SyntaxPlugin.LineUpShortDescription"));
         configureRichTextAction(sqlPanelAPI, rsEdit, RTextAreaEditorKit.rtaLineDownAction, SquirreLRSyntaxTextAreaUI.getLineDownKeyStroke(), s_stringMgr.getString("SyntaxPlugin.LineDownShortDescription"));

      }
   }

   private void configureRichTextAction(ISQLPanelAPI sqlPanelAPI, SquirrelRSyntaxTextArea rsEdit, String rtaKey, KeyStroke acceleratorKeyStroke, String shortDescription)
   {
      Action action = SquirreLRSyntaxTextAreaUI.getActionForName(rsEdit, rtaKey);
      action.putValue(Resources.ACCELERATOR_STRING, ShortcutUtil.getKeystrokeString(acceleratorKeyStroke));

      action.putValue(Action.SHORT_DESCRIPTION, shortDescription);
      action.putValue(Action.MNEMONIC_KEY, 0);
      action.putValue(Action.ACCELERATOR_KEY, acceleratorKeyStroke);

      JMenuItem mnu = sqlPanelAPI.addToSQLEntryAreaMenu(action);
      mnu.setText((String) action.getValue(Action.SHORT_DESCRIPTION));
      _resources.configureMenuItem(action, mnu, true);
   }


   /**
	 * Called when a session shutdown.
	 * 
	 * @param session
	 *           The session that is ending.
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
	 * @return preferences panel.
	 */
	public INewSessionPropertiesPanel[] getNewSessionPropertiesPanels()
	{
		return new INewSessionPropertiesPanel[] { new SyntaxPreferencesPanel(_syntaxPreferences, _resources) };
	}

	/**
	 * Create panels for the Session Properties dialog.
	 * 
	 * @return Array of panels for the properties dialog.
	 */
	public ISessionPropertiesPanel[] getSessionPropertiesPanels(ISession session)
	{
		SyntaxPreferences sessionPrefs = (SyntaxPreferences) session.getPluginObject(this, IConstants.ISessionKeys.PREFS);
		return new ISessionPropertiesPanel[] { new SyntaxPreferencesPanel(sessionPrefs, _resources) };
	}

	public SyntaxPluginResources getResources()
	{
		return _resources;
	}

	/**
	 * Load from preferences file.
	 */
	private void loadPrefs()
	{
		try
		{
			final XMLBeanReader doc = new XMLBeanReader();
			final FileWrapper file = fileWrapperFactory.create(_userSettingsFolder, IConstants.USER_PREFS_FILE_NAME);
			doc.load(file, getClass().getClassLoader());

			Iterator<?> it = doc.iterator();

			if (it.hasNext())
			{
				_syntaxPreferences = (SyntaxPreferences) it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			// property file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			final String msg = "Error occurred reading from preferences file: " + IConstants.USER_PREFS_FILE_NAME;
			s_log.error(msg, ex);
		}

		if (_syntaxPreferences == null)
		{
			_syntaxPreferences = new SyntaxPreferences();
		}
	}

	/**
	 * Save preferences to disk.
	 */
	private void savePrefs()
	{
		try
		{
			final XMLBeanWriter wtr = new XMLBeanWriter(_syntaxPreferences);
			wtr.save(fileWrapperFactory.create(_userSettingsFolder, IConstants.USER_PREFS_FILE_NAME));
		}
		catch (Exception ex)
		{
			final String msg = "Error occurred writing to preferences file: " + IConstants.USER_PREFS_FILE_NAME;
			s_log.error(msg, ex);
		}
	}

	public AutoCorrectProviderImpl getAutoCorrectProviderImpl()
	{
		return _autoCorrectProvider;
	}

	public SyntaxExternalService getExternalService()
	{
		return new SyntaxExternalServiceImpl(this);
	}

}
