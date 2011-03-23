package net.sourceforge.squirrel_sql.client.plugin;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
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

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers;
import net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappersImpl;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public abstract class DefaultPlugin implements IPlugin
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DefaultPlugin.class);

	/** Current application API. */
	protected IApplication _app;

	/** A FileWrapper-enhanced version of ApplicationFiles that removes direct references to File */
	protected ApplicationFileWrappers applicationFiles = new ApplicationFileWrappersImpl();
	
	/** factory for creating FileWrappers which insulate the application from direct reference to File */
	protected FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl();
	
	/**
	 * Called on application startup before application started up.
	 *
	 * @param	app	 Application API.
	 *
	 * @throws	PluginException
	 *			Thrown if an error occurs.
	 */
	public void load(IApplication app) throws PluginException
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
	}

	/**
	 * Called on application startup after application started.
	 *
	 * @throws	PluginException
	 *			Thrown if an error occurs.
	 */
	public void initialize() throws PluginException
	{
		// Empty method.
	}

	/**
	 * Called when app shutdown.
	 *
	 * @throws	PluginException
	 *			Thrown if an error occurs.
	 */
	public void unload() // throws PluginException
	{
		// TODO: Put throws clause back in
		// Empty method.
	}

	/**
	 * Returns a comma separated list of other contributors.
	 *
	 * @return	Contributors names.
	 */
	public String getContributors()
	{
		return "";
	}

	/**
	 * Returns the home page for this plugin.
	 *
	 * @return	the home page for this plugin.
	 */
	public String getWebSite()
	{
		return Version.getWebSite();
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
		return null;
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
		return null;
	}

	/**
	 * Returns the name of the licence file for the plugin. This should
	 * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
	 * directory.
	 *
	 * @return	the licence file name or <TT>null</TT> if plugin doesn't have
	 * 			a change log.
	 */
	public String getLicenceFileName()
	{
		return null;
	}

	/**
	 * Return the current application API.
	 *
	 * @return	The current application API.
	 */
	public IApplication getApplication()
	{
		return _app;
	}

	/**
	 * Return the folder with the Squirrel application folder
	 * that belongs to this plugin. If it doesn't exist then
	 * create it. This would normally be
	 * <PRE>
	 * &lt;squirrel_app&gt;/plugins/&lt;plugin_internal_name&gt;
	 * </PRE>
	 *
	 * @return	Plugins application folder.
	 *
	 * @throws	IllegalStateException
	 *			if plugin doesn't have an internal name.
	 *
	 * @throws	IOException
	 * 			An error occured retrieving/creating the folder.
	 */
	public synchronized FileWrapper getPluginAppSettingsFolder()
		throws IllegalStateException, IOException
	{
		String pluginAppFolderProp = System.getProperty("pluginAppFolder");
		if (pluginAppFolderProp != null) {
			return fileWrapperFactory.create(pluginAppFolderProp);
		}
		
		final String internalName = getInternalName();
		if (internalName == null || internalName.trim().length() == 0)
		{
			throw new IllegalStateException("IPlugin doesn't have a valid internal name");
		}
		final FileWrapper pluginDir = applicationFiles.getPluginsDirectory();
		final FileWrapper file = fileWrapperFactory.create(pluginDir, internalName);
		if (!file.exists())
		{
			file.mkdirs();
		}

		if (!file.isDirectory())
		{
			throw new IOException(s_stringMgr.getString("DefaultPlugin.error.cannotcreate", file.getAbsolutePath()));
		}

		return file;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getPluginJarFilePath()
	 */
	public synchronized String getPluginJarFilePath() throws IllegalStateException {
		final String internalName = getInternalName();
		final FileWrapper pluginDir = applicationFiles.getPluginsDirectory();
		if (internalName == null || internalName.trim().length() == 0)
		{
			throw new IllegalStateException("IPlugin doesn't have a valid internal name");
		}
		final FileWrapper resultFile = fileWrapperFactory.create(pluginDir, internalName + ".jar");
		return resultFile.getAbsolutePath();
	}
	
	/**
	 * Return the folder with the users home directory
	 * that belongs to this plugin. If it doesn't exist then
	 * create it. This would normally be
	 * <PRE>
	 * &lt;user_home&gt;/.squirrel-sql/plugins/&lt;plugin_internal_name&gt;
	 * </PRE>
	 *
	 * @return	Plugins user folder.
	 *
	 * @throws	IllegalStateException
	 *			if plugin doesn't have an internal name.
	 *
	 * @throws	IOException
	 * 			An error occured retrieving/creating the folder.
	 * 
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getPluginUserSettingsFolder()
	 */
	public synchronized FileWrapper getPluginUserSettingsFolder()
		throws IllegalStateException, IOException
	{
		final String internalName = getInternalName();
		if (internalName == null || internalName.trim().length() == 0)
		{
			throw new IllegalStateException("IPlugin doesn't have a valid internal name");
		}
		String name =
			new ApplicationFiles().getPluginsUserSettingsDirectory()
				+ File.separator
				+ internalName
				+ File.separator;
		FileWrapper file = fileWrapperFactory.create(name);
		if (!file.exists())
		{
			file.mkdirs();
		}

		if (!file.isDirectory())
		{
			throw new IOException(s_stringMgr.getString("DefaultPlugin.error.cannotcreate", name));
		}

		return file;
	}

	/**
	 * Create panels for the Global Preferences dialog.
	 *
	 * @return	<TT>null</TT> to indicate that this plugin doesn't require
	 *			any panels in the Global Preferences Dialog.
	 */
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		return null;
	}

   public IAliasPropertiesPanelController[] getAliasPropertiesPanelControllers(SQLAlias alias)
   {
      return null;
   }

   public void aliasCopied(SQLAlias source, SQLAlias target)
   {
   }

   public void aliasRemoved(SQLAlias alias)
   {
   }


   /**
	 * Create panels for the New Session Properties dialog.
	 *
	 * @return	<TT>null</TT>to indicate that this plugin doesn't require
	 *			any panels in the New Session Properties Dialog.
	 */
	public INewSessionPropertiesPanel[] getNewSessionPropertiesPanels()
	{
		return null;
	}

   /**
    * By default a plugin provieds no external services
    * @return
    */
   public Object getExternalService()
   {
      return null;
   }

	/**
	 * @param applicationFiles the applicationFiles to set
	 */
	public void setApplicationFiles(ApplicationFileWrappers applicationFiles)
	{
		Utilities.checkNull("setApplicationFiles", "applicationFiles", applicationFiles);
		this.applicationFiles = applicationFiles;
	}

	/**
	 * @param fileWrapperFactory the fileWrapperFactory to set
	 */
	public void setFileWrapperFactory(FileWrapperFactory fileWrapperFactory)
	{
		Utilities.checkNull("setFileWrapperFactory", "fileWrapperFactory", fileWrapperFactory);
		this.fileWrapperFactory = fileWrapperFactory;
	}
   

}
