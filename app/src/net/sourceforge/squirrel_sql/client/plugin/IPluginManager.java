/*
 * Copyright (C) 2008 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.plugin;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener;

public interface IPluginManager
{

	/**
	 * A new session has been created. At this point the <TT>SessionPanel</TT> does not exist for the new
	 * session.
	 * 
	 * @param session
	 *           The new session.
	 * @throws IllegalArgumentException
	 *            Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	public abstract void sessionCreated(ISession session);

	/**
	 * A new session is starting.
	 * 
	 * @param session
	 *           The new session.
	 * @throws IllegalArgumentException
	 *            Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	public abstract void sessionStarted(final ISession session);

	/**
	 * A session is ending.
	 * 
	 * @param session
	 *           The session ending.
	 * @throws IllegalArgumentException
	 *            Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	public abstract void sessionEnding(ISession session);

	public abstract void unloadPlugin(String pluginInternalName);

	/**
	 * Unload all plugins.
	 */
	public abstract void unloadPlugins();

	public abstract PluginInfo[] getPluginInformation();

	public abstract SessionPluginInfo[] getPluginInformation(ISession session);

	public abstract IPluginDatabaseObjectType[] getDatabaseObjectTypes(ISession session);

	/**
	 * Retrieve an array of all the <TT>URL</TT> objects that are used to find plugin classes.
	 * 
	 * @return <TT>URL[]</TT>.
	 */
	public abstract URL[] getPluginURLs();

	public abstract PluginStatus[] getPluginStatuses();

	public abstract void setPluginStatuses(PluginStatus[] values);

	/**
	 * Retrieve loaded session plugins
	 * 
	 * @return <TT>Iterator</TT> over a collection of <TT>ISessionPlugin</TT> objects.
	 */
	public abstract Iterator<SessionPluginInfo> getSessionPluginIterator();

	/**
	 * TODO: Clean this mess up!!!! Load plugins. Load all plugin jars into class loader.
	 */
	public abstract void loadPlugins();

	/**
	 * Initialize plugins.
	 */
	public abstract void initializePlugins();

	/**
	 * Sets the ClassLoaderListener to notify when archive files containing classes are loaded.
	 * 
	 * @param listener
	 *           a ClassLoaderListener implementation
	 */
	public abstract void setClassLoaderListener(ClassLoaderListener listener);

	/**
	 * Retrieve information about plugin load times
	 * 
	 * @return <TT>Iterator</TT> over a collection of <TT>PluginLoadInfo</TT> objects.
	 */
	public abstract Iterator<PluginLoadInfo> getPluginLoadInfoIterator();

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
	public abstract Object bindExternalPluginService(String internalNameOfPlugin, Class<?> toBindTo);

	public abstract IAliasPropertiesPanelController[] getAliasPropertiesPanelControllers(SQLAlias alias);

	public abstract void aliasCopied(SQLAlias source, SQLAlias target);

	public abstract void aliasRemoved(SQLAlias alias);

	/**
	 * Loads plugins from the current CLASSPATH using the specified list of plugin classes. Each member of the 
	 * list should be a fully qualified classname that is already in the CLASSPATH.
	 * 
	 * @param pluginList a list of fully qualified class names
	 */
	public abstract void loadPluginsFromList(List<String> pluginList);

}