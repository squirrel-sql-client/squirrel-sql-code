package net.sourceforge.squirrel_sql.client.session.event;
/*
 * Copyright (C) 2003-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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
import java.util.EventObject;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
/**
 * This class is an event fired for SQLPanel events.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLPanelEvent extends EventObject
{
	/** The session. */
	private final ISession _session;

	/**
	 * Ctor.
	 *
	 * @param	session	The session this is occuring in.
	 * @param	source	The <CODE>SQLPanel</CODE> that change has happened to.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> or <TT>SQLPanel/TT>
	 *			passed.
	 */
	public SQLPanelEvent(ISession session, SQLPanel source)
	{
		super(checkParams(session, source));
		_session = session;
	}

	/**
	 * Return the <CODE>ISession</CODE>.
	 */
	public ISession getSession()
	{
		return _session;
	}

	/**
	 * Retrieve the SQL panel that thsi event is associated with.
	 *
	 * @return	The SQL panel this event is associated with.
	 */
	public ISQLPanelAPI getSQLPanel()
	{
		return ((SQLPanel)getSource()).getSQLPanelAPI();
	}

	private static SQLPanel checkParams(ISession session, SQLPanel source)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (source == null)
		{
			throw new IllegalArgumentException("SQLPanel == null");
		}
		return source;
	}
}