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

import net.sourceforge.squirrel_sql.client.session.connectionpool.SessionConnectionPool;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.Statement;

/**
 * This class will loop continuously, pausing for a configurable amount of time and executing a configurable
 * SQL statement against a given SQLConnection.
 */
public class SessionConnectionKeepAlive implements Runnable
{
   private static final ILogger s_log = LoggerController.createLogger(SessionConnectionKeepAlive.class);

   private final long _sleepMillis;
   private final SessionConnectionPool _sessionConnectionPool;
   private final String _sql;
   private volatile boolean _isStopped = false;
   private final String _aliasName;

   public SessionConnectionKeepAlive(SessionConnectionPool sessionConnectionPool, long sleepMillis, String sql, String aliasName)
   {
      if (sleepMillis < 1000)
      {
         throw new IllegalArgumentException("Sleep time must be at least 1000ms(1 second)");
      }
      this._sleepMillis = sleepMillis;
      Utilities.checkNull("SessionConnectionKeepAlive", "sessionConnectionPool", sessionConnectionPool, "sql", sql);
      _sessionConnectionPool = sessionConnectionPool;
      this._sql = sql;
      this._aliasName = aliasName;
   }

   public void setStopped(boolean isStopped)
   {
      this._isStopped = isStopped;
   }

   @Override
   public void run()
   {
      for(;;)
      {
         for (ISQLConnection con : _sessionConnectionPool.getAllSQLConnections())
         {
            if(_isStopped)
            {
               return;
            }

            Statement stmt = null;
            try
            {
               stmt = con.createStatement();
               s_log.info("SessionConnectionKeepAlive (" + _aliasName + ") running SQL: " + _sql);
               stmt.executeQuery(_sql);
            }
            catch (Throwable t)
            {
               s_log.error("run: unexpected exception while executing sql (" + _sql + "): " + t.getMessage(), t);
            }
            finally
            {
               SQLUtilities.closeStatement(stmt);
            }
            // Always sleep at the end of the loop. In case we are stopped, we want to know that
            // immediately before executing the sql statement.
            Utilities.sleep(_sleepMillis);
         }

      }
   }

}
