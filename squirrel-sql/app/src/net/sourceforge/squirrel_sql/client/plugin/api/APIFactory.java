package net.sourceforge.squirrel_sql.client.plugin.api;

import net.sourceforge.squirrel_sql.client.session.IClientSession;
import net.sourceforge.squirrel_sql.client.session.ISession;

/*
 * Copyright (C) 2002 Colin Bell.
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

/**
 * This singleton class is a fatory for API objects.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class APIFactory
{
	/** Singleton instance of this class. */
	private static APIFactory s_instance = new APIFactory();

	/**
	 * Default ctor. Private as class is a singleton.
	 */
	private APIFactory()
	{
	}

	/**
	 * Return the API for the Object Tree.
	 * 
	 * @param	session		The sesion containing the object tree.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public IObjectTreeAPI getObjectTreeAPI(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		return new ObjectTreeAPI((IClientSession)session);
	}
}
