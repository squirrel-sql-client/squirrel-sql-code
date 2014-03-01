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
import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public abstract class DefaultSessionPlugin extends DefaultPlugin implements ISessionPlugin
{
	/** Subclasses that create a session JMenu will set this */
	private JMenu sessionMenu = null;

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(DefaultSessionPlugin.class);

	/**
	 * A new session has been created. At this point the <TT>SessionPanel</TT> does not exist for the new
	 * session.
	 * 
	 * @param session
	 *           The new session.
	 * @throws IllegalArgumentException
	 *            Thrown if a <TT>null</TT> ISession</TT> passed.
	 */
	public void sessionCreated(ISession session)
	{
		// Empty body.
	}

	public boolean allowsSessionStartedInBackground()
	{
		return false;
	}

	/**
	 * Called when a session shutdown.
	 * 
	 * @param session
	 *           The session that is ending.
	 */
	public void sessionEnding(ISession session)
	{
		// Empty body.
	}

	/**
	 * Override this to create panels for the Session Properties dialog.
	 * 
	 * @param session
	 *           The session that will be displayed in the properties dialog.
	 * @return <TT>null</TT> to indicate that this plugin doesn't use session property panels.
	 */
	public ISessionPropertiesPanel[] getSessionPropertiesPanels(ISession session)
	{
		return null;
	}

	/**
	 * Let app know what extra types of objects in object tree that plugin can handle.
	 */
	public IPluginDatabaseObjectType[] getObjectTypes(ISession session)
	{
		return null;
	}

	/**
	 * Return a node expander for the object tree for a particular default node type. A plugin could return non
	 * null here if they wish to override the default node expander bahaviour. Most plugins should return null
	 * here.
	 */
	public INodeExpander getDefaultNodeExpander(ISession session, DatabaseObjectType type)
	{
		return null;
	}

	/**
	 * This should be overridden by all databases-specific subclasses so that registerSessionMenu will work
	 * correctly. It should return true when the plugin subclass is db-specific and the specified session is
	 * for a database that the plugin supports; false is returned otherwise.
	 */
	protected boolean isPluginSession(ISession session)
	{
		if (s_log.isDebugEnabled() && sessionMenu != null)
		{
			s_log.debug("The default isPluginSession() impl was called for session \""
				+ session.getAlias().getName() + "\", but sessionMenu (" + sessionMenu.getText()
				+ ")is not null - this is probably a bug.");
		}
		return true;
	}

	/**
	 * Plugin sub-classes call this to register their session JMenu with this class so that this class can
	 * manage it's enabled state, as sessions become activated.
	 * 
	 * @param menu
	 *           the plugin session menu
	 */
	protected void registerSessionMenu(JMenu menu)
	{
		if (menu == null)
		{
			throw new IllegalArgumentException("menu cannot be null");
		}
		sessionMenu = menu;
		_app.getSessionManager().addSessionListener(new SessionListener());
	}

	/**
	 * A session listener that is used to determine when sessions are activated and to trigger the enable menu
	 * task if the session is relevant to this plugin.
	 */
	private class SessionListener extends SessionAdapter
	{
		public void sessionActivated(SessionEvent evt)
		{
         final boolean enable = isPluginSession(evt.getSession());
         sessionMenu.setEnabled(enable);
		}
	}
}
