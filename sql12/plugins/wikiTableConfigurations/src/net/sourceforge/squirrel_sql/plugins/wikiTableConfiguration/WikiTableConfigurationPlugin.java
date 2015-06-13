/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.wikiTableConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfiguration;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.IWikiTableConfigurationFactory;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.WikiTableConfigurationFactory;
import net.sourceforge.squirrel_sql.fw.gui.action.wikiTable.WikiTableConfigurationStorage;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.wikiTableConfiguration.configurations.JIRAConfluenceTableConfiguration;
import net.sourceforge.squirrel_sql.plugins.wikiTableConfiguration.configurations.MediaWikiTableConfiguration;
import net.sourceforge.squirrel_sql.plugins.wikiTableConfiguration.configurations.TiddlyWikiTableConfiguration;


/**
 * The scope of this plugin is providing some pre defined (aka build-in) configurations for WIKI tables.
 * This configurations are use, for exporting the content of a result table to a WIKI text.
 * For the following WIKI engines are configurations provided:
 * <li>Mediawiki</li>
 * <li>TiddlWiki</li>
 * <li>Atlassian JIRA &amp; Confluence</li>
 * @author Stefan Willinger
 *
 */
public class WikiTableConfigurationPlugin extends DefaultSessionPlugin {
	
	/**
	 * 
	 */
	private static final String BUILD_IN_CONFIGURATIONS_XML = "buildInConfigurations.xml";


	/** Logger for this class. */
	private final static ILogger log = LoggerController.createLogger(WikiTableConfigurationPlugin.class);

	
	/** Folder to store user settings in. */
	private FileWrapper _userSettingsFolder;


	private IWikiTableConfigurationFactory wikiTableConfigFactory;
	
	
	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.plugin.ISessionPlugin#sessionStarted(net.sourceforge.squirrel_sql.client.session.ISession)
	 */
	public PluginSessionCallback sessionStarted(ISession session) {
		// nothing to do
		return null;
	}
	
	
	

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getInternalName()
	 */
	public String getInternalName() {
		return "wiki tables";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getDescriptiveName()
	 */
	public String getDescriptiveName() {
		return "Configurations for exporting the result table as a WIKI table";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getAuthor()
	 */
	public String getAuthor() {
		return "Stefan Willinger";
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getContributors()
	 */
	public String getContributors() {
		return "Thomas Hackel";
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getVersion()
	 */
	public String getVersion() {
		return "0.2";
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin#load(net.sourceforge.squirrel_sql.client.IApplication)
	 */
	@Override
	public void load(IApplication app) throws PluginException {
		super.load(app);
		wikiTableConfigFactory = app.getWikiTableConfigFactory();
	}

	/**
	 * Add the provided build-in configurations to the factory
	 * @see MediaWikiTableConfiguration
	 * @see TiddlyWikiTableConfiguration
	 */
	private void addBuildInConfigs() {
		
		wikiTableConfigFactory.addBuilInConfiguration(new MediaWikiTableConfiguration());
		wikiTableConfigFactory.addBuilInConfiguration(new TiddlyWikiTableConfiguration());
		wikiTableConfigFactory.addBuilInConfiguration(new JIRAConfluenceTableConfiguration());
		
	}


	/**
	 * Initialize this plugin.
	 */
	@Override
	public synchronized void initialize() throws PluginException {
		super.initialize();
		
		// Folder to store user settings.
		try
		{
			_userSettingsFolder = getPluginUserSettingsFolder();
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}
		
		addBuildInConfigs();

		// Load plugin preferences.
		loadPrefs();
	}
	
	/**
	 * Load from preferences file.
	 */
	private void loadPrefs()
	{
		try
		{
			final XMLBeanReader doc = new XMLBeanReader();
			final FileWrapper file =
					fileWrapperFactory.create(_userSettingsFolder, BUILD_IN_CONFIGURATIONS_XML);
			doc.load(file, getClass().getClassLoader());

			Iterator<?> it = doc.iterator();

			if (it.hasNext()){
				WikiTableConfigurationStorage data = (WikiTableConfigurationStorage) it.next();

				List<IWikiTableConfiguration> savedBuildIn = data.configurationsAsList();
				List<IWikiTableConfiguration> currentBuildIn = wikiTableConfigFactory.getBuildInConfigurations();

				/*
				 * For build-in configurations, we need to merge the saved with the current one.
				 * For build-in configurations, we save a state like enabled. And after the last save, a plugin
				 * may have added additional configurations to the build-in ones. So we must merge the both.
				 * If the same config exists in both, the saved on will survival.
				 */

				List<IWikiTableConfiguration> mergedBuildInConfigs = WikiTableConfigurationFactory.merge(savedBuildIn, currentBuildIn);
				wikiTableConfigFactory.replaceBuilInConfiguration(mergedBuildInConfigs);
			}
		}
		catch (FileNotFoundException ignore)
		{
			// property file not found for user - first time user ran pgm.
		}
		catch (Exception ex)
		{
			final String msg = "Error occurred reading from preferences file: " + BUILD_IN_CONFIGURATIONS_XML;
			log.error(msg, ex);
		}
	}
	
	/**
	 * Save preferences to disk.
	 */
	private void savePrefs()
	{
		try
		{
			WikiTableConfigurationStorage storage = new WikiTableConfigurationStorage(wikiTableConfigFactory.getBuildInConfigurations());
			final XMLBeanWriter wtr = new XMLBeanWriter(storage);
			wtr.save(fileWrapperFactory.create(_userSettingsFolder, BUILD_IN_CONFIGURATIONS_XML));
		}
		catch (Exception ex)
		{
			final String msg = "Error occurred writing to preferences file: " + BUILD_IN_CONFIGURATIONS_XML;
			log.error(msg, ex);
		}
	}
	
	/**
	 * Application is shutting down so save preferences.
	 */
	public void unload()
	{
		savePrefs();
		super.unload();
	}
	
}
