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
import java.util.EventObject;

import javax.swing.JDialog;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.sql.event.IDriverPropertiesPanelListener;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
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
	private SQLDriverPropertyCollection _driverPropInfo;

	public static SQLDriverPropertyCollection showDialog(Dialog owner,
									SQLDriverPropertyCollection props)
	{
		final DriverPropertiesDialog dlog = new DriverPropertiesDialog(owner, props);
		dlog.setModal(true);
		dlog.setVisible(true);
		return dlog.getSQLDriverPropertyCollection();
	}

	public static SQLDriverPropertyCollection showDialog(Frame owner,
									SQLDriverPropertyCollection props)
	{
		final DriverPropertiesDialog dlog = new DriverPropertiesDialog(owner, props);
		dlog.setModal(true);
		dlog.setVisible(true);
		return dlog.getSQLDriverPropertyCollection();
	}

	public DriverPropertiesDialog(Dialog owner, SQLDriverPropertyCollection props)
	{
		super(owner, "Driver Properties");
		if (props == null)
		{
			throw new IllegalArgumentException("SQLDriverPropertyCollection == null");
		}

		createUserInterface(props);
	}

	public DriverPropertiesDialog(Frame owner, SQLDriverPropertyCollection props)
	{
		super(owner, "Driver Properties");
		if (props == null)
		{
			throw new IllegalArgumentException("SQLDriverPropertyCollection == null");
		}

		createUserInterface(props);
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
	public SQLDriverPropertyCollection getSQLDriverPropertyCollection()
	{
		return _driverPropInfo;
	}

	private void createUserInterface(SQLDriverPropertyCollection props)
	{
		_propsPnl = new DriverPropertiesPanel(props);
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
		_driverPropInfo = _propsPnl.getSQLDriverProperties();
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

