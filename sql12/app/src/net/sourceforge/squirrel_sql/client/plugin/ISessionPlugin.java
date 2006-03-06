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
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;
/**
 * Base interface for all plugins associated with a session.
 */
public interface ISessionPlugin extends IPlugin
{
	/**
	 * A new session has been created. At this point the
	 * <TT>SessionPanel</TT> does not exist for the new session.
	 *
	 * @param	session	 The new session.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	void sessionCreated(ISession session);

   /**
    * Tells the PluginManager if this Plugins sessionStarted() method
    * can be called in background
    * @see ISessionPlugin.sessionStarted(ISession)
    */
   boolean allowsSessionStartedInBackground();

   /**
	 * Called when a session started.
	 *
	 * @param	session	The session that is starting.
	 *
	 * @return  An implementation of PluginSessionCallback that requires the plugin to
    * adequately work with multible session windows.
    * Returning null tells the that the plugin is not applicable to this Session.
    *
    * @see ISessionPlugin.sessionStarted(ISession)
    *
	 */
	PluginSessionCallback sessionStarted(ISession session);

	/**
	 * Called when a session shutdown.
	 */
	void sessionEnding(ISession session);

	/**
	 * Create panels for the Session Properties dialog.
	 *
	 * @param	session	The session that will be displayed in the properties dialog.
	 *
	 * @return	Array of <TT>ISessionPropertiesPanel</TT> objects. Return
	 *			empty array of <TT>null</TT> if this plugin doesn't require
	 *			any panels in the Session Properties Dialog.
	 */
	ISessionPropertiesPanel[] getSessionPropertiesPanels(ISession session);

	/**
	 * Create panels for the Main Tabbed Pane.
	 *
	 * @param	session		The current session.
	 *
	 * @return	Array of <TT>IMainPanelTab</TT> objects. Return
	 *			empty array of <TT>null</TT> if this plugin doesn't require
	 *			any panels in the Main Tabbed Pane.
	 */
	//IMainPanelTab[] getMainTabbedPanePanels(ISession session);

	/**
	 * Let app know what extra types of objects in object tree that
	 * plugin can handle.
	 */
	IPluginDatabaseObjectType[] getObjectTypes(ISession session);

	/**
	 * Return a node expander for the object tree for a particular default node type.
	 * <p> A plugin could return non null here if they wish to override the default node
	 * expander bahaviour. Most plugins should return null here.
	 * <p>
	 * An example of this methods use is the oracle plugin. This plugin
	 * utilises this behaviour so that the procedure
	 * node does not list functions or procedures withint packages. This is different
	 * to the default bahaviour
	 * @return
	 */
	INodeExpander getDefaultNodeExpander(ISession session, DatabaseObjectType type);
}
