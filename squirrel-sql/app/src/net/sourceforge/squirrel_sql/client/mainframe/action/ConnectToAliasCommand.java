package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001-2002 Colin Bell and Johan Compagner
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
import java.awt.Frame;
import java.sql.SQLException;

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.db.ConnectionSheet;
import net.sourceforge.squirrel_sql.client.db.ConnectionSheet.IConnectionSheetHandler;
import net.sourceforge.squirrel_sql.client.session.IClientSession;
import net.sourceforge.squirrel_sql.client.session.SessionFactory;
import net.sourceforge.squirrel_sql.client.session.SessionSheet;

/**
 * This <CODE>ICommand</CODE> allows the user to connect to
 * an <TT>ISQLAlias</TT>.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ConnectToAliasCommand implements ICommand
{
	public interface ICompletionCallback
	{
		void connected(SQLConnection conn);
		void sessionCreated(IClientSession session);
		void errorOccured(Throwable th);
	}

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(ConnectToAliasCommand.class);

	/** Application API. */
	private IApplication _app;

	/** Owner of the connection internal frame. */
	private Frame _frame;

	/** The <TT>ISQLAlias</TT> to connect to. */
	private ISQLAlias _sqlAlias;

	/** If <TT>true</TT> a session is to be created as well as connecting to database. */
	private boolean _createSession;

	/** Callback to notify client on the progress of this command. */
	private ICompletionCallback _callback;

	/**
	 * Ctor. This ctor will create a new session as well as opening a connection.
	 *
	 * @param	app		The <TT>IApplication</TT> that defines app API.
	 * @param	frame	Owner of the connection internal frame.
	 * @param	alias	The <TT>ISQLAlias</TT> to connect to.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication</TT> or <TT>ISQLAlias</TT> passed.
	 */
	public ConnectToAliasCommand(IApplication app, Frame frame,
									ISQLAlias sqlAlias)
	{
		this(app, frame, sqlAlias, true, null);
	}

	/**
	 * Ctor.
	 *
	 * @param	app				The <TT>IApplication</TT> that defines app API.
	 * @param	frame			Owner of the connection internal frame.
	 * @param	alias			The <TT>ISQLAlias</TT> to connect to.
	 * @param	createSession	If <TT>true</TT> then create a session as well as connecting
	 * 							to the database.
	 * @param	callback		Callback for client code to be informed of the progress of this command.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication</TT> or <TT>ISQLAlias</TT> passed.
	 */
	public ConnectToAliasCommand(IApplication app, Frame frame, ISQLAlias sqlAlias,
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
		_frame = frame;
		_sqlAlias = sqlAlias;
		_createSession = createSession;
		_callback = callback != null ? callback : new ClientCallback(app);
	}

	/**
	 * Display connection internal frame.
	 */
	public void execute()
	{
		try
		{
			SheetHandler hdl = new SheetHandler(_app, _sqlAlias, _createSession, _callback);
			ConnectionSheet sheet = new ConnectionSheet(_app, /*_frame,*/
			_sqlAlias, hdl);
			_app.getMainFrame().addInternalFrame(sheet, true, null);
			GUIUtils.centerWithinDesktop(sheet);
			sheet.moveToFront();
			sheet.setVisible(true);
		}
		catch (Exception ex)
		{
			_app.showErrorDialog(ex);
		}
	}

	public static class ClientCallback implements ICompletionCallback
	{
		private IApplication _app;
		public ClientCallback(IApplication app)
		{
			super();
			if (app == null)
			{
				throw new IllegalArgumentException("IApplication == null");
			}
			_app = app;
		}

		/**
		 * @see CompletionCallback#connected(SQLConnection)
		 */
		public void connected(SQLConnection conn)
		{
		}

		/**
		 * @see CompletionCallback#sessionCreated(ISession)
		 */
		public void sessionCreated(IClientSession session)
		{
		}

		/**
		 * @see CompletionCallback#errorOccured(Throwable)
		 */
		public void errorOccured(Throwable th)
		{
			if (th instanceof SQLException)
			{
				String msg = "Unable to open SQL Connection";
				showErrorDialog(msg, th);
			}
			else if (th instanceof ClassNotFoundException)
			{
				String msg = "JDBC Driver class not found";
				showErrorDialog(msg, th);
			}
			else if (th instanceof NoClassDefFoundError)
			{
				String msg = "JDBC Driver class not found";
				s_log.error("JDBC Driver class not found", th);
				showErrorDialog(msg, th);
			}
			else
			{
				String msg = "Unexpected Error occured attempting to open an SQL connection.";
				s_log.error(msg, th);
				showErrorDialog(msg, th);
			}
		}

		protected IApplication getApplication()
		{
			return _app;
		}

		protected void showErrorDialog(final String msg, final Throwable th)
		{
			synchronized (this)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						new ErrorDialog(_app.getMainFrame(), msg, th).show();
					}
				});
			}
		}
	}

	/**
	 * Handler used for connection internal frame actions.
	 */
	private static class SheetHandler implements IConnectionSheetHandler, Runnable
	{
		/** The connection internal frame. */
		private ConnectionSheet _connSheet;

		/** Application API. */
		private IApplication _app;

		/** <TT>ISQLAlias</TT> to connect to. */
		private ISQLAlias _alias;

		/** If <TT>true</TT> a session is to be created as well as connecting to database. */
		private boolean _createSession;

		/** User name to use to connect to alias. */
		private String _user;

		/** Password to use to connect to alias. */
		private String _password;

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
		private SheetHandler(IApplication app, ISQLAlias alias, boolean createSession,
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
			if (alias == null)
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
		 */
		public void performOK(ConnectionSheet connSheet, String user,
								String password)
		{
			_stopConnection = false;
			_connSheet = connSheet;
			_user = user;
			_password = password;
			_app.getThreadPool().addTask(this);
		}

		/**
		 * User has clicked the Cancel button to cancel this connection attempt.
		 *
		 * @param	connSheet	Connection internal frame.
		 */
		public void performCancelConnect(ConnectionSheet connSheet)
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
		public void performClose(ConnectionSheet connSheet)
		{
		}

		/**
		 * Execute task. Connect to the alias with the information entered
		 * in the connection internal frame.
		 */
		public void run()
		{
			SQLConnection conn = null;
			final IIdentifier driverID = _alias.getDriverIdentifier();
			final ISQLDriver sqlDriver = _app.getDataCache().getDriver(driverID);

//			final Thread curThread = Thread.currentThread();
//			final SQLDriverManager mgr = _app.getSQLDriverManager();

			try
			{
				OpenConnectionCommand cmd = new OpenConnectionCommand(_app, _alias, _user, _password);
				cmd.execute();
				conn = cmd.getSQLConnection();
				synchronized (this)
				{
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
							final IClientSession session = SessionFactory.createSession(
												_app, sqlDriver, _alias, conn,
												_user, _password);
							_callback.sessionCreated(session);
							Runner runner = new Runner(session, _connSheet);
							SwingUtilities.invokeLater(runner);
						}
						else
						{
							_connSheet.executed(true);
						}
					}
				}
			}
			catch (Throwable ex)
			{
				_connSheet.executed(false);
				_callback.errorOccured(ex);
			}
			finally
			{
			}
		}

		private void closeConnection(SQLConnection conn)
		{
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (SQLException ex)
				{
					s_log.error("Error occured closing Connection", ex);
				}
			}
		}
	}

	private static final class Runner implements Runnable
	{
		private final IClientSession _session;
		private final ConnectionSheet _connSheet;
	
		Runner(IClientSession session, ConnectionSheet connSheet)
		{
			super();
			_session = session;
			_connSheet = connSheet;
		}

		public void run()
		{
			final IApplication app = _session.getApplication();
			app.getPluginManager().sessionCreated(_session);
			final SessionSheet child = new SessionSheet(_session);
			_session.setSessionSheet(child);
			app.getPluginManager().sessionStarted(_session);
			app.getMainFrame().addInternalFrame(child, true, null);
			child.setVisible(true);
			_connSheet.executed(true);
		}
	}
}
