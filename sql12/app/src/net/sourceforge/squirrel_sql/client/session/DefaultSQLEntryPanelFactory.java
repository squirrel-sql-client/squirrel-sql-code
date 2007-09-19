package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2003 Colin Bell
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

import java.util.Properties;
import java.util.HashMap;

/**
 * A factory that creates <TT>DefaultSQLEntrypanel</TT> objects.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DefaultSQLEntryPanelFactory implements ISQLEntryPanelFactory
{
	/**
	 * Create a new <TT>DefaultSQLEntrypanel</TT>.
	 *
	 * @param	session	The session that an SQL entry is to be created for.
	 *
	 * @param props
    * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public ISQLEntryPanel createSQLEntryPanel(ISession session, HashMap<String, Object> props)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		return new DefaultSQLEntryPanel(session);
	}
}
