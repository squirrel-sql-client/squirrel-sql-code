package net.sourceforge.squirrel_sql.client.mainframe.action;
/*
 * Copyright (C) 2001 Colin Bell and Johan Compagner
 * colbell@users.sourceforge.net
 * jcompagner@j-com.nl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.Frame;
import java.sql.SQLException;

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Logger;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionFactory;
import net.sourceforge.squirrel_sql.client.session.SessionSheet;
import net.sourceforge.squirrel_sql.client.db.ConnectionSheet;
import net.sourceforge.squirrel_sql.client.db.ConnectionSheet.IConnectionSheetHandler;
import net.sourceforge.squirrel_sql.client.db.DataCache;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;

/**
 * This <CODE>ICommand</CODE> allows the user to connect to
 * an <TT>ISQLAlias</TT>.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ConnectToAliasCommand implements ICommand {
	/** Application API. */
	private IApplication _app;

	/** Owner of the connection internal frame. */
	private Frame _frame;

	/** The <TT>ISQLAlias</TT> to connect to. */
	private ISQLAlias _sqlAlias;
	
	/**
	 * Ctor.
	 *
	 * @param	app		The <TT>IApplication</TT> that defines app API.
	 * @param	frame	Owner of the connection internal frame.
	 * @param	alias	The <TT>ISQLAlias</TT> to connect to.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ISQLAlias</TT> passed.
	 */
	public ConnectToAliasCommand(IApplication app, Frame frame, ISQLAlias sqlAlias)
			throws IllegalArgumentException {
		super();
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (sqlAlias == null) {
			throw new IllegalArgumentException("Null ISQLAlias passed");
		}
		_app = app;
		_frame = frame;
		_sqlAlias = sqlAlias;
	}

	/**
	 * Display connection internal frame.
	 */
	public void execute() {
		ConnectionSheet sheet = new ConnectionSheet(_app, _frame, _sqlAlias,
											new SheetHandler(_app, _sqlAlias));
		_app.getMainFrame().addInternalFrame(sheet, true, null);
		GUIUtils.centerWithinDesktop(sheet);
		sheet.setVisible(true);
	}

	/**
	 * Handler used for connection internal frame actions.
	 */
	private static class SheetHandler implements IConnectionSheetHandler, Runnable {
		/** The connection internal frame. */
		private ConnectionSheet _connSheet;
		
		/** Application API. */
		private IApplication _app;

		/** <TT>ISQLAlias</TT> to connect to. */
		private ISQLAlias _alias;

		/** User name to use to connect to alias. */
		private String _user;

		/** Password to use to connect to alias. */
		private String _password;

		/** If <TT>true</TT> user has requested cancellation of the connection attempt. */
		private boolean _stopConnection;

		/**
		 * Ctor.
		 * 
		 * @param	app			Application API.
		 * @param	alias		Database alias to connect to.
		 * 
		 * @throws	IllegalArgumentException
		 * 			Thrown if <TT>null</TT>IApplication</TT>, or <TT>ISQLAlias</TT>
		 *			passed.
		 */
		SheetHandler(IApplication app, ISQLAlias alias)
				throws IllegalArgumentException {
			super();
			if (app == null) {
				throw new IllegalArgumentException("Null IApplication passed");
			}
			if (alias == null) {
				throw new IllegalArgumentException("Null ISQLAlias passed");
			}
			_app = app;
			_alias = alias;
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
								String password) {
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
		public void performCancelConnect(ConnectionSheet connSheet) {
			// if blocked that means that it doesn't help anymore
			// Or an error dialog is shown or de connection is made
			// and the SessionFrame is being constructed/shown.
			synchronized (this) {
				_stopConnection = true;
			}
		}

		/**
		 * User has clicked the Close button to close the internal frame.
		 * 
		 * @param	connSheet	Connection internal frame.
		 */
		public void performClose(ConnectionSheet connSheet) {
		}

		/**
		 * Execute task. Connect to the alias with the information entered
		 * in the connection internal frame.
		 */		
		public void run() {
			SQLConnection conn = null;
			final ISQLDriver sqlDriver = _app.getDataCache().getDriver(_alias.getDriverIdentifier());
			try {
				SQLDriverManager mgr = _app.getSQLDriverManager();
				conn = mgr.getConnection(sqlDriver, _alias, _user, _password);
				synchronized (this) {
					if (_stopConnection) {
						if (conn != null) {
							closeConnection(conn);
							conn = null;
						}
					} else {
						// After this it can't be stopped anymore!
						final ISession session = SessionFactory.createSession(_app, sqlDriver, _alias, conn);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								final SessionSheet child = new SessionSheet(session);
								session.setSessionSheet(child);
								IApplication app = session.getApplication();
								app.getPluginManager().sessionStarted(session);
								app.getMainFrame().addInternalFrame(child, true, null);
								child.setVisible(true);
								_connSheet.executed(true);
							}
						});
					}
				}
			} catch (BaseSQLException ex) {
				showErrorDialog("Unable to open SQL Connection:<br>" + ex.getMessage());
			} catch (ClassNotFoundException ex) {
				showErrorDialog("JDBC Driver class not found:<br>" + ex.getMessage());
				log(ex, "JDBC Driver class not found");
			} catch (NoClassDefFoundError ex) {
				showErrorDialog("JDBC Driver class not found:<br>" + ex.getMessage());
				log(ex, "JDBC Driver class not found");
			} catch (Throwable ex) {
				log(ex, "Unexpected Error occured attempting to open an SQL connection.");
				closeConnection(conn);
				showErrorDialog(ex);
			}
		}

		protected void showErrorDialog(final String msg) {
			synchronized (this) {
				//if(!_stopConnection) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							new ErrorDialog(_app.getMainFrame(), msg).show();
							_connSheet.executed(false);
						}
					});
				//}
			}
		}

		protected void showErrorDialog(final Throwable th) {
			synchronized (this) {
				//if(!_stopConnection) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							new ErrorDialog(_app.getMainFrame(), th).show();
							_connSheet.executed(false);
						}
					});
				//}
			}
		}

		protected void log(final Throwable th, final String msg) {
			synchronized (this) {
				//if(!_stopConnection) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							Logger logger = _app.getLogger();
							logger.showMessage(Logger.ILogTypes.ERROR, msg);
							logger.showMessage(Logger.ILogTypes.ERROR, th);
						}
					});
				//}
			}
		}

		private void closeConnection(SQLConnection conn) {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
					Logger logger = _app.getLogger();
					logger.showMessage(Logger.ILogTypes.ERROR, "Error occured closing Connection");
					logger.showMessage(Logger.ILogTypes.ERROR, ex);
				}
			}
		}
	}
}
