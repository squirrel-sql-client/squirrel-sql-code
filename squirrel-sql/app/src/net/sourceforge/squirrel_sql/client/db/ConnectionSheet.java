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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.fw.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.fw.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;

import net.sourceforge.squirrel_sql.client.IApplication;

/**
 * This internal frame allows the user to connect to an alias.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ConnectionSheet extends JInternalFrame {
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

	/** Handler called for internal frame actions. */
	public interface IConnectionSheetHandler {
		/**
		 * User has clicked the OK button to connect to the alias.
		 * 
		 * @param	connSheet	The connection internal frame.
		 * @param	user		The user name entered.
		 * @param	password	The password entered.
		 */
		public void performOK(ConnectionSheet connSheet, String user, String password);

		/**
		 * User has clicked the Close button. They don't want to
		 * connect to the alias.
		 * 
		 * @param	connSheet	The connection internal frame.
		 */
		public void performClose(ConnectionSheet connSheet);

		/**
		 * User has clicked the Cancel button. They want to cancel
		 * the curently active attempt to connect to the database.
		 * 
		 * @param	connSheet	The connection internal frame.
		 */
		public void performCancelConnect(ConnectionSheet connSheet);
	}

	/** Application API. */
	private IApplication _app;
	
	/** Alias we are going to connect to. */
	private ISQLAlias _alias;
	
	/** JDBC driver for <TT>_alias</TT>. */
	private ISQLDriver _sqlDriver;
	
	/** <TT>true</TT> means that an attempt is being made to connect to the alias.*/
	private boolean _connecting;

	/** Set to <TT>true</TT> once <TT>dispose</TT> has been called. */
	private boolean _disposed;

	private IConnectionSheetHandler _handler;

	private JLabel _titleLbl = new JLabel();
	private JLabel _aliasName = new JLabel();
	private JLabel _driverName = new JLabel();
	private JLabel _url = new JLabel();
	private JTextField _user = new JTextField();
	private JTextField _password = new JPasswordField();
	private OkClosePanel _btnsPnl = new OkClosePanel();
	private StatusBar _statusBar = new StatusBar();

	/**
	 * Ctor.
	 * 
	 * @param	app		Application API.
	 * @param	owner	<TT>Frame</TT> that will own this internal frame.
	 * @param	alias	<TT>SQLAlias</TT> that we are going to connect to.
	 * @param	handler	Handler for internal frame actions.
	 * 
	 * @throws	IllegalArgumentException
	 * 			If <TT>null</TT> <TT>IApplication</TT>, <TT>ISQLAlias</TT>,
	 * 			or <TT>IConnectionSheetHandler</TT> passed.
	 */
	public ConnectionSheet(IApplication app, Frame owner, ISQLAlias alias,
								IConnectionSheetHandler handler) {
		super();
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (alias == null) {
			throw new IllegalArgumentException("Null ISQLAlias passed");
		}
		if (handler == null) {
			throw new IllegalArgumentException("Null IConnectionSheetHandler passed");
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
			setStatusText(null);
			_user.setEnabled(true);
			_password.setEnabled(true);
			_btnsPnl.setExecuting(false);
		}
	}

	/**
	 * Set title of this frame. Ensure that the title label
	 * matches the frame title.
	 * 
	 * @param	title	New title text.
	 */
	public void setTitle(String title) {
		super.setTitle(title);
		_titleLbl.setText(title);
	}
	
	/**
	 * Set the text in the status bar.
	 * 
	 * @param	text	The text to place in the status bar.
	 */
	public void setStatusText(String text) {
		_statusBar.setText(text);
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

		// This is mainly for long URLs that cannot be fully
		// displayed in the label.
		_aliasName.setToolTipText(_aliasName.getText());
		_driverName.setToolTipText(_driverName.getText());
		_url.setToolTipText(_url.getText());
	}

	private void connect(boolean connecting) {
		if (!_connecting) {
			_connecting = true;
			_btnsPnl.setExecuting(true);
			setStatusText(i18n.CONNECTING);
			_user.setEnabled(false);
			_password.setEnabled(false);
			_handler.performOK(this, _user.getText(), _password.getText());
		}
	}

	private void cancelConnect() {
		if(_connecting) {
			// abort first..
			setStatusText(i18n.CANCELLING);
			_btnsPnl.enableCloseButton(false);
			_handler.performCancelConnect(this);
			_connecting = false;
			dispose();
		}
	}

	private void createUserInterface() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // This is a tool window.
        GUIUtils.makeToolWindow(this, true);
        
        setTitle("Connect to " + _alias.getName());

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

		// This seems to be necessary to get background colours
		// correct. Without it labels added to the content pane
		// have a dark background while those added to a JPanel
		// in the content pane have a light background under
		// the java look and feel. Similar effects occur for other
		// look and feels.
		setContentPane(new JPanel());

		final Container contentPane = getContentPane();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		contentPane.setLayout(gbl);

		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = gbc.weighty = 1;

		// Title label at top.
		gbc.insets = new Insets(5, 10, 5, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(_titleLbl, gbc);
		contentPane.add(_titleLbl);

		// Separated by a line.
		gbc.insets = new Insets(0, 10, 5, 10);
		JSeparator sep = new JSeparator();
		gbl.setConstraints(sep, gbc);
		contentPane.add(sep);

		// Next is the data entry panel. Let it take up any excess space.
		//gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 10, 0, 10);
		gbl.setConstraints(dataEntryPnl, gbc);
		contentPane.add(dataEntryPnl);

		// Separated by a line.
		gbc.fill = GridBagConstraints.HORIZONTAL;
		sep = new JSeparator();
		gbc.insets = new Insets(5, 10, 5, 10);
		gbl.setConstraints(sep, gbc);
		contentPane.add(sep);

		gbc.insets = new Insets(0, 0, 0, 0);
		
		// Next the buttons.
		gbl.setConstraints(_btnsPnl, gbc);
		contentPane.add(_btnsPnl);
		
		// Finally the status bar.
		gbl.setConstraints(_statusBar, gbc);
		contentPane.add(_statusBar);

		_btnsPnl.addListener(new MyOkClosePanelListener());
		_btnsPnl.makeOKButtonDefault();

		pack();

		// Set focus to password control if default user name has been setup.
		addInternalFrameListener(new InternalFrameAdapter() {
			private InternalFrameAdapter _this;
			public void internalFrameActivated(InternalFrameEvent evt) {
				_this = this;
				final String userName = _user.getText();
				if (userName != null && userName.length() > 0) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							_password.requestFocus();
							ConnectionSheet.this.removeInternalFrameListener(_this);
						}
					});
				}
			}
		});
	}

	/**
	 * Listener to handle button events in OK/Close panel.
	 */
	private final class MyOkClosePanelListener implements IOkClosePanelListener {
		public void okPressed(OkClosePanelEvent evt) {
			ConnectionSheet.this.connect(true);
		}

		public void closePressed(OkClosePanelEvent evt) {
			ConnectionSheet.this.dispose();
		}

		public void cancelPressed(OkClosePanelEvent evt) {
			ConnectionSheet.this.cancelConnect();
		}
	}
}
