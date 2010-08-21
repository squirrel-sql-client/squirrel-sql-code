package net.sourceforge.squirrel_sql.plugins.editextras;
/*
 * Copyright (C) 2003 Gerd Wagner
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
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelEvent;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
/**
 * The plugin class.
 *
 * @author  Gerd Wagner
 */
public class EditExtrasPlugin extends DefaultSessionPlugin
{
	/** Logger for this class. */
	private final static ILogger
			s_log = LoggerController.createLogger(EditExtrasPlugin.class);

	private interface IMenuResourceKeys 
	{
		String MENU = "editextras";
	}

	/** Name of file to store user prefs in. */
	static final String USER_PREFS_FILE_NAME = "prefs.xml";

	/** Resources for this plugin. */
	private Resources _resources;

	/** Listener to the SQL panel. */
	private ISQLPanelListener _lis = new SQLPanelListener();


	/**
	 * Return the internal name of this plugin.
	 *
	 * @return  the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "editextras";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return  the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "SQL Entry Area Enhancements";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return  the current version of this plugin.
	 */
	public String getVersion()
	{
		return "1.0.1";
	}

	/**
	 * Returns the authors name.
	 *
	 * @return  the authors name.
	 */
	public String getAuthor()
	{
		return "Gerd Wagner";
	}

	/**
	 * Returns the name of the change log for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the changelog file name or <TT>null</TT> if plugin doesn't have
	 * 			a change log.
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
	 * 			a help file.
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
	 * 			a licence file.
	 */
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	/**
	 * Called on application startup after application started.
	 */
	public void initialize() throws PluginException
	{
		super.initialize();

		final IApplication app = getApplication();

		// Load resources.
		_resources = new Resources(this);

		createMenu();
	}

   public boolean allowsSessionStartedInBackground()
   {
      return true;
   }

   /**
	 * Session has been started.
	 * 
	 * @param	session		Session that has started.
    */
	public PluginSessionCallback sessionStarted(ISession session)
	{
      ISQLPanelAPI sqlPanelAPI = FrameWorkAcessor.getSQLPanelAPI(session, this);
      initEditExtras(sqlPanelAPI);

      PluginSessionCallback ret = new PluginSessionCallback()
      {
         public void sqlInternalFrameOpened(final SQLInternalFrame sqlInternalFrame, 
                                            final ISession sess)
         {
             initEditExtras(sqlInternalFrame.getSQLPanelAPI());         
         }

         public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
         {
         }
      };

      return ret;
	}

   private void initEditExtras(final ISQLPanelAPI sqlPanelAPI)
   {
       GUIUtils.processOnSwingEventThread(new Runnable() {
           public void run() {
               sqlPanelAPI.addSQLPanelListener(_lis);
               createSQLEntryAreaPopMenuItems(sqlPanelAPI);

               ActionCollection actions = getApplication().getActionCollection();
               sqlPanelAPI.addToToolsPopUp("quote", actions.get(InQuotesAction.class));
               sqlPanelAPI.addToToolsPopUp("unquote", actions.get(RemoveQuotesAction.class));
               sqlPanelAPI.addToToolsPopUp("quotesb", actions.get(ConvertToStringBufferAction.class));
               sqlPanelAPI.addToToolsPopUp("format", actions.get(FormatSQLAction.class));
               sqlPanelAPI.addToToolsPopUp("date", actions.get(EscapeDateAction.class));
               sqlPanelAPI.addToToolsPopUp("sqlcut", actions.get(CutSqlAction.class));
               sqlPanelAPI.addToToolsPopUp("sqlcopy", actions.get(CopySqlAction.class));
           }
       });
   }

   /**
	 * Called when a session shutdown.
	 *
	 * @param	session	The session that is ending.
	 */
	public void sessionEnding(ISession session)
	{
      ISessionWidget[] frames =
         session.getApplication().getWindowManager().getAllFramesOfSession(session.getIdentifier());

      for (int i = 0; i < frames.length; i++)
      {
         if(frames[i] instanceof SQLInternalFrame)
         {
            ((SQLInternalFrame)frames[i]).getSQLPanelAPI().removeSQLPanelListener(_lis);
         }

         if(frames[i] instanceof SessionInternalFrame)
         {
            ((SessionInternalFrame)frames[i]).getSQLPanelAPI().removeSQLPanelListener(_lis);
         }
      }

		super.sessionEnding(session);
	}

	/**
	 * Retrieve plugins resources.
	 * 
	 * @return	Plugins resources.
	 */
	public PluginResources getResources()
	{
		return _resources;
	}

   private void createMenu()
	{
		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		JMenu menu = _resources.createMenu(IMenuResourceKeys.MENU);
		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);

		Action act = new InQuotesAction(app, this);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new RemoveQuotesAction(app, this);
		coll.add(act);
		_resources.addToMenu(act, menu);

        act = new RemoveNewLinesAction(app, this);
        coll.add(act);
        _resources.addToMenu(act, menu);
        
		act = new ConvertToStringBufferAction(app, this);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new FormatSQLAction(app, this);
		coll.add(act);
		_resources.addToMenu(act, menu);


      act = new EscapeDateAction(getApplication(), _resources);
      coll.add(act);
      _resources.addToMenu(act, menu);

		act = new CutSqlAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new CopySqlAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

	}

	private void createSQLEntryAreaPopMenuItems(ISQLPanelAPI api)
	{
		JMenuItem mnu;

		ActionCollection actions = getApplication().getActionCollection();
		api.addToSQLEntryAreaMenu(actions.get(InQuotesAction.class));
		api.addToSQLEntryAreaMenu(actions.get(RemoveQuotesAction.class));
		api.addToSQLEntryAreaMenu(actions.get(ConvertToStringBufferAction.class));

		// To make the shortcut visible in the popup
		mnu = api.addToSQLEntryAreaMenu(actions.get(FormatSQLAction.class));
		_resources.configureMenuItem(actions.get(FormatSQLAction.class), mnu);        
        
        mnu = api.addToSQLEntryAreaMenu(actions.get(RemoveNewLinesAction.class));
        _resources.configureMenuItem(actions.get(RemoveNewLinesAction.class), mnu);                
        
		api.addToSQLEntryAreaMenu(actions.get(EscapeDateAction.class));

		mnu = api.addToSQLEntryAreaMenu(actions.get(CutSqlAction.class));
		_resources.configureMenuItem(actions.get(CutSqlAction.class), mnu);

		mnu = api.addToSQLEntryAreaMenu(actions.get(CopySqlAction.class));
		_resources.configureMenuItem(actions.get(CopySqlAction.class), mnu);

	}

	private class SQLPanelListener extends SQLPanelAdapter
	{
		public void sqlEntryAreaReplaced(SQLPanelEvent evt)
		{
			createSQLEntryAreaPopMenuItems(evt.getSQLPanel());
		}
	}


   public Object getExternalService()
   {
      return new EditExtrasExternalServiceImpl();
   }
}
