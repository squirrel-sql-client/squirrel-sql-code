package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2002-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This command will set the default catalog for a session.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SetDefaultCatalogCommand implements ICommand
{
	/** Current session. */
	private final ISession _session;

	/** Catalog. */
	private final String _catalog;

	/**
	 * Ctor.
	 *
	 * @param	session		Current session..
	 * @param	catalog		Name of catalog to become the defautl catalog.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISession</TT> or <TT>catalog</TT> passed.
	 */
	public SetDefaultCatalogCommand(ISession session, String catalog)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (catalog == null)
		{
			throw new IllegalArgumentException("Catalog == null");
		}

		_session = session;
		_catalog = catalog;
	}

	public void execute() throws BaseException
	{
		try
		{
			_session.getSQLConnection().setCatalog(_catalog);
		}
		catch (SQLException ex)
		{
			throw new BaseException(ex);
		}
	}
}
