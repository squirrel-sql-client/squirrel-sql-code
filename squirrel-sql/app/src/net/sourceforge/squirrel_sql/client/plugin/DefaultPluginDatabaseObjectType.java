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
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * Describes a type of object in the database.
 */
public class DefaultPluginDatabaseObjectType implements IPluginDatabaseObjectType {
	private final String _name;

	public DefaultPluginDatabaseObjectType(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Null or empty object type name");
		}
		_name = name;
	}

	/** Name. E.G. Trigger */
	public String getName() {
		return _name;
	}

	/**
	 * Return all the objects in the database for the passed type.
	 */
	public IPluginDatabaseObject[] getObjects(ISession session, SQLConnection conn, Statement stmt)
			throws SQLException {
		return null;
	}

	//?? Need to associate a panel with an object type somehow.
	public IPluginDatabaseObjectPanelWrapper createPanel() {
		return null;
	}

	/**
	 * SHOULD be overridden by Plugin
	 */
	public boolean equals(Object obj)
	{
		if(obj instanceof DefaultPluginDatabaseObjectType)
		{
			String sName = ((DefaultPluginDatabaseObjectType)obj)._name;
			return ( (sName == null && _name == null) ||
					((sName != null && _name != null) && sName.equals(_name)) );
		}
		return false;
	}
}