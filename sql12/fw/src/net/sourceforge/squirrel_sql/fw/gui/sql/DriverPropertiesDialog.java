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
import java.sql.SQLException;
import java.util.EventObject;

import javax.swing.JDialog;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.sql.event.IDriverPropertiesPanelListener;
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

	public DriverPropertiesDialog(Dialog owner, Driver driver, String url)
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

		createUserInterface(driver, url);
	}

	public DriverPropertiesDialog(Frame owner, Driver driver, String url)
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

		createUserInterface(driver, url);
	}

	public void dispose()
	{
		if (_propsPnlLis != null)
		{
			_propsPnl.removeListener(_propsPnlLis);
			_propsPnlLis = null;
		}
		super.dispose();
	}

	private void createUserInterface(Driver driver, String url)
		throws SQLException
	{
		_propsPnl = new DriverPropertiesPanel(driver, url);
		_propsPnlLis = new PropertiesPanelListener();
		_propsPnl.addListener(_propsPnlLis);
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

