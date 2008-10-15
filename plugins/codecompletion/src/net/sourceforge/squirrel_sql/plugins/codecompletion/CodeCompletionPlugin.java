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
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferences;
import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferencesController;

import javax.swing.*;
import java.io.File;
import java.util.Iterator;

/**
 * The plugin class.
 *
 * @author  Gerd Wagner
 */
public class CodeCompletionPlugin extends DefaultSessionPlugin
{
	/** Logger for this class. */
    @SuppressWarnings("unused")
	private final static ILogger
			s_log = LoggerController.createLogger(CodeCompletionPlugin.class);


	/** Resources for this plugin. */
	private Resources _resources;
	private static final String PREFS_FILE_NAME = "codecompletionprefs.xml";

	private CodeCompletionPreferences _newSessionPrefs;
	public static final String PLUGIN_OBJECT_PREFS_KEY = "codecompletionprefs";

   /**
	 * Return the internal name of this plugin.
	 *
	 * @return  the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "codecompletion";
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
		return "1.0";
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
		_resources = new Resources(this);
		loadPrefs();
	}

	public void unload()
	{
		savePrefs();
	}

	private void savePrefs()
	{
		try
		{
			File prefsFile = new File(getPluginUserSettingsFolder(), PREFS_FILE_NAME);
			final XMLBeanWriter wtr = new XMLBeanWriter(_newSessionPrefs);
			wtr.save(prefsFile);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}



	private void loadPrefs()
	{
		try
		{
			_newSessionPrefs = new CodeCompletionPreferences();
			File prefsFile = new File(getPluginUserSettingsFolder(), PREFS_FILE_NAME);
			if(prefsFile.exists())
			{
				XMLBeanReader reader = new XMLBeanReader();
				reader.load(prefsFile, getClass().getClassLoader());

				Iterator<?> it = reader.iterator();

				if (it.hasNext())
				{
					_newSessionPrefs = (CodeCompletionPreferences) it.next();
				}

			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
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
			new CodeCompletionPreferencesController(_newSessionPrefs)
		};
	}

	/**
	 * Create panels for the Session Properties dialog.
	 *
	 * @return		Array of panels for the properties dialog.
	 */
	public ISessionPropertiesPanel[] getSessionPropertiesPanels(ISession session)
	{
		CodeCompletionPreferences sessionPrefs = (CodeCompletionPreferences)session.getPluginObject(this, PLUGIN_OBJECT_PREFS_KEY);

		return new ISessionPropertiesPanel[]
		{
			new CodeCompletionPreferencesController(sessionPrefs)
		};
	}

	public void sessionCreated(ISession session)
	{
		CodeCompletionPreferences prefs = (CodeCompletionPreferences) Utilities.cloneObject(_newSessionPrefs, getClass().getClassLoader());
		session.putPluginObject(this, PLUGIN_OBJECT_PREFS_KEY, prefs);
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
	public PluginSessionCallback sessionStarted(final ISession session)
	{
		ISQLPanelAPI sqlPaneAPI = session.getSessionSheet().getSQLPaneAPI();
      initCodeCompletionSqlEditor(sqlPaneAPI, session);

      initCodeCompletionObjectTreeFind(session, session.getSessionSheet().getObjectTreePanel());

      PluginSessionCallback ret = new PluginSessionCallback()
		{
			public void sqlInternalFrameOpened(final SQLInternalFrame sqlInternalFrame, final ISession sess)
			{
            initCodeCompletionSqlEditor(sqlInternalFrame.getSQLPanelAPI(), sess);
			}

			public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
			{
            initCodeCompletionObjectTreeFind(sess, objectTreeInternalFrame.getObjectTreePanel());
			}
		};

		return ret;
	}

	private void initCodeCompletionSqlEditor(final ISQLPanelAPI sqlPaneAPI, final ISession session)
	{
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         public void run()
         {
            CodeCompletionInfoCollection c = new CodeCompletionInfoCollection(session, CodeCompletionPlugin.this,  true);

            CompleteCodeAction cca =
               new CompleteCodeAction(session.getApplication(),
                  CodeCompletionPlugin.this,
                  sqlPaneAPI.getSQLEntryPanel(),
                  session,
                  c,
                  null);

            JMenuItem item = sqlPaneAPI.addToSQLEntryAreaMenu(cca);

            _resources.configureMenuItem(cca, item);

            JComponent comp = sqlPaneAPI.getSQLEntryPanel().getTextComponent();
            comp.registerKeyboardAction(cca, _resources.getKeyStroke(cca), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

            sqlPaneAPI.addToToolsPopUp("completecode", cca);
         }

      });
   }

	private void initCodeCompletionObjectTreeFind(final ISession session, final ObjectTreePanel objectTreePanel)
	{
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         public void run()
         {
            ISQLEntryPanel findEntryPanel = objectTreePanel.getFindController().getFindEntryPanel();

            CodeCompletionInfoCollection c = new CodeCompletionInfoCollection(session, CodeCompletionPlugin.this, false);

            CompleteCodeAction cca =
               new CompleteCodeAction(session.getApplication(),
                  CodeCompletionPlugin.this,
                  findEntryPanel,
                  session,
                  c,
                  objectTreePanel);


            JComponent comp = findEntryPanel.getTextComponent();
            comp.registerKeyboardAction(cca, _resources.getKeyStroke(cca), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
         }

      });
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
