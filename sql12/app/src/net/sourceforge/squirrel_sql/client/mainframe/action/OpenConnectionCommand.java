package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2003 Colin Bell and Johan Compagner
 * colbell@users.sourceforge.net
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
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.sql.WrappedSQLException;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
/**
 * This <CODE>ICommand</CODE> allows the user to connect to
 * an <TT>ISQLAlias</TT>.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class OpenConnectionCommand implements ICommand
{
	/** Application API. */
	private IApplication _app;

	/** The <TT>ISQLAlias</TT> to connect to. */
	private ISQLAlias _sqlAlias;

	private final String _userName;
	private final String _password;
	private final SQLDriverPropertyCollection _props;

	private SQLConnection _conn;

	/**
	 * Ctor.
	 *
	 * @param	app			The <TT>IApplication</TT> that defines app API.
	 * @param	alias		The <TT>ISQLAlias</TT> to connect to.
	 * @param	userName	The user to connect as.
	 * @param	password	Password for userName.
	 * @param	props		Connection properties.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication</TT> or <TT>ISQLAlias</TT> passed.
	 */
	public OpenConnectionCommand(IApplication app, ISQLAlias sqlAlias,
									String userName, String password,
									SQLDriverPropertyCollection props)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (sqlAlias == null)
		{
			throw new IllegalArgumentException("Null ISQLAlias passed");
		}
		_app = app;
		_sqlAlias = sqlAlias;
		_userName = userName;
		_password = password;
		_props = props;
	}

	/**
	 * Display connection internal frame.
	 */
	public void execute() throws BaseException
	{
		_conn = null;
		final IIdentifier driverID = _sqlAlias.getDriverIdentifier();
		final ISQLDriver sqlDriver = _app.getDataCache().getDriver(driverID);
		final SQLDriverManager mgr = _app.getSQLDriverManager();
		try
		{
			_conn = mgr.getConnection(sqlDriver, _sqlAlias, _userName, _password, _props);
		}
		catch (SQLException ex)
		{
			throw new WrappedSQLException(ex);
		}
		catch (Throwable th)
		{
			throw new BaseException(th);
		}
	}

	/**
	 * Retrieve the newly opened connection.
	 *
	 * @return	The <TT>SQLConnection</T>.
	 */
	public SQLConnection getSQLConnection()
	{
		return _conn;
	}

}
