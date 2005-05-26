package net.sourceforge.squirrel_sql.plugins.codecompletion;
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
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.util.List;

/**
 * The plugin class.
 *
 * @author  Gerd Wagner
 */
public class CodeCompletionPlugin extends DefaultSessionPlugin
{
	/** Logger for this class. */
	private final static ILogger
			s_log = LoggerController.createLogger(CodeCompletionPlugin.class);



	/** Name of file to store user prefs in. */
	static final String USER_PREFS_FILE_NAME = "prefs.xml";

	/** Resources for this plugin. */
	private Resources _resources;

	/**
	 * Return the internal name of this plugin.
	 *
	 * @return  the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "completion";
	}

	/**
	 * Return the descriptive name of this plugin.
	 *
	 * @return  the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "SQL Entry Code Completion";
	}

	/**
	 * Returns the current version of this plugin.
	 *
	 * @return  the current version of this plugin.
	 */
	public String getVersion()
	{
		return "0.20";
	}


   /**
    * Returns a comma separated list of other contributors.
    *
    * @return      Contributors names.
    */
   public String getContributors()
   {
      return "Christian Sell";
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
		return "readme.txt";
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

	}

	/**
	 * Session has been started.
	 * 
	 * @param	session		Session that has started.
	 */
	public PluginSessionCallback sessionStarted(final ISession session)
	{
      ISQLPanelAPI sqlPaneAPI = session.getSessionSheet().getSQLPaneAPI();

      initCodeCompletion(sqlPaneAPI, session);

      PluginSessionCallback ret = new PluginSessionCallback()
      {
         public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
         {
            initCodeCompletion(sqlInternalFrame.getSQLPanelAPI(), sess);
         }

         public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
         {
         }
      };

		return ret;
	}

   private void initCodeCompletion(ISQLPanelAPI sqlPaneAPI, ISession session)
   {
      CompleteCodeAction cca = new CompleteCodeAction(session.getApplication(), _resources, sqlPaneAPI.getSQLEntryPanel(), session, new CodeCompletionInfoCollection(session));

      JMenuItem item = sqlPaneAPI.addToSQLEntryAreaMenu(cca);

      _resources.configureMenuItem(cca, item);

      JComponent comp = sqlPaneAPI.getSQLEntryPanel().getTextComponent();
      comp.registerKeyboardAction(cca, _resources.getKeyStroke(cca), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
   }

   /**
	 * Called when a session shutdown.
	 *
	 * @param	session	The session that is ending.
	 */
	public void sessionEnding(ISession session)
	{
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

}
