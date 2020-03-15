package net.sourceforge.squirrel_sql.client.gui.db;

/*
 * Copyright (C) 2009 Rob Manning
 * manningr@users.sourceforge.net
 * 
 * Based on initial work from Colin Bell
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

import java.io.Serializable;

public class SQLAliasConnectionProperties implements Serializable
{
   /** Whether or not to enable connection keep alives */
   private boolean _enableConnectionKeepAlive = false;
   
   /** time between executing the keep alive sql statement;  Default = 2 minutes */
   private int _keepAliveSleepTimeSeconds = 120;
   
   /** the statement to execute to keep the connection alive */
   private String _keepAliveSqlStatement = "";

	private SQLAliasVersioner _versioner = new SQLAliasVersioner();

	/**
	 * @return the isEnableConnectionKeepAlive
	 */
	public boolean isEnableConnectionKeepAlive()
	{
		return _enableConnectionKeepAlive;
	}

	/**
	 * @param enableConnectionKeepAlive the enableConnectionKeepAlive to set
	 */
	public void setEnableConnectionKeepAlive(boolean enableConnectionKeepAlive)
	{
		_versioner.trigger(_enableConnectionKeepAlive, enableConnectionKeepAlive);
		this._enableConnectionKeepAlive = enableConnectionKeepAlive;
	}

	/**
	 * @return the keepAliveSleepTimeSeconds
	 */
	public int getKeepAliveSleepTimeSeconds()
	{
		return _keepAliveSleepTimeSeconds;
	}

	/**
	 * @param keepAliveSleepTimeMillis the keepAliveSleepTimeSeconds to set
	 */
	public void setKeepAliveSleepTimeSeconds(int keepAliveSleepTimeSeconds)
	{
		_versioner.trigger(_keepAliveSleepTimeSeconds, keepAliveSleepTimeSeconds);
		this._keepAliveSleepTimeSeconds = keepAliveSleepTimeSeconds;
	}

	/**
	 * @return the keepAliveSqlStatement
	 */
	public String getKeepAliveSqlStatement()
	{
		return _keepAliveSqlStatement;
	}

	/**
	 * @param keepAliveSqlStatement the keepAliveSqlStatement to set
	 */
	public void setKeepAliveSqlStatement(String keepAliveSqlStatement)
	{
		_versioner.trigger(_keepAliveSqlStatement, keepAliveSqlStatement);
		this._keepAliveSqlStatement = keepAliveSqlStatement;
	}

	public void acceptAliasVersioner(SQLAliasVersioner versioner)
	{
		_versioner = versioner;
	}
}
