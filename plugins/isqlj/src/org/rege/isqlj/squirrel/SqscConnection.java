package org.rege.isqlj.squirrel;

/**
* <p>Title: sqsc-isqlj</p>
* <p>Description: SquirrelSQL plugin for iSqlJ</p>
* <p>Copyright: Copyright (c) 2003 Stathis Alexopoulos</p>
* @author Stathis Alexopoulos stathis@rege.org
* <br>
* <br>
* <p>
*    This file is part of sqsc-isqlj.
* </p>
* <br>
* <p>
*    sqsc-isqlj is free software; you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*    Foobar is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with Foobar; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
* </p>
*/

import java.sql.*;

import net.sourceforge.squirrel_sql.client.session.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.*;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;

import org.rege.isqlj.*;

public class SqscConnection
		implements SqlConnection
{
	private ISQLConnection squirrelCon = null;
	private Database db = null;
	private ISession session = null;
	private IPlugin plugin = null;

 
    public SqscConnection( ISession session, IPlugin plugin)
    {
		if( session == null)
		{
			throw new NullPointerException( "Null Session provided.");
		}
		if( plugin == null)
		{
			throw new NullPointerException( "Null Plugin provided.");
		}
		this.session = session;
		this.plugin = plugin;
		this.squirrelCon = session.getSQLConnection();
		db = new Database( getConnection());
    }

	public Connection getConnection()
	{
		return squirrelCon.getConnection();
	}

	public Database getDatabase()
	{
		return this.db;
	}


	public ResultSet executeQuery( String sql) throws SQLException
	{
		session.getSessionInternalFrame().getSQLPanelAPI().executeSQL( sql);
		return null;
	}

	public int executeUpdate( String sql) 
			throws SQLException
	{
		session.getSessionInternalFrame().getSQLPanelAPI().executeSQL( sql);
		return 0;
	}

}

