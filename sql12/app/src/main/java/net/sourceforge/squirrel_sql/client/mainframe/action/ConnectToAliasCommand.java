package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2004 Colin Bell and Johan Compagner
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

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectToAliasCallBack;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.ICompletionCallback;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This <CODE>ICommand</CODE> allows the user to connect to
 * an <TT>ISQLAlias</TT>.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ConnectToAliasCommand implements ICommand
{

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ConnectToAliasCommand.class);

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(ConnectToAliasCommand.class);

	/** Application API. */
	private IApplication _app;

	/** The <TT>ISQLAlias</TT> to connect to. */
	private SQLAlias _sqlAlias;

	/** If <TT>true</TT> a session is to be created as well as connecting to database. */
	private boolean _createSession;

	/** Callback to notify client on the progress of this command. */
	private ICompletionCallback _callback;

	/**
	 * Ctor. This ctor will create a new session as well as opening a connection.
	 *
	 * @param	app		The <TT>IApplication</TT> that defines app API.
	 * @param	alias	The <TT>ISQLAlias</TT> to connect to.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication</TT> or
	 *			<TT>ISQLAlias</TT> passed.
	 */
	public ConnectToAliasCommand(IApplication app, SQLAlias sqlAlias)
	{
		this(app, sqlAlias, true, null);
	}

	/**
	 * Ctor.
	 *
	 * @param	app				The <TT>IApplication</TT> that defines app API.
	 * @param	alias			The <TT>ISQLAlias</TT> to connect to.
	 * @param	createSession	If <TT>true</TT> then create a session as well
	 *							as connecting to the database.
	 * @param	callback		Callback for client code to be informed of the
	 *							progress of this command.
	 *
	 *			Thrown if a <TT>null</TT> <TT>IApplication</TT> or
	 *			<TT>ISQLAlias</TT> passed.
	 */
	public ConnectToAliasCommand(IApplication app, SQLAlias sqlAlias,
						boolean createSession, ICompletionCallback callback)
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
		_createSession = createSession;
		_callback = callback != null ? callback : new ConnectToAliasCallBack(app, _sqlAlias);
	}

	/**
	 * Display connection internal frame.
	 */
	public void execute()
	{
		try
		{
			final SheetHandler hdl = new SheetHandler(_app, _sqlAlias, _createSession, _callback);
			
            if (SwingUtilities.isEventDispatchThread()) {
                createConnectionInternalFrame(hdl);
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        createConnectionInternalFrame(hdl);
                    }
                });
            }
		}
		catch (Exception ex)
		{
			_app.showErrorDialog(ex);
		}
	}

    private void createConnectionInternalFrame(SheetHandler hdl) {
        ConnectionInternalFrame sheet = 
            new ConnectionInternalFrame(_app, _sqlAlias, hdl);                        
        _app.getMainFrame().addWidget(sheet);
        DialogWidget.centerWithinDesktop(sheet);
        sheet.moveToFront();
    }
    

	/**
	 * Handler used for connection internal frame actions.
	 */
	private static class SheetHandler implements ConnectionInternalFrame.IHandler
	{
		/** The connection internal frame. */
		private ConnectionInternalFrame _connSheet;

		/** Application API. */
		private IApplication _app;

		/** <TT>ISQLAlias</TT> to connect to. */
		private SQLAlias _alias;

		/** If <TT>true</TT> a session is to be created as well as connecting to database. */
		private boolean _createSession;

		/** User name to use to connect to alias. */
		private String _user;

		/** Password to use to connect to alias. */
		private String _password;

		/** Connection properties. */
		private SQLDriverPropertyCollection _props;

		/** If <TT>true</TT> user has requested cancellation of the connection attempt. */
		private boolean _stopConnection;

		/** Callback to notify client on the progress of this command. */
		private ICompletionCallback _callback;

		/**
		 * Ctor.
		 *
		 * @param	app				Application API.
		 * @param	alias			Database alias to connect to.
		 * @param	createSession	If <TT>true</TT> a session should be created.
		 * @param	cmd				Command executing this handler.
		 *
		 * @throws	IllegalArgumentException
		 * 			Thrown if <TT>null</TT>IApplication</TT>, <TT>ISQLAlias</TT>,
		 * 			or <TT>ICompletionCallback</TT> passed.
		 */
		private SheetHandler(IApplication app, SQLAlias alias, boolean createSession,
									ICompletionCallback callback)
		{
			super();
			if (app == null)
			{
				throw new IllegalArgumentException("IApplication == null");
			}
			if (alias == null)
			{
				throw new IllegalArgumentException("ISQLAlias == null");
			}
			if (callback == null)
			{
				throw new IllegalArgumentException("ICompletionCallback == null");
			}
			_app = app;
			_alias = alias;
			_createSession = createSession;
			_callback = callback;
		}

		/**
		 * User has clicked the OK button to connect to the alias. Run the connection
		 * attempt in a separate thread.
		 *
		 * @param	connSheet	Connection internal frame.
		 * @param	user		The user name entered.
		 * @param	password	The password entered.
		 * @param	props		Connection properties.
		 */
		public void performOK(ConnectionInternalFrame connSheet, String user,
								String password, SQLDriverPropertyCollection props)
		{
			_stopConnection = false;
			_connSheet = connSheet;
			_user = user;
			_password = password;
			_props = props;
         doConnect();
		}

		/**
		 * User has clicked the Cancel button to cancel this connection attempt.
		 *
		 * @param	connSheet	Connection internal frame.
		 */
		public void performCancelConnect(ConnectionInternalFrame connSheet)
		{
			// if blocked that means that it doesn't help anymore
			// Or an error dialog is shown or the connection is made
			// and the SessionFrame is being constructed/shown.
			synchronized (this)
			{
				_stopConnection = true;
			}
		}

		/**
		 * User has clicked the Close button to close the internal frame.
		 *
		 * @param	connSheet	Connection internal frame.
		 */
		public void performClose(ConnectionInternalFrame connSheet)
		{
			// Empty.
		}

		/**
		 * Execute task. Connect to the alias with the information entered
		 * in the connection internal frame.
		 */
      public void doConnect()
      {
         final IIdentifier driverID = _alias.getDriverIdentifier();
         final ISQLDriver sqlDriver = _app.getDataCache().getDriver(driverID);

         try
         {
            final OpenConnectionCommand cmd = new OpenConnectionCommand(_app, _alias, _user, _password, _props);

            cmd.execute(new OpenConnectionCommandListener(){
               @Override
               public void openConnectionFinished(Throwable t)
               {
                  afterExecuteFinished(sqlDriver, cmd, t);
               }
            });

         }
         catch (Throwable ex)
         {
            _connSheet.executed(false);
            _callback.errorOccured(ex, _stopConnection);
         }
      }

      private void afterExecuteFinished(ISQLDriver sqlDriver, OpenConnectionCommand cmd, Throwable t)
      {
         try
         {
            if(null != t)
            {
               throw t;
            }


            SQLConnection conn;
            if (_alias.isAutoLogon())
            {
               // If the user checked Auto Logon but gave wrong username/password
               // in the Alias definition. He will be asked to enter username/password again in an extra dialog.
               // Here for convenience we transfer these data back into the Alias.
               // Note: Don't do this when Auto Logon is false.
               _alias.setUserName(_user);
               _alias.setPassword(_password);
            }


            conn = cmd.getSQLConnection();
            if (_stopConnection)
            {
               if (conn != null)
               {
                  closeConnection(conn);
                  conn = null;
               }
            }
            else
            {
               // After this it can't be stopped anymore!
               _callback.connected(conn);
               if (_createSession)
               {
                  createSession(sqlDriver, conn);
               }
               else
               {
                  _connSheet.executed(true);
               }
            }
         }
         catch (Throwable th)
         {
            _connSheet.executed(false);
            _callback.errorOccured(th, _stopConnection);
         }
      }

      private void closeConnection(ISQLConnection conn)
		{
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (SQLException ex)
				{
                    // i18n[ConnectToAliasCommand.error.closeconnection=Error occured closing Connection]
					s_log.error(s_stringMgr.getString("ConnectToAliasCommand.error.closeconnection"), ex);
				}
			}
		}

		private ISession createSession(ISQLDriver sqlDriver,
												SQLConnection conn)
		{
			SessionManager sm = _app.getSessionManager();
			final ISession session = sm.createSession(_app, sqlDriver,
												_alias, conn, _user, _password);
			_callback.sessionCreated(session);
			SwingUtilities.invokeLater(new Runner(session, _connSheet));
			return session;
		}
	}

	private static final class Runner implements Runnable
	{
		private final ISession _session;
		private final ConnectionInternalFrame _connSheet;

		Runner(ISession session, ConnectionInternalFrame connSheet)
		{
			super();
			_session = session;
			_connSheet = connSheet;
		}

		public void run()
		{
			final IApplication app = _session.getApplication();
			try
			{
				app.getPluginManager().sessionCreated(_session);
				app.getWindowManager().createInternalFrame(_session);
                _connSheet.executed(true);
			}
			catch (Throwable th)
			{
				app.showErrorDialog(s_stringMgr.getString("ConnectToAliasCommand.error.opensession"), th);
			}
		}
	}
}
