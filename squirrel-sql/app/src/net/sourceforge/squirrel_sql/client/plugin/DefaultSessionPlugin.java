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
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;

public abstract class DefaultSessionPlugin extends DefaultPlugin implements ISessionPlugin {
	/**
	 * A new session has been created. At this point the
	 * <TT>SessionSheet</TT> does not exist for the new session.
	 *
	 * @param   session	 The new session.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	public void sessionCreated(ISession session) {
	}

	/**
	 * Called when a session started.
	 *
	 * @param	session	The session that is starting.
	 *
	 * @return	<TT>true</TT> to indicate that this plugin is
	 *			applicable to passed session.
	 */
	public boolean sessionStarted(ISession session) {
		return true;
	}

	/**
	 * Called when a session shutdown.
	 *
	 * @param	session	The session that is ending.
	 */
	public void sessionEnding(ISession session) {
	}

	/**
	 * Override this to create panels for the Session Properties dialog.
	 *
	 * @param	session	The session that will be displayed in the properties dialog.
	 *
	 * @return  <TT>null</TT> to indicate that this plugin doesn't use session property panels.
	 */
	public ISessionPropertiesPanel[] getSessionPropertiesPanels(ISession session) {
		return null;
	}

	/**
	 * Create panels for the Main Tabbed Pane.
	 *
	 * @param	session		The current session.
	 *
	 * @return	Array of <TT>IMainPanelTab</TT> objects. Return
	 *			empty array of <TT>null</TT> if this plugin doesn't require
	 *			any panels in the Main Tabbed Pane.
	 */
	//public IMainPanelTab[] getMainTabbedPanePanels(ISession session) {
	//	return null;
	//}

	/**
	 * Let app know what extra types of objects in object tree that
	 * plugin can handle.
	 */
	public IPluginDatabaseObjectType[] getObjectTypes(ISession session) {
		return null;
	}
}

