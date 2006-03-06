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

public abstract class DefaultSessionPlugin extends DefaultPlugin
											implements ISessionPlugin
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
	 * @param	session	The session that is ending.
	 */
	public void sessionEnding(ISession session)
	{
		// Empty body.
	}

	/**
	 * Override this to create panels for the Session Properties dialog.
	 *
	 * @param	session	The session that will be displayed in the properties dialog.
	 *
	 * @return	<TT>null</TT> to indicate that this plugin doesn't use session property panels.
	 */
	public ISessionPropertiesPanel[] getSessionPropertiesPanels(ISession session)
	{
		return null;
	}

	/**
	 * Let app know what extra types of objects in object tree that
	 * plugin can handle.
	 */
	public IPluginDatabaseObjectType[] getObjectTypes(ISession session)
	{
		return null;
	}

	/**
	* Return a node expander for the object tree for a particular default node type.
	* A plugin could return non null here if they wish to override the default node
	* expander bahaviour. Most plugins should return null here.
	*/
	public INodeExpander getDefaultNodeExpander(ISession session, DatabaseObjectType type)
	{
		return null;
	}

}
