package net.sourceforge.squirrel_sql.client.db;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.BaseSheet;
import net.sourceforge.squirrel_sql.client.mainframe.DriverListCellRenderer;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

public class AliasMaintSheet extends BaseSheet
{
	/**
	 * Maintenance types.
	 */
	public interface MaintenanceType
	{
		int NEW = 1;
		int MODIFY = 2;
		int COPY = 3;
	}

	private static final int COLUMN_COUNT = 25;

	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String ADD = "Add Alias";
		String CHANGE = "Change Alias";
		String DRIVER = "Driver:";
		String NAME = "Name:";
		String URL = "URL:";
		String USER_NAME = "User Name:";
	}

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(AliasMaintSheet.class);

	/** Application API. */
	private final IApplication _app;

	/** The <TT>ISQLAlias</TT> being maintained. */
	private final ISQLAlias _sqlAlias;

	/** Frame title. */
	private final JLabel _titleLbl = new JLabel();

	/**
	 * The requested type of maintenace.
	 * @see MaintenanceType
	 */
	private final int _maintType;

	/** Alias name. */
	private final JTextField _aliasName = new JTextField();

	/** Dropdown of all the drivers in the system. */
	private DriversCombo _drivers;

	/** URL to the data source. */
	private final JTextField _url = new JTextField();

	/** User name */
	private final JTextField _userName = new JTextField();

	/**
	 * Ctor.
	 *
	 * @param	app			Application API.
	 * @param	sqlAlias	The <TT>ISQLAlias</TT> to be maintained.
	 * @param	maintType	The maintenance type.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> passed for <TT>app</TT> or <TT>ISQLAlias</TT> or
	 * 			an invalid value passed for <TT>maintType</TT>.
	 */
	AliasMaintSheet(IApplication app, ISQLAlias sqlAlias, int maintType)
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
		if (maintType < MaintenanceType.NEW
			|| maintType > MaintenanceType.COPY)
		{
			throw new IllegalArgumentException(
				"Illegal value of "
					+ maintType
					+ " passed for Maintenance type");
		}

		_app = app;
		_sqlAlias = sqlAlias;
		_maintType = maintType;

		createUserInterface();
		loadData();
		pack();
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

	private void loadData()
	{
		_aliasName.setText(_sqlAlias.getName());
		_userName.setText(_sqlAlias.getUserName());
		if (_maintType != MaintenanceType.NEW)
		{
			_drivers.setSelectedItem(_sqlAlias.getDriverIdentifier());
			_url.setText(_sqlAlias.getUrl());
		}
		else
		{
			ISQLDriver driver = _drivers.getSelectedDriver();
			if (driver != null)
			{
				_url.setText(driver.getUrl());
			}
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
			if (_maintType == MaintenanceType.NEW
				|| _maintType == MaintenanceType.COPY)
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
			throw new ValidationException("Must select driver"); //i18n
		}
		alias.setName(_aliasName.getText().trim());
		alias.setDriverIdentifier(_drivers.getSelectedDriver().getIdentifier());
		alias.setUrl(_url.getText().trim());
		alias.setUserName(_userName.getText().trim());
	}

	private void showNewDriverDialog()
	{
		DriverMaintSheetFactory.getInstance().showCreateSheet();
	}

	private void createUserInterface()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// This is a tool window.
		GUIUtils.makeToolWindow(this, true);

		final String title =
			_maintType == MaintenanceType.MODIFY
				? (i18n.CHANGE + " " + _sqlAlias.getName())
				: i18n.ADD;
		setTitle(title);

		_aliasName.setColumns(COLUMN_COUNT);
		_url.setColumns(COLUMN_COUNT);
		_userName.setColumns(COLUMN_COUNT);

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

		PropertyPanel dataEntryPnl = new PropertyPanel();

		JLabel lbl = new JLabel(i18n.NAME, SwingConstants.RIGHT);
		dataEntryPnl.add(lbl, _aliasName);

		_drivers = new DriversCombo();
		_drivers.addItemListener(new DriversComboItemListener());
		lbl = new JLabel(i18n.DRIVER, SwingConstants.RIGHT);
		JPanel driverPnl = new JPanel(new BorderLayout());
		driverPnl.add(_drivers, BorderLayout.CENTER);
		JButton newDriverBtn = new JButton("New");
		newDriverBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				showNewDriverDialog();
			}
		});
		driverPnl.add(newDriverBtn, BorderLayout.EAST);
		dataEntryPnl.add(lbl, driverPnl);

		lbl = new JLabel(i18n.URL, SwingConstants.RIGHT);
		dataEntryPnl.add(lbl, _url);

		lbl = new JLabel(i18n.USER_NAME, SwingConstants.RIGHT);
		dataEntryPnl.add(lbl, _userName);

		gbc.insets = new Insets(0, 10, 0, 10);
		++gbc.gridy;
		gbc.weighty = 1;
		contentPane.add(dataEntryPnl, gbc);

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

		_app.getDataCache().addDriversListener(new ObjectCacheChangeListener()
		{
			public void objectAdded(ObjectCacheChangeEvent evt)
			{
				_drivers.addItem(evt.getObject());
			}
			public void objectRemoved(ObjectCacheChangeEvent evt)
			{
				_drivers.removeItem(evt.getObject());
			}
		});
	}

	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		JButton testBtn = new JButton("Test");
		testBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				final DataCache cache = _app.getDataCache();
				final IdentifierFactory factory =
					IdentifierFactory.getInstance();
				final ISQLAlias testAlias =
					cache.createAlias(factory.createIdentifier());
				try
				{
					applyFromDialog(testAlias);
					new ConnectToAliasCommand(
						_app,
						_app.getMainFrame(),
						testAlias,
						false,
						new ConnectionCallBack(_app))
						.execute();
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

		GUIUtils.setJButtonSizesTheSame(
			new JButton[] { okBtn, closeBtn, testBtn });
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
			for (Iterator it = AliasMaintSheet.this._app.getDataCache().drivers();
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
		private ConnectionCallBack(IApplication app)
		{
			super(app);
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
				s_log.error("Error closing Connection", th);
				_app.showErrorDialog(
					"Error closing opened connection: " + th.toString());
			}
			Dialogs.showOk(AliasMaintSheet.this, "Connection successful");
		}

		/**
		 * @see CompletionCallback#sessionCreated(ISession)
		 */
		public void sessionCreated(ISession session)
		{
			s_log.error("Test Button has created a session, this is a programming error");
		}
	}
}
