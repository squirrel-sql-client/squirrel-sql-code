package net.sourceforge.squirrel_sql.fw.gui.sql;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.awt.Dialog;
import java.awt.Frame;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.EventObject;

import javax.swing.JDialog;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.sql.event.IDriverPropertiesPanelListener;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
/**
 * This dialog allows the user to review and maintain
 * the properties for a JDBC driver.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriverPropertiesDialog extends JDialog
{
	private DriverPropertiesPanel _propsPnl;
	private IDriverPropertiesPanelListener _propsPnlLis;

	/** The driver properties. This is only available once OK pressed. */
	private DriverPropertyInfo[] _driverPropInfo;

	/**
	 * Show a modal dialog for the passed driver and URL and if OK
	 * pressed then return the driver properties else 
	 * return <TT>null</TT>.
	 *
	 * @param	owner		Owning frame.
	 * @param	driver		JDBC driver.
	 * @param	url			URL to the database.
	 * @param	override	Used to override the value of some or all
	 * 						of the properties instead of using the 
	 * 						default values.
	 *
	 * @throws	SQLException
	 * 			Thrown if an SQL error occurs.
	 */
	public static DriverPropertyInfo[] showDialog(Frame owner,
								Driver driver, String url,
								SQLDriverProperty[] override)
		throws SQLException
	{
		final DriverPropertiesDialog dlog = new DriverPropertiesDialog(owner,
							driver, url, override);
		dlog.setModal(true);
		dlog.setVisible(true);
		return dlog.getDriverPropertyInfo();
	}

	/**
	 * Show a modal dialog for the passed driver and URL and if OK
	 * pressed then return the driver properties else 
	 * return <TT>null</TT>.
	 *
	 * @param	owner		Owning dialog.
	 * @param	driver		JDBC driver.
	 * @param	url			URL to the database.
	 * @param	override	Used to override the value of some or all
	 * 						of the properties instead of using the 
	 * 						default values.
	 *
	 * @throws	SQLException
	 * 			Thrown if an SQL error occurs.
	 */
	public static DriverPropertyInfo[] showDialog(Dialog owner,
								Driver driver, String url,
								SQLDriverProperty[] override)
		throws SQLException
	{
		final DriverPropertiesDialog dlog = new DriverPropertiesDialog(owner,
							driver, url, override);
		dlog.setModal(true);
		dlog.setVisible(true);
		return dlog.getDriverPropertyInfo();
	}

	public DriverPropertiesDialog(Dialog owner, Driver driver, String url,
									SQLDriverProperty[] override)
		throws SQLException
	{
		super(owner, "Driver Properties");
		if (driver == null)
		{
			throw new IllegalArgumentException("Driver == null");
		}
		if (url == null)
		{
			throw new IllegalArgumentException("url == null");
		}

		createUserInterface(driver, url, override);
	}

	public DriverPropertiesDialog(Frame owner, Driver driver, String url,
									SQLDriverProperty[] override)
		throws SQLException
	{
		super(owner, "Driver Properties");
		if (driver == null)
		{
			throw new IllegalArgumentException("Driver == null");
		}
		if (url == null)
		{
			throw new IllegalArgumentException("url == null");
		}

		createUserInterface(driver, url, override);
	}

	public void dispose()
	{
		if (_propsPnlLis != null)
		{
			removeDriverPropertiesPanelListener(_propsPnlLis);
			_propsPnlLis = null;
		}
		super.dispose();
	}

	/**
	 * Adds a listener for actions in the driver properties panel.
	 *
	 * @param	lis	<TT>IDriverPropertiesPanelListener</TT> that
	 * 				will be notified when actions are performed
	 * 				in the driver properties panel.
	 */
	public void addDriverPropertiesPanelListener(IDriverPropertiesPanelListener lis)
	{
		_propsPnl.addListener(lis);
	}

	/**
	 * Removes a listener from the driver properties panel.
	 *
	 * @param	lis	<TT>IDriverPropertiesPanelListener</TT> to
	 * 				be removed.
	 */
	public void removeDriverPropertiesPanelListener(IDriverPropertiesPanelListener lis)
	{
		_propsPnl.removeListener(lis);
	}

	/**
	 * Retrieve the database driver properties. This is only valid if the
	 * OK button was pressed.
	 *
	 * @return		the database driver properties.
	 */
	public DriverPropertyInfo[] getDriverPropertyInfo()
	{
		return _driverPropInfo;
	}

	private void createUserInterface(Driver driver, String url,
									SQLDriverProperty[] override)
		throws SQLException
	{
		_propsPnl = new DriverPropertiesPanel(driver, url, override);
		_propsPnlLis = new PropertiesPanelListener();
		addDriverPropertiesPanelListener(_propsPnlLis);
		setContentPane(_propsPnl);
		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(true);
	}

	private void performClose()
	{
		dispose();
	}

	private void performOk()
	{
		_driverPropInfo = _propsPnl.getDriverPropertyInfo();
		dispose();
	}

	private final class PropertiesPanelListener implements IDriverPropertiesPanelListener
	{
		/**
		 * The OK button was pressed.
		 *
		 * @param	evt		Describes this event.
		 */
		public void okPressed(EventObject evt)
		{
			performOk();
		}

		/**
		 * The Close button was pressed.
		 *
		 * @param	evt		Describes this event.
		 */
		public void closePressed(EventObject evt)
		{
			performClose();
		}
	}
}

