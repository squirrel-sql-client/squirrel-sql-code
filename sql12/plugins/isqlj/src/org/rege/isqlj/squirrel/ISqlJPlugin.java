package org.rege.isqlj.squirrel;

/**
* <p>Title: sqsc-isqlj</p>
* <p>Description: SquirrelSQL plugin for iSqlJ</p>
* <p>Copyright: Copyright (c) 2003 Stathis Alexopoulos</p>
* @author Stathis Alexopoulos stathis@rege.org
* <br>
* <br>
* <p>
*    This file is part of sqsc-isqlj.
* </p>
* <br>
* <p>
*    sqsc-isqlj is free software; you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*    Foobar is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with Foobar; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
* </p>
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;


public class ISqlJPlugin extends DefaultSessionPlugin
{
	private interface IMenuResourceKeys 
	{
		String SCRIPTS = "isqlj";
	}

	private static ILogger  log = LoggerController.createLogger(ISqlJPlugin.class);
	private File            pluginAppFolder;
	private File            userSettingsFolder;
	private PluginResources resources;

    public ISqlJPlugin()
    {
    }

	
	public synchronized void initialize() 
			throws PluginException 
	{
		super.initialize();
		IApplication app = getApplication();

		PluginManager pmgr = app.getPluginManager();

		try 
		{
			pluginAppFolder = getPluginAppSettingsFolder();
		} catch (IOException ex) 
		{
			throw new PluginException(ex);
		}

		try 
		{
			userSettingsFolder = getPluginUserSettingsFolder();
		} catch (IOException ex) 
		{
			throw new PluginException(ex);
		}

		resources = new ISqlJPluginResources("org.rege.isqlj.squirrel.ISqlJ", this);

		ActionCollection coll = app.getActionCollection();
		coll.add( new ExecuteISqlJAction(app, resources, this));
		createMenu();
	}

	public boolean sessionStarted(ISession session) 
	{
		ActionCollection coll = getApplication().getActionCollection();
		IObjectTreeAPI api = session.getObjectTreeAPI(this);
		return true;
	}


	public void unload() 
	{
		super.unload();
	}

	private void createMenu() 
	{
		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		JMenu menu = resources.createMenu( IMenuResourceKeys.SCRIPTS);
		resources.addToMenu(coll.get(ExecuteISqlJAction.class), menu);

		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);
	}
	
	public String getInternalName()
	{
		return "sqsc-isqlj";
	}

	public String getDescriptiveName()
	{
		return "Plugin for iSqlJ Interpreter";
	}

	public String getAuthor()
	{
		return "Stathis Alexopoulos";
	}

	public String getContributors()
	{
		return "";
	}

	public String getWebSite()
	{
		return "http://www.rege.org/isqlj";
	}

	public String getVersion()
	{
		return "0.10";
	}

	public String getHelpFileName()
	{
		return "myHelp.txt";
	}

	public String getChangeLogFileName()
	{
		return "myLog.txt";
	}

	public String getLicenceFileName()
	{
		return "myLicence.txt";
	}
}

