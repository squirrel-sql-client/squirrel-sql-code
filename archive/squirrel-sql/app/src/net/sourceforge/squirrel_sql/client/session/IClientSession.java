package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2002 Colin Bell
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

import javax.swing.Action;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.INodeExpander;
/**
 * The current session.
 */
public interface IClientSession extends ISession
{
	/**
	 * Close this session.
	 */
	void close();

	/**
	 * Close the current connection to the database.
	 *
	 * @throws	SQLException  if an SQL error occurs.
	 */
	void closeSQLConnection() throws SQLException;

	/**
	 * Set the session sheet for this session.
	 * 
	 * @param	sheet	Sheet for this session.
	 */
	void setSessionSheet(SessionSheet child);
}
