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
import java.util.*;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
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
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class PluginManager
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
	 * Contains a <TT>PluginInfo</TT> object for every plugin that we attempted
	 * to load.
	 */
	private final List _plugins = new ArrayList();

	/**
	 * Contains all plugins (<TT>IPlugin</TT>) successfully
	 * loaded. Keyed by <TT>IPlugin.getInternalName()</TT>.
	 */
	private final Map _loadedPlugins = new HashMap();

	/**
	 * Contains a <TT>SessionPluginInfo</TT> object for evey object in
	 * <TT>_loadedPlugins<TT> that is an instance of <TT>ISessionPlugin</TT>.
	 */
	private final List _sessionPlugins = new ArrayList();

	/**
	 * Collection of active sessions. Keyed by <TT>ISession.getIdentifier()</TT>
	 * and contains a <TT>List</TT> of active <TT>ISessionPlugin</TT> objects
	 * for the session.
	 */
	private final Map _activeSessions = new HashMap();

	/**
	 * Collection of <TT>PluginLoadInfo</TT> objects for the
	 * plugins. Stores info about how long it took to load
	 * each plugin.
	 */
	private final Map _pluginLoadInfoColl = new HashMap();
    
   private HashMap _pluginSessionCallbacksBySessionID = new HashMap();

   /** The class that listens for notifications as archives are being loaded */
   private ClassLoaderListener classLoaderListener = null;
   
   
   /**
	 * Ctor.
	 *
	 * @param	app		Application API.
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
	 * <TT>SessionPanel</TT> does not exist for the new session.
	 *
	 * @param	session	 The new session.
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
				String msg = s_stringMgr.getString("PluginManager.error.sessioncreated",
								spi.getPlugin().getDescriptiveName());
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
	}

	/**
	 * A new session is starting.
	 *
	 * @param	session	 The new session.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	public synchronized void sessionStarted(final ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		final List plugins = new ArrayList();
        _activeSessions.put(session.getIdentifier(), plugins);


      ArrayList startInFG = new ArrayList();
      final ArrayList startInBG = new ArrayList();
		for (Iterator it = _sessionPlugins.iterator(); it.hasNext();)
		{
			SessionPluginInfo spi = (SessionPluginInfo) it.next();
         if(spi.getSessionPlugin().allowsSessionStartedInBackground())
         {
            startInBG.add(spi);
         }
         else
         {
            startInFG.add(spi);
         }

      }
		session.setPluginsfinishedLoading(true);



		for (Iterator it = startInFG.iterator(); it.hasNext();)
		{
			SessionPluginInfo spi = (SessionPluginInfo) it.next();
			sendSessionStarted(session, spi, plugins);
		}


		session.getApplication().getThreadPool().addTask(new Runnable()
		{
			public void run()
			{
				for (Iterator it = startInBG.iterator(); it.hasNext();)
				{
					SessionPluginInfo spi = (SessionPluginInfo) it.next();
					sendSessionStarted(session, spi, plugins);
				}
				session.setPluginsfinishedLoading(true);
			}
		});
	}

    private void sendSessionStarted(ISession session, 
                                    SessionPluginInfo spi,
                                    List plugins) {
        try
        {
            PluginSessionCallback pluginSessionCallback = spi.getSessionPlugin().sessionStarted(session);
            
            if (null != pluginSessionCallback)
            {
                List list = (List) _pluginSessionCallbacksBySessionID.get(session.getIdentifier());
                if(null == list)
                {
                    list = new ArrayList();
                    _pluginSessionCallbacksBySessionID.put(session.getIdentifier(), list);
                }
                list.add(pluginSessionCallback);
                
                plugins.add(spi);
            }
        }
        catch (final Throwable th)
        {
            final String msg = 
                s_stringMgr.getString("PluginManager.error.sessionstarted",
                                      spi.getPlugin().getDescriptiveName());
            s_log.error(msg, th);
            GUIUtils.processOnSwingEventThread(new Runnable() {
                public void run() {
                    _app.showErrorDialog(msg, th);
                }
            });
            
        }        
    }
    
	/**
	 * A session is ending.
	 *
	 * @param	session	 The session ending.
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
					String msg = s_stringMgr.getString("PluginManager.error.sessionended",
									spi.getPlugin().getDescriptiveName());
					s_log.error(msg, th);
					_app.showErrorDialog(msg, th);
				}
			}
            
         _pluginSessionCallbacksBySessionID.remove(session.getIdentifier());
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
				String msg = s_stringMgr.getString("PluginManager.error.unloading",
								plugin.getInternalName());
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
	 * @return	<TT>Iterator</TT> over a collection of
	 * 			<TT>ISessionPlugin</TT> objects.
	 */
	public Iterator getSessionPluginIterator()
	{
		return _sessionPlugins.iterator();
	}

	/**
	 * TODO: Clean this mess up!!!!
	 * Load plugins. Load all plugin jars into class loader.
	 */
	public void loadPlugins()
	{
		List pluginUrls = new ArrayList();
		File dir = new ApplicationFiles().getPluginsDirectory();
        boolean isMac = 
            System.getProperty("os.name").toLowerCase().startsWith("mac");
		if (dir.isDirectory())
		{
			final Map pluginStatuses = new HashMap();
			{
				final PluginStatus[]ar = getPluginStatuses();
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

		URL[] urls = (URL[]) pluginUrls.toArray(new URL[pluginUrls.size()]);
		if (s_log.isDebugEnabled())
		{
			for (int i = 0; i < urls.length; ++i)
			{
				s_log.debug("Plugin class loader URL[" + i + "] = " + urls[i]);
			}
		}
		
        loadPluginInfoCache();

		_pluginsClassLoader = new MyURLClassLoader(urls);
        _pluginsClassLoader.addClassLoaderListener(classLoaderListener);
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
				String msg = s_stringMgr.getString("PluginManager.error.loadpluginclass", clazz.getName());
            th.printStackTrace();
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
	}

	private void checkPlugin(File pluginFile, Map pluginStatuses, List pluginUrls, boolean isMac)
	{
		final String fileName = pluginFile.getAbsolutePath();
		if (!fileName.toLowerCase().endsWith("src.jar") && 
                (fileName.toLowerCase().endsWith(".zip") 
                || fileName.toLowerCase().endsWith(".jar")))
		{
			try
			{
				if (fileName.toLowerCase().endsWith("jedit.jar"))
				{
					_app.showErrorDialog(s_stringMgr.getString("PluginManager.error.jedit"));
					return;
				}

				final String fullFilePath = pluginFile.getAbsolutePath();
				final String internalName = Utilities.removeFileNameSuffix(pluginFile.getName());
				final PluginStatus ps = (PluginStatus)pluginStatuses.get(internalName);
				if (!isMac && internalName.startsWith("macosx")) 
				{
					s_log.info("Detected MacOS X plugin on non-Mac platform - skipping");
					return;
				}
				if (ps == null || ps.isLoadAtStartup())
				{
					pluginUrls.add(pluginFile.toURL());

					// See if plugin has any jars in lib dir.
					final String pluginDirName = Utilities.removeFileNameSuffix(fullFilePath);
					final File libDir = new File(pluginDirName, "lib");
					addPluginLibraries(libDir, pluginUrls);
				}
			}
			catch (IOException ex)
			{
				String msg = s_stringMgr.getString("PluginManager.error.loadplugin", fileName);
				s_log.error(msg, ex);
				_app.showErrorDialog(msg, ex);
			}
		}
	}

    private void loadPluginInfoCache()
    {
    
    }

    private void addPluginLibraries(File libDir, List pluginUrls)
    {
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
      _app.getWindowManager().addSessionSheetListener(new InternalFrameAdapter()
      {
         public void internalFrameOpened(InternalFrameEvent e)
         {
            onInternalFrameOpened(e);
         }
      });

		for (Iterator it = _loadedPlugins.values().iterator(); it.hasNext();)
		{
			IPlugin plugin = (IPlugin) it.next();
			try
			{
				final PluginLoadInfo pli = getPluginLoadInfo(plugin);
				pli.startInitializing();
				plugin.initialize();
				pli.endInitializing();
			}
			catch (Throwable th)
			{
				String msg = s_stringMgr.getString("PluginManager.error.initplugin", plugin.getInternalName());
				s_log.error(msg, th);
				_app.showErrorDialog(msg, th);
			}
		}
	}

    /**
     * Sets the ClassLoaderListener to notify when archive files containing 
     * classes are loaded.
     * 
     * @param listener a ClassLoaderListener implementation
     */
    public void setClassLoaderListener(ClassLoaderListener listener) {
        classLoaderListener = listener;
    }
    
   private void onInternalFrameOpened(InternalFrameEvent e)
   {
      JInternalFrame frame = e.getInternalFrame();

      if(frame instanceof BaseSessionInternalFrame)
      {
         ISession session = ((BaseSessionInternalFrame)frame).getSession();

         List list =(List) _pluginSessionCallbacksBySessionID.get(session.getIdentifier());

         if(null != list)
         {
            for (int i = 0; i < list.size(); i++)
            {
               PluginSessionCallback psc = (PluginSessionCallback) list.get(i);

               if(frame instanceof SQLInternalFrame)
               {
                  psc.sqlInternalFrameOpened((SQLInternalFrame)frame, session);
               }
               else if(frame instanceof ObjectTreeInternalFrame)
               {
                  psc.objectTreeInternalFrameOpened((ObjectTreeInternalFrame)frame, session);
               }
            }
         }
      }
   }



	/**
	 * Retrieve information about plugin load times
	 *
	 * @return	<TT>Iterator</TT> over a collection of
	 * 			<TT>PluginLoadInfo</TT> objects.
	 */
	public Iterator getPluginLoadInfoIterator()
	{
		return _pluginLoadInfoColl.values().iterator();
	}

	private void loadPlugin(Class pluginClass)
	{
		PluginInfo pi = new PluginInfo(pluginClass.getName());
		try
		{
			final PluginLoadInfo pli = new PluginLoadInfo();
			final IPlugin plugin = (IPlugin)pluginClass.newInstance();
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
		}
		catch (Throwable th)
		{
			String msg = s_stringMgr.getString("PluginManager.error.loadpluginclass",
												pluginClass.getName());
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

	private PluginLoadInfo getPluginLoadInfo(IPlugin plugin)
	{
		return (PluginLoadInfo)_pluginLoadInfoColl.get(plugin.getInternalName());
	}


   /**
    * Allows plugins to access each other without imports.
    *
    * @param internalNameOfPlugin Is the accessed plugins internal name returned by IPlugin.getInternalName().

    * @param toBindTo Is an interface that is to bind against the object that the accessed plugin returns by its
    * getExternalService() method.
    *
    * @return An Object that may be cast to the toBindTo interface and delegates all calls to the object
    * returned by the accessed plugin's getExternalService() method. The method signature of the methods
    * in the toBintTo interface and external service object must be identical.
    * This method returns null if the plugin can not be found / is not loaded.
    */
   public Object bindExternalPluginService(String internalNameOfPlugin, Class toBindTo)
   {
      IPlugin plugin = (IPlugin) _loadedPlugins.get(internalNameOfPlugin);

      if(null == plugin)
      {
         return null;
      }


      final Object obj = plugin.getExternalService();

      if(null == obj)
      {
         throw new RuntimeException("The plugin " + internalNameOfPlugin + " doesn't provide any external service.");
      }

      InvocationHandler ih = new InvocationHandler()
      {
         public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
         {
            Method m = obj.getClass().getMethod(method.getName(), method.getParameterTypes());
            return m.invoke(obj, args);
         }
      };

      return Proxy.newProxyInstance(_pluginsClassLoader, new Class[]{toBindTo}, ih);
   }

   public IAliasPropertiesPanelController[] getAliasPropertiesPanelControllers(SQLAlias alias)
   {
      ArrayList ret = new ArrayList();
      for(Iterator i = _loadedPlugins.values().iterator();i.hasNext();)
      {
         IPlugin plugin = (IPlugin) i.next();

         IAliasPropertiesPanelController[] ctrls = plugin.getAliasPropertiesPanelControllers(alias);
         if(null != ctrls)
         {
            ret.addAll(Arrays.asList(ctrls));
         }
      }

      return (IAliasPropertiesPanelController[]) ret.toArray(new IAliasPropertiesPanelController[ret.size()]);
   }

   public void aliasCopied(SQLAlias source, SQLAlias target)
   {
      for(Iterator i = _loadedPlugins.values().iterator();i.hasNext();)
      {
         IPlugin plugin = (IPlugin) i.next();
         plugin.aliasCopied(source, target);
      }
   }

   public void aliasRemoved(SQLAlias alias)
   {
      for(Iterator i = _loadedPlugins.values().iterator();i.hasNext();)
      {
         IPlugin plugin = (IPlugin) i.next();
         plugin.aliasRemoved(alias);
      }
   }
}
