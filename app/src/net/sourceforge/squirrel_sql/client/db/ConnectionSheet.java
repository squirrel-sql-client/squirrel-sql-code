package net.sourceforge.squirrel_sql.client.db;
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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.fw.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.fw.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.gui.StatusBar;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.BaseSheet;

/**
 * This internal frame allows the user to connect to an alias.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ConnectionSheet extends BaseSheet
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface ConnectionSheetI18n
	{
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
	public interface IConnectionSheetHandler
	{
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

	private static final int COLUMN_COUNT = 25;

	/** Application API. */
	private IApplication _app;

	/** Alias we are going to connect to. */
	private ISQLAlias _alias;

	/** JDBC driver for <TT>_alias</TT>. */
	private ISQLDriver _sqlDriver;

	/** <TT>true</TT> means that an attempt is being made to connect to the alias.*/
	private boolean _connecting;

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
	public ConnectionSheet(IApplication app, /*Frame owner,*/
	ISQLAlias alias, IConnectionSheetHandler handler)
	{
		super("", true);
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (alias == null)
		{
			throw new IllegalArgumentException("Null ISQLAlias passed");
		}
		if (handler == null)
		{
			throw new IllegalArgumentException("Null IConnectionSheetHandler passed");
		}

		_app = app;
		_alias = alias;
		_handler = handler;

		// Driver associated with the passed alias.
		_sqlDriver = _app.getDataCache().getDriver(_alias.getDriverIdentifier());

		if (_sqlDriver == null)
		{
			throw new IllegalStateException("Unable to find SQLDriver for " +
												_alias.getName());
		}

		createGUI();
		loadData();
	}

	public void executed(boolean connected)
	{
		_connecting = false;
		if (connected)
		{
			dispose();
		}
		else
		{
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
	public void setTitle(String title)
	{
		super.setTitle(title);
		_titleLbl.setText(title);
	}

	/**
	 * Set the text in the status bar.
	 *
	 * @param	text	The text to place in the status bar.
	 */
	public void setStatusText(String text)
	{
		_statusBar.setText(text);
	}

	/**
	 * Load data about selected alias into the UI.
	 */
	private void loadData()
	{
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

	private void connect(boolean connecting)
	{
		if (!_connecting)
		{
			_connecting = true;
			_btnsPnl.setExecuting(true);
			setStatusText(ConnectionSheetI18n.CONNECTING);
			_user.setEnabled(false);
			_password.setEnabled(false);
			_handler.performOK(this, _user.getText(), _password.getText());
		}
	}

	private void cancelConnect()
	{
		if (_connecting)
		{
			// abort first..
			setStatusText(ConnectionSheetI18n.CANCELLING);
			_btnsPnl.enableCloseButton(false);
			_handler.performCancelConnect(this);
			_connecting = false;
			dispose();
		}
	}

	private void createGUI()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		GUIUtils.makeToolWindow(this, true);
		setTitle("Connect to: " + _alias.getName());

		_user.setColumns(COLUMN_COUNT);
		_password.setColumns(COLUMN_COUNT);

		PropertyPanel dataEntryPnl = new PropertyPanel();

		JLabel lbl = new JLabel(ConnectionSheetI18n.ALIAS, SwingConstants.RIGHT);
		dataEntryPnl.add(lbl, _aliasName);

		lbl = new JLabel(ConnectionSheetI18n.DRIVER, SwingConstants.RIGHT);
		dataEntryPnl.add(lbl, _driverName);

		lbl = new JLabel(ConnectionSheetI18n.URL, SwingConstants.RIGHT);
		dataEntryPnl.add(lbl, _url);

		lbl = new JLabel(ConnectionSheetI18n.USER, SwingConstants.RIGHT);
		_user.setColumns(25);
		dataEntryPnl.add(lbl, _user);

		lbl = new JLabel(ConnectionSheetI18n.PASSWORD, SwingConstants.RIGHT);
		dataEntryPnl.add(lbl, _password);

		// This seems to be necessary to get background colours
		// correct. Without it labels added to the content pane
		// have a dark background while those added to a JPanel
		// in the content pane have a light background under
		// the java look and feel. Similar effects occur for other
		// look and feels.
		final JPanel contentPane = new JPanel();
		setContentPane(contentPane);

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		contentPane.setLayout(gbl);

		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;

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
		gbc.weighty = 1;
		contentPane.add(dataEntryPnl);

		// Next the case-sensitivity warning.
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 10, 5, 10);
		lbl = new JLabel("Warning - Caps lock may interfere with passwords");
		gbl.setConstraints(lbl, gbc);
		gbc.weighty = 0;
		contentPane.add(lbl);

		// Separated by a line.
		sep = new JSeparator();
		gbc.insets = new Insets(5, 10, 5, 10);
		gbl.setConstraints(sep, gbc);
		contentPane.add(sep);

		gbc.insets = new Insets(0, 0, 0, 0);

		// Next the buttons.
		gbl.setConstraints(_btnsPnl, gbc);
		contentPane.add(_btnsPnl);

		// Finally the status bar.
		Font fn = _app.getFontInfoStore().getStatusBarFontInfo().createFont();
		_statusBar.setFont(fn);
		gbl.setConstraints(_statusBar, gbc);
		contentPane.add(_statusBar);

		_btnsPnl.addListener(new MyOkClosePanelListener());
		_btnsPnl.makeOKButtonDefault();

		// Set focus to password control if default user name has been setup.
		addInternalFrameListener(new InternalFrameAdapter()
		{
			private InternalFrameAdapter _this;
			public void internalFrameActivated(InternalFrameEvent evt)
			{
				_this = this;
				final String userName = _user.getText();
				if (userName != null && userName.length() > 0)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							_password.requestFocus();
							ConnectionSheet.this.removeInternalFrameListener(_this);
						}
					});
				}
			}
		});

		pack();
	}

	/**
	 * Allow base class to create rootpane and add a couple
	 * of listeners for ENTER and ESCAPE to it.
	 */
	protected JRootPane createRootPane()
	{
		ActionListener escapeListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				ConnectionSheet.this.dispose();
			}
		};

		ActionListener enterListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent actionEvent)
			{
				ConnectionSheet.this.connect(true);
			}
		};

		JRootPane rootPane = super.createRootPane();

		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		rootPane.registerKeyboardAction(escapeListener, ks, WHEN_IN_FOCUSED_WINDOW);
		ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		rootPane.registerKeyboardAction(enterListener, ks, WHEN_IN_FOCUSED_WINDOW);

		return rootPane;
	}

	/**
	 * Listener to handle button events in OK/Close panel.
	 */
	private final class MyOkClosePanelListener implements IOkClosePanelListener
	{
		public void okPressed(OkClosePanelEvent evt)
		{
			ConnectionSheet.this.connect(true);
		}

		public void closePressed(OkClosePanelEvent evt)
		{
			ConnectionSheet.this.dispose();
		}

		public void cancelPressed(OkClosePanelEvent evt)
		{
			ConnectionSheet.this.cancelConnect();
		}
	}
}