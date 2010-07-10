package net.sourceforge.squirrel_sql.client.plugin;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.util.MyURLClassLoader;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

/**
 * Manages plugins for the application.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class PluginManager
{
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(PluginManager.class);

	/** Application API object. */
	private IApplication _app;

	/** Classloader used for plugins. */
	private MyURLClassLoader _pluginsClassLoader;

	/**
	 * Contains a <TT>PluginInfo</TT> object for every plugin that we attempted
	 * to load.
	 */
	private List _plugins = new ArrayList();

	/**
	 * Contains all plugins (<TT>IPlugin</TT>) successfully
	 * loaded. Keyed by <TT>IPlugin.getInternalName()</TT>.
	 */
	private Map _loadedPlugins = new HashMap();

	/**
	 * Contains a <TT>SessionPluginInfo</TT> object for evey object in
	 * <TT>_loadedPlugins<TT> that is an instance of <TT>ISessionPlugin</TT>.
	 */
	private List _sessionPlugins = new ArrayList();

	/**
	 * Collection of active sessions. Keyed by <TT>ISession.getIdentifier()</TT>
	 * and contains a <TT>List</TT> of active <TT>ISessionPlugin</TT> objects
	 * for the session.
	 */
	private Map _activeSessions = new HashMap();

	/**
	 * Ctor. Loads plugins from the plugins directory.
	 * 
	 * @param	app	Application API.
	 * 
	 * @throws	IllegalArgumentException.
	 * 			Thrown if <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public PluginManager(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
	}

	/**
	 * A new session has been created. At this point the
	 * <TT>SessionSheet</TT> does not exist for the new session.
	 *
	 * @param   session	 The new session.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	public synchronized void sessionCreated(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		for (Iterator it = _sessionPlugins.iterator(); it.hasNext();)
		{
			SessionPluginInfo spi = (SessionPluginInfo) it.next();
			try
			{
				spi.getSessionPlugin().sessionCreated(session);
			}
			catch (Throwable th)
			{
				String msg =
					"Error occured in IPlugin.sessionCreated() for " + spi.getPlugin().getDescriptiveName();
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
	}

	/**
	 * A new session is starting.
	 *
	 * @param   session	 The new session.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	public synchronized void sessionStarted(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		List plugins = new ArrayList();
		_activeSessions.put(session.getIdentifier(), plugins);
		for (Iterator it = _sessionPlugins.iterator(); it.hasNext();)
		{
			SessionPluginInfo spi = (SessionPluginInfo) it.next();
			try
			{
				if (spi.getSessionPlugin().sessionStarted(session))
				{
					plugins.add(spi);
				}
			}
			catch (Throwable th)
			{
				String msg =
					"Error occured in IPlugin.sessionStarted() for " + spi.getPlugin().getDescriptiveName();
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
	}

	/**
	 * A session is ending.
	 *
	 * @param   session	 The session ending.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	public synchronized void sessionEnding(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		List plugins = (List) _activeSessions.remove(session.getIdentifier());
		if (plugins != null)
		{
			for (Iterator it = plugins.iterator(); it.hasNext();)
			{
				SessionPluginInfo spi = (SessionPluginInfo) it.next();
				try
				{
					spi.getSessionPlugin().sessionEnding(session);
				}
				catch (Throwable th)
				{
					String msg =
						"Error occured in IPlugin.sessionEnding() for " + spi.getPlugin().getDescriptiveName();
					s_log.error(msg, th);
					_app.showErrorDialog(msg, th);
				}
			}
		}
	}

	/**
	 * Unload all plugins.
	 */
	public synchronized void unloadPlugins()
	{
		for (Iterator it = _loadedPlugins.values().iterator(); it.hasNext();)
		{
			IPlugin plugin = (IPlugin) it.next();
			try
			{
				plugin.unload();
			}
			catch (Throwable th)
			{
				String msg = "Error ocured unloading plugin: " + plugin.getInternalName();
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
	}

	public synchronized PluginInfo[] getPluginInformation()
	{
		return (PluginInfo[]) _plugins.toArray(new PluginInfo[_plugins.size()]);
	}

	public synchronized SessionPluginInfo[] getPluginInformation(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		List list = (List) _activeSessions.get(session.getIdentifier());
		if (list != null)
		{
			return (SessionPluginInfo[]) list.toArray(new SessionPluginInfo[list.size()]);
		}
		return new SessionPluginInfo[0];
	}

	public synchronized IPluginDatabaseObjectType[] getDatabaseObjectTypes(ISession session)
	{
		List objTypesList = new ArrayList();
		List plugins = (List) _activeSessions.get(session.getIdentifier());
		if (plugins != null)
		{
			for (Iterator it = plugins.iterator(); it.hasNext();)
			{
				SessionPluginInfo spi = (SessionPluginInfo) it.next();
				IPluginDatabaseObjectType[] objTypes = spi.getSessionPlugin().getObjectTypes(session);
				if (objTypes != null)
				{
					for (int i = 0; i < objTypes.length; ++i)
					{
						objTypesList.add(objTypes[i]);
					}
				}
			}
		}

		return (IPluginDatabaseObjectType[]) objTypesList.toArray(
			new IPluginDatabaseObjectType[objTypesList.size()]);
	}

	/**
	 * Retrieve an array of all the <TT>URL</TT> objects that are
	 * used to find plugin classes.
	 * 
	 * @return		<TT>URL[]</TT>.
	 */
	public URL[] getPluginURLs()
	{
		return _pluginsClassLoader.getURLs();
	}

	/**
	 * TODO: Clean this mess up!!!!
	 * Load plugins. Load all plugin jars into class loader.
	 */
	public void loadPlugins()
	{
		List pluginUrls = new ArrayList();
		File dir = new ApplicationFiles().getPluginsDirectory();
		if (dir.isDirectory())
		{
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; ++i)
			{
				final File file = files[i];
				if (file.isFile())
				{
					final String fileName = file.getAbsolutePath();
					if (fileName.toLowerCase().endsWith(".zip") || fileName.toLowerCase().endsWith(".jar"))
					{
						try
						{
							pluginUrls.add(file.toURL());
							
							// See if plugin has any jars in lib dir.
							final String pluginDirName = Utilities.removeFileNameSuffix(file.getAbsolutePath());
							final File libDir = new File(pluginDirName, "lib");
							if (libDir.exists() && libDir.isDirectory())
							{
								File[] libDirFiles = libDir.listFiles();
								for (int j = 0; j < libDirFiles.length; ++j)
								{
									if (libDirFiles[j].isFile())
									{
										final String fn = libDirFiles[j].getAbsolutePath();
										if (fn.toLowerCase().endsWith(".zip") ||
												fn.toLowerCase().endsWith(".jar"))
										{
											try
											{
												pluginUrls.add(libDirFiles[j].toURL());
											}
											catch (IOException ex)
											{
												String msg = "Unable to load plugin library file: " + fn;
												s_log.error(msg, ex);
												_app.showErrorDialog(msg, ex);
											}
										}
									}
								}
							}
						}
						catch (IOException ex)
						{
							String msg = "Unable to load plugin jar: " + fileName;
							s_log.error(msg, ex);
							_app.showErrorDialog(msg, ex);
						}
					}
				}
			}
		}

		URL[] urls = (URL[]) pluginUrls.toArray(new URL[pluginUrls.size()]);
		if (s_log.isDebugEnabled())
		{
			for (int i = 0; i < urls.length; ++i)
			{
				s_log.debug("Plugin class loader URL[" + i + "] = " + urls[i]);
			}
		}
		_pluginsClassLoader = new MyURLClassLoader(urls);

		try
		{
			Class[] classes = _pluginsClassLoader.getAssignableClasses(IPlugin.class, s_log);
			for (int i = 0; i < classes.length; ++i)
			{
				Class clazz = classes[i];
				try
				{
					loadPlugin(clazz);
				}
				catch (Throwable th)
				{
					String msg = "Error occured loading plugin class: " + clazz.getName();
					s_log.error(msg, th);
					_app.showErrorDialog(msg, th);
				}
			}
		}
		catch (IOException ex)
		{
			String msg = "Error occured retrieving plugins. No plugins have been loaded.";
			s_log.error(msg, ex);
			_app.showErrorDialog(msg, ex);
		}
	}

	/**
	 * Initialize plugins.
	 */
	public void initializePlugins()
	{
		for (Iterator it = _loadedPlugins.values().iterator(); it.hasNext();)
		{
			IPlugin plugin = (IPlugin) it.next();
			try
			{
				long now = System.currentTimeMillis();
				plugin.initialize();
				s_log.info(
					"Plugin "
						+ plugin.getInternalName()
						+ " initialised in "
						+ (System.currentTimeMillis() - now)
						+ " ms.");
			}
			catch (Throwable th)
			{
				String msg = "Error ocured initializing plugin: " + plugin.getInternalName();
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
	}

	private void loadPlugin(Class pluginClass)
	{
		PluginInfo pi = new PluginInfo(pluginClass.getName());
		try
		{
			long now = System.currentTimeMillis();
			IPlugin plugin = (IPlugin) pluginClass.newInstance();
			pi.setPlugin(plugin);
			_plugins.add(pi);
			if (validatePlugin(plugin))
			{
				plugin.load(_app);
				pi.setLoaded(true);
				_loadedPlugins.put(plugin.getInternalName(), plugin);
				if (ISessionPlugin.class.isAssignableFrom(pluginClass))
				{
					_sessionPlugins.add(new SessionPluginInfo(pi));
				}
				s_log.info(
					"Plugin "
						+ plugin.getInternalName()
						+ " loaded in "
						+ (System.currentTimeMillis() - now)
						+ " ms.");
			}
		}
		catch (Throwable th)
		{
			String msg = "Error occured loading class " + pluginClass.getName() + " from plugin";
			s_log.error(msg, th);
			_app.showErrorDialog(msg, th);
		}
	}

	private boolean validatePlugin(IPlugin plugin)
	{
		String pluginInternalName = plugin.getInternalName();
		if (pluginInternalName == null || pluginInternalName.trim().length() == 0)
		{
			s_log.error(
				"Plugin " + plugin.getClass().getName() + "doesn't return a valid getInternalName()");
			return false;
		}

		if (_loadedPlugins.get(pluginInternalName) != null)
		{
			s_log.error(
				"A Plugin with the internal name " + pluginInternalName + " has already been loaded");
			return false;
		}

		return true;
	}

}
