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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.encryption.AliasPasswordHandler;
import net.sourceforge.squirrel_sql.client.gui.db.passwordaccess.PasswordInAliasCtrl;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasPropertiesCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallToolTipInfoButton;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.List;
import java.util.*;

import static net.sourceforge.squirrel_sql.client.preferences.PreferenceType.ALIAS_DEFINITIONS;
/**
 * This internal frame allows the maintenance of an database alias.
 *
 * @author	<A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
@SuppressWarnings("serial")
public class AliasInternalFrame extends DialogWidget
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

	private static final StringManager s_stringMgr =  StringManagerFactory.getStringManager(AliasInternalFrame.class);

	private static final ILogger s_log = LoggerController.createLogger(AliasInternalFrame.class);

	/** Number of characters to show in text fields. */
	private static final int COLUMN_COUNT = 25;

	/** The <TT>SQLAlias</TT> being maintained. */
	private final SQLAlias _sqlAlias;

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
	private final JTextField _txtAliasName = new JTextField();

	/** Dropdown of all the drivers in the system. */
	private DriversCombo _drivers;

	/** URL to the data source text field. */
	private final JTextField _txtUrl = new JTextField();

	/** User name text field */
	private final JTextField _txtUserName = new JTextField();

	/** Password */
	private PasswordInAliasCtrl _passwordInAliasCtrl = new PasswordInAliasCtrl();

	/** Autologon checkbox. */
	private final JCheckBox _chkAutoLogon = new JCheckBox(s_stringMgr.getString("AliasInternalFrame.autologon"));

	/** Connect at startup checkbox. */
	private final JCheckBox _chkConnectAtStartup = new JCheckBox(s_stringMgr.getString("AliasInternalFrame.connectatstartup"));

	private JCheckBox _chkSavePasswordEncrypted = new JCheckBox(s_stringMgr.getString("AliasInternalFrame.password.encrypted"));

	/** Button that brings up the driver properties dialog. */
	private final JButton _btnAliasProps = new JButton(s_stringMgr.getString("AliasInternalFrame.props"));


	/**
	 * Ctor.
	 *
	 * @param	sqlAlias	The <TT>SQLAlias</TT> to be maintained.
	 * @param	maintType	The maintenance type.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> passed for <TT>app</TT> or
	 * 			<TT>SQLAlias</TT> or an invalid value passed for
	 *			<TT>maintType</TT>.
	 */
	AliasInternalFrame(SQLAlias sqlAlias, int maintType)
	{
		super("", true, Main.getApplication());

		if (sqlAlias == null)
		{
			throw new IllegalArgumentException("SQLAlias == null");
		}
		if (maintType < IMaintenanceType.NEW || maintType > IMaintenanceType.COPY)
		{
            // i18n[AliasInternalFrame.illegalValue=Illegal value of {0} passed for Maintenance type]
			final String msg = s_stringMgr.getString("AliasInternalFrame.illegalValue", Integer.valueOf(maintType));
			throw new IllegalArgumentException(msg);
		}

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
			Main.getApplication().getAliasesAndDriversManager().removeDriversListener(_driversCacheLis);
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
	SQLAlias getSQLAlias()
	{
		return _sqlAlias;
	}

	/**
	 * Load data from the alias into GUI controls.
	 */
	private void loadData()
	{
		_txtAliasName.setText(_sqlAlias.getName());
		_txtUserName.setText(_sqlAlias.getUserName());

		_passwordInAliasCtrl.setPassword(AliasPasswordHandler.getPassword(_sqlAlias));

		_chkAutoLogon.setSelected(_sqlAlias.isAutoLogon());
		_chkConnectAtStartup.setSelected(_sqlAlias.isConnectAtStartup());
		_chkSavePasswordEncrypted.setSelected(_sqlAlias.isEncryptPassword());
		//_useDriverPropsChk.setSelected(_sqlAlias.getUseDriverProperties());

		if (_maintType != IMaintenanceType.NEW)
		{
			_drivers.setSelectedItem(_sqlAlias.getDriverIdentifier());
			_txtUrl.setText(_sqlAlias.getUrl());
		}
		else
		{
			final ISQLDriver driver = _drivers.getSelectedDriver();
			if (driver != null)
			{
				_txtUrl.setText(driver.getUrl());
			}
		}
	}

	private void performClose()
	{
		setVisible(false);
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
			if (_maintType == IMaintenanceType.NEW || _maintType == IMaintenanceType.COPY)
			{
				Main.getApplication().getAliasesAndDriversManager().addAlias(_sqlAlias);
			}
         Main.getApplication().savePreferences(ALIAS_DEFINITIONS);
			dispose();
		}
		catch (ValidationException ex)
		{
			Main.getApplication().showErrorDialog(ex);
		}
	}

	private void applyFromDialog(SQLAlias alias) throws ValidationException
	{
		ISQLDriver driver = _drivers.getSelectedDriver();
		if (driver == null)
		{
			throw new ValidationException(s_stringMgr.getString("AliasInternalFrame.error.nodriver"));
		}
		alias.setName(_txtAliasName.getText().trim());
		alias.setDriverIdentifier(_drivers.getSelectedDriver().getIdentifier());
		alias.setUrl(_txtUrl.getText().trim());
		alias.setUserName(_txtUserName.getText().trim());

		StringBuffer buf = new StringBuffer();
		buf.append(_passwordInAliasCtrl.getPassword());

		alias.setEncryptPassword(_chkSavePasswordEncrypted.isSelected());

		String unencryptedPassword = buf.toString();
		AliasPasswordHandler.setPassword(alias, unencryptedPassword);

		alias.setAutoLogon(_chkAutoLogon.isSelected());
		alias.setConnectAtStartup(_chkConnectAtStartup.isSelected());
	}

	private void showNewDriverDialog()
	{
		Main.getApplication().getWindowManager().showNewDriverInternalFrame();
	}

	private void showDriverPropertiesDialog()
	{
		try
		{
         applyFromDialog(_sqlAlias);
         new AliasPropertiesCommand(_sqlAlias, Main.getApplication()).execute();
		}
		catch (Exception ex)
		{
			Main.getApplication().showErrorDialog(ex);
		}
	}

	/**
	 * Create user interface for this sheet.
	 */
	private void createUserInterface()
	{
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

		// This is a tool window.

      makeToolWindow(true);

		String winTitle; 
		if (_maintType == IMaintenanceType.MODIFY)
		{
			winTitle = s_stringMgr.getString("AliasInternalFrame.changealias", _sqlAlias.getName());
		}
		else
		{
			winTitle = s_stringMgr.getString("AliasInternalFrame.addalias");
		}
		setTitle(winTitle);

		_txtAliasName.setColumns(COLUMN_COUNT);
		_txtUrl.setColumns(COLUMN_COUNT);
		_txtUserName.setColumns(COLUMN_COUNT);
		_passwordInAliasCtrl.setColumns(COLUMN_COUNT);

		// This seems to be necessary to get background colours
		// correct. Without it labels added to the content pane
		// have a dark background while those added to a JPanel
		// in the content pane have a light background under
		// the java look and feel. Similar effects occur for other
		// look and feels.
		final JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		super.setContentPane(contentPane);

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
		Main.getApplication().getAliasesAndDriversManager().addDriversListener(_driversCacheLis);

		GUIUtils.enableCloseByEscape(this);
   }

	private JPanel createDataEntryPanel()
	{
		_btnAliasProps.addActionListener(evt -> showDriverPropertiesDialog());

		JPanel pnl = new JPanel(new GridBagLayout());

		GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.name"), SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnl.add(_txtAliasName, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.driver"), SwingConstants.RIGHT), gbc);


      _drivers = new DriversCombo();
		_drivers.addItemListener(new DriversComboItemListener());

		final Box driverPnl = Box.createHorizontalBox();
		driverPnl.add(_drivers);
		driverPnl.add(Box.createHorizontalStrut(5));
		JButton newDriverBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.new"));
		newDriverBtn.addActionListener(evt -> showNewDriverDialog());
		driverPnl.add(newDriverBtn);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnl.add(driverPnl, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
		pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.url"), SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnl.add(_txtUrl, gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
		pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.username"), SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnl.add(_txtUserName, gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
		pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.password"), SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnl.add(_passwordInAliasCtrl.getPanel(), gbc);

      gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
		pnl.add(createAutoLogonPanel(), gbc);

      gbc = new GridBagConstraints(1,5,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
		pnl.add(_chkConnectAtStartup, gbc);

		gbc = new GridBagConstraints(0,6,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnl.add(_chkSavePasswordEncrypted, gbc);

      gbc = new GridBagConstraints(1,7,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      _btnAliasProps.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.ALIAS_PROPERTIES));
      pnl.add(_btnAliasProps, gbc);



      // make it grow when added
      gbc = new GridBagConstraints(0,8,2,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
		pnl.add(new JPanel(), gbc);

		return pnl;
	}

	private JPanel createAutoLogonPanel()
	{
		JPanel ret = new JPanel(new GridBagLayout());

		GridBagConstraints gbc;

		gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
		_chkAutoLogon.setToolTipText(s_stringMgr.getString("AliasInternalFrame.autologon.security.tooltip"));
		ret.add(_chkAutoLogon, gbc);

		gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,2), 0,0);
		ret.add(new SmallToolTipInfoButton(s_stringMgr.getString("AliasInternalFrame.autologon.security.tooltip.long.html"), 10000).getButton(), gbc);

		return ret;
	}

	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.ok"));
		okBtn.addActionListener(evt -> performOk());

		JButton closeBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.close"));
		closeBtn.addActionListener(evt -> performClose());

		JButton testBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.test"));
		testBtn.addActionListener(evt -> performConnect(false));

		JButton connectBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.connect"));
		connectBtn.addActionListener(evt -> performConnect(true));

		pnl.add(okBtn);
		pnl.add(closeBtn);
		pnl.add(testBtn);
		pnl.add(connectBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, closeBtn, testBtn, connectBtn });
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}

	private void performConnect(boolean createSession)
	{

		if (createSession)
		{
			try
			{
				applyFromDialog(_sqlAlias);
			}
			catch (ValidationException e)
			{
				Main.getApplication().showErrorDialog(e);
				return;
			}

			ConnectToAliasCallBack connectToAliasCallBack = new ConnectToAliasCallBack(_sqlAlias)
			{
				@Override
				public void sessionCreated(ISession session)
				{
					performOk();
				}
			};

			new ConnectToAliasCommand(_sqlAlias, true, connectToAliasCallBack).execute();
		}
		else
		{

			// This block is merely used for testing the connection from within the Alias frame.
			// We need to create a copy of the Alias because we don't want the test to automatically save changes.

			final AliasesAndDriversManager cache = Main.getApplication().getAliasesAndDriversManager();
			final IIdentifierFactory factory = IdentifierFactory.getInstance();
			final SQLAlias alias = cache.createAlias(factory.createIdentifier());

			try
			{
				alias.assignFromWithValidationException(_sqlAlias, false);
			}
			catch (ValidationException e)
			{
				// Occurs if we are just adding this Alias.
			}

			try
			{
				applyFromDialog(alias);
			}
			catch (ValidationException e)
			{
				Main.getApplication().showErrorDialog(e);
				return;
			}

			new ConnectToAliasCommand(alias, false, new ConnectionTestCallBack(Main.getApplication(), alias)).execute();
		}
	}

	private final class DriversComboItemListener implements ItemListener
	{
		public void itemStateChanged(ItemEvent evt)
		{
			ISQLDriver driver = (ISQLDriver) evt.getItem();
			if (driver != null)
			{
				_txtUrl.setText(driver.getUrl());
			}
		}
	}

	/**
	 * This combobox displays all the JDBC drivers defined in SQuirreL.
	 */
	private final class DriversCombo extends JComboBox
	{
		private Map<IIdentifier, ISQLDriver> _map = new HashMap<>();

		SquirrelPreferences prefs = Main.getApplication().getSquirrelPreferences();

		DriversCombo()
		{
			SquirrelResources res = Main.getApplication().getResources();
			setRenderer(new DriverListCellRenderer(res.getIcon("list.driver.found"), res.getIcon("list.driver.notfound")));
			List<ISQLDriver> list = new ArrayList<ISQLDriver>();
			for (Iterator it = Main.getApplication().getAliasesAndDriversManager().drivers(); it.hasNext(); )
			{
				ISQLDriver sqlDriver = ((ISQLDriver) it.next());
				if (prefs.getShowLoadedDriversOnly() && !sqlDriver.isJDBCDriverClassLoaded())
				{
					continue;
				}
				_map.put(sqlDriver.getIdentifier(), sqlDriver);
				list.add(sqlDriver);
			}

			Collections.sort(list, new DriverComparator());
			for (Iterator it = list.iterator(); it.hasNext(); )
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

		private class DriverComparator implements Comparator<ISQLDriver>, Serializable
		{
			public int compare(ISQLDriver leftDriver, ISQLDriver rightDriver)
			{
				if (leftDriver.isJDBCDriverClassLoaded() && !rightDriver.isJDBCDriverClassLoaded())
				{
					return -1;
				}

				if (!leftDriver.isJDBCDriverClassLoaded() && rightDriver.isJDBCDriverClassLoaded())
				{
					return 1;
				}

				return leftDriver.toString().compareToIgnoreCase(rightDriver.toString());
			}
		}
	}

	private final class ConnectionTestCallBack extends ConnectToAliasCallBack
	{
		private ConnectionTestCallBack(IApplication app, SQLAlias alias)
		{
			super(alias);
		}

		/**
		 * @see CompletionCallback#connected(SQLConnection)
		 */
		public void connected(ISQLConnection conn)
		{
			try
			{
				conn.close();
			}
			catch (Throwable th)
			{
				String msg = s_stringMgr.getString("AliasInternalFrame.error.errorclosingconn");
				s_log.error(msg, th);
				Main.getApplication().showErrorDialog(msg + ": " + th.toString());
			}

			AliasInternalFrame.this.showOk(s_stringMgr.getString("AliasInternalFrame.connsuccess"));

         if(getAlias().isAutoLogon())
         {
            // If Auto Logon is true in ConnectToAliasCommand user name/password
            // of the Alias definiton may have changed.
            // Here we transfere this information back into the controls.
            _txtUserName.setText(getAlias().getUserName());
            _passwordInAliasCtrl.setPassword(AliasPasswordHandler.getPassword(getAlias()));
         }
      }

		/**
		 * @see CompletionCallback#sessionCreated(ISession)
		 */
		public void sessionCreated(ISession session)
		{
            // i18n[AliasInternalFrame.error.sessioncreation=Test Button has created a session, this is a programming error]
			s_log.error(s_stringMgr.getString("AliasInternalFrame.error.sessioncreation"));
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
