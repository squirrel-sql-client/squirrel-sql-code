package net.sourceforge.squirrel_sql.client.db;
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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.fw.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.fw.gui.OkClosePanelListener;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;

/**
 * This dialog allows the user to connect to an alias.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ConnectionDialog extends JDialog {
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		String ALIAS = "Alias:";
		String CANCELLING = "Cancelling connection attempt...";
		String CONNECT = "Connect";
		String CONNECTING = "Connecting...";
		String DRIVER = "Driver:";
		String PASSWORD = "Password:";
		String URL = "URL:";
		String USER = "User:";
	}

	/** Handler called for dialog actions. */
	public interface IConnectionDialogHandler {
		/**
		 * User has clicked the OK button to connect to the alias.
		 * 
		 * @param	connDlog	The connection dialog.
		 * @param	user		The user name entered.
		 * @param	password	The password entered.
		 */
		public void performOK(ConnectionDialog connDlog, String user, String password);

		/**
		 * User has clicked the Close button. They don't want to
		 * connect to the alias.
		 * 
		 * @param	connDlog	The connection dialog.
		 */
		public void performClose(ConnectionDialog connDlog);

		/**
		 * User has clicked the Cancel button. They want to cancel
		 * the curently active attempt to connect to the database.
		 * 
		 * @param	connDlog	The connection dialog.
		 */
		public void performCancelConnect(ConnectionDialog connDlog);
	}

	private IApplication _app;
	private ISQLAlias _alias;
	private ISQLDriver _sqlDriver;
	
	/** <TT>true</TT> means that an attempt is being made to connect to the alias.*/
	private boolean _connecting;

	/** Set to <TT>true</TT> once <TT>dispose</TT> has been called. */
	private boolean _disposed;

	private IConnectionDialogHandler _handler;

	private OkClosePanel _btnsPnl;

	private JLabel _aliasName = new JLabel();
	private JLabel _driverName = new JLabel();
	private JLabel _url = new JLabel();
	private JTextField _user = new JTextField();
	private JTextField _password = new JPasswordField();

	/**
	 * Ctor.
	 * 
	 * @param	app		Application API.
	 * @param	owner	<TT>Frame</TT> that will own this dialog.
	 * @param	alias	<TT>SQLAlias</TT> that we are going to connect to.
	 * @param	handler	Handler for dialog actions.
	 * 
	 * @throws	IllegalArgumentException
	 * 			If <TT>null</TT> <TT>IApplication</TT>, <TT>ISQLAlias</TT>,
	 * 			or <TT>IConnectionDialogHandler</TT> passed.
	 */
	public ConnectionDialog(IApplication app, Frame owner, ISQLAlias alias,
								IConnectionDialogHandler handler) {
		super(owner, i18n.CONNECT, false);
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (alias == null) {
			throw new IllegalArgumentException("Null ISQLAlias passed");
		}
		if (handler == null) {
			throw new IllegalArgumentException("Null IConnectionDialogHandler passed");
		}

		_app = app;
		_alias = alias;
		_handler = handler;

		// Driver associated with the passed alias.
		_sqlDriver = _app.getDataCache().getDriver(_alias.getDriverIdentifier());

		createUserInterface();
		loadData();
	}

	public synchronized void dispose() {
		if (!_disposed) {
			_disposed = true;
			super.dispose();
		}
	}

	public void executed(boolean connected) {
		_connecting = false;
		if (connected) {
			dispose();
		} else {
			setTitle(i18n.CONNECT);
			_user.setEnabled(true);
			_password.setEnabled(true);
			_btnsPnl.setExecuting(false);
		}
	}

	/**
	 * Load data about selected alias into the UI.
	 */
	private void loadData() {
		final String userName = _alias.getUserName();
		_aliasName.setText(_alias.getName());
		_driverName.setText(_sqlDriver.getName());
		_url.setText(_alias.getUrl());
		_user.setText(userName);
		_password.setText("");
	}

	private void connect(boolean connecting) {
		if (!_connecting) {
			_connecting = true;
			_btnsPnl.setExecuting(true);
			setTitle(i18n.CONNECTING);
			_user.setEnabled(false);
			_password.setEnabled(false);
			_handler.performOK(this, _user.getText(), _password.getText());
		}
	}

	private void cancelConnect() {
		if(_connecting) {
			// abort first..
			setTitle(i18n.CANCELLING);
			_btnsPnl.enableCloseButton(false);
			_handler.performCancelConnect(this);
			_connecting = false;
			dispose();
		}
	}

	private void createUserInterface() {
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		PropertyPanel dataEntryPnl = new PropertyPanel();

		JLabel lbl = new JLabel(i18n.ALIAS, SwingConstants.RIGHT);
		dataEntryPnl.add(lbl, _aliasName);

		lbl = new JLabel(i18n.DRIVER, SwingConstants.RIGHT);
		dataEntryPnl.add(lbl, _driverName);

		lbl = new JLabel(i18n.URL, SwingConstants.RIGHT);
		dataEntryPnl.add(lbl, _url);

		lbl = new JLabel(i18n.USER, SwingConstants.RIGHT);
		_user.setColumns(25);
		dataEntryPnl.add(lbl, _user);

		lbl = new JLabel(i18n.PASSWORD, SwingConstants.RIGHT);
		dataEntryPnl.add(lbl, _password);

		// Ok and Close buttons at bottom of dialog.
		_btnsPnl = new OkClosePanel();
		_btnsPnl.addListener(new MyOkClosePanelListener());

		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(dataEntryPnl, BorderLayout.CENTER);
		contentPane.add(_btnsPnl, BorderLayout.SOUTH);

		_btnsPnl.makeOKButtonDefault();
		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(false);

		// Set focus to password control if default user name has been setup.
		addWindowListener(new WindowAdapter() {
			private WindowAdapter _this;
			public void windowActivated(WindowEvent evt) {
				_this = this;
				final String userName = _user.getText();
				if (userName != null && userName.length() > 0) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							_password.requestFocus();
							ConnectionDialog.this.removeWindowListener(_this);
						}
					});
				}
			}
		});
	}

	/**
	 * Listener to handle button events in OK/Close panel.
	 */
	private final class MyOkClosePanelListener implements OkClosePanelListener {
		public void okPressed(OkClosePanelEvent evt) {
			ConnectionDialog.this.connect(true);
		}

		public void closePressed(OkClosePanelEvent evt) {
			ConnectionDialog.this.dispose();
		}

		public void cancelPressed(OkClosePanelEvent evt) {
			ConnectionDialog.this.cancelConnect();
		}
	}
}
