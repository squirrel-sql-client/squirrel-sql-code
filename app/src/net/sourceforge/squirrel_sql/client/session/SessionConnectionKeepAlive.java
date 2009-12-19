/*
 * Copyright (C) 2009 Rob Manning
 * manningr@users.sourceforge.net
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

package net.sourceforge.squirrel_sql.client.session;

import java.sql.Statement;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class will loop continuously, pausing for a configurable amount of time and executing a configurable
 * SQL statement against a given SQLConnection.
 */
public class SessionConnectionKeepAlive implements Runnable
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(SessionConnectionKeepAlive.class);

	private final long sleepMillis;

	private final ISQLConnection sqlConn;

	private final String sql;

	private volatile boolean isStopped = false;

	private final String aliasName;
	
	public SessionConnectionKeepAlive(ISQLConnection con, long sleepMillis, String sql, String aliasName)
	{
		if (sleepMillis < 1000) { 
			throw new IllegalArgumentException("Sleep time must be at least 1000ms(1 second)"); 
		}
		this.sleepMillis = sleepMillis;
		Utilities.checkNull("SessionConnectionKeepAlive", "con", con, "sql", sql);
		sqlConn = con;
		this.sql = sql;
		this.aliasName = aliasName;
	}

	public void setStopped(boolean isStopped)
	{
		this.isStopped = isStopped;
	}

	@Override
	public void run()
	{
		while (!isStopped)
		{
			Statement stmt = null;
			try
			{
				stmt = sqlConn.createStatement();
				if (s_log.isInfoEnabled()) {
					s_log.info("SessionConnectionKeepAlive ("+aliasName+") running SQL: "+sql);
				}
				stmt.executeQuery(sql);
			}
			catch (Throwable t)
			{
				s_log.error("run: unexpected exception while executing sql (" + sql + "): " + t.getMessage(), t);
			}
			finally
			{
				SQLUtilities.closeStatement(stmt);
			}
			// Always sleep at the end of the loop. In case we are stopped, we want to know that
			// immediately before executing the sql statement.
			Utilities.sleep(sleepMillis);
		}

	}

}
