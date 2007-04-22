package net.sourceforge.squirrel_sql.client.plugin;
/* TODO: Delete this class
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

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * Describes a type of object in the database.
 */
public interface IPluginDatabaseObjectType {
	/**
	 * Return the descriptive name (E.G. TRIGGER) for this
	 * object type. This is the name that will appear in the
	 * object tree.
	 *
	 * @return  the descriptive name for this object type.
	 */
	String getName();

	//?? Need to associate a panel with an object type somehow.
	IPluginDatabaseObjectPanelWrapper createPanel();

	/**
	 * Return all the objects in the database for the current type.
	 */
	public IPluginDatabaseObject[] getObjects(ISession session, ISQLConnection conn, Statement stmt)
			throws SQLException;
}