package net.sourceforge.squirrel_sql.client.gui.db;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.sql.DriverPropertiesDialog;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.DataCache;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.BaseInternalFrame;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
/**
 * This internal frame allows the maintenance of an database alias.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AliasInternalFrame extends BaseInternalFrame
{
	/**
	 * Maintenance types.
	 */
	public interface IMaintenanceType
	{
		/** A new alias is being created. */
		int NEW = 1;

		/** An existing alias is being maintained. */
		int MODIFY = 2;

		/** A new alias is being created as a copy of an existing one. */
		int COPY = 3;
	}
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AliasInternalFrame.class);

	/** Number of characters to show in text fields. */
	private static final int COLUMN_COUNT = 25;

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(AliasInternalFrame.class);

	/** Application API. */
	private final IApplication _app;

	/** The <TT>ISQLAlias</TT> being maintained. */
	private final ISQLAlias _sqlAlias;

	/** Frame title. */
	private final JLabel _titleLbl = new JLabel();

	/**
	 * The requested type of maintenace.
	 * @see IMaintenanceType
	 */
	private final int _maintType;

	/** Listener to the drivers cache. */
	private DriversCacheListener _driversCacheLis;

	/** Alias name text field.. */
	private final JTextField _aliasName = new JTextField();

	/** Dropdown of all the drivers in the system. */
	private DriversCombo _drivers;

	/** URL to the data source text field. */
	private final JTextField _url = new JTextField();

	/** User name text field */
	private final JTextField _userName = new JTextField();

	/** Password */
	private final JPasswordField _password = new JPasswordField();

	/** Autologon checkbox. */
	private final JCheckBox _autoLogonChk = new JCheckBox(s_stringMgr.getString("AliasInternalFrame.autologon"));

	/** Connect at startup checkbox. */
	private final JCheckBox _connectAtStartupChk = new JCheckBox(s_stringMgr.getString("AliasInternalFrame.connectatstartup"));

	/** If checked use the extended driver properties. */
	private final JCheckBox _useDriverPropsChk = new JCheckBox(s_stringMgr.getString("AliasInternalFrame.userdriverprops"));

	/** Button that brings up the driver properties dialog. */
	private final JButton _driverPropsBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.props"));

	/** Collection of the driver properties. */
	private SQLDriverPropertyCollection _sqlDriverProps;

	/**
	 * Ctor.
	 *
	 * @param	app			Application API.
	 * @param	sqlAlias	The <TT>ISQLAlias</TT> to be maintained.
	 * @param	maintType	The maintenance type.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> passed for <TT>app</TT> or
	 * 			<TT>ISQLAlias</TT> or an invalid value passed for
	 *			<TT>maintType</TT>.
	 */
	AliasInternalFrame(IApplication app, ISQLAlias sqlAlias, int maintType)
	{
		super("", true);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (sqlAlias == null)
		{
			throw new IllegalArgumentException("ISQLAlias == null");
		}
		if (maintType < IMaintenanceType.NEW
			|| maintType > IMaintenanceType.COPY)
		{
			final String msg = "Illegal value of " + maintType
								+ " passed for Maintenance type";
			throw new IllegalArgumentException(msg);
		}

		_app = app;
		_sqlAlias = sqlAlias;
		_maintType = maintType;
		createUserInterface();
		loadData();
		pack();
	}

	/**
	 * Remove listeners and then dispose of this sheet.
	 */
	public void dispose()
	{
		if (_driversCacheLis != null)
		{
			_app.getDataCache().removeDriversListener(_driversCacheLis);
			_driversCacheLis = null;
		}
		super.dispose();
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
	 * Return the alias that is being maintained.
	 *
	 * @return	the alias that is being maintained.
	 */
	ISQLAlias getSQLAlias()
	{
		return _sqlAlias;
	}

	/**
	 * Load data from the alias into GUI controls.
	 */
	private void loadData()
	{
		_driverPropsBtn.setEnabled(_sqlAlias.getUseDriverProperties());
		_aliasName.setText(_sqlAlias.getName());
		_userName.setText(_sqlAlias.getUserName());

		_password.setText(_sqlAlias.getPassword());

		_autoLogonChk.setSelected(_sqlAlias.isAutoLogon());
		_connectAtStartupChk.setSelected(_sqlAlias.isConnectAtStartup());
		_useDriverPropsChk.setSelected(_sqlAlias.getUseDriverProperties());

		if (_maintType != IMaintenanceType.NEW)
		{
			_drivers.setSelectedItem(_sqlAlias.getDriverIdentifier());
			_url.setText(_sqlAlias.getUrl());
			_sqlDriverProps = _sqlAlias.getDriverProperties();
		}
		else
		{
			final ISQLDriver driver = _drivers.getSelectedDriver();
			if (driver != null)
			{
				_url.setText(driver.getUrl());
			}
			_sqlDriverProps = new SQLDriverPropertyCollection();
		}
	}

	private void performClose()
	{
		dispose();
	}

	/**
	 * OK button pressed. Edit data and if ok save to aliases model
	 * and then close dialog.
	 */
	private void performOk()
	{
		try
		{
			applyFromDialog(_sqlAlias);
			if (_maintType == IMaintenanceType.NEW
				|| _maintType == IMaintenanceType.COPY)
			{
				_app.getDataCache().addAlias(_sqlAlias);
			}
			dispose();
		}
		catch (ValidationException ex)
		{
			_app.showErrorDialog(ex);
		}
		catch (DuplicateObjectException ex)
		{
			_app.showErrorDialog(ex);
		}
	}

	private void applyFromDialog(ISQLAlias alias) throws ValidationException
	{
		ISQLDriver driver = _drivers.getSelectedDriver();
		if (driver == null)
		{
			throw new ValidationException(s_stringMgr.getString("AliasInternalFrame.error.nodriver"));
		}
		alias.setName(_aliasName.getText().trim());
		alias.setDriverIdentifier(_drivers.getSelectedDriver().getIdentifier());
		alias.setUrl(_url.getText().trim());
		alias.setUserName(_userName.getText().trim());

		StringBuffer buf = new StringBuffer();
		buf.append(_password.getPassword());
		alias.setPassword(buf.toString());

		alias.setAutoLogon(_autoLogonChk.isSelected());
		alias.setConnectAtStartup(_connectAtStartupChk.isSelected());
		alias.setUseDriverProperties(_useDriverPropsChk.isSelected());
		alias.setDriverProperties(_sqlDriverProps);
	}

	private void showNewDriverDialog()
	{
		_app.getWindowManager().showNewDriverInternalFrame();
	}

	private void showDriverPropertiesDialog()
	{
		try
		{
			final Frame owner = _app.getMainFrame();
			final ISQLDriver driver = _drivers.getSelectedDriver();
			if (driver == null)
			{
				throw new BaseException(s_stringMgr.getString("AliasInternalFrame.error.noprops"));
			}
			final SQLDriverManager mgr = _app.getSQLDriverManager();
			final Driver jdbcDriver = mgr.getJDBCDriver(driver.getIdentifier());
			if (jdbcDriver == null)
			{
				throw new BaseException(s_stringMgr.getString("AliasInternalFrame.error.cannotloaddriver"));
			}

			DriverPropertyInfo[] infoAr = jdbcDriver.getPropertyInfo(_url.getText(), new Properties());
			_sqlDriverProps.applyDriverPropertynfo(infoAr);
			DriverPropertiesDialog.showDialog(owner, _sqlDriverProps);
		}
		catch (Exception ex)
		{
			_app.showErrorDialog(ex);
		}
	}

	/**
	 * Create user interface for this sheet.
	 */
	private void createUserInterface()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// This is a tool window.
		GUIUtils.makeToolWindow(this, true);

		String winTitle; 
		if (_maintType == IMaintenanceType.MODIFY)
		{
			winTitle = s_stringMgr.getString("AliasInternalFrame.changealias",
											_sqlAlias.getName());
		}
		else
		{
			winTitle = s_stringMgr.getString("AliasInternalFrame.addalias");
		}
		setTitle(winTitle);

		_aliasName.setColumns(COLUMN_COUNT);
		_url.setColumns(COLUMN_COUNT);
		_userName.setColumns(COLUMN_COUNT);
		_password.setColumns(COLUMN_COUNT);

		// This seems to be necessary to get background colours
		// correct. Without it labels added to the content pane
		// have a dark background while those added to a JPanel
		// in the content pane have a light background under
		// the java look and feel. Similar effects occur for other
		// look and feels.
		final JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setContentPane(contentPane);

		GridBagConstraints gbc = new GridBagConstraints();
		contentPane.setLayout(new GridBagLayout());

		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;

		// Title label at top.
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 10, 5, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(_titleLbl, gbc);

		// Separated by a line.
		++gbc.gridy;
		gbc.insets = new Insets(0, 10, 5, 10);
		contentPane.add(new JSeparator(), gbc);

		contentPane.add(createDataEntryPanel(), gbc);

		// Separated by a line.
		gbc.weighty = 0;
		++gbc.gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 10, 5, 10);
		contentPane.add(new JSeparator(), gbc);

		gbc.insets = new Insets(0, 0, 0, 0);

		// Next the buttons.
		++gbc.gridy;
		contentPane.add(createButtonsPanel(), gbc);

		_driversCacheLis = new DriversCacheListener();
		_app.getDataCache().addDriversListener(_driversCacheLis);
	}

	private JPanel createDataEntryPanel()
	{
		_driverPropsBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				showDriverPropertiesDialog();
			}

		});

		_useDriverPropsChk.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				_driverPropsBtn.setEnabled(_useDriverPropsChk.isSelected());
			}

		});

		final JPanel pnl = new JPanel(new GridBagLayout());

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.weightx = 1.0;

		gbc.gridx = 0;
		gbc.gridy = 0;
		pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.name"), SwingConstants.RIGHT), gbc);

		++gbc.gridx;
		pnl.add(_aliasName, gbc);

		_drivers = new DriversCombo();
		_drivers.addItemListener(new DriversComboItemListener());

		final Box driverPnl = Box.createHorizontalBox();
		driverPnl.add(_drivers);
		driverPnl.add(Box.createHorizontalStrut(5));
		JButton newDriverBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.new"));
		newDriverBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				showNewDriverDialog();
			}
		});
		driverPnl.add(newDriverBtn);

		gbc.gridx = 0;
		++gbc.gridy;
		pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.driver"), SwingConstants.RIGHT), gbc);

		++gbc.gridx;
		pnl.add(driverPnl, gbc);

		gbc.gridx = 0;
		++gbc.gridy;
		pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.url"), SwingConstants.RIGHT), gbc);

		++gbc.gridx;
		pnl.add(_url, gbc);

		gbc.gridx = 0;
		++gbc.gridy;
		pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.username"), SwingConstants.RIGHT), gbc);

		++gbc.gridx;
		pnl.add(_userName, gbc);

		gbc.gridx = 0;
		++gbc.gridy;
		pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.password"), SwingConstants.RIGHT), gbc);

		++gbc.gridx;
		pnl.add(_password, gbc);

		gbc.gridx = 0;
		++gbc.gridy;
		pnl.add(_autoLogonChk, gbc);

		++gbc.gridx;
		pnl.add(_connectAtStartupChk, gbc);

		final Box propsPnl = Box.createHorizontalBox();
		propsPnl.add(_useDriverPropsChk);
		propsPnl.add(Box.createHorizontalStrut(5));
		propsPnl.add(_driverPropsBtn);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		++gbc.gridy;
		gbc.gridx = 0;
		pnl.add(propsPnl, gbc);

		gbc.gridx = 0;
		++gbc.gridy;
		pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.cleartext")), gbc);

		return pnl;
	}

	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.ok"));
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
		JButton closeBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.close"));
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		JButton testBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.test"));
		testBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				final DataCache cache = _app.getDataCache();
				final IIdentifierFactory factory = IdentifierFactory.getInstance();
				final ISQLAlias testAlias = cache.createAlias(factory.createIdentifier());
				try
				{
					applyFromDialog(testAlias);
					ConnectionCallBack cb = new ConnectionCallBack(_app, testAlias);
					ConnectToAliasCommand cmd = new ConnectToAliasCommand(_app,
													testAlias, false, cb);
					cmd.execute();
				}
				catch (ValidationException ex)
				{
					_app.showErrorDialog(ex);
				}
			}
		});

		pnl.add(okBtn);
		pnl.add(closeBtn);
		pnl.add(testBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, closeBtn, testBtn });
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}

	private final class DriversComboItemListener implements ItemListener
	{
		public void itemStateChanged(ItemEvent evt)
		{
			ISQLDriver driver = (ISQLDriver) evt.getItem();
			if (driver != null)
			{
				_url.setText(driver.getUrl());
			}
		}
	}

	/**
	 * This combobox displays all the JDBC drivers defined in SQuirreL.
	 */
	private final class DriversCombo extends JComboBox
	{
		private Map _map = new HashMap();

		DriversCombo()
		{
			super();
			SquirrelResources res = _app.getResources();
			setRenderer(new DriverListCellRenderer(res.getIcon("list.driver.found"),
											res.getIcon("list.driver.notfound")));
			List list = new ArrayList();
			for (Iterator it = AliasInternalFrame.this._app.getDataCache().drivers();
					it.hasNext();)
			{
				ISQLDriver sqlDriver = ((ISQLDriver) it.next());
				_map.put(sqlDriver.getIdentifier(), sqlDriver);
				list.add(sqlDriver);
			}
			Collections.sort(list, new DriverComparator());
			for (Iterator it = list.iterator(); it.hasNext();)
			{
				addItem(it.next());
			}
		}

		void setSelectedItem(IIdentifier id)
		{
			super.setSelectedItem(_map.get(id));
		}

		ISQLDriver getSelectedDriver()
		{
			return (ISQLDriver) getSelectedItem();
		}

		private class DriverComparator implements Comparator
		{
			public int compare(Object o1, Object o2)
			{
				return o1.toString().compareToIgnoreCase(o2.toString());
			}

		}
	}

	private final class ConnectionCallBack
		extends ConnectToAliasCommand.ClientCallback
	{
		private ConnectionCallBack(IApplication app, ISQLAlias alias)
		{
			super(app, alias);
		}

		/**
		 * @see CompletionCallback#connected(SQLConnection)
		 */
		public void connected(SQLConnection conn)
		{
			try
			{
				conn.close();
			}
			catch (Throwable th)
			{
				String msg = s_stringMgr.getString("AliasInternalFrame.error.errorclosingconn");
				s_log.error(msg, th);
				_app.showErrorDialog(msg + ": " + th.toString());
			}
			Dialogs.showOk(AliasInternalFrame.this, s_stringMgr.getString("AliasInternalFrame.connsuccess"));
		}

		/**
		 * @see CompletionCallback#sessionCreated(ISession)
		 */
		public void sessionCreated(ISession session)
		{
			s_log.error("Test Button has created a session, this is a programming error");
		}
	}

	/**
	 * Listens to changes in the drivers cache and adds/removes drivers from the dropdown
	 * as they are added/removed from the cache.
	 */
	private final class DriversCacheListener implements IObjectCacheChangeListener
	{
		public void objectAdded(ObjectCacheChangeEvent evt)
		{
			_drivers.addItem(evt.getObject());
		}
		public void objectRemoved(ObjectCacheChangeEvent evt)
		{
			_drivers.removeItem(evt.getObject());
		}
	}
}
