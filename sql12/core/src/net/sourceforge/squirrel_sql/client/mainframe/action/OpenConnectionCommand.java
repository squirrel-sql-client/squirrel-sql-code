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
import java.sql.DriverManager;
import java.util.concurrent.*;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.action.reconnect.ReconnectInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.*;

/**
 * This <CODE>ICommand</CODE> allows the user to connect to
 * an <TT>ISQLAlias</TT>.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class OpenConnectionCommand
{
	/** The <TT>ISQLAlias</TT> to connect to. */
	private ISQLAlias _sqlAlias;

	private final String _userName;
	private final String _password;
	private final SQLDriverPropertyCollection _props;

	private SQLConnection _conn;
   private ReconnectInfo _reconnectInfo;

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
	public OpenConnectionCommand(ISQLAlias sqlAlias, String userName, String password, SQLDriverPropertyCollection props)
	{
	   this(sqlAlias, userName, password, props, null);
   }

	public OpenConnectionCommand(ISQLAlias sqlAlias, String userName, String password, SQLDriverPropertyCollection props, ReconnectInfo reconnectInfo)
	{
      if (sqlAlias == null)
		{
			throw new IllegalArgumentException("Null ISQLAlias passed");
		}
		_sqlAlias = sqlAlias;
		_userName = userName;
		_password = password;
		_props = props;
      _reconnectInfo = reconnectInfo;
	}

	/**
	 * Display connection internal frame.
    * @param openConnectionCommandListener
    */
	public void execute(final OpenConnectionCommandListener openConnectionCommandListener)
   {
      ExecutorService executorService = Executors.newFixedThreadPool(2);

      Runnable taskConnect = () -> executeConnect();

      final Future future = executorService.submit(taskConnect);

      Runnable taskAwaitConnect = () -> awaitConnection(future, openConnectionCommandListener);

      executorService.submit(taskAwaitConnect);
	}

   public void executeAndWait()
   {
      executeConnect();
   }

   private void awaitConnection(Future future, final OpenConnectionCommandListener openConnectionCommandListener)
   {
      try
      {
         if (0 < DriverManager.getLoginTimeout())
         {
            future.get(DriverManager.getLoginTimeout(), TimeUnit.SECONDS);
         }
         else
         {
            future.get();
         }

         SwingUtilities.invokeLater(() -> openConnectionCommandListener.openConnectionFinished(null));
      }
      catch (final Throwable t)
      {
         SwingUtilities.invokeLater(() -> openConnectionCommandListener.openConnectionFinished(t));
      }
   }

   private void executeConnect()
   {
      _conn = null;
      final IIdentifier driverID = _sqlAlias.getDriverIdentifier();
      final ISQLDriver sqlDriver = Main.getApplication().getDataCache().getDriver(driverID);
      final SQLDriverManager mgr = Main.getApplication().getSQLDriverManager();
      try
      {
         _conn = mgr.getConnection(sqlDriver, _sqlAlias, _userName, _password, _props, _reconnectInfo);
      }
      catch (Throwable th)
      {
         throw Utilities.wrapRuntime(th);
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
