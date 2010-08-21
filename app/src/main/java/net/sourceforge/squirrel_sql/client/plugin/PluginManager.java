package net.sourceforge.squirrel_sql.client.plugin;

/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (c) 2004 Jason Height.
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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener;
import net.sourceforge.squirrel_sql.fw.util.MyURLClassLoader;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Manages plugins for the application.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class PluginManager implements IPluginManager
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(PluginManager.class);

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(PluginManager.class);

	/** Application API object. */
	private IApplication _app;

	/** Classloader used for plugins. */
	private MyURLClassLoader _pluginsClassLoader;

	/**
	 * Contains a <TT>PluginInfo</TT> object for every plugin that we attempted to load.
	 */
	private final List<PluginInfo> _plugins = new ArrayList<PluginInfo>();

	/**
	 * Contains all plugins (<TT>IPlugin</TT>) successfully loaded. Keyed by
	 * <TT>IPlugin.getInternalName()</TT>.
	 */
	private final Map<String, IPlugin> _loadedPlugins = new HashMap<String, IPlugin>();

	/**
	 * Contains a <TT>SessionPluginInfo</TT> object for evey object in
	 * <TT>_loadedPlugins<TT> that is an instance of <TT>ISessionPlugin</TT>.
	 */
	private final List<SessionPluginInfo> _sessionPlugins = new ArrayList<SessionPluginInfo>();

	/**
	 * Collection of active sessions. Keyed by <TT>ISession.getIdentifier()</TT> and contains a <TT>List</TT>
	 * of active <TT>ISessionPlugin</TT> objects for the session.
	 */
	private final Map<IIdentifier, List<SessionPluginInfo>> _activeSessions =
		new HashMap<IIdentifier, List<SessionPluginInfo>>();

	/**
	 * Collection of <TT>PluginLoadInfo</TT> objects for the plugins. Stores info about how long it took to
	 * load each plugin.
	 */
	private final Map<String, PluginLoadInfo> _pluginLoadInfoColl = new HashMap<String, PluginLoadInfo>();

	private HashMap<IIdentifier, List<PluginSessionCallback>> _pluginSessionCallbacksBySessionID =
		new HashMap<IIdentifier, List<PluginSessionCallback>>();

	/** The class that listens for notifications as archives are being loaded */
	private ClassLoaderListener classLoaderListener = null;

	/**
	 * Ctor.
	 * 
	 * @param app
	 *           Application API.
	 * @throws IllegalArgumentException.
	 *            Thrown if <TT>null</TT> <TT>IApplication</TT> passed.
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
	 * A new session has been created. At this point the <TT>SessionPanel</TT> does not exist for the new
	 * session.
	 * 
	 * @param session
	 *           The new session.
	 * @throws IllegalArgumentException
	 *            Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	public synchronized void sessionCreated(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		for (Iterator<SessionPluginInfo> it = _sessionPlugins.iterator(); it.hasNext();)
		{
			SessionPluginInfo spi = it.next();
			try
			{
				spi.getSessionPlugin().sessionCreated(session);
			} catch (Throwable th)
			{
				String msg =
					s_stringMgr.getString("PluginManager.error.sessioncreated", spi.getPlugin()
																										.getDescriptiveName());
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
	}

	/**
	 * A new session is starting.
	 * 
	 * @param session
	 *           The new session.
	 * @throws IllegalArgumentException
	 *            Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	public synchronized void sessionStarted(final ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		final List<SessionPluginInfo> plugins = new ArrayList<SessionPluginInfo>();
		_activeSessions.put(session.getIdentifier(), plugins);

		ArrayList<SessionPluginInfo> startInFG = new ArrayList<SessionPluginInfo>();
		final ArrayList<SessionPluginInfo> startInBG = new ArrayList<SessionPluginInfo>();
		for (Iterator<SessionPluginInfo> it = _sessionPlugins.iterator(); it.hasNext();)
		{
			SessionPluginInfo spi = it.next();
			if (spi.getSessionPlugin().allowsSessionStartedInBackground())
			{
				startInBG.add(spi);
			} else
			{
				startInFG.add(spi);
			}

		}
		session.setPluginsfinishedLoading(true);

		for (Iterator<SessionPluginInfo> it = startInFG.iterator(); it.hasNext();)
		{
			SessionPluginInfo spi = it.next();
			sendSessionStarted(session, spi, plugins);
		}

		session.getApplication().getThreadPool().addTask(new Runnable()
		{
			public void run()
			{
				for (Iterator<SessionPluginInfo> it = startInBG.iterator(); it.hasNext();)
				{
					SessionPluginInfo spi = it.next();
					sendSessionStarted(session, spi, plugins);
				}
				session.setPluginsfinishedLoading(true);
			}
		});
	}

	private void sendSessionStarted(ISession session, SessionPluginInfo spi, List<SessionPluginInfo> plugins)
	{
		try
		{
			PluginSessionCallback pluginSessionCallback = spi.getSessionPlugin().sessionStarted(session);

			if (null != pluginSessionCallback)
			{
				List<PluginSessionCallback> list =
					_pluginSessionCallbacksBySessionID.get(session.getIdentifier());
				if (null == list)
				{
					list = new ArrayList<PluginSessionCallback>();
					_pluginSessionCallbacksBySessionID.put(session.getIdentifier(), list);
				}
				list.add(pluginSessionCallback);

				plugins.add(spi);
			}
		} catch (final Throwable th)
		{
			final String msg =
				s_stringMgr.getString("PluginManager.error.sessionstarted", spi.getPlugin().getDescriptiveName());
			s_log.error(msg, th);
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					_app.showErrorDialog(msg, th);
				}
			});

		}
	}

	/**
	 * A session is ending.
	 * 
	 * @param session
	 *           The session ending.
	 * @throws IllegalArgumentException
	 *            Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	public synchronized void sessionEnding(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		List<SessionPluginInfo> plugins = _activeSessions.remove(session.getIdentifier());
		if (plugins != null)
		{
			for (Iterator<SessionPluginInfo> it = plugins.iterator(); it.hasNext();)
			{
				SessionPluginInfo spi = it.next();
				try
				{
					spi.getSessionPlugin().sessionEnding(session);
				} catch (Throwable th)
				{
					String msg =
						s_stringMgr.getString("PluginManager.error.sessionended", spi	.getPlugin()
																											.getDescriptiveName());
					s_log.error(msg, th);
					_app.showErrorDialog(msg, th);
				}
			}

			_pluginSessionCallbacksBySessionID.remove(session.getIdentifier());
		}
	}

	public synchronized void unloadPlugin(String pluginInternalName)
	{
		for (Iterator<IPlugin> it = _loadedPlugins.values().iterator(); it.hasNext();)
		{
			IPlugin plugin = it.next();
			if (plugin.getInternalName().equals(pluginInternalName))
			{
				plugin.unload();
				it.remove();
			}
		}
		for (Iterator<SessionPluginInfo> it = _sessionPlugins.iterator(); it.hasNext();)
		{
			SessionPluginInfo plugin = it.next();
			if (plugin.getInternalName().equals(pluginInternalName))
			{
				it.remove();
			}
		}
	}

	/**
	 * Unload all plugins.
	 */
	public synchronized void unloadPlugins()
	{
		for (Iterator<IPlugin> it = _loadedPlugins.values().iterator(); it.hasNext();)
		{
			IPlugin plugin = it.next();
			try
			{
				plugin.unload();
			} catch (Throwable th)
			{
				String msg = s_stringMgr.getString("PluginManager.error.unloading", plugin.getInternalName());
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
	}

	public synchronized PluginInfo[] getPluginInformation()
	{
		return _plugins.toArray(new PluginInfo[_plugins.size()]);
	}

	public synchronized SessionPluginInfo[] getPluginInformation(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		List<SessionPluginInfo> list = _activeSessions.get(session.getIdentifier());
		if (list != null)
		{
			return list.toArray(new SessionPluginInfo[list.size()]);
		}
		return new SessionPluginInfo[0];
	}

	public synchronized IPluginDatabaseObjectType[] getDatabaseObjectTypes(ISession session)
	{
		List<IPluginDatabaseObjectType> objTypesList = new ArrayList<IPluginDatabaseObjectType>();
		List<SessionPluginInfo> plugins = _activeSessions.get(session.getIdentifier());
		if (plugins != null)
		{
			for (Iterator<SessionPluginInfo> it = plugins.iterator(); it.hasNext();)
			{
				SessionPluginInfo spi = it.next();
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

		return objTypesList.toArray(new IPluginDatabaseObjectType[objTypesList.size()]);
	}

	/**
	 * Retrieve an array of all the <TT>URL</TT> objects that are used to find plugin classes.
	 * 
	 * @return <TT>URL[]</TT>.
	 */
	public URL[] getPluginURLs()
	{
		return _pluginsClassLoader.getURLs();
	}

	public PluginStatus[] getPluginStatuses()
	{
		return _app.getSquirrelPreferences().getPluginStatuses();
	}

	public synchronized void setPluginStatuses(PluginStatus[] values)
	{
		_app.getSquirrelPreferences().setPluginStatuses(values);
	}

	/**
	 * Retrieve loaded session plugins
	 * 
	 * @return <TT>Iterator</TT> over a collection of <TT>ISessionPlugin</TT> objects.
	 */
	public Iterator<SessionPluginInfo> getSessionPluginIterator()
	{
		return _sessionPlugins.iterator();
	}

	/**
	 * TODO: Clean this mess up!!!! Load plugins. Load all plugin jars into class loader.
	 */
	public void loadPlugins()
	{
		List<URL> pluginUrls = new ArrayList<URL>();
		File dir = new ApplicationFiles().getPluginsDirectory();
		boolean isMac = System.getProperty("os.name").toLowerCase().startsWith("mac");
		if (dir.isDirectory())
		{
			final Map<String, PluginStatus> pluginStatuses = new HashMap<String, PluginStatus>();
			{
				final PluginStatus[] ar = getPluginStatuses();
				for (int i = 0; i < ar.length; ++i)
				{
					pluginStatuses.put(ar[i].getInternalName(), ar[i]);
				}
			}
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; ++i)
			{
				if (files[i].isFile())
				{
					checkPlugin(files[i], pluginStatuses, pluginUrls, isMac);
				}
			}
		}
		Collections.sort(pluginUrls, new PluginLoadOrderComparator());

		URL[] urls = pluginUrls.toArray(new URL[pluginUrls.size()]);
		if (s_log.isDebugEnabled())
		{
			for (int i = 0; i < urls.length; ++i)
			{
				s_log.debug("Plugin class loader URL[" + i + "] = " + urls[i]);
			}
		}

		_pluginsClassLoader = new MyURLClassLoader(urls);
		_pluginsClassLoader.addClassLoaderListener(classLoaderListener);
		Class<?>[] classes = _pluginsClassLoader.getAssignableClasses(IPlugin.class, s_log);
		for (int i = 0; i < classes.length; ++i)
		{
			try
			{
				loadPlugin(classes[i]);
			} catch (Throwable th)
			{
				String msg = s_stringMgr.getString("PluginManager.error.loadpluginclass", classes[i].getName());
				th.printStackTrace();
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
		Collections.sort(_plugins, new Comparator<PluginInfo>()
		{
			public int compare(PluginInfo arg0, PluginInfo arg1)
			{
				if (arg0 == null || arg1 == null)
				{
					throw new NullPointerException("arg1 and arg2 must not be null");
				}
				return arg0.getInternalName().compareTo(arg1.getInternalName());
			}

		});
	}

	private void checkPlugin(File pluginFile, Map<String, PluginStatus> pluginStatuses, List<URL> pluginUrls,
		boolean isMac)
	{
		final String fileName = pluginFile.getAbsolutePath();
		if (!fileName.toLowerCase().endsWith("src.jar")
			&& (fileName.toLowerCase().endsWith(".zip") || fileName.toLowerCase().endsWith(".jar")))
		{
			try
			{
				if (fileName.toLowerCase().endsWith("jedit.jar"))
				{
					String msg = s_stringMgr.getString("PluginManager.error.jedit");
					_app.showErrorDialog(msg);
					return;
				}

				final String fullFilePath = pluginFile.getAbsolutePath();
				final String internalName = Utilities.removeFileNameSuffix(pluginFile.getName());
				final PluginStatus ps = pluginStatuses.get(internalName);
				if (!isMac && internalName.startsWith("macosx"))
				{
					s_log.info("Detected MacOS X plugin on non-Mac platform - skipping");
					return;
				}
				if (ps != null && !ps.isLoadAtStartup())
				{
					// We need this in order to allow the user to see this
					// plugin - which isn't loaded - in the plugin summary
					// dialog, so that they can enable it.
					PluginInfo pi = new PluginInfo();
					pi.setPlugin(new MyPlaceHolderPlugin(internalName));
					_plugins.add(pi);

				} else
				{
					pluginUrls.add(pluginFile.toURI().toURL());

					// See if plugin has any jars in lib dir.
					final String pluginDirName = Utilities.removeFileNameSuffix(fullFilePath);
					final File libDir = new File(pluginDirName, "lib");
					addPluginLibraries(libDir, pluginUrls);

				}
			} catch (IOException ex)
			{
				String msg = s_stringMgr.getString("PluginManager.error.loadplugin", fileName);
				s_log.error(msg, ex);
				_app.showErrorDialog(msg, ex);
			}
		}
	}

	private void addPluginLibraries(File libDir, List<URL> pluginUrls)
	{
		if (libDir.exists() && libDir.isDirectory())
		{
			File[] libDirFiles = libDir.listFiles();
			for (int j = 0; j < libDirFiles.length; ++j)
			{
				if (libDirFiles[j].isFile())
				{
					final String fn = libDirFiles[j].getAbsolutePath();
					if (fn.toLowerCase().endsWith(".zip") || fn.toLowerCase().endsWith(".jar"))
					{
						try
						{
							pluginUrls.add(libDirFiles[j].toURI().toURL());
						} catch (IOException ex)
						{
							String msg = s_stringMgr.getString("PluginManager.error.loadlib", fn);
							s_log.error(msg, ex);
							_app.showErrorDialog(msg, ex);
						}
					}
				}
			}
		}

	}

	/**
	 * Initialize plugins.
	 */
	public void initializePlugins()
	{
		_app.getWindowManager().addSessionWidgetListener(new WidgetAdapter()
		{
			public void widgetOpened(WidgetEvent e)
			{
				onWidgetOpened(e);
			}
		});

		for (Iterator<IPlugin> it = _loadedPlugins.values().iterator(); it.hasNext();)
		{
			IPlugin plugin = it.next();
			try
			{
				final PluginLoadInfo pli = getPluginLoadInfo(plugin);
				pli.startInitializing();
				plugin.initialize();
				pli.endInitializing();
			} catch (Throwable th)
			{
				String msg = s_stringMgr.getString("PluginManager.error.initplugin", plugin.getInternalName());
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
	}

	/**
	 * Sets the ClassLoaderListener to notify when archive files containing classes are loaded.
	 * 
	 * @param listener
	 *           a ClassLoaderListener implementation
	 */
	public void setClassLoaderListener(ClassLoaderListener listener)
	{
		classLoaderListener = listener;
	}

	private void onWidgetOpened(WidgetEvent e)
	{
		IWidget widget = e.getWidget();

		if (widget instanceof ISessionWidget)
		{
			ISession session = ((ISessionWidget) widget).getSession();

			List<PluginSessionCallback> list = _pluginSessionCallbacksBySessionID.get(session.getIdentifier());

			if (null != list)
			{
				for (int i = 0; i < list.size(); i++)
				{
					PluginSessionCallback psc = list.get(i);

					if (widget instanceof SQLInternalFrame)
					{
						psc.sqlInternalFrameOpened((SQLInternalFrame) widget, session);
					} else if (widget instanceof ObjectTreeInternalFrame)
					{
						psc.objectTreeInternalFrameOpened((ObjectTreeInternalFrame) widget, session);
					}
				}
			}
		}
	}

	/**
	 * Retrieve information about plugin load times
	 * 
	 * @return <TT>Iterator</TT> over a collection of <TT>PluginLoadInfo</TT> objects.
	 */
	public Iterator<PluginLoadInfo> getPluginLoadInfoIterator()
	{
		return _pluginLoadInfoColl.values().iterator();
	}

	private void loadPlugin(Class<?> pluginClass)
	{
		PluginInfo pi = new PluginInfo(pluginClass.getName());
		try
		{
			final PluginLoadInfo pli = new PluginLoadInfo();
			final IPlugin plugin = (IPlugin) pluginClass.newInstance();
			pli.pluginCreated(plugin);
			_pluginLoadInfoColl.put(plugin.getInternalName(), pli);
			pi.setPlugin(plugin);
			_plugins.add(pi);
			if (validatePlugin(plugin))
			{
				pli.startLoading();
				plugin.load(_app);
				pi.setLoaded(true);
				_loadedPlugins.put(plugin.getInternalName(), plugin);
				if (ISessionPlugin.class.isAssignableFrom(pluginClass))
				{
					_sessionPlugins.add(new SessionPluginInfo(pi));
				}
			}
			pli.endLoading();
		} catch (Throwable th)
		{
			String msg = s_stringMgr.getString("PluginManager.error.loadpluginclass", pluginClass.getName());
			th.printStackTrace();
			s_log.error(msg, th);
			_app.showErrorDialog(msg, th);
		}
	}

	private boolean validatePlugin(IPlugin plugin)
	{
		String pluginInternalName = plugin.getInternalName();
		if (pluginInternalName == null || pluginInternalName.trim().length() == 0)
		{
			s_log.error("Plugin " + plugin.getClass().getName() + "doesn't return a valid getInternalName()");
			return false;
		}

		if (_loadedPlugins.get(pluginInternalName) != null)
		{
			s_log.error("A Plugin with the internal name " + pluginInternalName + " has already been loaded");
			return false;
		}

		return true;
	}

	private PluginLoadInfo getPluginLoadInfo(IPlugin plugin)
	{
		return _pluginLoadInfoColl.get(plugin.getInternalName());
	}

	/**
	 * Allows plugins to access each other without imports.
	 * 
	 * @param internalNameOfPlugin
	 *           Is the accessed plugins internal name returned by IPlugin.getInternalName().
	 * @param toBindTo
	 *           Is an interface that is to bind against the object that the accessed plugin returns by its
	 *           getExternalService() method.
	 * @return An Object that may be cast to the toBindTo interface and delegates all calls to the object
	 *         returned by the accessed plugin's getExternalService() method. The method signature of the
	 *         methods in the toBintTo interface and external service object must be identical. This method
	 *         returns null if the plugin can not be found / is not loaded.
	 */
	public Object bindExternalPluginService(String internalNameOfPlugin, Class<?> toBindTo)
	{
		IPlugin plugin = _loadedPlugins.get(internalNameOfPlugin);

		if (null == plugin)
		{
			return null;
		}

		final Object obj = plugin.getExternalService();

		if (null == obj)
		{
			throw new RuntimeException("The plugin " + internalNameOfPlugin
				+ " doesn't provide any external service.");
		}

		InvocationHandler ih = new InvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				Method m = obj.getClass().getMethod(method.getName(), method.getParameterTypes());
				return m.invoke(obj, args);
			}
		};

		return Proxy.newProxyInstance(_pluginsClassLoader, new Class[] { toBindTo }, ih);
	}

	public IAliasPropertiesPanelController[] getAliasPropertiesPanelControllers(SQLAlias alias)
	{
		ArrayList<IAliasPropertiesPanelController> ret = new ArrayList<IAliasPropertiesPanelController>();
		for (Iterator<IPlugin> i = _loadedPlugins.values().iterator(); i.hasNext();)
		{
			IPlugin plugin = i.next();

			IAliasPropertiesPanelController[] ctrls = plugin.getAliasPropertiesPanelControllers(alias);
			if (null != ctrls)
			{
				ret.addAll(Arrays.asList(ctrls));
			}
		}

		return ret.toArray(new IAliasPropertiesPanelController[ret.size()]);
	}

	public void aliasCopied(SQLAlias source, SQLAlias target)
	{
		for (Iterator<IPlugin> i = _loadedPlugins.values().iterator(); i.hasNext();)
		{
			IPlugin plugin = i.next();
			plugin.aliasCopied(source, target);
		}
	}

	public void aliasRemoved(SQLAlias alias)
	{
		for (Iterator<IPlugin> i = _loadedPlugins.values().iterator(); i.hasNext();)
		{
			IPlugin plugin = i.next();
			plugin.aliasRemoved(alias);
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.IPluginManager#loadPluginsFromList(java.util.Iterator)
	 */
	@Override
	public void loadPluginsFromList(List<String> pluginListIterator)
	{
		for (String className : pluginListIterator)
		{
			try
			{
				loadPlugin(Class.forName(className));
			}
			catch (Exception e)
			{
				s_log.error("Unable to load plugin class (" + className + ") from the classpath.  "
					+ "This plugin was specified by one of the following program arguments : "
					+ ApplicationArguments.IOptions.PLUGIN_LIST[0] + " or "
					+ ApplicationArguments.IOptions.PLUGIN_LIST[1] + ". Either remove this argument, or ensure "
					+ "that the plugin is on the CLASSPATH");
			}
		}

	}
	
   /**
	 * A plugin implementation that serves only to identify plugins that aren't being loaded, but which are
	 * still installed, and available to load on startup if the user changes the isLoadedOnStartup attribute.
	 * 
	 * @author rmmannin
	 */
	private static class MyPlaceHolderPlugin extends DefaultPlugin
	{
		private String _internalName = null;

		public MyPlaceHolderPlugin(String internalName)
		{
			_internalName = internalName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getAuthor()
		 */
		public String getAuthor()
		{
			return "";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getDescriptiveName()
		 */
		public String getDescriptiveName()
		{
			return "";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getInternalName()
		 */
		public String getInternalName()
		{
			return _internalName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getVersion()
		 */
		public String getVersion()
		{
			return "";
		}

	}


}
