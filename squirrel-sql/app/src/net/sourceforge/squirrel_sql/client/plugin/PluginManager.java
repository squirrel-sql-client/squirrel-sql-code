package net.sourceforge.squirrel_sql.client.plugin;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.squirrel_sql.fw.sql.SQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.MyURLClassLoader;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.Logger;

/**
 * Manages plugins for the application.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class PluginManager {
    /** Application API object. */
    private IApplication _app;

    /** Classloader used for plugins. */
    private MyURLClassLoader _pluginsClassLoader;

    /** Contains all plugins (<TT>PluginWrapper</TT>). */
    private List _plugins = new ArrayList();

    /**
     * Contains all plugins (<TT>IPlugin</TT>) successfully
     * loaded. Keyed by <TT>IPlugin.getInternalName()</TT>.
     */
    private Map _loadedPlugins = new HashMap();

    /**
     * Contains a subset of _loadedPlugins that are instances
     * of <TT>ISessionPlugin</TT>.
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
     */
    public PluginManager(IApplication app) {
        super();
        _app = app;
    }

    /**
     * A new session is starting.
     *
     * @param   session     The new session.
     */
    public synchronized void sessionStarted(ISession session) {
        List plugins = new ArrayList();
        _activeSessions.put(session.getIdentifier(), plugins);
        for (Iterator it = _sessionPlugins.iterator(); it.hasNext();) {
            ISessionPlugin plugin = (ISessionPlugin)it.next();
            try {
                boolean applicable = plugin.sessionStarted(session);
                if (applicable) {
                    plugins.add(plugin);
                }
            } catch (Throwable th) {
                Logger log = _app.getLogger();
                log.showMessage(Logger.ILogTypes.ERROR, "\nError occured in IPlugin.sessionStarted() for " + plugin.getDescriptiveName());
                log.showMessage(Logger.ILogTypes.ERROR, th);
            }
        }
    }

    /**
     * A session is ending.
     *
     * @param   session     The session ending.
     */
    public synchronized void sessionEnding(ISession session) {
        List plugins = (List)_activeSessions.remove(session.getIdentifier());
        if (plugins != null) {
            for (Iterator it = plugins.iterator(); it.hasNext();) {
                ISessionPlugin plugin = (ISessionPlugin)it.next();
                try {
                    plugin.sessionEnding(session);
                } catch (Throwable th) {
                    Logger log = _app.getLogger();
                    log.showMessage(Logger.ILogTypes.ERROR, "\nError occured in IPlugin.sessionEnding() for " + plugin.getDescriptiveName());
                    log.showMessage(Logger.ILogTypes.ERROR, th);
                }
            }
        }
    }

    /**
     * Unload all plugins.
     */
    public synchronized void unloadPlugins() {
        for (Iterator it = _loadedPlugins.values().iterator(); it.hasNext();) {
            IPlugin plugin = (IPlugin)it.next();
            try {
                plugin.unload();
            } catch (Throwable th) {
                Logger log = _app.getLogger();
                log.showMessage(Logger.ILogTypes.ERROR,
                    "Error ocured unloading plugin: " + plugin.getInternalName());
                log.showMessage(Logger.ILogTypes.ERROR, th);
            }
        }
    }

    public synchronized PluginInfo[] getPluginInformation() {
        List list = new ArrayList();
        for (Iterator it = _plugins.iterator(); it.hasNext();) {
            list.add((PluginInfo)it.next());
        }
        return (PluginInfo[])list.toArray(new PluginInfo[list.size()]);
    }

    public synchronized IPluginDatabaseObjectType[] getDatabaseObjectTypes(ISession session) {
        List objTypesList = new ArrayList();
        List plugins = (List)_activeSessions.get(session.getIdentifier());
        if (plugins != null) {
            for (Iterator it = plugins.iterator(); it.hasNext();) {
                ISessionPlugin plugin = (ISessionPlugin)it.next();
                IPluginDatabaseObjectType[] objTypes = plugin.getObjectTypes(session);
                if (objTypes != null) {
                    for (int i = 0; i < objTypes.length; ++i) {
                        objTypesList.add(objTypes[i]);
                    }
                }
            }
        }

        return (IPluginDatabaseObjectType[])objTypesList.toArray(new IPluginDatabaseObjectType[objTypesList.size()]);
    }

    /**
     * Load plugins. Load all plugin jars into class loader.
     */
    public void loadPlugins() {
        final Logger log = _app.getLogger();
        List pluginUrls = new ArrayList();
        File dir = new File(ApplicationFiles.SQUIRREL_PLUGINS_FOLDER);
        if (dir.isDirectory()) {
            String[] files = dir.list();
            for (int i = 0; i < files.length; ++i) {
                String fileName = files[i];
                if (fileName.toLowerCase().endsWith(".zip") ||
                        fileName.toLowerCase().endsWith(".jar")) {
                    String jarFileName = ApplicationFiles.SQUIRREL_PLUGINS_FOLDER +
                                            File.separator + fileName;
                    try {
                        pluginUrls.add(new File(jarFileName).toURL());
                    } catch (IOException ex) {
                        log.showMessage(Logger.ILogTypes.ERROR, "Unable to load plugin jar: " + jarFileName);
                        log.showMessage(Logger.ILogTypes.ERROR, ex);
                    }
                }
            }
        }

        URL[] urls = (URL[])pluginUrls.toArray(new URL[pluginUrls.size()]);
        _pluginsClassLoader = new MyURLClassLoader(urls);

        try {
            Class[] classes = _pluginsClassLoader.getAssignableClasses(IPlugin.class, _app.getLogger());
            for (int i = 0; i < classes.length; ++i) {
                try {
                    loadPlugin(classes[i]);
                } catch (Throwable th) {
                    log.showMessage(Logger.ILogTypes.ERROR, "\nError occured loading plugin class "
                                        + classes[i].getName());
                    log.showMessage(Logger.ILogTypes.ERROR, th);
                }
            }
        } catch (IOException ex) {
            log.showMessage(Logger.ILogTypes.ERROR, "\nError occured retrieving plugins. No plugins have been loaded.");
            log.showMessage(Logger.ILogTypes.ERROR, ex);
        }
    }

    /**
     * Initialize plugins.
     */
    public void initializePlugins() {
    	final boolean debug = _app.getSquirrelPreferences().isDebugMode();
        final Logger log = _app.getLogger();
        for (Iterator it = _loadedPlugins.values().iterator(); it.hasNext();) {
            IPlugin plugin = (IPlugin)it.next();
            try {
           		long now = System.currentTimeMillis();
                plugin.initialize();
               	log.showMessage(Logger.ILogTypes.STATUS, "Plugin " + plugin.getInternalName() +
               						" initialised in " + (System.currentTimeMillis() - now) + " ms.");
            } catch (Throwable th) {
                log.showMessage(Logger.ILogTypes.ERROR,
                    "Error ocured unloading plugin: " + plugin.getInternalName());
                log.showMessage(Logger.ILogTypes.ERROR, th);
            }
        }
    }

    private void loadPlugin(Class pluginClass) {
        final Logger log = _app.getLogger();
        PluginInfo wrapper = new PluginInfo(pluginClass.getName());
        try {
       		long now = System.currentTimeMillis();
            IPlugin plugin = (IPlugin)pluginClass.newInstance();
            wrapper.setPlugin(plugin);
            _plugins.add(wrapper);
            if (validatePlugin(plugin, log)) {
                plugin.load(_app);
                wrapper.setLoaded(true);
                _loadedPlugins.put(plugin.getInternalName(), plugin);
                if (ISessionPlugin.class.isAssignableFrom(pluginClass)) {
                    _sessionPlugins.add(plugin);
                }
	          	log.showMessage(Logger.ILogTypes.STATUS, "Plugin " + plugin.getInternalName()
									+ " loaded in " + (System.currentTimeMillis() - now) + " ms.");
            }
        } catch (Throwable th) {
            log.showMessage(Logger.ILogTypes.ERROR, "\nError occured loading class " +
                        pluginClass.getName() + " from plugin");
            log.showMessage(Logger.ILogTypes.ERROR, th);
        }
    }

    private boolean validatePlugin(IPlugin plugin, Logger log) {
        String pluginInternalName = plugin.getInternalName();
        if (pluginInternalName == null || pluginInternalName.trim().length() == 0) {
            log.showMessage(Logger.ILogTypes.ERROR,
                            "Plugin " + plugin.getClass().getName() +
                            "doesn't return a valid getInternalName()");
            return false;
        }

        if (_loadedPlugins.get(pluginInternalName) != null) {
            log.showMessage(Logger.ILogTypes.ERROR,
                            "A Plugin with the internal name " + pluginInternalName +
                            " has already been loaded");
            return false;
        }

        return true;
    }

}
