package net.sourceforge.squirrel_sql.client.db;
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
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.util.Properties;

import javax.swing.JButton;
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
import net.sourceforge.squirrel_sql.fw.gui.sql.DriverPropertiesDialog;
import net.sourceforge.squirrel_sql.fw.gui.sql.DriverPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

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
		 * @param	props		SQLDriverPropertyCollection to connect with.
		 */
		public void performOK(ConnectionSheet connSheet, String user,
								String password, SQLDriverPropertyCollection props);

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
	
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(ConnectionSheet.class);

	private static final int COLUMN_COUNT = 25;

	/** Application API. */
	private IApplication _app;

	/** Alias we are going to connect to. */
	private ISQLAlias _alias;

	/** JDBC driver for <TT>_alias</TT>. */
	private ISQLDriver _sqlDriver;

	/** <TT>true</TT> means that an attempt is being made to connect to the alias.*/
	private boolean _connecting;

	private SQLDriverPropertyCollection _props = new SQLDriverPropertyCollection();

	private IConnectionSheetHandler _handler;

	private JLabel _titleLbl = new JLabel();
	private JLabel _aliasName = new JLabel();
	private JLabel _driverName = new JLabel();
	private JLabel _url = new JLabel();
	private JTextField _user = new JTextField();
	private JTextField _password = new JPasswordField();
	private OkClosePanel _btnsPnl = new OkClosePanel("Connect");

	/** Button that brings up the driver properties dialog. */
	private final JButton _driverPropsBtn = new JButton("Properties...");

	private StatusBar _statusBar = new StatusBar();

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 * @param	alias	<TT>SQLAlias</TT> that we are going to connect to.
	 * @param	handler	Handler for internal frame actions.
	 *
	 * @throws	IllegalArgumentException
	 * 			If <TT>null</TT> <TT>IApplication</TT>, <TT>ISQLAlias</TT>,
	 * 			or <TT>IConnectionSheetHandler</TT> passed.
	 */
	public ConnectionSheet(IApplication app, ISQLAlias alias,
							IConnectionSheetHandler handler)
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
	 * If the alias specifies autologon then connect after the Dialog is visible.
	 * 
	 * @param	b	If <TT>true</TT> dialog is to be made visible.
	 */
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);

		if (visible && _alias.isAutoLogon())
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					connect(true);
				}
			});
		}
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
	 * Load data about selected alias into the UI.
	 */
	private void loadData()
	{
		final String userName = _alias.getUserName();
		final String password = _alias.getPassword();
		_aliasName.setText(_alias.getName());
		_driverName.setText(_sqlDriver.getName());
		_url.setText(_alias.getUrl());
		_user.setText(userName);
		_password.setText(password);

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
			_handler.performOK(this, _user.getText(), _password.getText(), _props);
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
		if (_alias.getUseDriverProperties())
		{
			try
			{
				final SQLDriverManager mgr = _app.getSQLDriverManager();
				final Driver jdbcDriver = mgr.getJDBCDriver(_sqlDriver.getIdentifier());
				if (jdbcDriver == null)
				{
					throw new BaseException("Cannot determine driver properties as the driver cannot be loaded.");
				}

				_props = _alias.getDriverProperties();
				DriverPropertyInfo[] infoAr = jdbcDriver.getPropertyInfo(_alias.getUrl(),
																new Properties());
				_props.applyDriverPropertynfo(infoAr);
			}
			catch (Exception ex)
			{
				String msg = "Error creating Driver Properties Panel";
				s_log.error(msg, ex);
				_app.showErrorDialog(msg, ex);
			}
		}

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		GUIUtils.makeToolWindow(this, true);
		setTitle("Connect to: " + _alias.getName());

		// This seems to be necessary to get background colours
		// correct. Without it labels added to the content pane
		// have a dark background while those added to a JPanel
		// in the content pane have a light background under
		// the java look and feel. Similar effects occur for other
		// look and feels.
		final JPanel contentPane = new JPanel(new GridBagLayout());
		setContentPane(contentPane);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1;

		// Title label at top.
		gbc.insets = new Insets(5, 10, 5, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = gbc.gridy = 0;
		contentPane.add(_titleLbl, gbc);

		// Separated by a line.
		gbc.insets = new Insets(0, 10, 5, 10);
		++gbc.gridy;
		contentPane.add(new JSeparator(), gbc);

		// Next is the data entry panel. Let it take up any excess space.
		gbc.insets = new Insets(5, 10, 5, 10);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		++gbc.gridy;
		contentPane.add(createMainPanel(), gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = 0;
		if (_alias.getUseDriverProperties())
		{
			++gbc.gridy;
			contentPane.add(_driverPropsBtn, gbc);
		}

		++gbc.gridy;
		contentPane.add(new JLabel("Warning - Caps lock may interfere with passwords"), gbc);

		// Separated by a line.
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 10, 5, 10);
		++gbc.gridy;
		contentPane.add(new JSeparator(), gbc);

		// Next the buttons.
		gbc.insets = new Insets(0, 0, 0, 0);
		++gbc.gridy;
		contentPane.add(createButtonsPanel(), gbc);

		// Finally the status bar.
		Font fn = _app.getFontInfoStore().getStatusBarFontInfo().createFont();
		_statusBar.setFont(fn);
		++gbc.gridy;
		contentPane.add(_statusBar, gbc);

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
							final String pw = _password.getText();
							if (pw != null && pw.length() > 0)
							{
								_btnsPnl.getOKButton().requestFocus();
							}
							else
							{
								_password.requestFocus();
							}
							ConnectionSheet.this.removeInternalFrameListener(_this);
						}
					});
				}
			}
		});

		_btnsPnl.makeOKButtonDefault();

		pack();
	}

	/**
	 * Create the panel in which user name and password is entered.
	 * 
	 * @return	user name/password panel.
	 */
	private Component createMainPanel()
	{
		_user.setColumns(COLUMN_COUNT);
		_password.setColumns(COLUMN_COUNT);

		_driverPropsBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				showDriverPropertiesDialog();
			}
		});

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

		return dataEntryPnl;
	}

	/**
	 * Create the panel that displays driver properties.
	 */
	private JPanel createPropertiesPanel()
	{
		return new DriverPropertiesPanel(_props);
	}

	/**
	 * Create the buttons panel.
	 * 
	 * @return	The buttons panel.
	 */
	private JPanel createButtonsPanel()
	{
		_btnsPnl.addListener(new MyOkClosePanelListener());
		_btnsPnl.getOKButton().setText("Connect");
		return _btnsPnl;
	}

	private void showDriverPropertiesDialog()
	{
		final Frame owner = _app.getMainFrame();
		DriverPropertiesDialog.showDialog(owner, _props);
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
