package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2002 Johan Compagner
 * jcompagner@j-com.nl
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
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IClientSession;

/**
 * @version 	$Id: DropTablesCommand.java,v 1.3 2002-08-15 08:40:34 colbell Exp $
 * @author		Johan Compagner
 */
public class DropTablesCommand implements ICommand
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(DropTablesCommand.class);

	/** Current session. */
	private final IClientSession _session;

	/** Tables to be deleted. */
	private final IDatabaseObjectInfo[] _tables;

	/**
	 * Ctor.
	 *
	 * @param	session		Current session..
	 * @param	tables		Array of <TT>IDatabaseObjectInfo</TT> objects
	 * 						representing the tables to be deleted.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public DropTablesCommand(IClientSession session, IDatabaseObjectInfo[] tables)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("IClientSession == null");
		}
		if (tables == null)
		{
			throw new IllegalArgumentException("Tables array is null");
		}

		_session = session;
		_tables = tables;
	}

	/**
	 * Drop selected tables in the object tree.
	 */
	public void execute()
	{
		final char sepChar = _session.getProperties().getSQLStatementSeparatorChar();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < _tables.length; i++)
		{
			buf.append("drop table ")
				.append(_tables[i].getQualifiedName())
				.append(sepChar)
				.append('\n');
		}
		IPlugin plugin = _session.getApplication().getDummyAppPlugin();
		_session.getSQLPanelAPI(plugin).executeSQL(buf.toString());
	}
}