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
   private static final long serialVersionUID = 1L;

   /** Whether or not to enable connection keep alives */
   private boolean enableConnectionKeepAlive = false;
   
   /** time between executing the keep alive sql statement;  Default = 2 minutes */
   private int keepAliveSleepTimeSeconds = 120;
   
   /** the statement to execute to keep the connection alive */
   private String keepAliveSqlStatement = "";
      
   /**
	 * @return the isEnableConnectionKeepAlive
	 */
	public boolean isEnableConnectionKeepAlive()
	{
		return enableConnectionKeepAlive;
	}

	/**
	 * @param enableConnectionKeepAlive the enableConnectionKeepAlive to set
	 */
	public void setEnableConnectionKeepAlive(boolean enableConnectionKeepAlive)
	{
		this.enableConnectionKeepAlive = enableConnectionKeepAlive;
	}

	/**
	 * @return the keepAliveSleepTimeSeconds
	 */
	public int getKeepAliveSleepTimeSeconds()
	{
		return keepAliveSleepTimeSeconds;
	}

	/**
	 * @param keepAliveSleepTimeMillis the keepAliveSleepTimeSeconds to set
	 */
	public void setKeepAliveSleepTimeSeconds(int keepAliveSleepTimeSeconds)
	{
		this.keepAliveSleepTimeSeconds = keepAliveSleepTimeSeconds;
	}

	/**
	 * @return the keepAliveSqlStatement
	 */
	public String getKeepAliveSqlStatement()
	{
		return keepAliveSqlStatement;
	}

	/**
	 * @param keepAliveSqlStatement the keepAliveSqlStatement to set
	 */
	public void setKeepAliveSqlStatement(String keepAliveSqlStatement)
	{
		this.keepAliveSqlStatement = keepAliveSqlStatement;
	}   
   
}
